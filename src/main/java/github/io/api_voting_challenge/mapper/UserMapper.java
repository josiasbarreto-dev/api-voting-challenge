package github.io.api_voting_challenge.mapper;

import github.io.api_voting_challenge.dto.AdminUserRequestDto;
import github.io.api_voting_challenge.dto.AdminUserResponseDto;
import github.io.api_voting_challenge.dto.VoterRequestDto;
import github.io.api_voting_challenge.dto.VoterResponseDto;
import github.io.api_voting_challenge.model.AdminUser;
import github.io.api_voting_challenge.model.VotingUser;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {
    AdminUserResponseDto toDto(AdminUser adminUser);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "role", ignore = true)
    AdminUser toEntity(AdminUserRequestDto adminUserRequestDto);

    VoterResponseDto toDto(VotingUser votingUser);

    @Mapping(target = "id", ignore = true)
    VotingUser toEntity(VoterRequestDto voterRequestDto);
}
