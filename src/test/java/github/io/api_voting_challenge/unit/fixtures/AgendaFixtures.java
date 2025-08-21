package github.io.api_voting_challenge.unit.fixtures;

import github.io.api_voting_challenge.dto.AgendaRequestDto;
import github.io.api_voting_challenge.dto.AgendaResponseDto;
import github.io.api_voting_challenge.model.Agenda;
import github.io.api_voting_challenge.model.enums.Status;

import java.time.LocalDate;

import static github.io.api_voting_challenge.unit.fixtures.TestConstants.*;

public class AgendaFixtures {
    public static AgendaRequestDto.AgendaRequestDtoBuilder createValidAgendaRequestDtoBuilder() {
        return AgendaRequestDto.builder()
                .title(VALID_AGENDA_TITLE)
                .description(VALID_AGENDA_DESCRIPTION);
    }

    public static AgendaRequestDto.AgendaRequestDtoBuilder createInvalidAgendaRequestDtoBuilder() {
        return AgendaRequestDto.builder()
                .title("")
                .description("");
    }

    public static AgendaResponseDto.AgendaResponseDtoBuilder createAgendaResponseDtoBuilder() {
        return AgendaResponseDto.builder()
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

    public static AgendaRequestDto createValidAgendaRequestDto() {
        return createValidAgendaRequestDtoBuilder().build();
    }

    public static AgendaResponseDto createAgendaResponseDto() {
        return createAgendaResponseDtoBuilder().build();
    }

    public static AgendaRequestDto createInvalidAgendaRequestDto() {
        return createInvalidAgendaRequestDtoBuilder().build();
    }

    public static Agenda createAgenda() {
        return createAgendaBuilder().build();
    }
}
