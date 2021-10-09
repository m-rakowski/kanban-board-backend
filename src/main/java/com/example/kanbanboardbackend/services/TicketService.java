package com.example.kanbanboardbackend.services;

import com.example.kanbanboardbackend.error.TicketNotFoundException;
import com.example.kanbanboardbackend.model.FullTicket;
import com.example.kanbanboardbackend.model.MoveRequest;
import com.example.kanbanboardbackend.model.Ticket;
import com.example.kanbanboardbackend.model.TicketStatus;

import java.util.List;
import java.util.Map;

public interface TicketService {
    FullTicket save(Ticket ticket);

    FullTicket findById(String id) throws TicketNotFoundException;

    FullTicket findLast(TicketStatus ticketStatus);

    List<FullTicket> findAllAsList();

    Map<String, FullTicket> findAllAsMap();

    Map<TicketStatus, List<FullTicket>> getAll();

    void deleteById(String id) throws TicketNotFoundException;

    List<FullTicket> findByTitleContaining(String title);

    FullTicket update(String id, Ticket ticket) throws TicketNotFoundException;

    FullTicket findByNextId(String nextId);

    void moveTicket(MoveRequest moveRequest) throws TicketNotFoundException;
}
