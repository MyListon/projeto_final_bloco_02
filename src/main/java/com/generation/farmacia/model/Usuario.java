package com.generation.farmacia.model;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "tb_usuarios")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "O Nome é obrigatório!")
    @Size(min = 5, max = 100, message = "O Nome deve ter entre 5 e 100 caracteres")
    private String nome;

    @NotBlank(message = "O E-mail (Usuário) é obrigatório!")
    @Email(message = "O E-mail deve ser um endereço de e-mail válido!")
    @Size(max = 255)
    private String usuario;

    @NotBlank(message = "A Senha é obrigatória!")
    @Size(min = 8, message = "A Senha deve ter no mínimo 8 caracteres.")
    private String senha;
    
	private String foto;
    
    @NotNull(message = "A Data de Nascimento é obrigatória!")
	@Column(name = "data_nascimento")
	@JsonFormat(pattern = "yyyy-MM-dd")
	private LocalDate dataNascimento;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getUsuario() {
		return usuario;
	}

	public void setUsuario(String usuario) {
		this.usuario = usuario;
	}

	public String getSenha() {
		return senha;
	}

	public void setSenha(String senha) {
		this.senha = senha;
	}

	public String getFoto() {
		return foto;
	}

	public void setFoto(String foto) {
		this.foto = foto;
	}

	public LocalDate getDataNascimento() {
		return dataNascimento;
	}

	public void setDataNascimento(LocalDate dataNascimento) {
		this.dataNascimento = dataNascimento;
	}
    
    
    
}