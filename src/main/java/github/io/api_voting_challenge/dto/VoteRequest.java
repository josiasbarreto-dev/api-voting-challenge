package github.io.api_voting_challenge.dto;

import github.io.api_voting_challenge.model.enums.VoteOption;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@Builder
public record VoteRequest(
        @NotNull(message = "User ID cannot be null.")
        Long userId,

        @NotNull(message = "Vote option cannot be null.")
        VoteOption voteOption
){}