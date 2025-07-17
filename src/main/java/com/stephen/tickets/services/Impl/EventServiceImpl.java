package com.stephen.tickets.services.Impl;

import com.stephen.tickets.domain.CreateEventRequest;
import com.stephen.tickets.domain.UpdateEventRequest;
import com.stephen.tickets.domain.UpdateTicketTypeRequest;
import com.stephen.tickets.domain.entities.Event;
import com.stephen.tickets.domain.entities.TicketType;
import com.stephen.tickets.exceptions.EventNotFoundException;
import com.stephen.tickets.exceptions.EventUpdateException;
import com.stephen.tickets.exceptions.TicketTypeNotFoundException;
import com.stephen.tickets.exceptions.UserNotFoundException;
import com.stephen.tickets.repositories.EventRepository;
import com.stephen.tickets.repositories.UserRepository;
import com.stephen.tickets.services.EventService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import com.stephen.tickets.domain.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    @Override
    @Transactional
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

    @Override
    public Optional<Event> getEventForOrganizer(UUID organizerId, UUID id) {
        return eventRepository.findByIdAndOrganizerId(id,organizerId);
    }

    @Override
    @Transactional
    public Event updateEventForOrganizer(UUID organizerId, UUID id, UpdateEventRequest event) {
        if(null == event.getId()){
            throw new  EventUpdateException("Event ID cannot be null");
        }
        if(!(id == event.getId())){
            throw new EventUpdateException("Provided ID does not match");
        }
        Event existingEvent = eventRepository
                .findByIdAndOrganizerId(id,organizerId)
                .orElseThrow(() -> new EventNotFoundException(
                        String.format("Event with ID '%s' does not not exist",id))
                );

        existingEvent.setName(event.getName());
        existingEvent.setStart(event.getStart());
        existingEvent.setEnd(event.getEnd());
        existingEvent.setVenue(event.getVenue());
        existingEvent.setSalesStart(event.getSalesStart());
        existingEvent.setSalesEnd(event.getSalesEnd());
        existingEvent.setStatus(event.getStatus());


        Set<UUID> requestTicketTypeIds = event.getTicketTypes()
                .stream()
                .map(UpdateTicketTypeRequest::getId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        existingEvent.getTicketTypes().removeIf(existingTicketType ->
                !requestTicketTypeIds.contains(existingTicketType.getId())
        );

        Map<UUID,TicketType> existingTicketTypeIndex = existingEvent.getTicketTypes().stream()
                .collect(Collectors.toMap(TicketType::getId, Function.identity()));

        for(UpdateTicketTypeRequest ticketType : event.getTicketTypes()){
            if(null == ticketType.getId()){
                // create
                TicketType ticketTypeToCreate = new TicketType();
                ticketTypeToCreate.setName(ticketType.getName());
                ticketTypeToCreate.setPrice(ticketType.getPrice());
                ticketTypeToCreate.setDescription(ticketType.getDescription());
                ticketTypeToCreate.setTotalAvailable(ticketType.getTotalAvailable());
                ticketTypeToCreate.setEvent(existingEvent);
                existingEvent.getTicketTypes().add(ticketTypeToCreate);
            } else if(existingTicketTypeIndex.containsKey(ticketType.getId())){
                // update
                TicketType existingTicketType = existingTicketTypeIndex.get(ticketType.getId());
                existingTicketType.setName(ticketType.getName());
                existingTicketType.setPrice(ticketType.getPrice());
                existingTicketType.setDescription(ticketType.getDescription());
                existingTicketType.setTotalAvailable(ticketType.getTotalAvailable());
            } else {
                throw new TicketTypeNotFoundException(String.format(
                        "Ticket type with ID '%s' does not exist", ticketType.getId()
                ));
            }
        }
        return eventRepository.save(existingEvent);
    }
}
