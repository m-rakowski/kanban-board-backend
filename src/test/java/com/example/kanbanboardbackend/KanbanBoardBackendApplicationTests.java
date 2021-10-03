package com.example.kanbanboardbackend;

import com.example.kanbanboardbackend.error.TicketNotFoundException;
import com.example.kanbanboardbackend.model.FullTicket;
import com.example.kanbanboardbackend.model.MoveRequest;
import com.example.kanbanboardbackend.model.Ticket;
import com.example.kanbanboardbackend.model.TicketStatus;
import com.example.kanbanboardbackend.services.TicketService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = KanbanBoardBackendApplication.class)

public class KanbanBoardBackendApplicationTests {

    @Autowired
    private TicketService ticketService;

    private FullTicket first;
    private FullTicket second;
    private FullTicket third;

    @Before
    public void setUpFirstSecondAndThird() {

        first = FullTicket.builder()
                .id("2e25ddd1-602e-4f94-ab54-fc0147989042")
                .content("First")
                .nextId("c0ed5dfa-8eb9-40f4-a425-2065b97631a5")
                .title("first")
                .status(TicketStatus.toDo)
                .build();

        second = FullTicket.builder()
                .id("c0ed5dfa-8eb9-40f4-a425-2065b97631a5")
                .content("Second")
                .nextId("e3929b60-6910-4a54-b4f1-324af7180fa6")
                .title("second")
                .status(TicketStatus.toDo)
                .build();

        third = FullTicket.builder()
                .id("e3929b60-6910-4a54-b4f1-324af7180fa6")
                .content("Third")
                .nextId(null)
                .title("third")
                .status(TicketStatus.toDo)
                .build();

    }

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
    }

    @Test
    @Transactional
    @Sql(scripts = "/reset_db.sql") // to create DB tables and init sample DB data
    public void givenTicketRepository_whenAddingMultipleTickets_setNextId() throws Exception {

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

        assertEquals(third.getId(), ticketService.findById(second.getId()).getNextId());
        assertEquals(second.getId(), ticketService.findById(first.getId()).getNextId());
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

        ticketService.deleteById(third.getId());
    }

    @Test
    @Transactional
    public void testFindByNextId() throws Exception {
        FullTicket byNextId = ticketService.findByNextId("c0ed5dfa-8eb9-40f4-a425-2065b97631a5");

        assertEquals(FullTicket.builder()
                .id("2e25ddd1-602e-4f94-ab54-fc0147989042")
                .content("First")
                .nextId("c0ed5dfa-8eb9-40f4-a425-2065b97631a5")
                .title("first")
                .status(TicketStatus.toDo)
                .build(), byNextId);
    }

    @Test
    @Transactional
    public void testMovingInPlace() throws Exception {
        ticketService.moveTicket(new MoveRequest(first, first, null, TicketStatus.toTest, TicketStatus.toDo));
        assertEquals(List.of(first, second, third), ticketService.findAll());
    }

    @Test
    @Transactional
    public void testMovingFirstToBeLast() throws Exception {

        ticketService.moveTicket(new MoveRequest(first, third, null, TicketStatus.toTest, TicketStatus.toDo));

        third.setNextId(first.getId());
        first.setNextId(null);
        assertEquals(List.of(first, second, third), ticketService.findAll());
    }

    @Test
    @Transactional
    public void testMovingLastToBeFirst() throws Exception {

        ticketService.moveTicket(new MoveRequest(
                third,
                null,
                first, TicketStatus.toDo, TicketStatus.toDo));

        third.setNextId(first.getId());
        second.setNextId(null);
        assertEquals(List.of(first, second, third), ticketService.findAll());
    }

    @Transactional
    @Test(expected = IllegalArgumentException.class)
    public void testMovingBothNulls() throws Exception {
        ticketService.moveTicket(new MoveRequest(third, null, null, TicketStatus.toDo, TicketStatus.toDo));
    }

    @Transactional
    @Test
    public void testMovingToEmptyList() throws Exception {
        ticketService.moveTicket(new MoveRequest(
                third, null, null, TicketStatus.toDo, TicketStatus.toTest));
        second.setNextId(null);
        third.setNextId(null);
        third.setStatus(TicketStatus.toTest);
        assertEquals(List.of(first, second, third), this.ticketService.findAll());
    }
}