package com.example.kanbanboardbackend.services;

import com.example.kanbanboardbackend.error.TicketNotFoundException;
import com.example.kanbanboardbackend.model.FullTicket;
import com.example.kanbanboardbackend.model.Ticket;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface TicketService {
    @Transactional
    FullTicket save(Ticket ticket);

    FullTicket findById(String id) throws TicketNotFoundException;

    FullTicket findLast();

    FullTicket findFirst();

    List<FullTicket> findAll();

    void deleteById(String id) throws TicketNotFoundException;

    List<FullTicket> findByTitleContaining(String title);

    FullTicket update(String id, Ticket ticket) throws TicketNotFoundException;
}
