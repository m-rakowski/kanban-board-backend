package com.example.kanbanboardbackend.controller;

import com.example.kanbanboardbackend.error.TicketNotFoundException;
import com.example.kanbanboardbackend.model.FullTicket;
import com.example.kanbanboardbackend.model.MoveRequest;
import com.example.kanbanboardbackend.model.Ticket;
import com.example.kanbanboardbackend.services.TicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@CrossOrigin(origins = "http://localhost:8081")
@RestController
@Validated
@RequestMapping("/api")
public class TicketController {

    @Autowired
    TicketService ticketService;

    @GetMapping("/tickets")
    public ResponseEntity<List<FullTicket>> getAllTickets() {
        return new ResponseEntity<>(ticketService.findAll(), HttpStatus.OK);
    }

    @GetMapping("/tickets/{id}")
    public ResponseEntity<FullTicket> getTicketById(@PathVariable("id") String id) throws TicketNotFoundException {
        return new ResponseEntity<>(ticketService.findById(id), HttpStatus.OK);
    }

    @PostMapping("/tickets")
    public ResponseEntity<FullTicket> createTicket(@Valid @RequestBody Ticket ticket) {
        FullTicket saved = this.ticketService.save(ticket);
        return new ResponseEntity<>(saved, HttpStatus.CREATED);
    }

    @PutMapping("/tickets/{id}")
    public ResponseEntity<FullTicket> updateTicket(@PathVariable("id") String id, @RequestBody Ticket ticket) throws TicketNotFoundException {
        return new ResponseEntity<>(ticketService.update(id, ticket), HttpStatus.OK);
    }

    @DeleteMapping("/tickets/{id}")
    public ResponseEntity<HttpStatus> deleteTicket(@PathVariable("id") String id) throws TicketNotFoundException {
        ticketService.deleteById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping("/tickets/move")
    public ResponseEntity<HttpStatus> moveTicket(@Valid @RequestBody MoveRequest moveRequest) throws TicketNotFoundException {

        this.ticketService.moveTicket(moveRequest);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}