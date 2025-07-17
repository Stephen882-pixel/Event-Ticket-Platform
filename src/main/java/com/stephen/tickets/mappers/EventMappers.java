package com.stephen.tickets.mappers;


import com.stephen.tickets.domain.CreateEventRequest;
import com.stephen.tickets.domain.CreateTicketTypeRequest;
import com.stephen.tickets.domain.dtos.*;
import com.stephen.tickets.domain.entities.Event;
import com.stephen.tickets.domain.entities.TicketType;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface EventMappers {

    CreateTicketTypeRequest fromDto(CreateTicketTypeRequestDto dto);

    CreateEventRequest fromDto(CreateEventRequestDto dto);

    CreateEventResponseDto toDto(Event event);

    @Mapping(source = "totalAvailable",target = "totalAvailable")
    CreateTypeResponseDto toTicketTypeDto(TicketType ticketType);

    ListEventTicketTypeResponseDto toDto(TicketType ticketType);

    ListEventsResponseDto  toListEventResponseDto(Event event);
}
