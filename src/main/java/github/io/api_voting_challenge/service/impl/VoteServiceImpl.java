package github.io.api_voting_challenge.service.impl;

import github.io.api_voting_challenge.client.CpfApiClient;
import github.io.api_voting_challenge.client.response.CpfStatusResponse;
import github.io.api_voting_challenge.dto.request.VoteRequest;
import github.io.api_voting_challenge.dto.response.VoteResultResponse;
import github.io.api_voting_challenge.exception.BusinessException;
import github.io.api_voting_challenge.model.Agenda;
import github.io.api_voting_challenge.model.User;
import github.io.api_voting_challenge.model.Vote;
import github.io.api_voting_challenge.model.VotingSession;
import github.io.api_voting_challenge.model.enums.VoteOption;
import github.io.api_voting_challenge.repository.UserRepository;
import github.io.api_voting_challenge.repository.VoteRepository;
import github.io.api_voting_challenge.repository.VotingSessionRepository;
import github.io.api_voting_challenge.service.VoteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class VoteServiceImpl implements VoteService {
    public static final String ABLE_TO_VOTE = "ABLE_TO_VOTE";
    private final UserRepository userRepository;
    private final VotingSessionRepository votingSessionRepository;
    private final VoteRepository voteRepository;
    private final CpfApiClient cpfApiClient;

    @Override
    public void registerVote(Long sessionId, VoteRequest voteRequest) {
        log.info("Registering vote for session ID: {} by user ID: {}", sessionId, voteRequest.userId());
        log.info("Retrieving user with ID: {}", voteRequest.userId());
        User user = findExistingUser(voteRequest.userId());

        log.info("Validating CPF for user ID: {}", voteRequest.userId());
        ResponseEntity<CpfStatusResponse> response = cpfApiClient.validateCpf(user.getCpf());
        validateUserEligibilityToVote(response);

        log.info("Retrieving voting session with ID: {}", sessionId);
        VotingSession session = findExistingVotingSession(sessionId);

        log.info("Validating if voting session is open.");
        if (!session.isOpen()){
            log.info("Voting session with ID: {} is closed.", sessionId);
            throw new BusinessException("Voting session is closed.", HttpStatus.UNPROCESSABLE_ENTITY);
        }

        log.info("Checking if user ID: {} has already voted in session ID: {}", voteRequest.userId(), sessionId);
        checkIfUserAlreadyVoted(voteRequest.userId(), sessionId);

        Vote vote = Vote.createVote(user, session.getAgenda(), voteRequest.voteOption());

        log.info("Agenda associated with session: {}", session.getAgenda());
        voteRepository.save(vote);
        log.info("Vote registered successfully for session ID: {} by user ID: {}", sessionId, voteRequest.userId());
    }

    private static void validateUserEligibilityToVote(ResponseEntity<CpfStatusResponse> response) {
        if(response.getStatusCode() != HttpStatus.OK || response.getBody() == null) {
            throw new BusinessException("User unable to vote!", HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }

    @Override
    public VoteResultResponse calculateVotingResult(Long sessionId) {
        log.info("Calculating voting results for session ID: {}", sessionId);
        VotingSession session = findExistingVotingSession(sessionId);

        log.info("Checking if voting session is closed for ID: {}", sessionId);
        checkIfSessionIsClosed(session);

        log.info("Retrieving agenda associated with session ID: {}", sessionId);
        Agenda agenda = session.getAgenda();

        log.info("Counting yes votes for agenda ID: {}", agenda.getId());
        long countYesVotes = countYesVotesByAgenda(agenda);

        log.info("Counting no votes for agenda ID: {}", agenda.getId());
        long countNoVotes = countNoVotesByAgenda(agenda);

        var result = new VoteResultResponse("Vote Result: ", countYesVotes, countNoVotes);
        log.info("Voting results calculated successfully for session ID: {}", sessionId);

        return result;
    }

    private long countNoVotesByAgenda(Agenda agenda) {
        return voteRepository.countByAgendaIdAndVoteOption(agenda.getId(), VoteOption.NO);
    }

    private long countYesVotesByAgenda(Agenda agenda) {
        return voteRepository.countByAgendaIdAndVoteOption(agenda.getId(), VoteOption.YES);
    }

    private VotingSession findExistingVotingSession(Long sessionId) {
        return votingSessionRepository.findById(sessionId).orElseThrow(
                () -> new BusinessException("Voting session not found with ID: " + sessionId, HttpStatus.NOT_FOUND));
    }

    private User findExistingUser(Long id) {
        return userRepository.findById(id).orElseThrow(
                () -> new BusinessException("User not found with ID: " + id, HttpStatus.NOT_FOUND));
    }

    private void checkIfUserAlreadyVoted(Long userId, Long sessionId) {
        boolean alreadyVoted = voteRepository.existsByUserIdAndAgenda_Id(userId, sessionId);
        if (alreadyVoted) {
            throw new BusinessException("User has already voted in this session.", HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }

    private void checkIfSessionIsClosed(VotingSession session) {
        if (LocalDateTime.now().isBefore(session.getEndTime())) {
            throw new BusinessException("Voting session is still in progress.", HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }
}