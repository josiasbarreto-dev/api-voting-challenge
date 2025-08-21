package github.io.api_voting_challenge.impl;

import github.io.api_voting_challenge.dto.VoteRequestDTO;
import github.io.api_voting_challenge.dto.VoteResultResponseDTO;
import github.io.api_voting_challenge.exception.*;
import github.io.api_voting_challenge.fixtures.AgendaFixtures;
import github.io.api_voting_challenge.fixtures.VoteFixtures;
import github.io.api_voting_challenge.fixtures.VoterFixtures;
import github.io.api_voting_challenge.fixtures.VotingSessionFixtures;
import github.io.api_voting_challenge.model.Agenda;
import github.io.api_voting_challenge.model.Vote;
import github.io.api_voting_challenge.model.VotingSession;
import github.io.api_voting_challenge.model.VotingUser;
import github.io.api_voting_challenge.model.enums.VoteOption;
import github.io.api_voting_challenge.repository.UserVotingRepository;
import github.io.api_voting_challenge.repository.VoteRepository;
import github.io.api_voting_challenge.repository.VotingSessionRepository;
import github.io.api_voting_challenge.service.impl.VoteServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static github.io.api_voting_challenge.fixtures.TestConstants.VALID_ID;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("VoteServiceImpl Test")
public class VoteServiceImplTest {

    @InjectMocks
    private VoteServiceImpl voteServiceImpl;

    @Mock
    private UserVotingRepository userVotingRepository;

    @Mock
    private VotingSessionRepository votingSessionRepository;

    @Mock
    private VoteRepository voteRepository;

    @Test
    @DisplayName("Deve registrar um voto com sucesso")
    void shouldRegisterVoteSuccessfully() {
        VotingUser votingUser = VoterFixtures.createValidVotingUserEntity();
        Agenda agenda = AgendaFixtures.createAgenda();
        VotingSession votingSession = VotingSessionFixtures.createValidVotingSessionEntity();
        votingSession.setAgenda(agenda);
        VoteRequestDTO voteRequestDto = VoteFixtures.createValidVoteRequestDto();

        when(userVotingRepository.findById(VALID_ID)).thenReturn(Optional.of(votingUser));
        when(votingSessionRepository.findById(VALID_ID)).thenReturn(Optional.of(votingSession));
        when(voteRepository.existsByUserIdAndAgenda_Id(VALID_ID, VALID_ID)).thenReturn(false);
        when(voteRepository.save(any(Vote.class))).thenAnswer(invocation -> invocation.getArgument(0));

        voteServiceImpl.registerVote(VALID_ID, VALID_ID, voteRequestDto);

        ArgumentCaptor<Vote> voteCaptor = ArgumentCaptor.forClass(Vote.class);
        verify(voteRepository, times(1)).save(voteCaptor.capture());

        Vote capturedVote = voteCaptor.getValue();

        assertEquals(votingUser, capturedVote.getUser());
        assertEquals(agenda, capturedVote.getAgenda());
        assertEquals(voteRequestDto.voteOption(), capturedVote.getVoteOption());

        verifyNoMoreInteractions(userVotingRepository, votingSessionRepository, voteRepository);
    }

    @Test
    @DisplayName("Deve lançar exceção se o usuário não for encontrado")
    void shouldThrowExceptionWhenUserNotFound() {
        VoteRequestDTO voteRequestDto = VoteFixtures.createValidVoteRequestDto();
        when(userVotingRepository.findById(VALID_ID)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () ->
                voteServiceImpl.registerVote(VALID_ID, VALID_ID, voteRequestDto));

        verifyNoMoreInteractions(votingSessionRepository, voteRepository);
    }

    @Test
    @DisplayName("Deve lançar exceção se a sessão de votação não for encontrada")
    void shouldThrowExceptionWhenVotingSessionNotFound() {
        VotingUser votingUser = VoterFixtures.createValidVotingUserEntity();
        VoteRequestDTO voteRequestDto = VoteFixtures.createValidVoteRequestDto();

        when(userVotingRepository.findById(VALID_ID)).thenReturn(Optional.of(votingUser));
        when(votingSessionRepository.findById(VALID_ID)).thenReturn(Optional.empty());

        assertThrows(VotingSessionNotFoundException.class, () ->
                voteServiceImpl.registerVote(VALID_ID, VALID_ID, voteRequestDto));

        verifyNoMoreInteractions(voteRepository);
    }

    @Test
    @DisplayName("Deve lançar exceção se a sessão de votação estiver fechada")
    void shouldThrowExceptionWhenVotingSessionIsClosed() {
        VotingUser votingUser = VoterFixtures.createValidVotingUserEntity();
        VotingSession votingSession = VotingSessionFixtures.createValidVotingSessionEntity();
        votingSession.setEndTime(LocalDateTime.now().minusMinutes(5));
        VoteRequestDTO voteRequestDto = VoteFixtures.createValidVoteRequestDto();

        when(userVotingRepository.findById(VALID_ID)).thenReturn(Optional.of(votingUser));
        when(votingSessionRepository.findById(VALID_ID)).thenReturn(Optional.of(votingSession));

        assertThrows(VotingSessionClosedException.class, () ->
                voteServiceImpl.registerVote(VALID_ID, VALID_ID, voteRequestDto));

        verifyNoMoreInteractions(voteRepository);
    }

    @Test
    @DisplayName("Deve lançar exceção se o usuário já tiver votado")
    void shouldThrowExceptionWhenUserAlreadyVoted() {
        VotingUser votingUser = VoterFixtures.createValidVotingUserEntity();
        VotingSession votingSession = VotingSessionFixtures.createValidVotingSessionEntity();
        VoteRequestDTO voteRequestDto = VoteFixtures.createValidVoteRequestDto();

        when(userVotingRepository.findById(VALID_ID)).thenReturn(Optional.of(votingUser));
        when(votingSessionRepository.findById(VALID_ID)).thenReturn(Optional.of(votingSession));
        when(voteRepository.existsByUserIdAndAgenda_Id(VALID_ID, VALID_ID)).thenReturn(true);

        assertThrows(UserAlreadyVotedException.class, () ->
                voteServiceImpl.registerVote(VALID_ID, VALID_ID, voteRequestDto));

        verifyNoMoreInteractions(voteRepository);
    }

    @Test
    @DisplayName("Deve calcular o resultado da votação com sucesso")
    void shouldCalculateVotingResultSuccessfully() {
        Agenda agenda = AgendaFixtures.createAgenda();
        VotingSession votingSession = VotingSessionFixtures.createValidVotingSessionEntity();
        votingSession.setAgenda(agenda);
        votingSession.setEndTime(LocalDateTime.now().minusMinutes(5));

        when(votingSessionRepository.findById(VALID_ID)).thenReturn(Optional.of(votingSession));
        when(voteRepository.countByAgendaIdAndVoteOption(agenda.getId(), VoteOption.YES)).thenReturn(10L);
        when(voteRepository.countByAgendaIdAndVoteOption(agenda.getId(), VoteOption.NO)).thenReturn(5L);

        VoteResultResponseDTO result = voteServiceImpl.calculateVotingResult(VALID_ID);

        assertNotNull(result);
        assertEquals(10L, result.yesVotes());
        assertEquals(5L, result.noVotes());

        verifyNoMoreInteractions(votingSessionRepository, voteRepository);
    }

    @Test
    @DisplayName("Deve lançar exceção se a sessão de votação não for encontrada")
    void shouldThrowException_whenVotingSessionNotFound() {
        when(votingSessionRepository.findById(VALID_ID)).thenReturn(Optional.empty());

        assertThrows(VotingSessionNotFoundException.class, () ->
                voteServiceImpl.calculateVotingResult(VALID_ID));

        verifyNoMoreInteractions(votingSessionRepository, voteRepository);
    }

    @Test
    @DisplayName("Deve lançar exceção se a sessão de votação ainda estiver em andamento")
    void shouldThrowException_whenVotingSessionIsInProgress() {
        VotingSession votingSession = VotingSessionFixtures.createValidVotingSessionEntity();
        votingSession.setEndTime(LocalDateTime.now().plusMinutes(5));

        when(votingSessionRepository.findById(VALID_ID)).thenReturn(Optional.of(votingSession));

        assertThrows(VotingSessionInProgressException.class, () ->
                voteServiceImpl.calculateVotingResult(VALID_ID));

        verifyNoMoreInteractions(votingSessionRepository, voteRepository);
    }
}