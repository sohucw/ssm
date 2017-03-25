package com.sohucw.exception;

/**
 * Created by baidu on 17/3/25.
 */
public class SeckillException extends RuntimeException {


    public SeckillException(String message) {
        super(message);
    }

    public SeckillException (String message, Throwable cause) {

        super(message, cause);
    }


}
