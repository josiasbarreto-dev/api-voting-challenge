package github.io.api_voting_challenge.fixtures;

import github.io.api_voting_challenge.dto.request.AgendaRequest;
import github.io.api_voting_challenge.dto.response.AgendaResponse;
import github.io.api_voting_challenge.model.Agenda;
import github.io.api_voting_challenge.model.enums.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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
                .status(Status.PENDING)
                .creationDate(LocalDate.now());
    }

    public static Agenda.AgendaBuilder createAgendaBuilder() {
        return Agenda.builder()
                .id(VALID_ID)
                .title(VALID_AGENDA_TITLE)
                .description(VALID_AGENDA_DESCRIPTION)
                .status(Status.PENDING)
                .creationDate(LocalDate.now());
    }

    public static List<AgendaResponse> createAgendaResponseList(int count) {
        return IntStream.range(0, count)
                .mapToObj(i -> AgendaResponse.builder()
                        .id((long) i + 1)
                        .title(VALID_AGENDA_TITLE + " " + (i + 1))
                        .description(VALID_AGENDA_DESCRIPTION + " " + (i + 1))
                        .creationDate(LocalDate.now())
                        .status(Status.PENDING)
                        .build())
                .collect(Collectors.toList());
    }

    public static Page<AgendaResponse> createAgendaResponsePage(long totalElements, Pageable pageable) {
        List<AgendaResponse> content = IntStream.range(
                        pageable.getPageNumber() * pageable.getPageSize(),
                        Math.min((pageable.getPageNumber() + 1) * pageable.getPageSize(), (int) totalElements))
                .mapToObj(i -> AgendaResponse.builder()
                        .id((long) i + 1)
                        .title(VALID_AGENDA_TITLE + " " + (i + 1))
                        .description(VALID_AGENDA_DESCRIPTION + " " + (i + 1))
                        .creationDate(LocalDate.now())
                        .status(Status.PENDING)
                        .build())
                .collect(Collectors.toList());

        return new PageImpl<>(content, pageable, totalElements);
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
