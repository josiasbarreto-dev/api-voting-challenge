package github.io.api_voting_challenge.mapper;

import github.io.api_voting_challenge.dto.UserRequest;
import github.io.api_voting_challenge.dto.UserResponse;
import github.io.api_voting_challenge.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserResponse toDto(User user);

    @Mapping(target = "id", ignore = true)
    User toEntity(UserRequest userRequest);
}
