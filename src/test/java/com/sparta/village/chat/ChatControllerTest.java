//package com.sparta.village.chat;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
//import com.sparta.village.WithCustomMockUser;
//import com.sparta.village.domain.chat.controller.ChatController;
//import com.sparta.village.domain.chat.dto.ChatMessageDto;
//import com.sparta.village.domain.chat.dto.MyChatRoomResponseDto;
//import com.sparta.village.domain.chat.service.ChatService;
//import com.sparta.village.domain.user.entity.User;
//import com.sparta.village.domain.user.entity.UserRoleEnum;
//import com.sparta.village.global.exception.ResponseMessage;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.junit.runner.RunWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.Spy;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.http.MediaType;
//import org.springframework.http.ResponseEntity;
//import org.springframework.messaging.simp.SimpMessageSendingOperations;
//import org.springframework.test.context.junit4.SpringRunner;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
//import org.springframework.test.web.servlet.setup.MockMvcBuilders;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.Optional;
//
//import static org.mockito.Mockito.*;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//@Transactional
//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//@RunWith(SpringRunner.class)
//public class ChatControllerTest {
//
//    @InjectMocks
//    private ChatController chatController;
//
//    @Mock
//    private ChatService chatService;
//
//    @Spy
//    private SimpMessageSendingOperations simpMessageSendingOperations;
//    private MockMvc mockMvc;
//
//    @BeforeEach
//    public void init() {
//        mockMvc = MockMvcBuilders.standaloneSetup(chatController).build();
//    }
//
//    @Test
//    @DisplayName("채팅방 입장하기")
//    public void enterRoomSuccessTest() throws Exception {
//        Long productId = 1L;
//        String nickname = "nickname";
//        ResponseEntity<ResponseMessage> response = ResponseMessage.SuccessResponse("방 입장", "roomId");
//        doReturn(response).when(chatService).enterRoom(productId, nickname);
////        doReturn(Optional.of(user)).when(userRepository).findByKakaoId(1L);
//
//        mockMvc.perform(
//                        MockMvcRequestBuilders.post("/chat/room/{productId}/{nickname}", productId, nickname))
//                .andExpect(status().isOk())
//                .andExpect(result -> ResponseEntity.of(Optional.of(response)));
//
//    }
//
//    @Test
//    @WithCustomMockUser
//    @DisplayName("채팅 기록 가져오기")
//    public void findMessageHistorySuccessTest() throws Exception {
//        String roomId = "roomId";
//        User user = new User(1L,1L, "nickname", "profile1", UserRoleEnum.USER);
//        ResponseEntity<ResponseMessage> response = ResponseMessage.SuccessResponse("대화 불러오기 성공", MyChatRoomResponseDto.class);
//        doReturn(response).when(chatService).findMessageHistory(roomId, user);
////        doReturn(Optional.of(user)).when(userRepository).findByKakaoId(1L);
//
//        mockMvc.perform(
//                        MockMvcRequestBuilders.get("/chat/room").param("roomId", "roomId"))
//                .andExpect(status().isOk())
//                .andExpect(result -> ResponseEntity.of(Optional.of(response)));
//
//    }
//
//    @Test
//    @DisplayName("채팅방 삭제")
//    public void deleteRoomSuccessTest() throws Exception {
//        String roomId = "roomId";
//        ResponseEntity<ResponseMessage> response = ResponseMessage.SuccessResponse("채팅방 삭제 성공", "");
//        doReturn(response).when(chatService).deleteRoom(roomId);
//
//        mockMvc.perform(
//                        MockMvcRequestBuilders.delete("/chat/room/{roomId}", roomId))
//                .andExpect(status().isOk())
//                .andExpect(result -> ResponseEntity.of(Optional.of(response)));
//
//    }
//
//    @Test
//    @DisplayName("메시지 보내기")
//    public void saveMessageSuccessTest() throws Exception {
//        String roomId = "roomId";
//        User user = new User(1L,1L, "nickname", "profile1", UserRoleEnum.USER);
//        ChatMessageDto chatMessageDto = new ChatMessageDto("roomId", "nickname", "content");
//        doNothing().when(chatService).saveMessage(chatMessageDto);
//
//        ObjectMapper objectMapper = new ObjectMapper();
//        String jsonRequestDto = objectMapper.writeValueAsString(chatMessageDto);
//
//
//        simpMessageSendingOperations.convertAndSend("/sub/chat/message/roomId", chatMessageDto);
//
////        mockMvc.perform(
////                        MockMvcRequestBuilders.("/chat/message")
////                                .content(jsonRequestDto)
////                                .contentType(MediaType.APPLICATION_JSON))
////                .andExpect(status().isOk());
////        verify(chatService, times(1)).saveMessage(any(ChatMessageDto.class));
//    }
//
//}
