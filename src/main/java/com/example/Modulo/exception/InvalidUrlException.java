package com.example.Modulo.exception;

import com.example.Modulo.global.exception.BusinessException;
import com.example.Modulo.global.exception.ErrorCode;

public class InvalidUrlException extends BusinessException {
    public InvalidUrlException() {
        super(ErrorCode.INVALID_URL);
    }
}