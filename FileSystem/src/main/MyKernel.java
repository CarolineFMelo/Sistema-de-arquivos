package main;

import java.util.ArrayList;
import java.util.Stack;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
import operatingSystem.Kernel;

/**
 *
 * @author Caroline
 */
public class MyKernel implements Kernel {
	
	public Diretorio dirRaiz = new Diretorio("/", null);
	public Diretorio dirAtual;
	public Diretorio dirAntigo;

    public MyKernel() {
    	this.dirRaiz = new Diretorio("/", null);
    	this.dirAtual = null;
    	this.dirAntigo = null;
    }
    
    boolean isPathRelative(String path) {
    	if (!path.equals("")) {
	    	if (path.charAt(0) == '/') {
	    		return false;
	    	}
    	}
    	return true;
    }
    
    String getCaminhoCompleto(Diretorio dir) {
    	Stack<String> pilha = new Stack<String>();
    	while(!dir.getNome().equals("/")) {
    		pilha.push(dir.getNome());
    		dir = dir.getPai();
    	}
    	String caminho = "/";
    	int pilhaSize = pilha.size();
    	for(int i = 0; i < pilhaSize; i++) {
    		caminho = caminho.concat(pilha.pop() + "/");
    	}
    	return caminho;
    }

    public String ls(String parameters) {
        //variável result deverá conter o que vai ser impresso na tela após comando do usuário
        String result = "";
        System.out.println("Chamada de Sistema: ls");
        System.out.println("\tParametros: " + parameters);

        //início da implementação do aluno
    	String[] path = parameters.split("/");
    	Diretorio curDir = null;
    	String[] currentDir = operatingSystem.fileSystem.FileSytemSimulator.currentDir.split("/");
    	
    	if(isPathRelative(parameters)) {
    		String[] index = operatingSystem.fileSystem.FileSytemSimulator.currentDir.split("/");
    		curDir = dirRaiz;
    		for(int i = 1; i < index.length; i++) {
    			curDir = curDir.buscaDiretorioPeloNome(index[i]);
    		}
    		if (!parameters.equals("")) {
    			for(int i = 0; i < path.length; i++) {
        			curDir = curDir.buscaDiretorioPeloNome(path[i]);
        		}
        	}
    	}
    	else {
    		curDir = dirRaiz;
    		for(int i = 1; i < path.length; i++) {
    			curDir = curDir.buscaDiretorioPeloNome(path[i]);
    		}
    	}
    	
    	for(int i = 0; i < curDir.getFilhos().size(); i++) {
    		result = result + curDir.getFilhos().get(i).getNome() + "\n";
    	}
        
        //fim da implementação do aluno
        return result;
    }

    public String mkdir(String parameters) {
    	//variável result deverá conter o que vai ser impresso na tela após comando do usuário
        String result = "";
        System.out.println("Chamada de Sistema: mkdir"); 
        System.out.println("\tParametros: " + parameters);
        
    	String[] path = parameters.split("/");
    	Diretorio curDir = null;
    	
    	if(isPathRelative(parameters)) {
    		String[] index = operatingSystem.fileSystem.FileSytemSimulator.currentDir.split("/");
    		curDir = dirRaiz;
    		for(int i = 1; i < index.length; i++) {
    			curDir = curDir.buscaDiretorioPeloNome(index[i]);
    		}
    	}
    	else {
    		curDir = dirRaiz;
    	}
    
    	for(int i = 0; i < path.length; i++) {
//    		if (curDir.getFilhos().get(j).getNome().equals(in[i])) {
    		if((path[i] == "") || (path[i].contains("."))) {
    			continue;
    		}
			if(curDir.buscaDiretorioPeloNome(path[i]) != null) {
				if(i == path.length - 1) {
					result = "mkdir: " + path[i] + ": Diretorio já existe (Nenhum diretorio foi criado).";
				}
				break;
			}
    		else {
//    			curDir.getFilhos().add(new Diretorio(path[i], curDir));
    			curDir.criaDiretorioFilho(path[i], curDir);
			}
			curDir = curDir.buscaDiretorioPeloNome(path[i]);
    	}
    	
        return result;
    }

    public String cd(String parameters) {
    	//variável result deverá conter o que vai ser impresso na tela após comando do usuário
    	String result = "";
        String currentDir = operatingSystem.fileSystem.FileSytemSimulator.currentDir;
        System.out.println("Chamada de Sistema: cd");
        System.out.println("\tParametros: " + parameters);
        
        //inicio da implementacao do aluno
        String[] cDir = currentDir.split("/");
        String[] path = parameters.split("/");
    	Diretorio curDir = dirRaiz;
    	
    	//encontra o diretório atual
    	for(int i = 1; i < cDir.length; i++) {
    		curDir = curDir.buscaDiretorioPeloNome(cDir[i]);
    	}
    	
    	//verifica se diretório do parâmetro existe
    	if(!path[0].contains(".")) {
//    	   		if (curDir.getFilhos().get(j).getNome().equals(in[i])) {
    		for(int i = 0; i < path.length; i++) {
    			if(curDir.buscaDiretorioPeloNome(path[i]) != null) {
    				curDir = curDir.buscaDiretorioPeloNome(path[i]);
    			}
    	    	else {
    	   			result = path[i] + ": Diretório não existe.";
    	   			return result;
    			}
       		}
    	}
    	
        //indica o novo diretório
		for(int i = 0; i < path.length; i++) {
			if(path[i].equals("..")) {
	    		curDir = curDir.getPai();
	    		currentDir = getCaminhoCompleto(curDir);
	    	}
			else if(path[i].equals(".")) {
				continue;
	    	}
			else {
				if (currentDir.charAt(currentDir.length()-1) == '/') {
					currentDir = currentDir.concat(path[i]);
				}
				else {
					currentDir = currentDir.concat("/" + path[i]);
				}
    		}
    	}
		
        //setando parte gráfica do diretorio atual
        operatingSystem.fileSystem.FileSytemSimulator.currentDir = currentDir;

        //fim da implementacao do aluno
        return result;
    }

    public String rmdir(String parameters) {
        //variavel result deverah conter o que vai ser impresso na tela apos comando do usuário
        String result = "";
        System.out.println("Chamada de Sistema: rmdir");
        System.out.println("\tParametros: " + parameters);

        //inicio da implementacao do aluno
        String[] path = parameters.split("/");
    	Diretorio curDir = null;
    	
    	if(isPathRelative(parameters)) {
    		String[] index = operatingSystem.fileSystem.FileSytemSimulator.currentDir.split("/");
    		curDir = dirRaiz;
    		for(int i = 1; i < index.length; i++) {
    			curDir = curDir.buscaDiretorioPeloNome(index[i]);
    		}
    		if (!parameters.equals("")) {
    			for(int i = 0; i < path.length; i++) {
        			curDir = curDir.buscaDiretorioPeloNome(path[i]);
        		}
        	}
    	}
    	else {
    		curDir = dirRaiz;
    		for(int i = 1; i < path.length; i++) {
    			curDir = curDir.buscaDiretorioPeloNome(path[i]);
    		}
    	}

		if(curDir != null) {
			if(curDir.getFilhos().isEmpty()) {
				curDir.getPai().getFilhos().remove(curDir);
			}
			else {
				result = "rmdir: Diretório: " + parameters + " possui arquivos e/ou diretórios. (Nada foi removido)";
			}
		}
		else {
			result = "rmdir: Diretório: " + path + " não existe. (Nada foi removido)";
			}
    	
        //fim da implementacao do aluno
        return result;
    }

    public String cp(String parameters) {
        //variavel result deverah conter o que vai ser impresso na tela apos comando do usuário
        String result = "";
        System.out.println("Chamada de Sistema: cp");
        System.out.println("\tParametros: " + parameters);

        //inicio da implementacao do aluno
        //fim da implementacao do aluno
        return result;
    }

    public String mv(String parameters) {
        //variavel result deverah conter o que vai ser impresso na tela apos comando do usuário
        String result = "";
        System.out.println("Chamada de Sistema: mv");
        System.out.println("\tParametros: " + parameters);

        //inicio da implementacao do aluno
        String[] path = parameters.split(" ");
    	Diretorio curDir = null;
    	String[] currentDir = operatingSystem.fileSystem.FileSytemSimulator.currentDir.split("/");

//    	if(isPathRelative(parameters)) {
//    		String[] index = operatingSystem.fileSystem.FileSytemSimulator.currentDir.split("/");
//    		curDir = dirRaiz;
//    		for(int i = 1; i < index.length; i++) {
//    			curDir = curDir.buscaDiretorioPeloNome(index[i]);
//    		}
//    		if (!parameters.equals("")) {
//    			for(int i = 0; i < path.length; i++) {
//        			curDir = curDir.buscaDiretorioPeloNome(path[i]);
//        		}
//        	}
//    	}
//    	else {
//    		curDir = dirRaiz;
//    		for(int i = 1; i < path.length; i++) {
//    			curDir = curDir.buscaDiretorioPeloNome(path[i]);
//    		}
//    	}
    	
    	String origem = path[0];
    	String destino = path[1];
    	Diretorio dirOrigem = dirRaiz;
    	Diretorio dirDestino = dirRaiz;
    	Diretorio dirAux = null;
    	
    	for(int i = 0; i < origem.length(); i++) {
    		dirOrigem = dirOrigem.buscaDiretorioPeloNome(origem);
    		
    	}
    	
    	for(int i = 0; i < destino.length(); i++) {
    		dirDestino = dirDestino.buscaDiretorioPeloNome(destino);
    	}
    	
    	for(int i = 0; i < destino.length(); i++) {
    		if(dirOrigem.getPai().getFilhos().get(i) == dirOrigem) {
    			dirAux = dirOrigem.getPai().getFilhos().remove(i);
    		}
    	}
    	
    	dirDestino.getFilhos().add(dirAux);
    	
        //fim da implementacao do aluno
        return result;
    }

    public String rm(String parameters) {
        //variavel result deverah conter o que vai ser impresso na tela apos comando do usuário
        String result = "";
        System.out.println("Chamada de Sistema: rm");
        System.out.println("\tParametros: " + parameters);

        //inicio da implementacao do aluno
        //fim da implementacao do aluno
        return result;
    }

    public String chmod(String parameters) {
        //variavel result deverah conter o que vai ser impresso na tela apos comando do usuário
        String result = "";
        System.out.println("Chamada de Sistema: chmod");
        System.out.println("\tParametros: " + parameters);

        //inicio da implementacao do aluno
        //fim da implementacao do aluno
        return result;
    }

    public String createfile(String parameters) {
        //variavel result deverah conter o que vai ser impresso na tela apos comando do usuário
        String result = "";
        System.out.println("Chamada de Sistema: createfile");
        System.out.println("\tParametros: " + parameters);

        //inicio da implementacao do aluno
        //fim da implementacao do aluno
        return result;
    }

    public String cat(String parameters) {
        //variavel result deverah conter o que vai ser impresso na tela apos comando do usuário
        String result = "";
        System.out.println("Chamada de Sistema: cat");
        System.out.println("\tParametros: " + parameters);

        //inicio da implementacao do aluno
        //fim da implementacao do aluno
        return result;
    }

    public String batch(String parameters) {
        //variavel result deverah conter o que vai ser impresso na tela apos comando do usuário
        String result = "";
        System.out.println("Chamada de Sistema: batch");
        System.out.println("\tParametros: " + parameters);

        //inicio da implementacao do aluno
        //fim da implementacao do aluno
        return result;
    }

    public String dump(String parameters) {
        //variavel result deverah conter o que vai ser impresso na tela apos comando do usuário
        String result = "";
        System.out.println("Chamada de Sistema: dump");
        System.out.println("\tParametros: " + parameters);

        //inicio da implementacao do aluno
        //fim da implementacao do aluno
        return result;
    }

    public String info() {
        //variavel result deverah conter o que vai ser impresso na tela apos comando do usuário
        String result = "";
        System.out.println("Chamada de Sistema: info");
        System.out.println("\tParametros: sem parametros");

        //nome do aluno
        String name = "Caroline Melo";
        //numero de matricula
        String registration = "2021.11.02.00.09";
        //versao do sistema de arquivos
        String version = "0.1";

        result += "Nome do Aluno:        " + name;
        result += "\nMatricula do Aluno:   " + registration;
        result += "\nVersao do Kernel:     " + version;

        return result;
    }

}
