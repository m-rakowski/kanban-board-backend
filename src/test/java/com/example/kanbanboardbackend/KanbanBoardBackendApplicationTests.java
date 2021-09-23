package com.example.kanbanboardbackend;

import com.example.kanbanboardbackend.error.TicketNotFoundException;
import com.example.kanbanboardbackend.model.FullTicket;
import com.example.kanbanboardbackend.model.Ticket;
import com.example.kanbanboardbackend.model.TicketStatus;
import com.example.kanbanboardbackend.services.TicketService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = KanbanBoardBackendApplication.class)

public class KanbanBoardBackendApplicationTests {

    @Autowired
    private TicketService ticketService;

    @Test
    @Transactional
    public void givenTicketRepositoryInitiated_ThreeTicketsFound() {
        assertEquals(3, ticketService.findAll().size());
    }

    @Test
    @Transactional
    @Sql(scripts = "/reset_db.sql") // to create DB tables and init sample DB data
    public void givenTicketRepository_whenTableIsEmpty_createANewTicketWithEmptyNextAndPrevious() throws Exception {

        // given a new ticket as sent from user via the API
        Ticket newTicket = Ticket.builder()
                .content("this is its content")
                .status(TicketStatus.toDo)
                .title("title").build();
        final FullTicket ticket = ticketService.save(newTicket);

        // when saving
        FullTicket found = ticketService.findById(ticket.getId());

        assertNull(found.getNextId());
        assertNull(found.getPreviousId());
    }

    @Test
    @Transactional
    @Sql(scripts = "/reset_db.sql") // to create DB tables and init sample DB data
    public void givenTicketRepository_whenAddingMultipleTickets_setNextIdAndPreviousId() throws Exception {

        final FullTicket first = ticketService.save(
                Ticket.builder()
                        .title("first")
                        .content("content")
                        .status(TicketStatus.toDo)
                        .build());
        final FullTicket second = ticketService.save(
                Ticket.builder()
                        .title("second")
                        .content("content")
                        .status(TicketStatus.toDo)
                        .build());
        final FullTicket third = ticketService.save(
                Ticket.builder()
                        .title("third")
                        .content("content")
                        .status(TicketStatus.toDo)
                        .build());

        assertEquals(first, ticketService.findById(first.getId()));
        assertEquals(second, ticketService.findById(second.getId()));
        assertEquals(third, ticketService.findById(third.getId()));

        assertEquals(first.getId(), ticketService.findById(second.getId()).getPreviousId());
        assertEquals(third.getId(), ticketService.findById(second.getId()).getNextId());
        assertEquals(null, ticketService.findById(first.getId()).getPreviousId());
        assertEquals(second.getId(), ticketService.findById(first.getId()).getNextId());
        assertEquals(second.getId(), ticketService.findById(third.getId()).getPreviousId());
        assertEquals(null, ticketService.findById(third.getId()).getNextId());


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

    @Test(expected = TicketNotFoundException.class)
    @Transactional
    @Sql(scripts = "/reset_db.sql") // to create DB tables and init sample DB data
    public void givenTicketRepository_whenDeletingAlreadyDeleted_thenException() throws Exception {

        final FullTicket first = ticketService.save(
                Ticket.builder()
                        .title("first")
                        .content("content")
                        .status(TicketStatus.toDo)
                        .build());
        final FullTicket second = ticketService.save(
                Ticket.builder()
                        .title("second")
                        .content("content")
                        .status(TicketStatus.toDo)
                        .build());
        final FullTicket third = ticketService.save(
                Ticket.builder()
                        .title("third")
                        .content("content")
                        .status(TicketStatus.toDo)
                        .build());

        ticketService.deleteById(second.getId());

        assertEquals(third.getId(), ticketService.findById(first.getId()).getNextId());
        assertEquals(first.getId(), ticketService.findById(third.getId()).getPreviousId());

        ticketService.deleteById(second.getId());
    }

    @Test
    @Transactional
    @Sql(scripts = "/reset_db.sql") // to create DB tables and init sample DB data
    public void givenTicketRepository_whenDeleting_thenOK() throws Exception {

        final FullTicket first = ticketService.save(
                Ticket.builder()
                        .title("first")
                        .content("content")
                        .status(TicketStatus.toDo)
                        .build());
        final FullTicket second = ticketService.save(
                Ticket.builder()
                        .title("second")
                        .content("content")
                        .status(TicketStatus.toDo)
                        .build());
        final FullTicket third = ticketService.save(
                Ticket.builder()
                        .title("third")
                        .content("content")
                        .status(TicketStatus.toDo)
                        .build());

        ticketService.deleteById(second.getId());

        assertEquals(third.getId(), ticketService.findById(first.getId()).getNextId());
        assertEquals(first.getId(), ticketService.findById(third.getId()).getPreviousId());

        ticketService.deleteById(third.getId());
    }
}