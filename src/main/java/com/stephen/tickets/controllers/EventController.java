package com.stephen.tickets.controllers;


import com.stephen.tickets.domain.CreateEventRequest;
import com.stephen.tickets.domain.dtos.CreateEventRequestDto;
import com.stephen.tickets.domain.dtos.CreateEventResponseDto;
import com.stephen.tickets.domain.entities.Event;
import com.stephen.tickets.mappers.EventMappers;
import com.stephen.tickets.services.EventService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping(params = "api/v1/events")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;
    private final EventMappers eventMappers;

    @PostMapping( )
    public ResponseEntity<CreateEventResponseDto> createEvent(
             @AuthenticationPrincipal Jwt jwt,
             @Valid @RequestBody CreateEventRequestDto createEventRequestDto) {
        CreateEventRequest createEventRequest = eventMappers.fromDto(createEventRequestDto);
        UUID userId = UUID.fromString(jwt.getSubject());
        Event createdEvent = eventService.createEvent(userId, createEventRequest);
        CreateEventResponseDto createEventResponseDto = eventMappers.toDto(createdEvent);
        return new  ResponseEntity<>(createEventResponseDto,HttpStatus.CREATED);
    }
}
