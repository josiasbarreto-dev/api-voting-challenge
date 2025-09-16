package github.io.api_voting_challenge.service.impl;

import github.io.api_voting_challenge.dto.VoteRequest;
import github.io.api_voting_challenge.dto.VoteResultResponse;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Transactional
@RequiredArgsConstructor
public class VoteServiceImpl implements VoteService {
    private final UserRepository userRepository;
    private final VotingSessionRepository votingSessionRepository;
    private final VoteRepository voteRepository;

    @Override
    public void registerVote(Long sessionId, VoteRequest voteRequest) {
        User user = getUser(voteRequest.userId());
        VotingSession session = getVotingSession(sessionId);

        validateVotingSessionIsOpen(session);
        checkIfUserAlreadyVoted(voteRequest.userId(), sessionId);

        Vote vote = Vote.builder()
                .user(user)
                .agenda(session.getAgenda())
                .voteOption(voteRequest.voteOption())
                .build();

        voteRepository.save(vote);
    }

    @Override
    public VoteResultResponse calculateVotingResult(Long sessionId) {
        VotingSession session = votingSessionRepository.findById(sessionId).orElseThrow(
                () -> new VotingSessionNotFoundException("Voting session not found with ID: " + sessionId));
        if (LocalDateTime.now().isBefore(session.getEndTime())) {
            throw new VotingSessionInProgressException("Voting session is still in progress.");
        }

        Agenda agenda = session.getAgenda();
        long yesVotes = voteRepository.countByAgendaIdAndVoteOption(agenda.getId(), VoteOption.YES);
        long noVotes = voteRepository.countByAgendaIdAndVoteOption(agenda.getId(), VoteOption.NO);

        return new VoteResultResponse("Vote Result: ", yesVotes, noVotes);
    }

    private User getUser(Long id) {
        return userRepository.findById(id).orElseThrow(
                () -> new UserNotFoundException("User not found with ID: " + id));
    }

    private VotingSession getVotingSession(Long id) {
        return votingSessionRepository.findById(id).orElseThrow(
                () -> new VotingSessionNotFoundException("Voting session not found with ID: " + id));
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
}