package com.sparta.village.global.exception;


import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    /* 400 BAD_REQUEST : 잘못된 요청 */
    DUPLICATE_RESERVATION_DATE(BAD_REQUEST, "예약할 수 없는 날짜입니다."),
    DUPLICATE_USER(BAD_REQUEST, "중복된 사용자가 존재합니다"),
    DUPLICATE_NICKNAME(BAD_REQUEST, "중복된 닉네임이 존재합니다"),
    NOT_PROPER_DATE(BAD_REQUEST, "예약 시작일이 반납일보다 뒤에 있으면 안됩니다."),
    NOT_PROPER_INPUTFORM(BAD_REQUEST, "입력한 형식이 맞지 않습니다."),
    NOT_PROPER_URLFORM(BAD_REQUEST, "입력한 URL 형식이 맞지 않습니다."),
    NOT_AUTHOR(BAD_REQUEST, "예약 취소 권한이 없습니다."),
    NOT_SELLER(BAD_REQUEST, "상태 변경 권한이 없습니다."),
    WRONG_ADMIN_TOKEN(BAD_REQUEST, "관리자 암호가 틀려 등록이 불가능합니다."),

    /* 404 NOT_FOUND : Resource 를 찾을 수 없음 */
    USER_NOT_FOUND(NOT_FOUND, "등록된 사용자가 없습니다"),
    RESERVATION_NOT_FOUND(NOT_FOUND, "선택한 댓글에 해당되는 예약이 없습니다."),
    PRODUCT_NOT_FOUND(NOT_FOUND, "선택한 제품을 찾을 수 없습니다.");


    private final HttpStatus httpStatus;
    private final String message;
}
