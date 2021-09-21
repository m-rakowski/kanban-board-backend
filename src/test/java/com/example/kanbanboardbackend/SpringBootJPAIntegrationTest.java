package com.example.kanbanboardbackend;

import com.example.kanbanboardbackend.model.FullTicket;
import com.example.kanbanboardbackend.model.Ticket;
import com.example.kanbanboardbackend.model.TicketStatus;
import com.example.kanbanboardbackend.services.TicketService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = KanbanBoardBackendApplication.class)
public class SpringBootJPAIntegrationTest {

    @Autowired
    private TicketService ticketService;

    @Test
    @Transactional
    public void givenTicketRepository_whenTableIsEmpty_createANewTicketWithEmptyNextAndPrevious() {

        // given a new ticket as sent from user via the API
        Ticket newTicket = new Ticket(
                "this is a new ticket", "this is its content", TicketStatus.toDo);

        final FullTicket ticket = ticketService.save(newTicket);

        List<FullTicket> all = ticketService.findAll();


        // when saving
        Optional<FullTicket> found = ticketService.findById(ticket.getId());

        assertEquals(found.get().getNextId(), null);
        assertEquals(found.get().getPreviousId(), null);
    }

    @Test
    @Transactional
    public void givenTicketRepository_whenAddingASecondTicket_createANewTicketWithEmptyNextAndNonEmptyPrevious() {

        // given a new ticket as sent from user via the API
        Ticket firstTicket = new Ticket(
                "first ticket", "this is its content", TicketStatus.toDo);
        Ticket secondTicket = new Ticket(
                "second ticket", "this is its content", TicketStatus.toDo);

        FullTicket firstSaved = ticketService.save(firstTicket);
        FullTicket secondSaved = ticketService.save(secondTicket);

        Optional<FullTicket> firstFound = ticketService.findById(firstSaved.getId());
        Optional<FullTicket> secondFound = ticketService.findById(secondSaved.getId());

        assertNotNull(firstFound);
        assertNotNull(secondFound);

        assertEquals(null, firstFound.get().getPreviousId());
        assertEquals(null, secondFound.get().getNextId());

        assertEquals(secondFound.get().getId(), firstFound.get().getNextId());
        assertEquals(firstFound.get().getId(), secondFound.get().getPreviousId());
    }
    @Test
    @Transactional
    public void testFindLast() {

        // given a new ticket as sent from user via the API
        Ticket firstTicket = new Ticket(
                "first ticket", "this is its content", TicketStatus.toDo);

        FullTicket firstSaved = ticketService.save(firstTicket);

        FullTicket last = ticketService.findLast();
        assertEquals(firstSaved, last);
    }

    @Test()
    @Transactional
    public void givenTicketRepository_whenSaved_thenOK() {

        // given a new ticket as sent from user via the API
        Ticket newTicket = new Ticket(
                "this is a new ticket", "this is its content", TicketStatus.toDo);

        // when saving
        final FullTicket ticket = ticketService.save(newTicket);
        var found = ticketService.findById(ticket.getId());

        // then it is saved
        assertNotNull(found);
    }
}