package github.io.api_voting_challenge.mapper;

import github.io.api_voting_challenge.dto.AdminUserRequestDto;
import github.io.api_voting_challenge.dto.AdminUserResponseDto;
import github.io.api_voting_challenge.dto.VoterRequestDto;
import github.io.api_voting_challenge.dto.VoterResponseDto;
import github.io.api_voting_challenge.model.AdminUser;
import github.io.api_voting_challenge.model.VotingUser;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    AdminUserResponseDto toDto(AdminUser adminUser);
    AdminUser toEntity(AdminUserRequestDto adminUserRequestDto);

    VotingUser toEntity(VoterRequestDto voterRequestDto);
    VoterResponseDto toDto(VotingUser votingUser);
}
