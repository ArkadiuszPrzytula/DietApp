package com.pl.przytula.dietapp.exception;

import javax.security.sasl.AuthenticationException;

public class OAuth2AuthenticationProcessingException extends AuthenticationException {
    public OAuth2AuthenticationProcessingException(String msg, Throwable t) {
        super(msg,t);
    }
    public OAuth2AuthenticationProcessingException(String msg) {
        super(msg);
    }
}
