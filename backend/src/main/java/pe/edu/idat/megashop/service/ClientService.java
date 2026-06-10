package pe.edu.idat.megashop.service;

import org.springframework.stereotype.Service;
import pe.edu.idat.megashop.dto.ClientRequest;
import pe.edu.idat.megashop.exception.ConflictException;
import pe.edu.idat.megashop.exception.NotFoundException;
import pe.edu.idat.megashop.model.Client;
import pe.edu.idat.megashop.repository.ClientRepository;

import java.time.Instant;
import java.util.List;

@Service
public class ClientService {
    private final ClientRepository clients;

    public ClientService(ClientRepository clients) {
        this.clients = clients;
    }

    public List<Client> list() {
        return clients.findAll();
    }

    public Client get(String id) {
        return clients.findById(id).orElseThrow(() -> new NotFoundException("Cliente no encontrado"));
    }

    public Client create(ClientRequest request) {
        if (clients.existsByEmail(request.email())) throw new ConflictException("El correo ya esta registrado");
        Client client = new Client();
        apply(client, request);
        client.setCreatedAt(Instant.now());
        client.setUpdatedAt(Instant.now());
        return clients.save(client);
    }

    public Client update(String id, ClientRequest request) {
        Client client = get(id);
        apply(client, request);
        client.setUpdatedAt(Instant.now());
        return clients.save(client);
    }

    public boolean delete(String id) {
        if (!clients.existsById(id)) return false;
        clients.deleteById(id);
        return true;
    }

    private void apply(Client client, ClientRequest request) {
        client.setNombre(request.nombre());
        client.setEmail(request.email());
        client.setTelefono(request.telefono());
        client.setDirecciones(request.direcciones() == null ? List.of() : request.direcciones());
    }
}
