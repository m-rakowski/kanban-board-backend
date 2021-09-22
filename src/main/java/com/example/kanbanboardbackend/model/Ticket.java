package com.example.kanbanboardbackend.model;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import org.hibernate.validator.constraints.Length;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotBlank;

@Data
@Builder
public class Ticket {

    @NonNull
    @NotBlank(message = "title cannot be blank")
    @Length(min = 3, max = 15, message = "title length has to be between 3 and 15 characters")
    private String title;

    @NonNull
    @NotBlank(message = "content cannot be blank")
    private String content;

    @NonNull
    @Enumerated(EnumType.STRING)
    private TicketStatus status;

}