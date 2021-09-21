package com.example.kanbanboardbackend.services;

import com.example.kanbanboardbackend.model.FullTicket;
import com.example.kanbanboardbackend.model.Ticket;
import com.example.kanbanboardbackend.repository.TicketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class TicketService {

    @Autowired
    private TicketRepository ticketRepository;

    @Transactional
    public FullTicket save(Ticket ticket) {
        FullTicket lastTicket = this.findLast();
        FullTicket newTicket = new FullTicket(ticket.getTitle(), ticket.getContent(), ticket.getStatus());

        if (lastTicket != null) {
            newTicket.setPreviousId(lastTicket.getId());
            FullTicket save = this.ticketRepository.save(newTicket);
            lastTicket.setNextId(save.getId());
            this.ticketRepository.save(lastTicket);
            return save;
        } else {
            return this.ticketRepository.save(newTicket);
        }
    }

    public Optional<FullTicket> findById(String id) {
        return ticketRepository.findById(id);
    }

    public FullTicket findLast() {

        List<FullTicket> last = this.ticketRepository.findLast();

        if (last.size() == 0) {
            return null;
        } else {
            return last.get(0);
        }
    }

    public FullTicket findFirst() {

        List<FullTicket> first = this.ticketRepository.findFirst();

        if (first.size() == 0) {
            return null;
        } else {
            return first.get(0);
        }
    }

    public List<FullTicket> findAll() {
        return this.ticketRepository.findAll();
    }
}
