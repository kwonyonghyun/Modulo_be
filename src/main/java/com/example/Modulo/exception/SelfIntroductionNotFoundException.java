package com.example.Modulo.exception;

import com.example.Modulo.global.exception.BusinessException;
import com.example.Modulo.global.exception.ErrorCode;

public class SelfIntroductionNotFoundException extends BusinessException {

    public SelfIntroductionNotFoundException() {
        super(ErrorCode.SELF_INTRODUCTION_NOT_FOUND);
    }
}
