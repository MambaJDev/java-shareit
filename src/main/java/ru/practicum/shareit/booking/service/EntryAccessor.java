package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.model.State;


public abstract class EntryAccessor {
    private EntryAccessor next;

    protected void checkNext(Long bookerId, State state) {
        if (next != null) {
            next.check(bookerId, state);
        }
    }

    protected void bind(EntryAccessor next) {
        this.next = next;
    }

    protected abstract void check(Long bookerId, State state);
}