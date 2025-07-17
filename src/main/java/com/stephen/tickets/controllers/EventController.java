package com.stephen.tickets.controllers;


import com.stephen.tickets.domain.CreateEventRequest;
import com.stephen.tickets.domain.dtos.ApiResponseDto;
import com.stephen.tickets.domain.dtos.CreateEventRequestDto;
import com.stephen.tickets.domain.dtos.CreateEventResponseDto;
import com.stephen.tickets.domain.dtos.ListEventsResponseDto;
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
    private UUID parseUserId(Jwt jwt) {
        return UUID.fromString(jwt.getSubject());
    }
}


