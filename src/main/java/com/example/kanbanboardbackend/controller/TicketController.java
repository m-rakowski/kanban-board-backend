package com.example.kanbanboardbackend.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.example.kanbanboardbackend.model.FullTicket;
import com.example.kanbanboardbackend.model.Ticket;
import com.example.kanbanboardbackend.repository.TicketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = "http://localhost:8081")
@RestController
@RequestMapping("/api")
public class TicketController {

    @Autowired
    TicketRepository ticketRepository;

    @GetMapping("/tickets")
    public ResponseEntity<List<FullTicket>> getAllTickets(@RequestParam(required = false) String title) {
        try {
            List<FullTicket> fullTickets = new ArrayList<FullTicket>();

            if (title == null)
                ticketRepository.findAll().forEach(fullTickets::add);
            else
                ticketRepository.findByTitleContaining(title).forEach(fullTickets::add);

            if (fullTickets.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }

            return new ResponseEntity<>(fullTickets, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/tickets/{id}")
    public ResponseEntity<FullTicket> getTicketById(@PathVariable("id") String id) {
        Optional<FullTicket> ticketData = ticketRepository.findById(id);

        if (ticketData.isPresent()) {
            return new ResponseEntity<>(ticketData.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/tickets")
    public ResponseEntity<FullTicket> createTicket(@RequestBody Ticket ticket) {
        try {

            List<FullTicket> lastFullTicket = ticketRepository.findLast();

            if (lastFullTicket.size() > 1) {
                throw new Error("There should only be one LAST element");
            }

            FullTicket toBeSaved = new FullTicket(
                    ticket.getTitle(), ticket.getContent(), ticket.getStatus()
            );
            toBeSaved.setNextId("LAST");

            if (lastFullTicket.size() > 0) {
                toBeSaved.setPreviousId(lastFullTicket.get(0).getId());
            } else {
                toBeSaved.setPreviousId("FIRST");
            }

            FullTicket _fullTicket = ticketRepository.save(toBeSaved);

            if (lastFullTicket.size() > 0) {
                // update the ticket that used to be last
                lastFullTicket.get(0).setNextId(_fullTicket.getId());
                ticketRepository.save(lastFullTicket.get(0));
            }
            return new ResponseEntity<>(_fullTicket, HttpStatus.CREATED);

        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/tickets/{id}")
    public ResponseEntity<FullTicket> updateTicket(@PathVariable("id") String id, @RequestBody FullTicket fullTicket) {
        Optional<FullTicket> ticketData = ticketRepository.findById(id);

        if (ticketData.isPresent()) {
            FullTicket _fullTicket = ticketData.get();
            _fullTicket.setTitle(fullTicket.getTitle());
            _fullTicket.setContent(fullTicket.getContent());
            return new ResponseEntity<>(ticketRepository.save(_fullTicket), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/tickets/{id}")
    public ResponseEntity<HttpStatus> deleteTicket(@PathVariable("id") String id) {
        try {
            FullTicket found = ticketRepository.getById(id);
            ticketRepository.deleteById(id);

//            if (!"LAST".equals(found.getNextId())) {
//                FullTicket next = ticketRepository.getById(found.getNextId());
//
//                next.setPreviousId(found.getPreviousId());
//                ticketRepository.save(next);
//
//            }
//            if (!"FIRST".equals(found.getPreviousId())) {
//                ticketRepository.deleteById(found.getPreviousId());
//
//                FullTicket previous = ticketRepository.getById(found.getPreviousId());
//                previous.setNextId(found.getNextId());
//                ticketRepository.save(next);
//            }



            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/tickets")
    public ResponseEntity<HttpStatus> deleteAllTickets() {
        try {
            ticketRepository.deleteAll();
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }


}