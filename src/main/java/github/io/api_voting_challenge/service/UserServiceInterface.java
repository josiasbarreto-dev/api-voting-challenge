package github.io.api_voting_challenge.service;

import github.io.api_voting_challenge.model.User;

public interface UserServiceInterface {
    User create(User user);
    User update(Long id, User user);
    User getById(Long id);
    void delete(Long id);
    User getByEmail(String email);
    User getByCpf(String cpf);
}
