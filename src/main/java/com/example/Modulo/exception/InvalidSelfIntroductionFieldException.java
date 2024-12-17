package com.example.Modulo.exception;

import com.example.Modulo.global.exception.BusinessException;
import com.example.Modulo.global.exception.ErrorCode;

public class InvalidSelfIntroductionFieldException extends BusinessException {
    public InvalidSelfIntroductionFieldException() {
        super(ErrorCode.INVALID_SELF_INTRODUCTION_FIELD);
    }
}