package com.example.kanbanboardbackend.controller;

import com.example.kanbanboardbackend.model.FullTicket;
import com.example.kanbanboardbackend.model.Ticket;
import com.example.kanbanboardbackend.model.TicketStatus;
import com.example.kanbanboardbackend.services.TicketService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TicketController.class)
public class TicketControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TicketService ticketService;

    private FullTicket fullTicket;

    @BeforeEach
    void setup() {
        this.fullTicket = FullTicket.builder()
                .id("123")
                .content("123123")
                .status(TicketStatus.toDo)
                .title("title")
                .build();
    }

    @Test
    void saveTicket() throws Exception {
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
                )).andExpect(status().isOk());
    }
}