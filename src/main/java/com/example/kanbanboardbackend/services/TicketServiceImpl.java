package com.example.kanbanboardbackend.services;

import com.example.kanbanboardbackend.error.TicketNotFoundException;
import com.example.kanbanboardbackend.model.FullTicket;
import com.example.kanbanboardbackend.model.MoveRequest;
import com.example.kanbanboardbackend.model.Ticket;
import com.example.kanbanboardbackend.model.TicketStatus;
import com.example.kanbanboardbackend.repository.TicketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class TicketServiceImpl implements TicketService {

    @Autowired
    private TicketRepository ticketRepository;

    @Override
    @Transactional
    public FullTicket save(Ticket ticket) {
        FullTicket lastTicket = this.findLast(ticket.getStatus());
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
    public FullTicket findLast(TicketStatus ticketStatus) {
        return this.ticketRepository.findByNextIdAndStatus(null, ticketStatus);
    }

    @Override
    public List<FullTicket> findAllAsList() {
        var all = this.ticketRepository.findAll();
        return Stream.of(
                        sorted(TicketStatus.toDo, all),
                        sorted(TicketStatus.toTest, all),
                        sorted(TicketStatus.done, all)
                )
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }

    @Override
    public Map<String, FullTicket> findAllAsMap() {
        return findAllAsList()
                .stream()
                .collect(Collectors.toMap(FullTicket::getId, ticket -> ticket)
                );
    }

    @Override
    @Transactional
    public void deleteById(String id) throws TicketNotFoundException {

        FullTicket foundById = this.findById(id);

        // update left neighbor
        FullTicket leftNeighbor = findByNextId(id);
        if (leftNeighbor != null) {
            leftNeighbor.setNextId(foundById.getNextId());
            this.ticketRepository.save(leftNeighbor);
        }
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

    @Override
    public Map<TicketStatus, List<FullTicket>> getAll() {
        var all = this.ticketRepository.findAll();
        return Map.of(
                TicketStatus.toDo, sorted(TicketStatus.toDo, all),
                TicketStatus.toTest, sorted(TicketStatus.toTest, all),
                TicketStatus.done, sorted(TicketStatus.done, all)
        );
    }

    @Override
    @Transactional
    public void moveTicket(MoveRequest moveRequest) throws TicketNotFoundException {

        FullTicket movedTicket = findById(moveRequest.getMovedTicketId());
        FullTicket afterThisOne = findById(moveRequest.getAfterThisOneId());

        if (movedTicket.getStatus().equals(afterThisOne.getStatus())) {
            this.moveTicketWithinTheSameTicketStatus(movedTicket, afterThisOne);
        } else {
            this.moveTicketToAnotherTicketStatus(movedTicket, afterThisOne);
        }
    }

    private void moveTicketWithinTheSameTicketStatus(FullTicket movedTicket, FullTicket afterThisOne) throws TicketNotFoundException {

        if (movedTicket.getId().equals(afterThisOne.getId())) {
            return;
        }

        FullTicket leftNeighbor = findByNextId(movedTicket.getId());
        if (leftNeighbor != null) {
            leftNeighbor.setNextId(movedTicket.getNextId());
            this.ticketRepository.save(leftNeighbor);
        }
        movedTicket.setNextId(afterThisOne.getNextId());
        this.ticketRepository.save(movedTicket);

        afterThisOne.setNextId(movedTicket.getId());
        this.ticketRepository.save(afterThisOne);
    }

    private void moveTicketToAnotherTicketStatus(FullTicket movedTicket, FullTicket afterThisOne) throws TicketNotFoundException {

        movedTicket.setStatus(afterThisOne.getStatus());

        if (movedTicket.getId().equals(afterThisOne.getId())) {
            return;
        }

        FullTicket leftNeighbor = findByNextId(movedTicket.getId());
        if (leftNeighbor != null) {
            leftNeighbor.setNextId(movedTicket.getNextId());
            this.ticketRepository.save(leftNeighbor);
        }
        movedTicket.setNextId(afterThisOne.getNextId());
        this.ticketRepository.save(movedTicket);

        afterThisOne.setNextId(movedTicket.getId());
        this.ticketRepository.save(afterThisOne);
    }

    private List<FullTicket> sorted(TicketStatus ticketStatus, List<FullTicket> all) {
        Optional<FullTicket> root = all
                .stream()
                .filter(ticket -> (ticket.getIsRoot() && ticket.getStatus().equals(ticketStatus))).findFirst();

        if (root.isEmpty()) {
            throw new IllegalStateException("No root with status" + ticketStatus + " found");
        }

        Map<String, FullTicket> map = all
                .stream()
                .collect(Collectors.toMap(FullTicket::getId, ticket -> ticket)
                );

        var results = new ArrayList<FullTicket>();
        FullTicket current = root.get();
        while (current != null) {
            results.add(current);
            current = map.get(current.getNextId());
        }
        return results;
    }
}
