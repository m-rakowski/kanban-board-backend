package com.example.kanbanboardbackend.controller;

import com.example.kanbanboardbackend.error.TicketNotFoundException;
import com.example.kanbanboardbackend.model.FullTicket;
import com.example.kanbanboardbackend.model.Ticket;
import com.example.kanbanboardbackend.services.TicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@CrossOrigin(origins = "http://localhost:8081")
@RestController
@Validated
@RequestMapping("/api")
public class TicketController {

    @Autowired
    TicketService ticketService;

    @GetMapping("/tickets")
    public ResponseEntity<List<FullTicket>> getAllTickets(@RequestParam(required = false) String title) {
        try {
            List<FullTicket> fullTickets = new ArrayList<FullTicket>();

            if (title == null)
                ticketService.findAll().forEach(fullTickets::add);
            else
                ticketService.findByTitleContaining(title).forEach(fullTickets::add);

            if (fullTickets.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }

            return new ResponseEntity<>(fullTickets, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
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

//    @PutMapping("/tickets/{id}")
//    public ResponseEntity<FullTicket> updateTicket(@PathVariable("id") String id, @RequestBody FullTicket fullTicket) {
//        Optional<FullTicket> ticketData = ticketService.findById(id);
//
//        if (ticketData.isPresent()) {
//            FullTicket _fullTicket = ticketData.get();
//            _fullTicket.setTitle(fullTicket.getTitle());
//            _fullTicket.setContent(fullTicket.getContent());
//            return new ResponseEntity<>(ticketService.save(_fullTicket), HttpStatus.OK);
//        } else {
//            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
//        }
//    }
//
//    @DeleteMapping("/tickets/{id}")
//    public ResponseEntity<HttpStatus> deleteTicket(@PathVariable("id") String id) {
//        try {
//            ticketService.deleteById(id);
//            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
//        } catch (Exception e) {
//            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//    }
//
//    @DeleteMapping("/tickets")
//    public ResponseEntity<HttpStatus> deleteAllTickets() {
//        try {
//            ticketRepository.deleteAll();
//            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
//        } catch (Exception e) {
//            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
//        }
//
//    }


}