package com.example.Modulo.exception;

import com.example.Modulo.global.exception.BusinessException;
import com.example.Modulo.global.exception.ErrorCode;

public class InvalidProjectDateException extends BusinessException {
    public InvalidProjectDateException() {
        super(ErrorCode.INVALID_PROJECT_DATE);
    }
}