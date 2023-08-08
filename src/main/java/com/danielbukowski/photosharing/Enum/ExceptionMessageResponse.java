package com.danielbukowski.photosharing.Enum;

public enum ExceptionMessageResponse {

    ACCOUNT_NOT_FOUND("An account with the provided email does not exist"),
    IMAGE_NOT_FOUND("Could not find an image"),
    ACCOUNT_WITH_ALREADY_EXISTING_EMAIL("An account with this email already exists"),
    PASSWORD_SHOULD_NOT_BE_THE_SAME("The old password should not be the same as the new one")
    ;

    private final String message;

    ExceptionMessageResponse(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

}
