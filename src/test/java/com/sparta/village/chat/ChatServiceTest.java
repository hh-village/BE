package com.sparta.village.chat;

import com.sparta.village.domain.chat.dto.ChatMessageResponseDto;
import com.sparta.village.domain.chat.dto.MessageListDto;
import com.sparta.village.domain.chat.dto.RoomListDto;
import com.sparta.village.domain.chat.entity.ChatMessage;
import com.sparta.village.domain.chat.entity.ChatRoom;
import com.sparta.village.domain.chat.repository.ChatMessageRepository;
import com.sparta.village.domain.chat.repository.ChatRoomRepository;
import com.sparta.village.domain.chat.service.ChatService;
import com.sparta.village.domain.product.entity.Product;
import com.sparta.village.domain.product.repository.ProductRepository;
import com.sparta.village.domain.reservation.dto.ReservationRequestDto;
import com.sparta.village.domain.reservation.entity.Reservation;
import com.sparta.village.domain.user.entity.User;
import com.sparta.village.domain.user.entity.UserRoleEnum;
import com.sparta.village.domain.user.repository.UserRepository;
import com.sparta.village.domain.user.service.UserService;
import com.sparta.village.global.exception.CustomException;
import com.sparta.village.global.exception.ResponseMessage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@Transactional
@ExtendWith(MockitoExtension.class)
public class ChatServiceTest {
    @InjectMocks
    private ChatService chatService;

    @Mock
    private ChatRoomRepository chatRoomRepository;
    @Mock
    private ChatMessageRepository chatMessageRepository;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ChatRoom chatRoom;
    @Mock
    private UserService userService;
    @Mock
    private Product product;
    @Mock
    private User user;
    @Mock
    private ChatRoom room;

    @Spy
    private SimpMessageSendingOperations simpMessageSendingOperations;

    @Test
    @DisplayName("방 입장 하기 - 정상 케이스(방이 있는 경우)")
    public void reEnterRoomSuccessTest() {
        //given
        Long productId = 1L;
        String nickname = "nickname";
        User user = new User(1L,1L, "nickname", "profile1", UserRoleEnum.USER);
        User owner = new User(2L,2L, "nickname1", "profile1", UserRoleEnum.USER);
        Product product = new Product(productId, "title", "description", 1000, "대전광역시", 0, owner);
        ChatRoom room = new ChatRoom(1L, "roomid", product, user, owner);

//        doReturn(Optional.of(user)).when(userRepository).findByNickname(nickname);
        doReturn(user).when(userService).getUserByNickname(nickname);
        doReturn(owner).when(userService).getUserByNickname(product.getUser().getNickname());
        doReturn(Optional.of(product)).when(productRepository).findById(productId);
        doReturn(Optional.of(room)).when(chatRoomRepository).findChatRoomByProductAndUser(product, user);

        //when
        ResponseEntity<ResponseMessage> response = chatService.enterRoom(productId, nickname);

        //then
        assertEquals(response.getStatusCode(), HttpStatus.OK);
        assertEquals(Objects.requireNonNull(response.getBody()).getMessage(), "방 입장");
        assertEquals(Objects.requireNonNull(response.getBody()).getData(), room.getRoomId());

        // verify
        verify(productRepository, times(1)).findById(productId);
        verify(chatRoomRepository, times(1)).findChatRoomByProductAndUser(product, user);
        verify(userService, times(1)).getUserByNickname(nickname);
        verify(userService, times(1)).getUserByNickname(product.getUser().getNickname());

    }

    @Test
    @DisplayName("방 입장 하기 - 정상 케이스(방이 없는 경우)")
    public void firstTimeEnterRoomSuccessTest() {
        //given
        Long productId = 1L;
        String nickname = "nickname";
        User user = new User(1L,1L, "nickname", "profile1", UserRoleEnum.USER);
        User owner = new User(2L,2L, "nickname1", "profile1", UserRoleEnum.USER);
        Product product = new Product(productId, "title", "description", 1000, "대전광역시", 0, owner);

        doReturn(user).when(userService).getUserByNickname(nickname);
        doReturn(owner).when(userService).getUserByNickname(product.getUser().getNickname());
        doReturn(Optional.of(product)).when(productRepository).findById(productId);
        doReturn(Optional.empty()).when(chatRoomRepository).findChatRoomByProductAndUser(product, user);
        when(chatRoomRepository.saveAndFlush(any(ChatRoom.class))).thenAnswer(invocation -> invocation.getArgument(0));

        //when
        ResponseEntity<ResponseMessage> response = chatService.enterRoom(productId, nickname);

        //then
        assertEquals(response.getStatusCode(), HttpStatus.OK);
        assertEquals(Objects.requireNonNull(response.getBody()).getMessage(), "방 입장");

        // verify
        verify(productRepository, times(1)).findById(productId);
        verify(chatRoomRepository, times(1)).findChatRoomByProductAndUser(product, user);
        verify(userService, times(1)).getUserByNickname(nickname);
        verify(userService, times(1)).getUserByNickname(product.getUser().getNickname());
    }

    @Test
    @DisplayName("방 입장-제품이 없을 때")
    public void enterRoomTest_NoProduct() {
        //given
        Long productId = 1L;
        Product product = new Product(productId, "title", "description", 1000, "대전광역시", 0, user);
        String nickname = "nickname";

        doReturn(Optional.empty()).when(productRepository).findById(productId);

        //when
//        ResponseEntity<ResponseMessage> response = chatService.enterRoom(productId, nickname);

        //then
        assertThrows(CustomException.class, () -> chatService.enterRoom(productId, nickname));

        // verify
        verify(productRepository, times(1)).findById(productId);
        verify(chatRoomRepository, times(0)).findChatRoomByProductAndUser(product, user);
        verify(userService, times(1)).getUserByNickname(nickname);
        verify(userService, times(0)).getUserByNickname(product.getUser().getNickname());

    }

    @Test
    @DisplayName("메세지 기록 찾기 - 정상 케이스(방이 있는 경우)")
    public void findMessageHistorySuccessTest() {
        //given
        String roomId = "roomId1";
        User user = new User(1L,1L, "nickname", "profile1", UserRoleEnum.USER);
        User owner = new User(2L,2L, "nickname1", "profile1", UserRoleEnum.USER);
        ChatRoom room1 = new ChatRoom(1L, "roomId1", product, user, owner);
        ChatRoom room2 = new ChatRoom(2L, "roomId2", product, user, owner);
        ChatMessageResponseDto responseDto = new ChatMessageResponseDto(getMessageDtoList(room1, user), getChatRoomDtoList(room1, room2));

        doReturn(Optional.of(room1)).when(chatRoomRepository).findByRoomId(roomId);
        doReturn(getMessageList(room1, user)).when(chatMessageRepository).findAllByRoom(room1);
        doReturn(Arrays.asList(room1, room2)).when(chatRoomRepository).findAllChatRoomByUser(user);
        doReturn(getLastMessageList(room1, user)).when(chatMessageRepository).findLastMessageByRoom(room1);
        doReturn(getLastMessageList(room2, user)).when(chatMessageRepository).findLastMessageByRoom(room2);

        //when
        ResponseEntity<ResponseMessage> response = chatService.findMessageHistory(roomId, user);
        ChatMessageResponseDto chatMessageResponseDto = (ChatMessageResponseDto) Objects.requireNonNull(response.getBody()).getData();

        //then
        assertEquals(response.getStatusCode(), HttpStatus.OK);
        assertEquals(Objects.requireNonNull(response.getBody()).getMessage(), "대화 불러오기 성공");
        assertIterableEquals(chatMessageResponseDto.getMessageList(), responseDto.getMessageList());
        assertIterableEquals(chatMessageResponseDto.getRoomList(), responseDto.getRoomList());

        // verify
        verify(chatRoomRepository, times(1)).findByRoomId(roomId);
        verify(chatRoomRepository, times(1)).findAllChatRoomByUser(user);
        verify(chatMessageRepository, times(1)).findAllByRoom(room1);
        verify(chatMessageRepository, times(1)).findLastMessageByRoom(room1);
        verify(chatMessageRepository, times(1)).findLastMessageByRoom(room2);
    }

    private List<ChatMessage> getMessageList(ChatRoom room, User user) {
        List<ChatMessage> messageList = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            messageList.add(new ChatMessage((long) i+1, user,"content", room));
        }
        return messageList;
    }

    private List<ChatMessage> getLastMessageList(ChatRoom room, User user) {
        List<ChatMessage> messageList = new ArrayList<>();
        for (int i = 5; i > 0; i--) {
            messageList.add(new ChatMessage((long) i, user,"content", room));
        }
        return messageList;
    }

    private List<MessageListDto> getMessageDtoList(ChatRoom room, User user) {
        List<MessageListDto> messageList = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            messageList.add(new MessageListDto("nickname", "content", "roomId1"));
        }
        return messageList;
    }

    private List<RoomListDto> getChatRoomDtoList(ChatRoom room1, ChatRoom room2) {
        List<RoomListDto> chatRoomList = new ArrayList<>();
        chatRoomList.add(new RoomListDto(room1.getRoomId(), "nickname1", "profile1", "content", true));
        chatRoomList.add(new RoomListDto(room2.getRoomId(), "nickname1", "profile1", "content", false));
        return chatRoomList;
    }



}
