package github.io.api_voting_challenge.client;

import github.io.api_voting_challenge.client.response.CpfStatusResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
        name = "CPFValidatorClient",
        url = "${external.cpf-api.url}",
        path = "/api/v1"
)
public interface CpfApiClient {
    @GetMapping("/users/{cpf}")
    ResponseEntity<CpfStatusResponse> validateCpf(@PathVariable("cpf") String cpf);
}
