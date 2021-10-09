package com.example.kanbanboardbackend.model;

import lombok.AllArgsConstructor;
import lombok.Value;

import javax.validation.constraints.NotNull;

@Value
@AllArgsConstructor
public class MoveRequest {

    @NotNull
    String movedTicketId;

    @NotNull
    String afterThisOneId;
}
