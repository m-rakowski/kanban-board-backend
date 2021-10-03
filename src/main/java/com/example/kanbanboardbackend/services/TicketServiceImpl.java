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

import java.util.List;
import java.util.Optional;

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
    public List<FullTicket> findAll() {
        return this.ticketRepository.findAll();
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
    @Transactional
    public void moveTicket(MoveRequest moveRequest) throws TicketNotFoundException {
        if (moveRequest.getFromListStatus().equals(moveRequest.getToListStatus())) {
            this.moveTicketWithinTheSameTicketStatus(moveRequest);
        } else {
            this.moveTicketToAnotherTicketStatus(moveRequest);
        }
    }

    private void moveTicketWithinTheSameTicketStatus(MoveRequest moveRequest) throws TicketNotFoundException {
        FullTicket movedTicket = findById(moveRequest.getMovedTicket().getId());


        if (moveRequest.getAfterThisOne() != null) {


            if (movedTicket.getId().equals(moveRequest.getAfterThisOne().getId())) {
                return;
            }

            FullTicket leftNeighbor = findByNextId(movedTicket.getId());
            if (leftNeighbor != null) {
                leftNeighbor.setNextId(movedTicket.getNextId());
                this.ticketRepository.save(leftNeighbor);
            }
            movedTicket.setNextId(moveRequest.getAfterThisOne().getNextId());
            this.ticketRepository.save(movedTicket);

            moveRequest.getAfterThisOne().setNextId(movedTicket.getId());
            this.ticketRepository.save(moveRequest.getAfterThisOne());
        }

        if (moveRequest.getBeforeThisOne() != null) {

            if (movedTicket.getId().equals(moveRequest.getBeforeThisOne().getId())) {
                return;
            }
            FullTicket leftOfBeforeThisOne = findByNextId(moveRequest.getBeforeThisOne().getId());
            if (leftOfBeforeThisOne != null) {
                leftOfBeforeThisOne.setNextId(movedTicket.getId());
                this.ticketRepository.save(leftOfBeforeThisOne);
            }

            FullTicket leftOfThisOne = findByNextId(movedTicket.getId());
            if (leftOfThisOne != null) {
                leftOfThisOne.setNextId(movedTicket.getNextId());
                this.ticketRepository.save(leftOfThisOne);
            }
            movedTicket.setNextId(moveRequest.getBeforeThisOne().getId());
            this.ticketRepository.save(movedTicket);
        }
    }

    private void moveTicketToAnotherTicketStatus(MoveRequest moveRequest) throws TicketNotFoundException {

        FullTicket movedTicket = findById(moveRequest.getMovedTicket().getId());
        movedTicket.setStatus(moveRequest.getToListStatus());

        if (moveRequest.getAfterThisOne() == null && moveRequest.getBeforeThisOne() == null) {

            FullTicket leftNeighbor = findByNextId(movedTicket.getId());

            if (leftNeighbor != null) {
                leftNeighbor.setNextId(movedTicket.getNextId());
                this.ticketRepository.save(leftNeighbor);
            }
            movedTicket.setNextId(null);
            this.ticketRepository.save(movedTicket);
            return;
        }

        if (moveRequest.getAfterThisOne() != null) {

            FullTicket afterThisOne = findById(moveRequest.getAfterThisOne().getId());
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

        if (moveRequest.getBeforeThisOne() != null) {

            FullTicket beforeThisOne = findById(moveRequest.getBeforeThisOne().getId());
            if (movedTicket.getId().equals(beforeThisOne.getId())) {
                return;
            }
            FullTicket leftOfBeforeThisOne = findByNextId(beforeThisOne.getId());
            if (leftOfBeforeThisOne != null) {
                leftOfBeforeThisOne.setNextId(movedTicket.getId());
                this.ticketRepository.save(leftOfBeforeThisOne);
            }

            FullTicket leftOfThisOne = findByNextId(movedTicket.getId());
            if (leftOfThisOne != null) {
                leftOfThisOne.setNextId(movedTicket.getNextId());
                this.ticketRepository.save(leftOfThisOne);
            }
            movedTicket.setNextId(beforeThisOne.getId());
            this.ticketRepository.save(movedTicket);
        }
    }
}
