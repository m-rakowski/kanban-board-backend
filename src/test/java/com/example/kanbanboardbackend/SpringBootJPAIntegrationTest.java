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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = KanbanBoardBackendApplication.class)
public class SpringBootJPAIntegrationTest {

    @Autowired
    private TicketService ticketService;

    @Test
    @Transactional
    public void givenTicketRepository_whenTableIsEmpty_createANewTicketWithEmptyNextAndPrevious() throws Exception {

        // given a new ticket as sent from user via the API
        Ticket newTicket = Ticket.builder()
                .content("this is its content")
                .status(TicketStatus.toDo)
                .title("title").build();
        final FullTicket ticket = ticketService.save(newTicket);

        List<FullTicket> all = ticketService.findAll();


        // when saving
        FullTicket found = ticketService.findById(ticket.getId());

        assertEquals(found.getNextId(), null);
        assertEquals(found.getPreviousId(), null);
    }

    @Test
    @Transactional
    public void givenTicketRepository_whenAddingASecondTicket_createANewTicketWithEmptyNextAndNonEmptyPrevious() throws Exception {

        // given a new ticket as sent from user via the API

        Ticket firstTicket = Ticket.builder()
                .content("this is its content")
                .status(TicketStatus.toDo)
                .title("first ticket").build();
        Ticket secondTicket = Ticket.builder()
                .content("this is its content")
                .status(TicketStatus.toDo)
                .title("second ticket").build();
        FullTicket firstSaved = ticketService.save(firstTicket);
        FullTicket secondSaved = ticketService.save(secondTicket);

        FullTicket firstFound = ticketService.findById(firstSaved.getId());
        FullTicket secondFound = ticketService.findById(secondSaved.getId());

        assertNotNull(firstFound);
        assertNotNull(secondFound);

        assertEquals(null, firstFound.getPreviousId());
        assertEquals(null, secondFound.getNextId());

        assertEquals(secondFound.getId(), firstFound.getNextId());
        assertEquals(firstFound.getId(), secondFound.getPreviousId());
    }

    @Test
    @Transactional
    public void testFindLast() throws Exception {

        // given a new ticket as sent from user via the API
        Ticket firstTicket = Ticket.builder()
                .content("this is its content")
                .status(TicketStatus.toDo)
                .title("first ticket").build();
        FullTicket firstSaved = ticketService.save(firstTicket);

        FullTicket last = ticketService.findLast();
        assertEquals(firstSaved, last);
    }

    @Test()
    @Transactional
    public void givenTicketRepository_whenSaved_thenOK() throws Exception {

        // given a new ticket as sent from user via the API
        Ticket newTicket = Ticket.builder()
                .content("this is its content")
                .status(TicketStatus.toDo)
                .title("first ticket").build();
        // when saving
        final FullTicket ticket = ticketService.save(newTicket);
        var found = ticketService.findById(ticket.getId());

        // then it is saved
        assertNotNull(found);
    }

    @Test()
    @Transactional
    public void givenTicketRepository_whenDeleting_thenOK() throws Exception {
//        final FullTicket ticket = ticketService.save(
//                Ticket.builder().build("this is a new ticket",
//                        "this is its content",
//                        TicketStatus.toDo));
//
//        assertEquals(false, ticketService.findById(ticket.getId()).isEmpty());
//
//        ticketService.deleteById(ticket.getId());
//
//        assertEquals(true, ticketService.findById(ticket.getId()).isEmpty());
    }
}