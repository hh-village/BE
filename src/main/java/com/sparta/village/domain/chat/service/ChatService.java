package com.sparta.village.domain.chat.service;

import com.sparta.village.domain.chat.dto.ChatMessageDto;
import com.sparta.village.domain.chat.dto.ChatMessageResponseDto;
import com.sparta.village.domain.chat.dto.MessageListDto;
import com.sparta.village.domain.chat.dto.RoomListDto;
import com.sparta.village.domain.chat.entity.ChatMessage;
import com.sparta.village.domain.chat.entity.ChatRoom;
import com.sparta.village.domain.chat.repository.ChatMessageRepository;
import com.sparta.village.domain.chat.repository.ChatRoomRepository;
import com.sparta.village.domain.product.entity.Product;
import com.sparta.village.domain.product.repository.ProductRepository;
import com.sparta.village.domain.user.entity.User;
import com.sparta.village.domain.user.service.UserService;
import com.sparta.village.global.exception.CustomException;
import com.sparta.village.global.exception.ErrorCode;
import com.sparta.village.global.exception.ResponseMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatService {
    private final ProductRepository productRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final SimpMessageSendingOperations template;
    private final UserService userService;
    @Transactional
    public ResponseEntity<ResponseMessage> enterRoom(Long productId, String nickname) {
        User user = userService.getUserByNickname(nickname);
        Product product = productRepository.findById(productId).orElseThrow(() -> new CustomException(ErrorCode.PRODUCT_NOT_FOUND));
        User owner = userService.getUserByNickname(product.getUser().getNickname());
        ChatRoom room = chatRoomRepository.findChatRoomByProductAndUser(product, user).orElse(null);
        if (room == null) {
            room = new ChatRoom(product, user, owner);
            chatRoomRepository.saveAndFlush(room);
        }
        return ResponseMessage.SuccessResponse("방 입장", room.getRoomId());
    }

    @Transactional
    public ResponseEntity<ResponseMessage> findMessageHistory(String roomId, User user) {
        if (roomId == null) {
            List<String> roomList = chatMessageRepository.findRoomIdOfLastChatMessage(user.getId());
            if (roomList.size() > 0) {
                roomId = roomList.get(0);
            }
        }
        ChatMessageResponseDto data = roomId == null ? null : findMessageHistoryByRoomId(roomId, user);
        return ResponseMessage.SuccessResponse("대화 불러오기 성공", data);
    }

    @Transactional
    public void saveMessage(ChatMessageDto message) {
        User user = userService.getUserByNickname(message.getSender());
        ChatRoom room = chatRoomRepository.findByRoomId(message.getRoomId()).orElseThrow(() -> new CustomException(ErrorCode.CHATROOM_NOT_FOUND));
        chatMessageRepository.save(new ChatMessage(user, message.getContent(), room));
        template.convertAndSend("/sub/chat/room/" + message.getRoomId(), message);
    }

    private User getConversationPartner(ChatRoom chatRoom, User user) {
        return chatRoom.getUser().getId().equals(user.getId()) ? chatRoom.getOwner() : chatRoom.getUser();
    }

    private ChatMessageResponseDto findMessageHistoryByRoomId(String roomId, User user) {
        ChatRoom room = chatRoomRepository.findByRoomId(roomId).orElseThrow(() -> new CustomException(ErrorCode.CHATROOM_NOT_FOUND));
        List<MessageListDto> messageList = chatMessageRepository.findAllByRoom(room).stream()
                .map(m -> new MessageListDto(m.getSender().getNickname(), m.getContent(), m.getRoom().getRoomId())).toList();
        List<ChatRoom> chatRoomList = chatRoomRepository.findAllChatRoomByUser(user);
        List<RoomListDto> roomList = new ArrayList<>();
        for (ChatRoom chatRoom : chatRoomList) {
            boolean target = chatRoom.getRoomId().equals(roomId);
            User other = getConversationPartner(chatRoom, user);
            List<ChatMessage> chatMessageList = chatMessageRepository.findLastMessageByRoom(chatRoom);
            String lastMessage = chatMessageList.size() > 0 ? chatMessageList.get(0).getContent() : null;
            roomList.add(new RoomListDto(chatRoom.getRoomId(), other.getNickname(), other.getProfile(), lastMessage, target));
        }
        return new ChatMessageResponseDto(messageList, roomList);
    }

    public void deleteByProductId(Long id) {
        chatRoomRepository.deleteByProductId(id);
    }

    public void deleteMessagesByProductId(Long id) {
        chatMessageRepository.deleteMessagesByProductId(id);
    }


}
