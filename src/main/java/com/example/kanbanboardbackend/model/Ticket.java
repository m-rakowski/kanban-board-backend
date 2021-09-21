package com.example.kanbanboardbackend.model;

import lombok.Data;
import lombok.NonNull;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;

@Data
public class Ticket {

    @NonNull
    private String title;

    @NonNull
    private String content;

    @NonNull
    @Enumerated(EnumType.STRING)
    private TicketStatus status;

}