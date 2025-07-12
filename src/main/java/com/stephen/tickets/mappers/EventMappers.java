package com.stephen.tickets.mappers;


import com.stephen.tickets.domain.CreateEventRequest;
import com.stephen.tickets.domain.CreateTicketTypeRequest;
import com.stephen.tickets.domain.dtos.CreateEventRequestDto;
import com.stephen.tickets.domain.dtos.CreateEventResponseDto;
import com.stephen.tickets.domain.dtos.CreateTicketTypeRequestDto;
import com.stephen.tickets.domain.entities.Event;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring",unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface EventMappers {

    CreateTicketTypeRequest fromDto(CreateTicketTypeRequestDto dto);

    CreateEventRequest fromDto(CreateEventRequestDto dto);

    CreateEventResponseDto toDto(Event event);
}
