package github.io.api_voting_challenge.service;

import github.io.api_voting_challenge.dto.AdminUserRequestDto;
import github.io.api_voting_challenge.dto.AdminUserResponseDto;
import github.io.api_voting_challenge.dto.VoterRequestDto;
import github.io.api_voting_challenge.dto.VoterResponseDto;

public interface AdminServiceInterface {
    AdminUserResponseDto create(AdminUserRequestDto userRequestDto);
    AdminUserResponseDto update(Long id, AdminUserRequestDto updateUserRequest );
    AdminUserResponseDto getById(Long id);
    void delete(Long id);
    AdminUserResponseDto getByEmail(String email);
    AdminUserResponseDto getByCpf(String cpf);
    VoterResponseDto createVoter(VoterRequestDto voterRequestDto);
}
