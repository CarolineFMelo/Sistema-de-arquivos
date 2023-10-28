package main;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Arquivo implements Cloneable {
	
	private String conteudo;
	private Diretorio dirPai;
	private Date dataCriacao;
	private String dataCriacaoFormatada;
	private String nome;
	private String permissao;
	
	public Arquivo(String nome, Diretorio pai, String conteudo) {
		this.nome = nome;
		this.dirPai = pai;
		this.permissao = "-rw-r--r--";
		this.dataCriacao 
        	= new Date(System.currentTimeMillis());
		SimpleDateFormat sdf 
        	= new SimpleDateFormat("MM dd HH:mm:ss");
		this.dataCriacaoFormatada 
        	= sdf.format(dataCriacao);
		this.conteudo = conteudo;
	}
	
	@Override
	public Arquivo clone() throws CloneNotSupportedException {
		return (Arquivo) super.clone();
	}
	
	public Diretorio getDirPai() {
		return dirPai;
	}

	public void setDirPai(Diretorio dirPai) {
		this.dirPai = dirPai;
	}

	public String getConteudo() {
		return conteudo;
	}

	public void setConteudo(String conteudo) {
		this.conteudo = conteudo;
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
