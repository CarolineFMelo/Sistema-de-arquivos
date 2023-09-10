/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 *
 * @author Douglas
 */
public class Diretorio {
    
    private Diretorio pai;
    private ArrayList<Diretorio> filhos;
    private ArrayList<Arquivo> arquivos;
    private Date dataCriacao;
    private String dataCriacaoFormatada;
    private String nome;
    private String permissao;
    
    public Diretorio(String nome, Diretorio pai) {
    	this.nome = nome;
        this.pai = pai;
        this.dataCriacao 
                = new Date(System.currentTimeMillis());
        SimpleDateFormat sdf 
                = new SimpleDateFormat("MM dd HH:mm:ss");
        this.dataCriacaoFormatada 
                = sdf.format(dataCriacao);
        this.filhos = new ArrayList<>();
        this.arquivos = new ArrayList<>();
    }
    
    public void criaDiretorioFilho(String nome, Diretorio pai) {
    	this.filhos.add(new Diretorio(nome, pai));
    }
   
    public Diretorio buscaDiretorioPeloNome(String nome) {
        if (nome.equals(".")){
            return this;
        }
        else if (nome.equals("..")){
            return this.pai;
        }
        else {
            for (Diretorio dir : filhos) {
                if (dir.getNome().equals(nome)) {
                    return dir;
                }
            }
        }
        return null;
    }

    public Diretorio getPai() {
        return pai;
    }

    public void setPai(Diretorio pai) {
        this.pai = pai;
    }

    public ArrayList<Diretorio> getFilhos() {
        return filhos;
    }

    public void setFilhos(ArrayList<Diretorio> filhos) {
        this.filhos = filhos;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getPermissao() {
        return permissao;
    }

    public void setPermissao(String permissao) {
        this.permissao = permissao;
    }
    
    
    
}