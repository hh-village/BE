package com.sparta.village.reservation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.sparta.village.WithCustomMockUser;
import com.sparta.village.domain.reservation.controller.ReservationController;
import com.sparta.village.domain.reservation.dto.ReservationRequestDto;
import com.sparta.village.domain.reservation.dto.StatusRequestDto;
import com.sparta.village.domain.reservation.service.ReservationService;
import com.sparta.village.domain.user.entity.User;
import com.sparta.village.domain.user.entity.UserRoleEnum;
import com.sparta.village.domain.user.repository.UserRepository;
import com.sparta.village.global.exception.ResponseMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Optional;

import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc
public class ReservationControllerTest {
    @InjectMocks
    private ReservationController reservationController;

    @Mock
    private ReservationService reservationService;

    @Mock
    private UserRepository userRepository;

    private MockMvc mockMvc;

    @BeforeEach
    public void init() {
        mockMvc = MockMvcBuilders.standaloneSetup(reservationController).build();
    }

    @Test
    @WithCustomMockUser
    @DisplayName("예약하기")
    public void reserveSuccessTest() throws Exception {
        Long id = 1L;
        ReservationRequestDto requestDto = reservationRequest(true);
        User user = new User(1L,1L, "nickname", "profile1", UserRoleEnum.USER, false);
        ResponseEntity<ResponseMessage> response = ResponseMessage.SuccessResponse("예약 되었습니다.", "");
        doReturn(response).when(reservationService).reserve(id, requestDto, user);
        doReturn(Optional.of(user)).when(userRepository).findByKakaoId(1L);

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        String jsonRequestDto = objectMapper.writeValueAsString(requestDto);

        mockMvc.perform(
                MockMvcRequestBuilders.post("/products/{id}/reserve", id)
                .content(jsonRequestDto)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

    }


    @Test
    @WithCustomMockUser
    @DisplayName("예약 삭제하기")
    public void deleteReserveSuccessTest() throws Exception {
        Long id = 1L;
        User user = new User(1L,1L, "nickname", "profile1", UserRoleEnum.USER, false);
        ResponseEntity<ResponseMessage> response = ResponseMessage.SuccessResponse("예약 취소되었습니다.", "");
        doReturn(response).when(reservationService).deleteReservation(id, user);
        doReturn(Optional.of(user)).when(userRepository).findByKakaoId(1L);

        mockMvc.perform(
                        MockMvcRequestBuilders.delete("/products/reservation/{id}", id)).andExpect(status().isOk());

    }

    @Test
    @WithCustomMockUser
    @DisplayName("예약 상태변경하기")
    public void changeStatusSuccessTest() throws Exception {
        Long id = 1L;
        StatusRequestDto requestDto = new StatusRequestDto("accepted");
        User user = new User(1L,1L, "nickname", "profile1", UserRoleEnum.USER, false);
        ResponseEntity<ResponseMessage> response = ResponseMessage.SuccessResponse("상태 변경되었습니다.", "");
        doReturn(response).when(reservationService).changeStatus(id, requestDto, user);
        doReturn(Optional.of(user)).when(userRepository).findByKakaoId(1L);

        ObjectMapper objectMapper = new ObjectMapper();
        String jsonRequestDto = objectMapper.writeValueAsString(requestDto);

        mockMvc.perform(
                        MockMvcRequestBuilders.patch("/products/reservation/{id}/status", id)
                                .content(jsonRequestDto)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

    }

    private ReservationRequestDto reservationRequest(boolean checkProper) {
        return checkProper ? new ReservationRequestDto(LocalDate.of(2023,5,10), LocalDate.of(2023,5,13)) :
                new ReservationRequestDto(LocalDate.of(2023,5,13), LocalDate.of(2023,5,10));
    }
}
