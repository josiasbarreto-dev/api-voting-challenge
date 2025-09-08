package github.io.api_voting_challenge.fixtures;

import github.io.api_voting_challenge.dto.VotingSessionRequest;
import github.io.api_voting_challenge.dto.VotingSessionResponse;
import github.io.api_voting_challenge.model.Agenda;
import github.io.api_voting_challenge.model.VotingSession;
import github.io.api_voting_challenge.model.enums.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static github.io.api_voting_challenge.fixtures.AgendaFixtures.createAgenda;
import static github.io.api_voting_challenge.fixtures.TestConstants.*;

public class VotingSessionFixtures {
    public static VotingSessionRequest.VotingSessionRequestBuilder createValidVotingSessionRequestBuilder() {
        return VotingSessionRequest.builder()
                .durationInMinutes(60);
    }

    public static VotingSessionRequest.VotingSessionRequestBuilder createInvalidVotingSessionRequestBuilder() {
        return VotingSessionRequest.builder()
                .durationInMinutes(null);
    }

    public static VotingSessionResponse.VotingSessionResponseBuilder createVotingSessionResponseBuilder() {
        return VotingSessionResponse.builder()
                .id(VALID_ID)
                .durationInMinutes(60)
                .startTime(LocalDateTime.now().toString())
                .endTime(LocalDateTime.now().plusMinutes(60).toString())
                .status(Status.IN_PROGRESS)
                .agendaId(VALID_ID);
    }

    public static List<VotingSessionResponse> createVotingSessionResponseListBuilder() {
        return List.of(
                createVotingSessionResponseBuilder().build(),
                VotingSessionResponse.builder()
                        .id(2L)
                        .durationInMinutes(30)
                        .startTime(LocalDateTime.now().toString())
                        .endTime(LocalDateTime.now().plusMinutes(30).toString())
                        .status(Status.IN_PROGRESS)
                        .agendaId(2L)
                        .build()
        );
    }

    public static VotingSession.VotingSessionBuilder createVotingSessionEntity() {
        return VotingSession.builder()
                .id(VALID_ID)
                .durationInMinutes(60)
                .startTime(LocalDateTime.now())
                .endTime(LocalDateTime.now().plusMinutes(60));
    }

    public static VotingSession createExpiredVotingSession() {
        Agenda agenda = createAgenda();
        agenda.setStatus(Status.IN_PROGRESS);

        return VotingSession.builder()
                .id(99L)
                .durationInMinutes(1)
                .startTime(LocalDateTime.now().minusMinutes(5))
                .endTime(LocalDateTime.now().minusMinutes(4))
                .agenda(agenda)
                .build();
    }

    public static List<VotingSession> createExpiredVotingSessionList(int count) {
        return IntStream.range(0, count)
                .mapToObj(i -> {
                    Agenda agenda = Agenda.builder()
                            .id((long) i + 100)
                            .status(Status.IN_PROGRESS)
                            .build();

                    return VotingSession.builder()
                            .id((long) i + 100)
                            .durationInMinutes(1)
                            .startTime(LocalDateTime.now().minusMinutes(10 + i))
                            .endTime(LocalDateTime.now().minusMinutes(5 + i))
                            .agenda(agenda)
                            .build();
                })
                .collect(Collectors.toList());
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

    public static VotingSession createOpenVotingSession() {
        Agenda openAgenda = createAgendaWithStatus(Status.IN_PROGRESS);

        return VotingSession.builder()
                .id(VALID_ID)
                .durationInMinutes(60)
                .startTime(LocalDateTime.now().minusMinutes(10))
                .endTime(LocalDateTime.now().plusMinutes(50))
                .agenda(openAgenda)
                .build();
    }

    public static VotingSessionRequest createValidVotingSessionRequest() {
        return createValidVotingSessionRequestBuilder().build();
    }

    public static VotingSessionResponse createVotingSessionResponse() {
        return createVotingSessionResponseBuilder().build();
    }

    public static List<VotingSessionResponse> createVotingSessionResponseList() {
        return createVotingSessionResponseListBuilder();
    }

    public static VotingSession createValidVotingSessionEntity() {
        return createVotingSessionEntity().build();
    }

    public static VotingSessionRequest createInvalidVotingSessionRequest() {
        return createInvalidVotingSessionRequestBuilder().build();
    }

    public static List<VotingSessionResponse> createVotingSessionResponseList(int count) {
        return IntStream.range(0, count)
                .mapToObj(i -> VotingSessionResponse.builder()
                        .id((long) i + 1)
                        .durationInMinutes(60)
                        .startTime(LocalDateTime.now().toString())
                        .endTime(LocalDateTime.now().plusMinutes(60).toString())
                        .status(Status.IN_PROGRESS)
                        .agendaId((long) i + 1)
                        .build())
                .collect(Collectors.toList());
    }

    public static Page<VotingSessionResponse> createVotingSessionResponsePage(long totalElements, Pageable pageable) {
        List<VotingSessionResponse> content = IntStream.range(pageable.getPageNumber() * pageable.getPageSize(),
                        Math.min((pageable.getPageNumber() + 1) * pageable.getPageSize(), (int) totalElements))
                .mapToObj(i -> VotingSessionResponse.builder()
                        .id((long) i + 1)
                        .durationInMinutes(60)
                        .startTime(LocalDateTime.now().toString())
                        .endTime(LocalDateTime.now().plusMinutes(60).toString())
                        .status(Status.IN_PROGRESS)
                        .agendaId((long) i + 1)
                        .build())
                .collect(Collectors.toList());

        return new PageImpl<>(content, pageable, totalElements);
    }
}