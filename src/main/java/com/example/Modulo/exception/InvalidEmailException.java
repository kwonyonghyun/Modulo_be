package com.example.Modulo.exception;

import com.example.Modulo.global.exception.BusinessException;
import com.example.Modulo.global.exception.ErrorCode;

public class InvalidEmailException extends BusinessException {
    public InvalidEmailException(String email) {
        super(ErrorCode.INVALID_EMAIL);
    }
}