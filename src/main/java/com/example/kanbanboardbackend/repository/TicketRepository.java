package com.example.kanbanboardbackend.repository;


import com.example.kanbanboardbackend.model.FullTicket;
import com.example.kanbanboardbackend.model.TicketStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TicketRepository extends JpaRepository<FullTicket, String> {

    FullTicket findByNextId(String nextId);

    FullTicket findByNextIdAndStatus(String nextId, TicketStatus ticketStatus);

    List<FullTicket> findByTitleContaining(String title);

}