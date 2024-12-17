package com.example.Modulo.exception;

import com.example.Modulo.global.exception.BusinessException;
import com.example.Modulo.global.exception.ErrorCode;

public class InvalidEtcFieldException extends BusinessException {
    public InvalidEtcFieldException() {
        super(ErrorCode.INVALID_ETC_FIELD);
    }
}