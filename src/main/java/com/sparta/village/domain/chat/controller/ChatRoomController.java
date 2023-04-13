package com.sparta.village.domain.chat.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.sparta.village.domain.chat.dto.ChatMessageDto;
import com.sparta.village.domain.chat.service.ChatRoomService;
import com.sparta.village.global.exception.ResponseMessage;
import com.sparta.village.global.security.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class ChatRoomController {
    private final ChatRoomService chatRoomService;

    @PostMapping("/chat/room/{productId}/{nickname}")
    public ResponseEntity<ResponseMessage> enterRoom(@PathVariable Long productId, @PathVariable String nickname) {
        return chatRoomService.enterRoom(productId, nickname);
    }

    @GetMapping("/chat/room")
    public ResponseEntity<ResponseMessage> findMessageHistory(@RequestParam(value = "roomId", required = false) String roomId, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        return chatRoomService.findMessageHistory(roomId, userDetails.getUser());
    }

    @MessageMapping(value = "/chat/message")
    public void message(ChatMessageDto message) throws JsonProcessingException {
        System.out.println("컨트롤러 들어옴");
        chatRoomService.saveMessage(message);
    }
}
