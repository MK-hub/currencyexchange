package com.example.currencyexchange.errorhandling;

public class AccountAlreadyExistsException extends RuntimeException {

    public AccountAlreadyExistsException(String message) {
        super(message);
    }

    public AccountAlreadyExistsException(Throwable throwable) {
        super(throwable);
    }

    public AccountAlreadyExistsException(String messsage, Throwable cause) {
        super(messsage, cause);
    }

}
