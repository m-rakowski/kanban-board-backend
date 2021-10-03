package com.example.kanbanboardbackend.model;

import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor
public class MoveRequest {
    FullTicket movedTicket;
    FullTicket afterThisOne;
    FullTicket beforeThisOne;
    TicketStatus fromListStatus;
    TicketStatus toListStatus;
}
