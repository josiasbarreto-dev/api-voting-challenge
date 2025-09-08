package github.io.api_voting_challenge.mapper;

import github.io.api_voting_challenge.dto.AdminUserRequest;
import github.io.api_voting_challenge.dto.AdminUserResponse;
import github.io.api_voting_challenge.dto.VoterRequest;
import github.io.api_voting_challenge.dto.VoterResponse;
import github.io.api_voting_challenge.model.AdminUser;
import github.io.api_voting_challenge.model.VotingUser;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {
    AdminUserResponse toDto(AdminUser adminUser);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "role", ignore = true)
    AdminUser toEntity(AdminUserRequest adminUserRequest);

    VoterResponse toDto(VotingUser votingUser);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "role", ignore = true)
    VotingUser toEntity(VoterRequest voterRequest);
}
