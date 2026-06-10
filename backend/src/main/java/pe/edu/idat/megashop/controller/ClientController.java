package pe.edu.idat.megashop.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import pe.edu.idat.megashop.dto.ClientRequest;
import pe.edu.idat.megashop.model.Client;
import pe.edu.idat.megashop.service.ClientService;
import pe.edu.idat.megashop.web.ApiResponse;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/clientes")
public class ClientController {
    private final ClientService clients;

    public ClientController(ClientService clients) {
        this.clients = clients;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<List<Client>> list() {
        return ApiResponse.of(clients.list());
    }

    @GetMapping("/{id}")
    public ApiResponse<Client> get(@PathVariable String id) {
        return ApiResponse.of(clients.get(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Client> create(@Valid @RequestBody ClientRequest request) {
        return ApiResponse.of(clients.create(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Client> update(@PathVariable String id, @Valid @RequestBody ClientRequest request) {
        return ApiResponse.of(clients.update(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Map<String, Boolean>> delete(@PathVariable String id) {
        return ApiResponse.of(Map.of("deleted", clients.delete(id)));
    }
}
