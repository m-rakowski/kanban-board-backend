package com.example.kanbanboardbackend.controller;

import com.example.kanbanboardbackend.error.TicketNotFoundException;
import com.example.kanbanboardbackend.model.FullTicket;
import com.example.kanbanboardbackend.model.Ticket;
import com.example.kanbanboardbackend.model.TicketStatus;
import com.example.kanbanboardbackend.services.TicketService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@WebMvcTest(TicketController.class)
public class TicketControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TicketService ticketService;

    private FullTicket fullTicket;

    @BeforeEach
    void setup() {
        this.fullTicket = FullTicket.builder()
                .id("85d86c3c-5707-4526-b1fe-f2fb9b4a8228")
                .content("123123")
                .status(TicketStatus.toDo)
                .title("title")
                .build();
    }

    @Test
    void whenPOSTingToTickets_ResponseIs201() throws Exception {
        Ticket inputTicket = Ticket.builder()
                .content("some content")
                .title("some title")
                .status(TicketStatus.toDo)
                .build();

        Mockito.when(ticketService.save(inputTicket))
                .thenReturn(fullTicket);

        mockMvc.perform(post("/api/tickets")
                .contentType(MediaType.APPLICATION_JSON).content(
                        "{\n" +
                                "\t\n" +
                                "\t\"title\": \"First\",\n" +
                                "\t\"content\": \"First\",\n" +
                                "\t\"status\": \"toDo\"\n" +
                                "}"
                )).andExpect(MockMvcResultMatchers.status().is(201));
    }

    @Test
    void whenGETtingATicketThatDoesNotExist_ResponseIs400() throws Exception {

        Mockito.when(ticketService.findById(anyString()))
                .thenThrow(TicketNotFoundException.class);

        mockMvc.perform(get("/api/tickets/id/12312321321321321"))
                .andExpect(MockMvcResultMatchers.content().string("Ticket Not Available"))
                .andExpect(MockMvcResultMatchers.status().isConflict());
    }

    @Test
    void whenPOSTingIncompleteTickets_ResponseIsError() throws Exception {
        Ticket inputTicket = Ticket.builder()
                .title("")
                .content("")
                .status(TicketStatus.toDo)
                .build();

        Mockito.when(ticketService.save(inputTicket))
                .thenReturn(fullTicket);

        mockMvc.perform(post("/api/tickets")
                .contentType(MediaType.APPLICATION_JSON).content(
                        "{\n" +
                                "\t\n" +
                                "\t\"content\": \"\",\n" +
                                "\t\"status\": \"toDo\"\n" +
                                "}"
                )).andExpect(MockMvcResultMatchers.status().is(400));
    }

    @Test
    void whenInputIsInvalid_thenReturnsStatus400() throws Exception {
        Ticket ticket = Ticket.builder()
                .content("")
                .status(TicketStatus.toTest)
                .title("").build();
        String body = objectMapper.writeValueAsString(ticket);

        mockMvc.perform(post("/api/tickets")
                        .contentType("application/json")
                        .content(body))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }
}