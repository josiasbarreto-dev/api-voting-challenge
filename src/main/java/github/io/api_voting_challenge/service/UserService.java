package github.io.api_voting_challenge.service;

import github.io.api_voting_challenge.dto.request.UserRequest;
import github.io.api_voting_challenge.dto.response.UserResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {
    UserResponse create(UserRequest userRequest);
    UserResponse update(Long id, UserRequest updateUserRequest );
    UserResponse getById(Long id);
    UserResponse getByCpf(String cpf);
    Page<UserResponse> getAll(Pageable pageable);
    void delete(Long id);
}
