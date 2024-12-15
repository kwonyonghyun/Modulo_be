package com.example.Modulo.exception;

import com.example.Modulo.global.exception.BusinessException;
import com.example.Modulo.global.exception.ErrorCode;

public class ResumeNotFoundException extends BusinessException {

    public ResumeNotFoundException() {
        super(ErrorCode.RESUME_NOT_FOUND);
    }
}