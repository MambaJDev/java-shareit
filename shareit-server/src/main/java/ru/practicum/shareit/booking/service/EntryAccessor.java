package ru.practicum.shareit.booking.service;

public abstract class EntryAccessor {
    private EntryAccessor next;

    protected void checkNext(InputRequest inputRequest) {
        if (next != null) {
            next.check(inputRequest);
        }
    }

    protected void bind(EntryAccessor next) {
        this.next = next;
    }

    protected abstract void check(InputRequest inputRequest);
}