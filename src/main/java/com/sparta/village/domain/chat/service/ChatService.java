package com.sparta.village.domain.chat.service;

import com.sparta.village.domain.chat.dto.ChatMessageDto;
import com.sparta.village.domain.chat.dto.ChatMessageResponseDto;
import com.sparta.village.domain.chat.entity.ChatMessage;
import com.sparta.village.domain.chat.entity.ChatRoom;
import com.sparta.village.domain.chat.repository.ChatMessageQueryRepository;
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

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class ChatService {
    private final ProductRepository productRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final ChatMessageQueryRepository chatMessageQueryRepository;
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
        return ResponseMessage.SuccessResponse("방 입장", room.getId());
    }

    @Transactional
    public ResponseEntity<ResponseMessage> findMessageHistory(Long roomId, User user) {
        return ResponseMessage.SuccessResponse("대화 불러오기 성공", chatMessageQueryRepository.findMessageList(roomId, user.getId()));
    }

    @Transactional
    public void saveMessage(ChatMessageDto message) {
        User user = userService.getUserByNickname(message.getSender());
        ChatRoom room = chatRoomRepository.findByRoomId(message.getRoomId()).orElseThrow(() -> new CustomException(ErrorCode.CHATROOM_NOT_FOUND));
        ChatMessage chatMessage = new ChatMessage(user, message.getContent(), room);
        chatMessageRepository.saveAndFlush(chatMessage);
        ChatMessageResponseDto responseDto = new ChatMessageResponseDto(chatMessage.getRoom().getId(), chatMessage.getSender().getNickname(), chatMessage.getContent(), changeDateFormat(chatMessage.getCreatedAt()));
        template.convertAndSend("/sub/chat/room/" + message.getRoomId(), responseDto);
    }

    @Transactional
    public ResponseEntity<ResponseMessage> deleteRoom(Long roomId) {
        ChatRoom room = chatRoomRepository.findById(roomId).orElseThrow(() -> new CustomException(ErrorCode.CHATROOM_NOT_FOUND));
        chatRoomRepository.deleteAllAboutRoomById(room.getId());
        return ResponseMessage.SuccessResponse("채팅방 삭제 성공", "");
    }

    private String changeDateFormat(String createdAt) {
        String[] date = createdAt.split(" ");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String today = ZonedDateTime.now(ZoneId.of("Asia/Seoul")).format(formatter);
        return date[0].equals(today) ? date[1] : date[0];
    }

}
