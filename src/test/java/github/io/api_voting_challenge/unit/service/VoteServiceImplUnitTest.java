package github.io.api_voting_challenge.unit.service;

import github.io.api_voting_challenge.dto.VoteRequest;
import github.io.api_voting_challenge.dto.VoteResultResponse;
import github.io.api_voting_challenge.exception.*;
import github.io.api_voting_challenge.fixtures.AgendaFixtures;
import github.io.api_voting_challenge.fixtures.UserFixtures;
import github.io.api_voting_challenge.fixtures.VoteFixtures;
import github.io.api_voting_challenge.fixtures.VotingSessionFixtures;
import github.io.api_voting_challenge.model.Agenda;
import github.io.api_voting_challenge.model.User;
import github.io.api_voting_challenge.model.Vote;
import github.io.api_voting_challenge.model.VotingSession;
import github.io.api_voting_challenge.model.enums.VoteOption;
import github.io.api_voting_challenge.repository.UserRepository;
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
import static github.io.api_voting_challenge.fixtures.TestConstants.VALID_SESSION_ID;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Vote Service Unit Tests")
public class VoteServiceImplUnitTest {
    @InjectMocks
    private VoteServiceImpl voteService;

    @Mock
    private VoteRepository voteRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private VotingSessionRepository votingSessionRepository;

    @Test
    @DisplayName("Deve registrar um voto com sucesso")
    void shouldRegisterVoteSuccessfully() {
        User user = UserFixtures.createValidUserEntity();
        Agenda agenda = AgendaFixtures.createAgenda();
        VotingSession votingSession = VotingSessionFixtures.createValidVotingSessionEntity();
        votingSession.setAgenda(agenda);

        VoteRequest voteRequest = VoteFixtures.createValidVoteRequest();

        when(userRepository.findById(VALID_ID)).thenReturn(Optional.of(user));
        when(votingSessionRepository.findById(VALID_ID)).thenReturn(Optional.of(votingSession));
        when(voteRepository.existsByUserIdAndAgenda_Id(VALID_ID, VALID_ID)).thenReturn(false);
        when(voteRepository.save(any(Vote.class))).thenAnswer(invocation -> invocation.getArgument(0));

        voteService.registerVote(VALID_ID, voteRequest);

        ArgumentCaptor<Vote> voteCaptor = ArgumentCaptor.forClass(Vote.class);
        verify(voteRepository, times(1)).save(voteCaptor.capture());

        Vote capturedVote = voteCaptor.getValue();

        assertEquals(user, capturedVote.getUser());
        assertEquals(agenda, capturedVote.getAgenda());
        assertEquals(voteRequest.voteOption(), capturedVote.getVoteOption());

        verifyNoMoreInteractions(userRepository, votingSessionRepository, voteRepository);
    }

    @Test
    @DisplayName("Deve lançar exceção se o usuário não for encontrado")
    void shouldThrowExceptionWhenUserNotFound() {
        VoteRequest voteRequest = VoteFixtures.createValidVoteRequest();
        when(userRepository.findById(VALID_ID)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () ->
                voteService.registerVote(VALID_ID, voteRequest));

        verify(userRepository).findById(VALID_ID);
        verifyNoInteractions(votingSessionRepository, voteRepository);
    }

    @Test
    @DisplayName("Deve lançar exceção se a sessão de votação não for encontrada")
    void shouldThrowExceptionWhenVotingSessionNotFound() {
        User user = UserFixtures.createValidUserEntity();
        VoteRequest voteRequest = VoteFixtures.createValidVoteRequest();

        when(userRepository.findById(VALID_ID)).thenReturn(Optional.of(user));
        when(votingSessionRepository.findById(VALID_ID)).thenReturn(Optional.empty());

        assertThrows(VotingSessionNotFoundException.class, () ->
                voteService.registerVote(VALID_ID, voteRequest)
        );

        verify(userRepository).findById(VALID_ID);
        verify(votingSessionRepository).findById(VALID_ID);
        verifyNoInteractions(voteRepository);
    }

    @Test
    @DisplayName("Deve lançar exceção se a sessão de votação estiver fechada")
    void shouldThrowExceptionWhenVotingSessionIsClosed() {
        User votingUser = UserFixtures.createValidUserEntity();
        VotingSession votingSession = VotingSessionFixtures.createValidVotingSessionEntity();
        votingSession.setEndTime(LocalDateTime.now().minusMinutes(5));
        VoteRequest voteRequestDto = VoteFixtures.createValidVoteRequest();

        when(userRepository.findById(VALID_ID)).thenReturn(Optional.of(votingUser));
        when(votingSessionRepository.findById(VALID_ID)).thenReturn(Optional.of(votingSession));

        assertThrows(VotingSessionClosedException.class, () ->
                voteService.registerVote(VALID_ID, voteRequestDto));

        verify(userRepository).findById(VALID_ID);
        verify(votingSessionRepository).findById(VALID_ID);
        verifyNoInteractions(voteRepository);
    }

    @Test
    @DisplayName("Deve lançar exceção se o usuário já tiver votado")
    void shouldThrowExceptionWhenUserAlreadyVoted() {
        User user = UserFixtures.createValidUserEntity();
        VotingSession votingSession = VotingSessionFixtures.createValidVotingSessionEntity();
        VoteRequest voteRequest = VoteFixtures.createValidVoteRequest();

        when(userRepository.findById(VALID_ID)).thenReturn(Optional.of(user));
        when(votingSessionRepository.findById(VALID_ID)).thenReturn(Optional.of(votingSession));
        when(voteRepository.existsByUserIdAndAgenda_Id(VALID_ID, VALID_ID)).thenReturn(true);

        assertThrows(UserAlreadyVotedException.class, () ->
                voteService.registerVote(VALID_ID, voteRequest));

        verify(userRepository).findById(VALID_ID);
        verify(votingSessionRepository).findById(VALID_ID);
        verifyNoMoreInteractions(voteRepository, userRepository, votingSessionRepository);
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

        VoteResultResponse result = voteService.calculateVotingResult(VALID_SESSION_ID);

        assertNotNull(result);
        assertEquals(10L, result.yesVotes());
        assertEquals(5L, result.noVotes());

        verify(votingSessionRepository).findById(VALID_ID);
        verify(voteRepository).countByAgendaIdAndVoteOption(agenda.getId(), VoteOption.YES);
        verify(voteRepository).countByAgendaIdAndVoteOption(agenda.getId(), VoteOption.NO);
        verifyNoMoreInteractions(votingSessionRepository, voteRepository);
    }

    @Test
    @DisplayName("Deve lançar exceção se a sessão de votação não for encontrada")
    void shouldThrowException_whenVotingSessionNotFound() {
        when(votingSessionRepository.findById(VALID_ID)).thenReturn(Optional.empty());

        assertThrows(VotingSessionNotFoundException.class, () ->
                voteService.calculateVotingResult(VALID_ID));

        verifyNoMoreInteractions(votingSessionRepository, voteRepository);
    }

    @Test
    @DisplayName("Deve lançar exceção se a sessão de votação ainda estiver em andamento")
    void shouldThrowException_whenVotingSessionIsInProgress() {
        VotingSession votingSession = VotingSessionFixtures.createValidVotingSessionEntity();
        votingSession.setEndTime(LocalDateTime.now().plusMinutes(5));

        when(votingSessionRepository.findById(VALID_ID)).thenReturn(Optional.of(votingSession));

        assertThrows(VotingSessionInProgressException.class, () ->
                voteService.calculateVotingResult(VALID_ID));

        verify(votingSessionRepository).findById(VALID_ID);
        verifyNoInteractions(voteRepository);
        verifyNoMoreInteractions(votingSessionRepository, voteRepository);
    }
}
