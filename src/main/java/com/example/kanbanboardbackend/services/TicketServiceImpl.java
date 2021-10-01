package com.example.kanbanboardbackend.services;

import com.example.kanbanboardbackend.error.TicketNotFoundException;
import com.example.kanbanboardbackend.model.FullTicket;
import com.example.kanbanboardbackend.model.Ticket;
import com.example.kanbanboardbackend.repository.TicketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class TicketServiceImpl implements TicketService {

    @Autowired
    private TicketRepository ticketRepository;

    @Override
    @Transactional
    public FullTicket save(Ticket ticket) {
        FullTicket lastTicket = this.findLast();
        FullTicket newTicket = FullTicket.builder()
                .title(ticket.getTitle())
                .status(ticket.getStatus())
                .content(ticket.getContent()).build();

        if (lastTicket != null) {
            FullTicket savedNewTicket = this.ticketRepository.save(newTicket);
            lastTicket.setNextId(savedNewTicket.getId());
            this.ticketRepository.save(lastTicket);
            return savedNewTicket;
        } else {
            return this.ticketRepository.save(newTicket);
        }
    }

    @Override
    public FullTicket findById(String id) throws TicketNotFoundException {
        Optional<FullTicket> foundById = ticketRepository.findById(id);

        if (!foundById.isPresent()) {
            throw new TicketNotFoundException("Ticket Not Available");
        }

        return foundById.get();

    }

    @Override
    public FullTicket findLast() {
        return this.ticketRepository.findLast();
    }

    @Override
    public List<FullTicket> findAll() {
        return this.ticketRepository.findAll();
    }

    @Override
    @Transactional
    public void deleteById(String id) throws TicketNotFoundException {

        FullTicket foundById = this.findById(id);

        // update left neighbor
        FullTicket leftNeighbor = findByNextId(id);
        leftNeighbor.setNextId(foundById.getNextId());
        this.ticketRepository.save(leftNeighbor);

        this.ticketRepository.deleteById(id);
    }

    @Override
    public List<FullTicket> findByTitleContaining(String title) {
        return ticketRepository.findByTitleContaining(title);
    }

    @Override
    public FullTicket update(String id, Ticket ticket) throws TicketNotFoundException {
        FullTicket byId = this.findById(id);
        byId.setContent(ticket.getContent());
        byId.setStatus(ticket.getStatus());
        byId.setTitle(ticket.getTitle());
        return ticketRepository.save(byId);
    }

    @Override
    public FullTicket findByNextId(String nextId) {
        return this.ticketRepository.findByNextId(nextId);
    }
}
