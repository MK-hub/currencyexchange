package com.example.currencyexchange.errorhandling;

public class AccountNotFoundException extends RuntimeException {

    public AccountNotFoundException(String message) {
        super(message);
    }

    public AccountNotFoundException(Throwable throwable) {
        super(throwable);
    }

    public AccountNotFoundException(String messsage, Throwable cause) {
        super(messsage, cause);
    }

}
