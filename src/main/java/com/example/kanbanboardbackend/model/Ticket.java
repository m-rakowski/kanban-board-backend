package com.example.kanbanboardbackend.model;

import lombok.*;
import org.hibernate.validator.constraints.Length;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.NotBlank;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Ticket {

    @NotBlank(message = "title cannot be blank")
    @Length(min = 3, max = 15, message = "title length has to be between 3 and 15 characters")
    private String title;

    @NotBlank(message = "content cannot be blank")
    private String content;

    @Enumerated(EnumType.STRING)
    private TicketStatus status;

}