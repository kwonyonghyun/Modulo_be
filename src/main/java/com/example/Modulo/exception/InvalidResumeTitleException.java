package com.example.Modulo.exception;

import com.example.Modulo.global.exception.BusinessException;
import com.example.Modulo.global.exception.ErrorCode;

public class InvalidResumeTitleException extends BusinessException {
    public InvalidResumeTitleException() {
        super(ErrorCode.INVALID_RESUME_TITLE);
    }
}
