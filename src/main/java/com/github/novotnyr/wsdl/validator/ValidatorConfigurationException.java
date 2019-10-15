package com.github.novotnyr.wsdl.validator;

public class ValidatorConfigurationException extends RuntimeException {

    public ValidatorConfigurationException() {
        super();
    }

    public ValidatorConfigurationException(String msg) {
        super(msg);
    }

    public ValidatorConfigurationException(String message, Throwable cause) {
        super(message, cause);
    }

    public ValidatorConfigurationException(Throwable cause) {
        super(cause);
    }
}