package main;

import java.util.Date;

public class Arquivo {

	private String nome;
	private Date dataCriacao;
	private String dataCriacaoFormatada;
	private String permissao;
	
	public void criarArquivo() {
		
	}
	
	public String getNome() {
		return nome;
	}
	
	public void setNome(String nome) {
		this.nome = nome;
	}
	
	public Date getDataCriacao() {
		return dataCriacao;
	}
	
	public void setDataCriacao(Date dataCriacao) {
		this.dataCriacao = dataCriacao;
	}
	
	public String getDataCriacaoFormatada() {
		return dataCriacaoFormatada;
	}
	
	public void setDataCriacaoFormatada(String dataCriacaoFormatada) {
		this.dataCriacaoFormatada = dataCriacaoFormatada;
	}
	
	public String getPermissao() {
		return permissao;
	}
	
	public void setPermissao(String permissao) {
		this.permissao = permissao;
	}
	
}
