package com.stephen.tickets.controllers;


import com.stephen.tickets.domain.CreateEventRequest;
import com.stephen.tickets.domain.UpdateEventRequest;
import com.stephen.tickets.domain.dtos.*;
import com.stephen.tickets.domain.entities.Event;
import com.stephen.tickets.mappers.EventMappers;
import com.stephen.tickets.services.EventService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping(path = "api/v1/events")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;
    private final EventMappers eventMappers;

    @PostMapping
    public ResponseEntity<ApiResponseDto<CreateEventResponseDto>> createEvent(
             @AuthenticationPrincipal Jwt jwt,
             @Valid @RequestBody CreateEventRequestDto createEventRequestDto) {
        CreateEventRequest createEventRequest = eventMappers.fromDto(createEventRequestDto);
        UUID userId = parseUserId(jwt);
        Event createdEvent = eventService.createEvent(userId, createEventRequest);
        CreateEventResponseDto createEventResponseDto = eventMappers.toDto(createdEvent);

        ApiResponseDto<CreateEventResponseDto> response = ApiResponseDto.success(
                "Event created successfully",
                createEventResponseDto
        );

        return new ResponseEntity<>(response,HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<ApiResponseDto<Page<ListEventsResponseDto>>>listEvents(
            @AuthenticationPrincipal Jwt jwt,Pageable pageable
    ){
        UUID UserId = parseUserId(jwt);
        Page<Event> events = eventService.listEventsForOrganizer(UserId,pageable);
        Page<ListEventsResponseDto> dtoPage = events.map(eventMappers::toListEventResponseDto);

        ApiResponseDto<Page<ListEventsResponseDto>> response = ApiResponseDto.success(
                "Events fetched successfully", dtoPage
        );

        return ResponseEntity.ok(response);
    }

    @GetMapping(path = "/{eventId}")
    public ResponseEntity<ApiResponseDto<GetEventDetailsResponseDto>> getEvent(
            @AuthenticationPrincipal Jwt jwt, @PathVariable UUID eventId
    ) {
        UUID userId = parseUserId(jwt);
        return eventService.getEventForOrganizer(userId, eventId)
                .map(event -> {
                    GetEventDetailsResponseDto dto = eventMappers.toGetEventDetailsResponseDto(event);
                    ApiResponseDto<GetEventDetailsResponseDto> response = ApiResponseDto.success("Event fetched successfully", dto);
                    return ResponseEntity.ok(response);
                })
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponseDto.error("Event not found")));
    }

    @PutMapping(path = "/{eventId}")
    public ResponseEntity<ApiResponseDto<UpdateEventResponseDto>> updateEvent(
            @AuthenticationPrincipal Jwt jwt,
            @PathVariable UUID eventId,
            @Valid @RequestBody UpdateEventRequestDto updateEventRequestDto) {
        UpdateEventRequest updateEventRequest = eventMappers.fromDto(updateEventRequestDto);
        UUID userId = parseUserId(jwt);

        Event updatedEvent = eventService.updateEventForOrganizer(userId, eventId, updateEventRequest);
        UpdateEventResponseDto updateEventResponseDto = eventMappers.toUpdateEventResponseDto(updatedEvent);

        ApiResponseDto<UpdateEventResponseDto> response = ApiResponseDto.success(
                "Event updated successfully",
                updateEventResponseDto
        );

        return new ResponseEntity<>(response,HttpStatus.OK);
    }

    private UUID parseUserId(Jwt jwt) {
        return UUID.fromString(jwt.getSubject());
    }

}


