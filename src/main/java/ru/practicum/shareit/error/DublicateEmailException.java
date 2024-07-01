package ru.practicum.shareit.error;

public class DublicateEmailException extends RuntimeException {
    public DublicateEmailException(String message) {
        super(message);
    }
}