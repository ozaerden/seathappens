package com.seathappens.common.exception;

import lombok.Getter;

@Getter
public class InfrastructureException extends RuntimeException {

    private final ErrorCode errorCode;

    public InfrastructureException(ErrorCode errorCode, Throwable cause) {
        super(errorCode.message(), cause);
        this.errorCode = errorCode;
    }

}
