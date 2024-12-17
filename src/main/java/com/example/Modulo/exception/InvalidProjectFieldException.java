package com.example.Modulo.exception;

import com.example.Modulo.global.exception.BusinessException;
import com.example.Modulo.global.exception.ErrorCode;

public class InvalidProjectFieldException extends BusinessException {
    public InvalidProjectFieldException() {
        super(ErrorCode.INVALID_PROJECT_FIELD);
    }
}