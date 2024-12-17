package com.example.Modulo.exception;

import com.example.Modulo.global.exception.BusinessException;
import com.example.Modulo.global.exception.ErrorCode;

public class InvalidResumeSectionException extends BusinessException {
    public InvalidResumeSectionException() {
        super(ErrorCode.INVALID_RESUME_SECTION);
    }
}