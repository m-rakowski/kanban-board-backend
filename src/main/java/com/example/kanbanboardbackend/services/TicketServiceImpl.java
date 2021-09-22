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
            newTicket.setPreviousId(lastTicket.getId());
            FullTicket save = this.ticketRepository.save(newTicket);
            lastTicket.setNextId(save.getId());
            this.ticketRepository.save(lastTicket);
            return save;
        } else {
            return this.ticketRepository.save(newTicket);
        }
    }

    @Override
    public Optional<FullTicket> findById(String id) {
        return ticketRepository.findById(id);
    }

    @Override
    public FullTicket findLast() {

        return this.ticketRepository.findLast();
//        List<FullTicket> last = this.ticketRepository.findLast();
//
//        if (last.size() == 0) {
//            return null;
//        } else {
//            return last.get(0);
//        }
    }

    @Override
    public FullTicket findFirst() {
        return this.ticketRepository.findFirst();

//        List<FullTicket> first = this.ticketRepository.findFirst();
//
//        if (first.size() == 0) {
//            return null;
//        } else {
//            return first.get(0);
//        }
    }

    @Override
    public List<FullTicket> findAll() {
        return this.ticketRepository.findAll();
    }

    @Override
    public void deleteById(String id) {

        Optional<FullTicket> byId = this.findById(id);

        if (byId.isPresent()) {

            if (byId.get().getPreviousId() != null) {
                Optional<FullTicket> previous = this.findById(byId.get().getPreviousId());
                Optional<FullTicket> next = this.findById(byId.get().getNextId());
//                previous.get().setNextId();
            }
        }
        this.ticketRepository.deleteById(id);
    }
}
