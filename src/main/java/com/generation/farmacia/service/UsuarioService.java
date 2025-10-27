package com.generation.farmacia.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.generation.farmacia.model.Usuario;
import com.generation.farmacia.model.UsuarioLogin;
import com.generation.farmacia.repository.UsuarioRepository;
import com.generation.farmacia.security.JwtService;

import java.time.LocalDate;
import java.time.Period;

@Service
public class UsuarioService {

	@Autowired
	private UsuarioRepository usuarioRepository;

	@Autowired
	private JwtService jwtService;

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private PasswordEncoder passwordEncoder;

	private void validarMaioridade(LocalDate dataNascimento) {
		if (dataNascimento == null) {
			return;
		}

		Period periodo = Period.between(dataNascimento, LocalDate.now());
		if (periodo.getYears() < 18) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
					"O usuário deve ter 18 anos ou mais para se cadastrar!");
		}
	}

	public List<Usuario> getAll() {
		return usuarioRepository.findAll();
	}

	public Optional<Usuario> getById(Long id) {
		return usuarioRepository.findById(id);
	}

	public Optional<Usuario> cadastrarUsuario(Usuario usuario) {

		if (usuarioRepository.findByUsuario(usuario.getUsuario()).isPresent()) {
			return Optional.empty();
		}

		if (usuario.getDataNascimento() == null || !isAdult(usuario.getDataNascimento())) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
					"Cadastro permitido apenas para maiores de 18 anos");
		}

		usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));
		usuario.setId(null);

		return Optional.of(usuarioRepository.save(usuario));
	}

	public Optional<Usuario> atualizarUsuario(Usuario usuario) {

		if (!usuarioRepository.findById(usuario.getId()).isPresent()) {
			return Optional.empty();
		}

		Optional<Usuario> usuarioExistente = usuarioRepository.findByUsuario(usuario.getUsuario());

		if (usuarioExistente.isPresent() && !usuarioExistente.get().getId().equals(usuario.getId())) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Usuário já existe!", null);
		}

		// Verifica se é maior de idade ao atualizar
		if (usuario.getDataNascimento() == null || !isAdult(usuario.getDataNascimento())) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
					"Atualização permitida apenas para maiores de 18 anos");
		}

		usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));
		return Optional.of(usuarioRepository.save(usuario));
	}

	public Optional<UsuarioLogin> autenticarUsuario(Optional<UsuarioLogin> usuarioLogin) {

		if (!usuarioLogin.isPresent()) {
			return Optional.empty();
		}

		UsuarioLogin login = usuarioLogin.get();

		try {

			authenticationManager
					.authenticate(new UsernamePasswordAuthenticationToken(login.getUsuario(), login.getSenha()));

			return usuarioRepository.findByUsuario(login.getUsuario()).filter(u -> isAdult(u.getDataNascimento()))
					.map(usuario -> construirRespostaLogin(login, usuario));

		} catch (Exception e) {

			return Optional.empty();
		}
	}

	private UsuarioLogin construirRespostaLogin(UsuarioLogin usuarioLogin, Usuario usuario) {

		usuarioLogin.setId(usuario.getId());
		usuarioLogin.setNome(usuario.getNome());
		usuarioLogin.setFoto(usuario.getFoto());
		usuarioLogin.setSenha("");
		usuarioLogin.setToken(gerarToken(usuario.getUsuario()));
		return usuarioLogin;

	}

	private String gerarToken(String usuario) {
		return "Bearer " + jwtService.generateToken(usuario);
	}

	private boolean isAdult(LocalDate birthDate) {
		if (birthDate == null)
			return false;
		return Period.between(birthDate, LocalDate.now()).getYears() >= 18;
	}
}