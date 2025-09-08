package github.io.api_voting_challenge.fixtures;

import github.io.api_voting_challenge.dto.AgendaRequest;
import github.io.api_voting_challenge.dto.AgendaResponse;
import github.io.api_voting_challenge.model.Agenda;
import github.io.api_voting_challenge.model.enums.Status;

import java.time.LocalDate;

import static github.io.api_voting_challenge.fixtures.TestConstants.*;

public class AgendaFixtures {
    public static AgendaRequest.AgendaRequestBuilder createValidAgendaRequestBuilder() {
        return AgendaRequest.builder()
                .title(VALID_AGENDA_TITLE)
                .description(VALID_AGENDA_DESCRIPTION);
    }

    public static AgendaRequest.AgendaRequestBuilder createInvalidAgendaRequestBuilder() {
        return AgendaRequest.builder()
                .title("")
                .description("");
    }

    public static AgendaResponse.AgendaResponseBuilder createAgendaResponseBuilder() {
        return AgendaResponse.builder()
                .id(VALID_ID)
                .title(VALID_AGENDA_TITLE)
                .description(VALID_AGENDA_DESCRIPTION)
                .status(String.valueOf(Status.PENDING))
                .creationDate(LocalDate.now())
                .createdBy(VALID_NAME);
    }

    public static Agenda.AgendaBuilder createAgendaBuilder() {
        return Agenda.builder()
                .id(VALID_ID)
                .title(VALID_AGENDA_TITLE)
                .description(VALID_AGENDA_DESCRIPTION)
                .status(Status.PENDING)
                .creationDate(LocalDate.now())
                .createdBy(VALID_NAME);
    }

    public static Agenda createAgendaWithStatus(Status status) {
        return Agenda.builder()
                .id(VALID_ID)
                .title(VALID_AGENDA_TITLE)
                .description(VALID_AGENDA_DESCRIPTION)
                .status(status)
                .creationDate(LocalDate.now())
                .createdBy(VALID_NAME)
                .build();
    }

    public static AgendaRequest createValidAgendaRequest() {
        return createValidAgendaRequestBuilder().build();
    }

    public static AgendaResponse createAgendaResponse() {
        return createAgendaResponseBuilder().build();
    }

    public static AgendaRequest createInvalidAgendaRequest() {
        return createInvalidAgendaRequestBuilder().build();
    }

    public static Agenda createAgenda() {
        return createAgendaBuilder().build();
    }
}
