package com.example.kanbanboardbackend.model;

import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import javax.persistence.*;

@Entity
@Data
@Builder
@Table(name = "tickets")
public class FullTicket {


    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    @ColumnDefault("random_uuid()")
    private String id;

    @NonNull
    @Column(name = "title")
    private String title;

    @NonNull
    @Column(name = "content")
    private String content;

    @NonNull
    @Column(name = "status")
    private TicketStatus status;

    @Column(name = "nextId")
    private String nextId;

    @Column(name = "previousId")
    private String previousId;
}