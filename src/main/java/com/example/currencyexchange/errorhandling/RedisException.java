package com.example.currencyexchange.errorhandling;

public class RedisException extends RuntimeException {

    public RedisException(String message) {
        super(message);
    }

    public RedisException(Throwable throwable) {
        super(throwable);
    }

    public RedisException(String messsage, Throwable cause) {
        super(messsage, cause);
    }

}
