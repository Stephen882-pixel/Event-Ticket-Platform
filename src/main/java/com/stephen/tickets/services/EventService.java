package com.stephen.tickets.services;

import com.stephen.tickets.domain.CreateEventRequest;
import com.stephen.tickets.domain.entities.Event;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public interface EventService {
    Event createEvent(UUID organizer, CreateEventRequest event);
}
