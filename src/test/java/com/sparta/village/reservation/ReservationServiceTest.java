package com.sparta.village.reservation;

import com.sparta.village.domain.product.entity.Product;
import com.sparta.village.domain.product.repository.ProductRepository;
import com.sparta.village.domain.reservation.dto.ReservationRequestDto;
import com.sparta.village.domain.reservation.dto.StatusRequestDto;
import com.sparta.village.domain.reservation.dto.UserLevelDto;
import com.sparta.village.domain.reservation.entity.Reservation;
import com.sparta.village.domain.reservation.repository.ReservationRepository;
import com.sparta.village.domain.reservation.service.ReservationService;
import com.sparta.village.domain.user.entity.User;
import com.sparta.village.domain.user.entity.UserRoleEnum;
import com.sparta.village.domain.user.service.UserService;
import com.sparta.village.global.exception.CustomException;
import com.sparta.village.global.exception.ResponseMessage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;

//@SpringBootTest
@Transactional
@ExtendWith(MockitoExtension.class)
class ReservationServiceTest {
    @InjectMocks
    private ReservationService reservationService;

    @Mock
    private ReservationRepository reservationRepository;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private UserService userService;
    @Mock
    private User user;

    @Mock
    private Product product;

    @Test
    @DisplayName("예약하기-정상케이스")
    public void ReserveSuccessTest() {
        //given
        Long productId = 1L;
        Product product = new Product(productId, "title", "description", 1000, "대전광역시", 0, user);
        ReservationRequestDto reservationRequestDto = reservationRequest();

        doReturn(Optional.of(product)).when(productRepository).findById(productId);
        doReturn(false).when(reservationRepository).checkOverlapDateByProduct(product, reservationRequestDto.getStartDate(), reservationRequestDto.getEndDate());
        doReturn(new Reservation(1L, "waiting", reservationRequestDto.getStartDate(), reservationRequestDto.getEndDate(), user, product)).when(reservationRepository).saveAndFlush(any(Reservation.class));

        //when
        ResponseEntity<ResponseMessage> response = reservationService.reserve(productId, reservationRequestDto, user);

        //then
        assertEquals(response.getStatusCode(), HttpStatus.OK);
        assertEquals(Objects.requireNonNull(response.getBody()).getMessage(), "예약 되었습니다.");

        // verify
        verify(productRepository, times(1)).findById(productId);
        verify(reservationRepository, times(1)).checkOverlapDateByProduct(product, reservationRequestDto.getStartDate(), reservationRequestDto.getEndDate());
        verify(reservationRepository, times(1)).saveAndFlush(any(Reservation.class));
    }
    private ReservationRequestDto reservationRequest() {
        return new ReservationRequestDto(LocalDate.of(2023,5,10), LocalDate.of(2023,5,13));
    }

    @Test
    @DisplayName("예약하기-중복 날짜 예외 처리")
    public void ReserveDuplicateDateTest() {
        //given
        Long productId = 1L;
        Product product = new Product(productId, "title", "description", 1000, "대전광역시", 0, user);
        ReservationRequestDto reservationRequestDto = reservationRequest();

        doReturn(Optional.of(product)).when(productRepository).findById(productId);
        doReturn(true).when(reservationRepository).checkOverlapDateByProduct(product, reservationRequestDto.getStartDate(), reservationRequestDto.getEndDate());

        //when
//        ResponseEntity<ResponseMessage> response = reservationService.reserve(productId, reservationRequestDto, user);

        //then
        assertThrows(CustomException.class, () -> reservationService.reserve(productId, reservationRequestDto, user));

        // verify
        verify(productRepository, times(1)).findById(productId);
        verify(reservationRepository, times(1)).checkOverlapDateByProduct(product, reservationRequestDto.getStartDate(), reservationRequestDto.getEndDate());
        verify(reservationRepository, times(0)).saveAndFlush(any(Reservation.class));
    }

    @Test
    @DisplayName("예약하기-제품이 없을 때")
    public void ReserveProductNotFoundTest() {
        //given
        Long productId = 1L;
        Product product = new Product(productId, "title", "description", 1000, "대전광역시", 0, user);
        ReservationRequestDto reservationRequestDto = reservationRequest();

        doReturn(Optional.empty()).when(productRepository).findById(productId);

        //when
//        ResponseEntity<ResponseMessage> response = reservationService.reserve(productId, reservationRequestDto, user);

        //then
        assertThrows(CustomException.class, () -> reservationService.reserve(productId, reservationRequestDto, user));

        // verify
        verify(productRepository, times(1)).findById(productId);
        verify(reservationRepository, times(0)).checkOverlapDateByProduct(product, reservationRequestDto.getStartDate(), reservationRequestDto.getEndDate());
        verify(reservationRepository, times(0)).saveAndFlush(any(Reservation.class));
    }

    @Test
    @DisplayName("예약 취소 - 정상케이스")
    public void testDeleteReservation() {
        //given
        Long reservationId = 1L;
        User user = new User(1L,1L, "nickname", "profile1", UserRoleEnum.USER);
        Reservation reservation = new Reservation(reservationId, "waiting", LocalDate.of(2023,5,10), LocalDate.of(2023,5,13), user, product);

        doReturn(Optional.of(reservation)).when(reservationRepository).findById(reservationId);
        doNothing().when(reservationRepository).deleteById(reservationId);

        //when
        ResponseEntity<ResponseMessage> response = reservationService.deleteReservation(reservationId, user);

        //then
        assertEquals(response.getStatusCode(), HttpStatus.OK);
        assertEquals(Objects.requireNonNull(response.getBody()).getMessage(), "예약 취소되었습니다.");

        // verify
        verify(reservationRepository, times(1)).findById(reservationId);
        verify(reservationRepository, times(1)).deleteById(reservationId);
    }

    @Test
    @DisplayName("예약 취소 - 예약 번호가 다를 경우")
    public void testDeleteReservationByNotProperId() {
        //given
        Long reservationId = 1L;
        User user = new User(1L,1L, "nickname", "profile1", UserRoleEnum.USER);

        doReturn(Optional.empty()).when(reservationRepository).findById(reservationId);

        //when
//        ResponseEntity<ResponseMessage> response = reservationService.deleteReservation(reservationId, user);

        //then
        assertThrows(CustomException.class, () -> reservationService.deleteReservation(reservationId, user));

        // verify
        verify(reservationRepository, times(1)).findById(reservationId);
        verify(reservationRepository, times(0)).deleteById(reservationId);
    }

    @Test
    @DisplayName("예약 취소 - 구매자가 아닌 경우")
    public void testDeleteReservationByInvalidUserId() {
        //given
        Long reservationId = 1L;
        User user = new User(1L,1L, "nickname", "profile1", UserRoleEnum.USER);
        User other = new User(2L,2L, "nickname1", "profile1", UserRoleEnum.USER);
        Reservation reservation = new Reservation(reservationId, "waiting", LocalDate.of(2023,5,10), LocalDate.of(2023,5,13), user, product);
        doReturn(Optional.of(reservation)).when(reservationRepository).findById(reservationId);

        //when
//        ResponseEntity<ResponseMessage> response = reservationService.deleteReservation(reservationId, user);

        //then
        assertThrows(CustomException.class, () -> reservationService.deleteReservation(reservationId, other));

        // verify
        verify(reservationRepository, times(1)).findById(reservationId);
        verify(reservationRepository, times(0)).deleteById(reservationId);
    }

    @Test
    @DisplayName("예약 취소 - 현재 상태값이 대기중이 아닐 경우")
    public void testDeleteReservationWithStatusNotWaiting() {
        //given
        Long reservationId = 1L;
        User user = new User(1L,1L, "nickname", "profile1", UserRoleEnum.USER);
        Reservation reservation = new Reservation(reservationId, "accepted", LocalDate.of(2023,5,10), LocalDate.of(2023,5,13), user, product);
        doReturn(Optional.of(reservation)).when(reservationRepository).findById(reservationId);

        //when
//        ResponseEntity<ResponseMessage> response = reservationService.deleteReservation(reservationId, user);

        //then
        assertThrows(CustomException.class, () -> reservationService.deleteReservation(reservationId, user));

        // verify
        verify(reservationRepository, times(1)).findById(reservationId);
        verify(reservationRepository, times(0)).deleteById(reservationId);
    }

    @Test
    @DisplayName("예약 취소 - 유저가 로그인 안 했을경우")
    public void testDeleteReservationWithNotLoginUser() {
        //given
        Long reservationId = 1L;
        User user = new User(1L,1L, "nickname", "profile1", UserRoleEnum.USER);
        Reservation reservation = new Reservation(reservationId, "accepted", LocalDate.of(2023,5,10), LocalDate.of(2023,5,13), user, product);
        doReturn(Optional.of(reservation)).when(reservationRepository).findById(reservationId);

        //when
//        ResponseEntity<ResponseMessage> response = reservationService.deleteReservation(reservationId, user);

        //then
        assertThrows(CustomException.class, () -> reservationService.deleteReservation(reservationId, null));

        // verify
        verify(reservationRepository, times(1)).findById(reservationId);
        verify(reservationRepository, times(0)).deleteById(reservationId);
    }

    @Test
    @DisplayName("예약 상태 변경 - 정상케이스(유저 등급5)")
    public void testChangeStatusLevel5() {
        //given
        Long reservationId = 1L;
        User user = new User(1L,1L, "nickname", "profile1", UserRoleEnum.USER);
        Reservation reservation = new Reservation(reservationId, "waiting", LocalDate.of(2023,5,10), LocalDate.of(2023,5,13), user, product);
        StatusRequestDto statusRequestDto = new StatusRequestDto("accepted");

        doReturn(Optional.of(reservation)).when(reservationRepository).findById(reservationId);
        doReturn(true).when(reservationRepository).checkProductOwner(reservationId, user);
        doNothing().when(reservationRepository).updateStatus(reservationId, statusRequestDto.getStatus());
        doReturn(reservationList(10, 9)).when(reservationRepository).findUserLevelData(user);
        doNothing().when(userService).updateProfileIfNeeded(user, "https://s3-village-image.s3.ap-northeast-2.amazonaws.com/profile5.png");

        //when
        ResponseEntity<ResponseMessage> response = reservationService.changeStatus(reservationId, statusRequestDto, user);

        //then
        assertEquals(response.getStatusCode(), HttpStatus.OK);
        assertEquals(Objects.requireNonNull(response.getBody()).getMessage(), "상태 변경되었습니다.");

        // verify
        verify(reservationRepository, times(1)).findById(reservationId);
        verify(reservationRepository, times(1)).checkProductOwner(reservationId, user);
        verify(reservationRepository, times(1)).updateStatus(reservationId, statusRequestDto.getStatus());
        verify(reservationRepository, times(1)).findUserLevelData(user);
        verify(userService, times(1)).updateProfileIfNeeded(user, "https://s3-village-image.s3.ap-northeast-2.amazonaws.com/profile5.png");
    }

    @Test
    @DisplayName("예약 상태 변경 - 정상케이스(유저 등급4_1)")
    public void testChangeStatusLevel4_1() {
        //given
        Long reservationId = 1L;
        User user = new User(1L,1L, "nickname", "profile1", UserRoleEnum.USER);
        Reservation reservation = new Reservation(reservationId, "waiting", LocalDate.of(2023,5,10), LocalDate.of(2023,5,13), user, product);
        StatusRequestDto statusRequestDto = new StatusRequestDto("accepted");

        doReturn(Optional.of(reservation)).when(reservationRepository).findById(reservationId);
        doReturn(true).when(reservationRepository).checkProductOwner(reservationId, user);
        doNothing().when(reservationRepository).updateStatus(reservationId, statusRequestDto.getStatus());
        doReturn(reservationList(10, 8)).when(reservationRepository).findUserLevelData(user);
        doNothing().when(userService).updateProfileIfNeeded(user, "https://s3-village-image.s3.ap-northeast-2.amazonaws.com/profile4.png");

        //when
        ResponseEntity<ResponseMessage> response = reservationService.changeStatus(reservationId, statusRequestDto, user);

        //then
        assertEquals(response.getStatusCode(), HttpStatus.OK);
        assertEquals(Objects.requireNonNull(response.getBody()).getMessage(), "상태 변경되었습니다.");

        // verify
        verify(reservationRepository, times(1)).findById(reservationId);
        verify(reservationRepository, times(1)).checkProductOwner(reservationId, user);
        verify(reservationRepository, times(1)).updateStatus(reservationId, statusRequestDto.getStatus());
        verify(reservationRepository, times(1)).findUserLevelData(user);
        verify(userService, times(1)).updateProfileIfNeeded(user, "https://s3-village-image.s3.ap-northeast-2.amazonaws.com/profile4.png");
    }

    @Test
    @DisplayName("예약 상태 변경 - 정상케이스(유저 등급4_2)")
    public void testChangeStatusLevel4_2() {
        //given
        Long reservationId = 1L;
        User user = new User(1L,1L, "nickname", "profile1", UserRoleEnum.USER);
        Reservation reservation = new Reservation(reservationId, "waiting", LocalDate.of(2023,5,10), LocalDate.of(2023,5,13), user, product);
        StatusRequestDto statusRequestDto = new StatusRequestDto("accepted");

        doReturn(Optional.of(reservation)).when(reservationRepository).findById(reservationId);
        doReturn(true).when(reservationRepository).checkProductOwner(reservationId, user);
        doNothing().when(reservationRepository).updateStatus(reservationId, statusRequestDto.getStatus());
        doReturn(reservationList(4, 9)).when(reservationRepository).findUserLevelData(user);
        doNothing().when(userService).updateProfileIfNeeded(user, "https://s3-village-image.s3.ap-northeast-2.amazonaws.com/profile4.png");

        //when
        ResponseEntity<ResponseMessage> response = reservationService.changeStatus(reservationId, statusRequestDto, user);

        //then
        assertEquals(response.getStatusCode(), HttpStatus.OK);
        assertEquals(Objects.requireNonNull(response.getBody()).getMessage(), "상태 변경되었습니다.");

        // verify
        verify(reservationRepository, times(1)).findById(reservationId);
        verify(reservationRepository, times(1)).checkProductOwner(reservationId, user);
        verify(reservationRepository, times(1)).updateStatus(reservationId, statusRequestDto.getStatus());
        verify(reservationRepository, times(1)).findUserLevelData(user);
        verify(userService, times(1)).updateProfileIfNeeded(user, "https://s3-village-image.s3.ap-northeast-2.amazonaws.com/profile4.png");
    }

    @Test
    @DisplayName("예약 상태 변경 - 정상케이스(유저 등급4)")
    public void testChangeStatusLevel4() {
        //given
        Long reservationId = 1L;
        User user = new User(1L,1L, "nickname", "profile1", UserRoleEnum.USER);
        Reservation reservation = new Reservation(reservationId, "waiting", LocalDate.of(2023,5,10), LocalDate.of(2023,5,13), user, product);
        StatusRequestDto statusRequestDto = new StatusRequestDto("accepted");

        doReturn(Optional.of(reservation)).when(reservationRepository).findById(reservationId);
        doReturn(true).when(reservationRepository).checkProductOwner(reservationId, user);
        doNothing().when(reservationRepository).updateStatus(reservationId, statusRequestDto.getStatus());
        doReturn(reservationList(4, 7)).when(reservationRepository).findUserLevelData(user);
        doNothing().when(userService).updateProfileIfNeeded(user, "https://s3-village-image.s3.ap-northeast-2.amazonaws.com/profile4.png");

        //when
        ResponseEntity<ResponseMessage> response = reservationService.changeStatus(reservationId, statusRequestDto, user);

        //then
        assertEquals(response.getStatusCode(), HttpStatus.OK);
        assertEquals(Objects.requireNonNull(response.getBody()).getMessage(), "상태 변경되었습니다.");

        // verify
        verify(reservationRepository, times(1)).findById(reservationId);
        verify(reservationRepository, times(1)).checkProductOwner(reservationId, user);
        verify(reservationRepository, times(1)).updateStatus(reservationId, statusRequestDto.getStatus());
        verify(reservationRepository, times(1)).findUserLevelData(user);
        verify(userService, times(1)).updateProfileIfNeeded(user, "https://s3-village-image.s3.ap-northeast-2.amazonaws.com/profile4.png");

    }

    @Test
    @DisplayName("예약 상태 변경 - 정상케이스(유저 등급3)")
    public void testChangeStatusLevel3() {
        //given
        Long reservationId = 1L;
        User user = new User(1L,1L, "nickname", "profile1", UserRoleEnum.USER);
        Reservation reservation = new Reservation(reservationId, "waiting", LocalDate.of(2023,5,10), LocalDate.of(2023,5,13), user, product);
        StatusRequestDto statusRequestDto = new StatusRequestDto("accepted");

        doReturn(Optional.of(reservation)).when(reservationRepository).findById(reservationId);
        doReturn(true).when(reservationRepository).checkProductOwner(reservationId, user);
        doNothing().when(reservationRepository).updateStatus(reservationId, statusRequestDto.getStatus());
        doReturn(reservationList(2, 5)).when(reservationRepository).findUserLevelData(user);
        doNothing().when(userService).updateProfileIfNeeded(user, "https://s3-village-image.s3.ap-northeast-2.amazonaws.com/profile3.png");

        //when
        ResponseEntity<ResponseMessage> response = reservationService.changeStatus(reservationId, statusRequestDto, user);

        //then
        assertEquals(response.getStatusCode(), HttpStatus.OK);
        assertEquals(Objects.requireNonNull(response.getBody()).getMessage(), "상태 변경되었습니다.");

        // verify
        verify(reservationRepository, times(1)).findById(reservationId);
        verify(reservationRepository, times(1)).checkProductOwner(reservationId, user);
        verify(reservationRepository, times(1)).updateStatus(reservationId, statusRequestDto.getStatus());
        verify(reservationRepository, times(1)).findUserLevelData(user);
        verify(userService, times(1)).updateProfileIfNeeded(user, "https://s3-village-image.s3.ap-northeast-2.amazonaws.com/profile3.png");

    }

    @Test
    @DisplayName("예약 상태 변경 - 정상케이스(유저 등급2)")
    public void testChangeStatusLevel2() {
        //given
        Long reservationId = 1L;
        User user = new User(1L,1L, "nickname", "profile1", UserRoleEnum.USER);
        Reservation reservation = new Reservation(reservationId, "waiting", LocalDate.of(2023,5,10), LocalDate.of(2023,5,13), user, product);
        StatusRequestDto statusRequestDto = new StatusRequestDto("accepted");

        doReturn(Optional.of(reservation)).when(reservationRepository).findById(reservationId);
        doReturn(true).when(reservationRepository).checkProductOwner(reservationId, user);
        doNothing().when(reservationRepository).updateStatus(reservationId, statusRequestDto.getStatus());
        doReturn(reservationList(2, 3)).when(reservationRepository).findUserLevelData(user);
        doNothing().when(userService).updateProfileIfNeeded(user, "https://s3-village-image.s3.ap-northeast-2.amazonaws.com/profile2.png");

        //when
        ResponseEntity<ResponseMessage> response = reservationService.changeStatus(reservationId, statusRequestDto, user);

        //then
        assertEquals(response.getStatusCode(), HttpStatus.OK);
        assertEquals(Objects.requireNonNull(response.getBody()).getMessage(), "상태 변경되었습니다.");

        // verify
        verify(reservationRepository, times(1)).findById(reservationId);
        verify(reservationRepository, times(1)).checkProductOwner(reservationId, user);
        verify(reservationRepository, times(1)).updateStatus(reservationId, statusRequestDto.getStatus());
        verify(reservationRepository, times(1)).findUserLevelData(user);
        verify(userService, times(1)).updateProfileIfNeeded(user, "https://s3-village-image.s3.ap-northeast-2.amazonaws.com/profile2.png");

    }

    @Test
    @DisplayName("예약 상태 변경 - 정상케이스(유저 등급1)")
    public void testChangeStatusLevel1() {
        //given
        Long reservationId = 1L;
        User user = new User(1L,1L, "nickname", "profile1", UserRoleEnum.USER);
        Reservation reservation = new Reservation(reservationId, "waiting", LocalDate.of(2023,5,10), LocalDate.of(2023,5,13), user, product);
        StatusRequestDto statusRequestDto = new StatusRequestDto("accepted");

        doReturn(Optional.of(reservation)).when(reservationRepository).findById(reservationId);
        doReturn(true).when(reservationRepository).checkProductOwner(reservationId, user);
        doNothing().when(reservationRepository).updateStatus(reservationId, statusRequestDto.getStatus());
        doReturn(reservationList(1, 2)).when(reservationRepository).findUserLevelData(user);
        doNothing().when(userService).updateProfileIfNeeded(user, "https://s3-village-image.s3.ap-northeast-2.amazonaws.com/profile1.png");

        //when
        ResponseEntity<ResponseMessage> response = reservationService.changeStatus(reservationId, statusRequestDto, user);

        //then
        assertEquals(response.getStatusCode(), HttpStatus.OK);
        assertEquals(Objects.requireNonNull(response.getBody()).getMessage(), "상태 변경되었습니다.");

        // verify
        verify(reservationRepository, times(1)).findById(reservationId);
        verify(reservationRepository, times(1)).checkProductOwner(reservationId, user);
        verify(reservationRepository, times(1)).updateStatus(reservationId, statusRequestDto.getStatus());
        verify(reservationRepository, times(1)).findUserLevelData(user);
        verify(userService, times(1)).updateProfileIfNeeded(user, "https://s3-village-image.s3.ap-northeast-2.amazonaws.com/profile1.png");

    }

    private List<UserLevelDto> reservationList(int date, int count) {
        List<UserLevelDto> result = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            result.add(new UserLevelDto(LocalDate.of(2023, i+1, 10), LocalDate.of(2023, i+1, 10+date)));
        }
        return result;
    }

    @Test
    @DisplayName("예약 상태 변경 - 판매자가 아닌 경우")
    public void testChangeStatusWithNotProductOwner() {
        //given
        Long reservationId = 1L;
        User user = new User(1L,1L, "nickname", "profile1", UserRoleEnum.USER);
        User other = new User(2L,2L, "nickname1", "profile1", UserRoleEnum.USER);
        Reservation reservation = new Reservation(reservationId, "waiting", LocalDate.of(2023,5,10), LocalDate.of(2023,5,13), user, product);
        StatusRequestDto statusRequestDto = new StatusRequestDto("accepted");

        doReturn(Optional.of(reservation)).when(reservationRepository).findById(reservationId);
        doReturn(false).when(reservationRepository).checkProductOwner(reservationId, other);

        //when
//        ResponseEntity<ResponseMessage> response = reservationService.changeStatus(reservationId, statusRequestDto, user);

        //then
        assertThrows(CustomException.class, () -> reservationService.changeStatus(reservationId, statusRequestDto, other));

        // verify
        verify(reservationRepository, times(1)).findById(reservationId);
        verify(reservationRepository, times(1)).checkProductOwner(reservationId, other);
        verify(reservationRepository, times(0)).updateStatus(reservationId, statusRequestDto.getStatus());
        verify(reservationRepository, times(0)).findUserLevelData(user);
        verify(userService, times(0)).updateProfileIfNeeded(user, "https://s3-village-image.s3.ap-northeast-2.amazonaws.com/profile3.png");

    }
}