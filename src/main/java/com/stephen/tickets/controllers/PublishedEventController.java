package com.stephen.tickets.controllers;


import com.stephen.tickets.domain.dtos.response.ApiResponseDto;
import com.stephen.tickets.domain.dtos.response.ListPublishedEventResponseDto;
import com.stephen.tickets.mappers.EventMappers;
import com.stephen.tickets.services.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/published-events")
@RequiredArgsConstructor
public class PublishedEventController {

    private final EventService eventService;
    private final EventMappers eventMappers;

    @GetMapping
    public ResponseEntity<ApiResponseDto<Page<ListPublishedEventResponseDto>>> listPublishedEvents(Pageable pageable){
        Page<ListPublishedEventResponseDto> eventsPage = eventService.listPublishedEvents(pageable)
                .map(eventMappers::toListPublishedEventResponseDto);

        return ResponseEntity.ok(ApiResponseDto.success("published events fetched successfully", eventsPage));

    }
}
