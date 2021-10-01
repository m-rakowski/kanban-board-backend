package com.example.kanbanboardbackend.repository;


import com.example.kanbanboardbackend.model.FullTicket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TicketRepository extends JpaRepository<FullTicket, String> {

    FullTicket findByNextId(String nextId);

    List<FullTicket> findByTitleContaining(String title);

    @Query(value = "SELECT * FROM TICKETS WHERE NEXT_ID IS NULL", nativeQuery = true)
    FullTicket findLast();

}