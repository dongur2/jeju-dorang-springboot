package com.donguri.jejudorang.global.error;

import lombok.Getter;

@Getter
public class CustomException extends RuntimeException {
    private CustomErrorCode customErrorCode;

    public CustomException(CustomErrorCode customErrorCode) {
        this.customErrorCode = customErrorCode;
    }
}
