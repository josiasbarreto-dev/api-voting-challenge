package github.io.api_voting_challenge.fixtures;

import github.io.api_voting_challenge.dto.VotingSessionRequestDto;
import github.io.api_voting_challenge.dto.VotingSessionResponseDto;
import github.io.api_voting_challenge.model.VotingSession;
import github.io.api_voting_challenge.model.enums.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static github.io.api_voting_challenge.fixtures.TestConstants.VALID_ID;

public class VotingSessionFixtures {
    public static VotingSessionRequestDto.VotingSessionRequestDtoBuilder createValidVotingSessionRequestDtoBuilder() {
        return VotingSessionRequestDto.builder()
                .durationInMinutes(60);
    }

    public static VotingSessionRequestDto.VotingSessionRequestDtoBuilder createInvalidVotingSessionRequestDtoBuilder() {
        return VotingSessionRequestDto.builder()
                .durationInMinutes(null);
    }

    public static VotingSessionResponseDto.VotingSessionResponseDtoBuilder createVotingSessionResponseDtoBuilder() {
        return VotingSessionResponseDto.builder()
                .id(VALID_ID)
                .durationInMinutes(60)
                .startTime(LocalDateTime.now().toString())
                .endTime(LocalDateTime.now().plusMinutes(60).toString())
                .status(Status.IN_PROGRESS)
                .agendaId(VALID_ID);
    }

    public static List<VotingSessionResponseDto> createVotingSessionResponseDtoListBuilder() {
        return List.of(
                createVotingSessionResponseDtoBuilder().build(),
                VotingSessionResponseDto.builder()
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

    public static VotingSessionRequestDto createValidVotingSessionRequestDto() {
        return createValidVotingSessionRequestDtoBuilder().build();
    }

    public static VotingSessionResponseDto createVotingSessionResponseDto() {
        return createVotingSessionResponseDtoBuilder().build();
    }

    public static List<VotingSessionResponseDto> createVotingSessionResponseDtoList() {
        return createVotingSessionResponseDtoListBuilder();
    }

    public static VotingSession createValidVotingSessionEntity() {
        return createVotingSessionEntity().build();
    }

    public static VotingSessionRequestDto createInvalidVotingSessionRequestDto() {
        return createInvalidVotingSessionRequestDtoBuilder().build();
    }

    public static List<VotingSessionResponseDto> createVotingSessionResponseDtoList(int count) {
        return IntStream.range(0, count)
                .mapToObj(i -> VotingSessionResponseDto.builder()
                        .id((long) i + 1)
                        .durationInMinutes(60)
                        .startTime(LocalDateTime.now().toString())
                        .endTime(LocalDateTime.now().plusMinutes(60).toString())
                        .status(Status.IN_PROGRESS)
                        .agendaId((long) i + 1)
                        .build())
                .collect(Collectors.toList());
    }

    public static Page<VotingSessionResponseDto> createVotingSessionResponseDtoPage(long totalElements, Pageable pageable) {
        List<VotingSessionResponseDto> content = IntStream.range(pageable.getPageNumber() * pageable.getPageSize(),
                        Math.min((pageable.getPageNumber() + 1) * pageable.getPageSize(), (int) totalElements))
                .mapToObj(i -> VotingSessionResponseDto.builder()
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