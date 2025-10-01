package github.io.api_voting_challenge.controller;

import github.io.api_voting_challenge.controller.docs.UserControllerDocs;
import github.io.api_voting_challenge.dto.UserRequest;
import github.io.api_voting_challenge.dto.UserResponse;
import github.io.api_voting_challenge.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserController implements UserControllerDocs {
    private final UserService userService;

    @Override
    @PostMapping
    public ResponseEntity<UserResponse> create(@RequestBody @Valid UserRequest userRequest) {
        log.info("Received request to create user: {}", userRequest.name());
        UserResponse userResponse = userService.create(userRequest);
        log.info("User with id {} created successfully.", userResponse.id());
        return ResponseEntity.status(HttpStatus.CREATED).body(userResponse);
    }

    @Override
    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> update(@PathVariable Long id, @RequestBody @Valid UserRequest userRequest) {
        log.info("Received request to update user with id {}: {}", id, userRequest.name());
        UserResponse userResponse = userService.update(id, userRequest);
        log.info("User with id {} updated successfully.", userResponse.id());
        return ResponseEntity.ok(userResponse);
    }

    @Override
    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getById(@PathVariable Long id) {
        log.info("Received request to get user with id: {}", id);
        UserResponse userResponse = userService.getById(id);
        log.info("User with id {} retrieved successfully.", userResponse.id());
        return ResponseEntity.ok(userResponse);
    }

    @Override
    @GetMapping
    public ResponseEntity<UserResponse> getByCpf(@RequestParam String cpf) {
        log.info("Received request to get user with CPF.");
        UserResponse userResponse = userService.getByCpf(cpf);
        log.info("User successfully recovered by CPF: {}", userResponse.name());
        return ResponseEntity.ok(userResponse);
    }

    @Override
    @GetMapping("/all")
    public ResponseEntity<Page<UserResponse>> list(@ParameterObject Pageable pageable) {
        log.info("Received request to get all users - page={}, size={}, sort={}",
                pageable.getPageNumber(), pageable.getPageSize(), pageable.getSort());
        Page<UserResponse> users = userService.getAll(pageable);
        log.info("Users retrieved successfully: {}", users.getTotalElements());
        return ResponseEntity.ok(users);
    }

    @Override
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        log.info("Received request to delete user with id: {}", id);
        userService.delete(id);
        log.info("User with id {} deleted successfully.", id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}