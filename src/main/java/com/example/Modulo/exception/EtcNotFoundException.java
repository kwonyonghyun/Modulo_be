package com.example.Modulo.exception;

import com.example.Modulo.global.exception.BusinessException;
import com.example.Modulo.global.exception.ErrorCode;

public class EtcNotFoundException extends BusinessException {
    public EtcNotFoundException() {
        super(ErrorCode.ETC_NOT_FOUND);
    }
} 