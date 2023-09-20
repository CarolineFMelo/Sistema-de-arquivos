package main;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Arquivo {
	
	private String conteudo;
	private Diretorio dirPai;
	private Date dataCriacao;
	private String dataCriacaoFormatada;
	private String nome;
	private String permissao;
	
	public Arquivo(String nome, Diretorio pai) {
		this.nome = nome;
		this.dirPai = pai;
		this.permissao = "-rw-r--r--";
		this.dataCriacao 
        	= new Date(System.currentTimeMillis());
		SimpleDateFormat sdf 
        	= new SimpleDateFormat("MM dd HH:mm:ss");
		this.dataCriacaoFormatada 
        	= sdf.format(dataCriacao);
	}
	
	public void criaArquivo(String nome, Diretorio pai) {
		
	}
	
//	public Arquivo buscaArquivoPeloNome(String nomeDir, String nomeFile) {
//		Diretorio dir = null;
//		dir = dir.buscaDiretorioPeloNome(nomeDir);
//		if(dir.getArquivos() != null) {
//			
//		}
//        return null;
//	}
	
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
