package ru.practicum.shareit.user.exception;

public class DublicateEmailException extends RuntimeException {
    public DublicateEmailException(String message) {
        super(message);
    }
}