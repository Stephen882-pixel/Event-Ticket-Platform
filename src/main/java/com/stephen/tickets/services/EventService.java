package com.stephen.tickets.services;

import com.stephen.tickets.domain.CreateEventRequest;
import com.stephen.tickets.domain.entities.Event;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


import java.util.List;
import java.util.UUID;

public interface EventService {
    Event createEvent(UUID organizer, CreateEventRequest event);

    Page<Event> listEventsForOrganizer(UUID organizerId, Pageable pageable);
}
