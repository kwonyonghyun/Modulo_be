package com.example.Modulo.exception;

import com.example.Modulo.global.exception.BusinessException;
import com.example.Modulo.global.exception.ErrorCode;

public class InvalidTokenException extends BusinessException {

    public InvalidTokenException() {
        super(ErrorCode.INVALID_TOKEN);
    }
}