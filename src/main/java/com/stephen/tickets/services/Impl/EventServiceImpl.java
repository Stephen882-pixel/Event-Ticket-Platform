package com.stephen.tickets.services.Impl;

import com.stephen.tickets.domain.CreateEventRequest;
import com.stephen.tickets.domain.entities.Event;
import com.stephen.tickets.domain.entities.TicketType;
import com.stephen.tickets.exceptions.UserNotFoundException;
import com.stephen.tickets.repositories.EventRepository;
import com.stephen.tickets.repositories.UserRepository;
import com.stephen.tickets.services.EventService;
import lombok.RequiredArgsConstructor;
import com.stephen.tickets.domain.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    @Override
    public Event createEvent(UUID organizerId, CreateEventRequest event) {
        User organizer =  userRepository.findById(organizerId)
                .orElseThrow(() -> new UserNotFoundException(
                        String.format("User with ID  '%s' not found",organizerId))
                );


        // Create the event first
        Event evenToCreate = new Event();
        evenToCreate.setName(event.getName());
        evenToCreate.setStart(event.getStart());
        evenToCreate.setEnd(event.getEnd());
        evenToCreate.setVenue(event.getVenue());
        evenToCreate.setSalesStart(event.getSalesStart());
        evenToCreate.setSalesEnd(event.getSalesEnd());
        evenToCreate.setStatus(event.getStatus());
        evenToCreate.setStatus(event.getStatus());
        evenToCreate.setOrganizer(organizer);


        // Create ticket types and establish bidirectional relationship
        List<TicketType> ticketTypesToCreate = event.getTicketTypes().stream().map(
                ticketType -> {
                    TicketType ticketTypeToCreate = new TicketType();
                    ticketTypeToCreate.setName(ticketType.getName());
                    ticketTypeToCreate.setPrice(ticketType.getPrice());
                    ticketTypeToCreate.setDescription(ticketType.getDescription());
                    ticketTypeToCreate.setTotalAvailable(ticketType.getTotalAvailable());
                    ticketTypeToCreate.setEvent(evenToCreate);
                    return ticketTypeToCreate;
                }
        ).toList();
        evenToCreate.setTicketTypes(ticketTypesToCreate);
        return eventRepository.save(evenToCreate);
    }

    @Override
    public Page<Event> listEventsForOrganizer(UUID organizerId, Pageable pageable) {
        return eventRepository.findByOrganizerId(organizerId,pageable);
    }
}
