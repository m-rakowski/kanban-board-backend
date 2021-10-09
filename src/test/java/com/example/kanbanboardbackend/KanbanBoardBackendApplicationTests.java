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

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = KanbanBoardBackendApplication.class)
@Transactional
public class KanbanBoardBackendApplicationTests {

    @Autowired
    private TicketService ticketService;

    private FullTicket rootToDo;
    private FullTicket rootToTest;
    private FullTicket rootDone;
    private FullTicket firstToDo;
    private FullTicket secondToDo;
    private FullTicket thirdToDo;

    @Before
    public void setUpFirstSecondAndThird() {
        rootToDo = FullTicket.builder()
                .id("1b1a957e-4335-470e-babe-60d32b17aa2d").content("toDoRoot")
                .nextId("2e25ddd1-602e-4f94-ab54-fc0147989042").status(TicketStatus.toDo)
                .title("toDoRoot").isRoot(true).build();
        rootToTest = FullTicket.builder()
                .id("103fe588-fefa-4814-9bf3-6055f0149adb").content("toTestRoot")
                .nextId(null).status(TicketStatus.toTest)
                .title("toTestRoot").isRoot(true).build();
        rootDone = FullTicket.builder()
                .id("43efb13a-b048-4533-b7b1-cbbd851365e6").content("doneRoot")
                .nextId(null).status(TicketStatus.done)
                .title("doneRoot").isRoot(true).build();
        firstToDo = FullTicket.builder()
                .id("2e25ddd1-602e-4f94-ab54-fc0147989042").content("First ToDo")
                .nextId("c0ed5dfa-8eb9-40f4-a425-2065b97631a5").status(TicketStatus.toDo)
                .title("first toDo").isRoot(false).build();
        secondToDo = FullTicket.builder()
                .id("c0ed5dfa-8eb9-40f4-a425-2065b97631a5").content("Second ToDo")
                .nextId("e3929b60-6910-4a54-b4f1-324af7180fa6").status(TicketStatus.toDo)
                .title("second toDo").isRoot(false).build();
        thirdToDo = FullTicket.builder()
                .id("e3929b60-6910-4a54-b4f1-324af7180fa6").content("Third ToDo")
                .nextId(null).status(TicketStatus.toDo)
                .title("third toDo").isRoot(false).build();
    }

    @Test
    @Sql(scripts = "/mixed_tickets.sql") // to create DB tables and init sample DB data
    public void testIfFindAllReturnsSortedTickets() {
        assertEquals(List.of(rootToDo, firstToDo, secondToDo, thirdToDo, rootToTest, rootDone), ticketService.findAllAsList());
        assertEquals(Map.of(
                TicketStatus.toDo, List.of(rootToDo, firstToDo, secondToDo, thirdToDo),
                TicketStatus.toTest, List.of(rootToTest),
                TicketStatus.done, List.of(rootDone)
        ), ticketService.getAll());
    }

    @Test
    public void givenTicketRepositoryInitiated_ThreeRootTicketsFound() {
        var roots = ticketService.findAllAsList()
                .stream()
                .filter(FullTicket::getIsRoot)
                .collect(Collectors.toList());
        assertEquals(3, roots.size());
    }

    @Test
    public void givenTicketRepositoryInitiated_SixTicketsFound() {
        assertEquals(6, ticketService.findAllAsList().size());
    }

    @Test
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
    @Sql(scripts = "/reset_db.sql") // to create DB tables and init sample DB data
    public void testFindLast() throws Exception {

        // given a new ticket as sent from user via the API
        Ticket firstTicket = Ticket.builder()
                .content("this is its content")
                .status(TicketStatus.toDo)
                .title("first ticket").build();
        FullTicket firstSaved = ticketService.save(firstTicket);

        FullTicket last = ticketService.findLast(TicketStatus.toDo);
        assertEquals(firstSaved, last);
    }

    @Test(expected = TicketNotFoundException.class)
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
    public void testFindByNextId() throws Exception {
        FullTicket byNextId = ticketService.findByNextId("c0ed5dfa-8eb9-40f4-a425-2065b97631a5");

        assertNotNull(byNextId);
        assertEquals("c0ed5dfa-8eb9-40f4-a425-2065b97631a5", byNextId.getNextId());
    }

    @Test
    public void testMovingInPlace() throws Exception {
        ticketService.moveTicket(new MoveRequest(firstToDo.getId(), firstToDo.getId()));

        assertEquals(
                Map.of(
                        TicketStatus.toDo, listOfTickets(rootToDo, firstToDo, secondToDo, thirdToDo),
                        TicketStatus.toTest, listOfTickets(rootToTest),
                        TicketStatus.done, listOfTickets(rootDone)
                ),
                this.ticketService.getAll());
    }

    @Test
    public void testMovingFirstToBeLast() throws Exception {

        ticketService.moveTicket(new MoveRequest(firstToDo.getId(), thirdToDo.getId()));

        assertEquals(
                Map.of(
                        TicketStatus.toDo, listOfTickets(rootToDo, secondToDo, thirdToDo, firstToDo),
                        TicketStatus.toTest, listOfTickets(rootToTest),
                        TicketStatus.done, listOfTickets(rootDone)
                ),
                this.ticketService.getAll());
    }

    @Test
    public void testMovingLastToBeFirst() throws Exception {

        ticketService.moveTicket(new MoveRequest(
                thirdToDo.getId(),
                firstToDo.getId()));

        assertEquals(
                Map.of(
                        TicketStatus.toDo, listOfTickets(rootToDo, firstToDo, thirdToDo, secondToDo),
                        TicketStatus.toTest, listOfTickets(rootToTest),
                        TicketStatus.done, listOfTickets(rootDone)
                ),
                this.ticketService.getAll());
    }

    @Test
    public void testMovingToEmptyList() throws Exception {
        ticketService.moveTicket(
                new MoveRequest(
                        thirdToDo.getId(),
                        rootToTest.getId())
        );

        thirdToDo.setStatus(TicketStatus.toTest);
        assertEquals(
                Map.of(
                        TicketStatus.toDo, listOfTickets(rootToDo, firstToDo, secondToDo),
                        TicketStatus.toTest, listOfTickets(rootToTest, thirdToDo),
                        TicketStatus.done, listOfTickets(rootDone)
                ),
                this.ticketService.getAll());
    }

    private List<FullTicket> listOfTickets(FullTicket... tickets) {

        for (int i = 0; i < tickets.length - 1; i++) {
            tickets[i].setNextId(tickets[i + 1].getId());
        }

        tickets[tickets.length - 1].setNextId(null);
        return Arrays.asList(tickets);
    }
}