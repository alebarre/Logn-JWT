package com.br.login_jwt.controller;

import com.br.login_jwt.DTO.ClienteDTO;
import com.br.login_jwt.DTO.ClienteRequestDTO;
import com.br.login_jwt.DTO.EnderecoRequestDTO;
import com.br.login_jwt.model.Cliente;
import com.br.login_jwt.model.Endereco;
import com.br.login_jwt.service.ClienteService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/clientes")
public class ClienteController {

    private final ClienteService service;

    public ClienteController(ClienteService service) {
        this.service = service;
    }

    @GetMapping
    public List<ClienteDTO> listar() {
        return service.listar().stream().map(ClienteDTO::from).toList();
    }

    @GetMapping("/{id}")
    public ClienteDTO buscarPorId(@PathVariable Long id) {
        return ClienteDTO.from(service.buscarPorId(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ClienteDTO criar(@Valid @RequestBody ClienteRequestDTO request) {
        return ClienteDTO.from(service.criar(toEntity(request)));
    }

    @PutMapping("/{id}")
    public ClienteDTO atualizar(@PathVariable Long id, @Valid @RequestBody ClienteRequestDTO request) {
        return ClienteDTO.from(service.atualizar(id, toEntity(request)));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletar(@PathVariable Long id) {
        service.deletar(id);
    }

    private Cliente toEntity(ClienteRequestDTO request) {
        Cliente cliente = new Cliente();
        cliente.setNome(request.getNome());
        cliente.setSobrenome(request.getSobrenome());
        cliente.setTelefone(request.getTelefone());
        cliente.setEmail(request.getEmail());
        cliente.setEnderecos(request.getEnderecos().stream().map(this::toEntity).toList());
        return cliente;
    }

    private Endereco toEntity(EnderecoRequestDTO request) {
        Endereco endereco = new Endereco();
        endereco.setCep(normalizarCep(request.getCep()));
        endereco.setLogradouro(request.getLogradouro());
        endereco.setNumero(request.getNumero());
        endereco.setComplemento(request.getComplemento());
        endereco.setBairro(request.getBairro());
        endereco.setLocalidade(request.getLocalidade());
        endereco.setUf(request.getUf().toUpperCase());
        return endereco;
    }

    private String normalizarCep(String cep) {
        String digitos = cep.replace("-", "");
        return digitos.substring(0, 5) + "-" + digitos.substring(5);
    }
}
