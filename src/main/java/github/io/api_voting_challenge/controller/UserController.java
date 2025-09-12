package github.io.api_voting_challenge.controller;

import github.io.api_voting_challenge.controller.docs.UserControllerDocs;
import github.io.api_voting_challenge.dto.UserRequest;
import github.io.api_voting_challenge.dto.UserResponse;
import github.io.api_voting_challenge.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserController implements UserControllerDocs {
    private final UserService userService;

    @Override
    @PostMapping
    public ResponseEntity<UserResponse> create(@RequestBody @Valid UserRequest userRequest) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.create(userRequest));
    }

    @Override
    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> update(@PathVariable Long id, @RequestBody @Valid UserRequest userRequest) {
        return ResponseEntity.ok(userService.update(id, userRequest));
    }

    @Override
    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getById(id));
    }

    @Override
    @GetMapping
    public ResponseEntity<UserResponse> getByCpf(@RequestParam String cpf) {
        return ResponseEntity.ok(userService.getByCpf(cpf));
    }

    @Override
    @GetMapping("/all")
    public ResponseEntity<Page<UserResponse>> list(@ParameterObject Pageable pageable) {
        Page<UserResponse> users = userService.getAll(pageable);
        return ResponseEntity.ok(users);
    }

    @Override
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        userService.delete(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}