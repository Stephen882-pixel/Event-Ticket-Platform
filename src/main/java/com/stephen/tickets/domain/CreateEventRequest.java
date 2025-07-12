package com.stephen.tickets.domain;


import com.stephen.tickets.domain.entities.EventStatusEnum;
import com.stephen.tickets.domain.entities.Ticket;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.userdetails.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateEventRequest {

    private String name;
    private LocalDateTime start;
    private LocalDateTime end;
    private String venue;
    private LocalDateTime salesStart;
    private LocalDateTime salesEnd;
    private EventStatusEnum status;
    private List<CreateTicketTypeRequest> ticketsTypes = new ArrayList<>();

}
