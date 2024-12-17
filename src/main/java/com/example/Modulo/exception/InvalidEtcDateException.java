package com.example.Modulo.exception;

import com.example.Modulo.global.exception.BusinessException;
import com.example.Modulo.global.exception.ErrorCode;

public class InvalidEtcDateException extends BusinessException {
    public InvalidEtcDateException() {
        super(ErrorCode.INVALID_ETC_DATE);
    }
}