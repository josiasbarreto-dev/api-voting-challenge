package github.io.api_voting_challenge.service.impl;

import github.io.api_voting_challenge.dto.request.VoteRequest;
import github.io.api_voting_challenge.dto.response.VoteResultResponse;
import github.io.api_voting_challenge.exception.*;
import github.io.api_voting_challenge.model.Agenda;
import github.io.api_voting_challenge.model.Vote;
import github.io.api_voting_challenge.model.VotingSession;
import github.io.api_voting_challenge.model.User;
import github.io.api_voting_challenge.model.enums.VoteOption;
import github.io.api_voting_challenge.repository.UserRepository;
import github.io.api_voting_challenge.repository.VoteRepository;
import github.io.api_voting_challenge.repository.VotingSessionRepository;
import github.io.api_voting_challenge.service.VoteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class VoteServiceImpl implements VoteService {
    private final UserRepository userRepository;
    private final VotingSessionRepository votingSessionRepository;
    private final VoteRepository voteRepository;

    @Override
    public void registerVote(Long sessionId, VoteRequest voteRequest) {
        log.info("Registering vote for session ID: {} by user ID: {}", sessionId, voteRequest.userId());
        log.info("Retrieving user with ID: {}", voteRequest.userId());
        User user = getUser(voteRequest.userId());

        log.info("Retrieving voting session with ID: {}", sessionId);
        VotingSession session = getVotingSession(sessionId);

        log.info("Validating if voting session is open for ID: {}", sessionId);
        validateVotingSessionIsOpen(session);

        log.info("Checking if user ID: {} has already voted in session ID: {}", voteRequest.userId(), sessionId);
        checkIfUserAlreadyVoted(voteRequest.userId(), sessionId);

        Vote vote = Vote.builder()
                .user(user)
                .agenda(session.getAgenda())
                .voteOption(voteRequest.voteOption())
                .build();

        voteRepository.save(vote);
        log.info("Vote registered successfully for session ID: {} by user ID: {}", sessionId, voteRequest.userId());
    }

    @Override
    public VoteResultResponse calculateVotingResult(Long sessionId) {
        log.info("Calculating voting results for session ID: {}", sessionId);
        VotingSession session = getSession(sessionId);

        log.info("Checking if voting session is closed for ID: {}", sessionId);
        checkIfSessionIsClosed(session);

        log.info("Retrieving agenda associated with session ID: {}", sessionId);
        Agenda agenda = session.getAgenda();

        log.info("Counting yes votes for agenda ID: {}", agenda.getId());
        long yesVotes = getYesVotes(agenda);

        log.info("Counting no votes for agenda ID: {}", agenda.getId());
        long noVotes = getNoVotes(agenda);

        var result = new VoteResultResponse("Vote Result: ", yesVotes, noVotes);
        log.info("Voting results calculated successfully for session ID: {}", sessionId);

        return result;
    }

    private long getNoVotes(Agenda agenda) {
        return voteRepository.countByAgendaIdAndVoteOption(agenda.getId(), VoteOption.NO);
    }

    private long getYesVotes(Agenda agenda) {
        return voteRepository.countByAgendaIdAndVoteOption(agenda.getId(), VoteOption.YES);
    }

    private VotingSession getSession(Long sessionId) {
        return votingSessionRepository.findById(sessionId).orElseThrow(
                () -> new VotingSessionNotFoundException("Voting session not found with ID: " + sessionId));
    }

    private User getUser(Long id) {
        return userRepository.findById(id).orElseThrow(
                () -> new UserNotFoundException("User not found with ID: " + id));
    }

    private VotingSession getVotingSession(Long id) {
        return getSession(id);
    }

    private void validateVotingSessionIsOpen(VotingSession session) {
        if(LocalDateTime.now().isAfter(session.getEndTime())){
            throw new VotingSessionClosedException("Voting session is closed for ID: " + session.getId());
        }
    }

    private void checkIfUserAlreadyVoted(Long userId, Long sessionId) {
        boolean alreadyVoted = voteRepository.existsByUserIdAndAgenda_Id(userId, sessionId);
        if (alreadyVoted) {
            throw new UserAlreadyVotedException("User has already voted in this session.");
        }
    }

    private void checkIfSessionIsClosed(VotingSession session) {
        if (LocalDateTime.now().isBefore(session.getEndTime())) {
            throw new VotingSessionInProgressException("Voting session is still in progress.");
        }
    }
}