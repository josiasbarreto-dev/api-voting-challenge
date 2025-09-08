package github.io.api_voting_challenge.service.impl;

import github.io.api_voting_challenge.dto.VoteRequest;
import github.io.api_voting_challenge.dto.VoteResultResponse;
import github.io.api_voting_challenge.exception.*;
import github.io.api_voting_challenge.model.Agenda;
import github.io.api_voting_challenge.model.Vote;
import github.io.api_voting_challenge.model.VotingSession;
import github.io.api_voting_challenge.model.VotingUser;
import github.io.api_voting_challenge.model.enums.VoteOption;
import github.io.api_voting_challenge.repository.UserVotingRepository;
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
    private final UserVotingRepository userVotingRepository;
    private final VotingSessionRepository votingSessionRepository;
    private final VoteRepository voteRepository;

    @Override
    public void registerVote(Long sessionId, Long userId, VoteRequest voteRequest) {
        VotingUser user = userVotingRepository.findById(userId).orElseThrow(
                () -> new UserNotFoundException("User not found with ID: " + userId));
        VotingSession session = votingSessionRepository.findById(sessionId).orElseThrow(
                () -> new VotingSessionNotFoundException("Voting session not found with ID: " + sessionId));

        if(LocalDateTime.now().isAfter(session.getEndTime())){
            throw new VotingSessionClosedException("Voting session is closed for ID: " + sessionId);
        }

        boolean alreadyVoted = voteRepository.existsByUserIdAndAgenda_Id(userId, sessionId);
        if (alreadyVoted) {
            throw new UserAlreadyVotedException("User has already voted in this session.");
        }

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
}