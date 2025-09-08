package github.io.api_voting_challenge.service;

import github.io.api_voting_challenge.dto.AdminUserRequest;
import github.io.api_voting_challenge.dto.AdminUserResponse;
import github.io.api_voting_challenge.dto.VoterRequest;
import github.io.api_voting_challenge.dto.VoterResponse;

public interface AdminServiceInterface {
    AdminUserResponse create(AdminUserRequest userRequest);
    AdminUserResponse update(Long id, AdminUserRequest updateUserRequest );
    AdminUserResponse getById(Long id);
    void delete(Long id);
    AdminUserResponse getByEmail(String email);
    AdminUserResponse getByCpf(String cpf);
    VoterResponse createVoter(VoterRequest voterRequest);
}
