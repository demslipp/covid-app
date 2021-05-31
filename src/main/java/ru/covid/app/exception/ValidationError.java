package ru.covid.app.exception;

public enum ValidationError {

    SHEET_ID_OR_USER_ID_IS_REQUIRED(400, "User id or sheet id is required to generate qr");

    public final int status;
    public final String description;

    ValidationError(int status, String description) {
        this.status = status;
        this.description = description;
    }

    public ValidationErrorException exception(String body) {
        return new ValidationErrorException(this, body);
    }

    public ValidationErrorException exception() {
        return new ValidationErrorException(this);
    }

    public ValidationErrorException exception(Exception e, String body) {
        return new ValidationErrorException(e, this, body);
    }

    public static class ValidationErrorException extends HttpCodeException {

        public ValidationErrorException(ValidationError error, String args) {
            super(error.status, error.description + ": ".concat(args));
        }

        public ValidationErrorException(ValidationError error) {
            super(error.status, error.description);
        }

        public ValidationErrorException(Exception e, ValidationError error, String args) {
            super(e, error.status, error.description + ": ".concat(args));
        }
    }
}
