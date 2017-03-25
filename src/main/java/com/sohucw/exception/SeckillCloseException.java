package com.sohucw.exception;

/**
 * Created by baidu on 17/3/25.
 */
public class SeckillCloseException extends SeckillException{

    public SeckillCloseException(String message) {
        super(message);
    }

    public SeckillCloseException(String message, Throwable cause) {
        super(message, cause);
    }
}
