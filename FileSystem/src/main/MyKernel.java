package main;

import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
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
	public String regexArq = "[a-zA-Z]+\\.[a-zA-Z]+";

    public MyKernel() {
    	this.dirRaiz = new Diretorio("/", null);
    	this.dirAtual = null;
    	this.dirAntigo = null;
    }
    
    //verifica se há flags na entrada do programa
    boolean argParser(String parameters, String flag) {
    	if(parameters.contains("-" + flag)) {
    		return true;
    	}
    	else {
    		return false;
    	}
    }
    
    //pega o caminho completo do path
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
    	String result = "";
        String[] currentDir = operatingSystem.fileSystem.FileSytemSimulator.currentDir.split("/");
        String[] path = null;
        String[] param = parameters.split(" ");
    	Diretorio curDir = dirRaiz;
    	
    	// seta flags opcionais - modificam o fluxo do programa
        boolean listMode = false;
        if (param[0].equals("-l")) listMode = true; // usamos a posição 0 porque o -l só pode aparecer nesta posição
    	
        //verifica parâmetros
    	//path = param[param.length-1].split("/");
    	
    	if(param.length == 2) {
    		path = param[1].split("/");
    	}
    	else if((param.length == 1) && (!argParser(param[0], "l"))) {
    		path = param[0].split("/");
    	}
    	else {
    		path = "".split("/");
    	}
        
    	//encontra o diretório atual
    	for(int i = 1; i < currentDir.length; i++) {
    		curDir = curDir.buscaDiretorioPeloNome(currentDir[i]);
    	}
    	
    	//encontra caminho do parâmetro
	    for(int i = 0; i < path.length; i++) {
	    	if(path[i] == "") {
    			continue;
    		}
	   		if(path[i].contains(".")) {
	    		curDir = curDir.buscaDiretorioPeloNome(path[i]);
	   			continue;
	   		}
	   		if(curDir.buscaDiretorioPeloNome(path[i]) != null) {
	    		if(i == path.length - 1) {
	    			curDir = curDir.buscaDiretorioPeloNome(path[i]);
	    			break;
		   		}
	   		}
	   		else {        			
       			result = path[i].concat(": Diretório não existe.");
	   			return result;
    		}
    		curDir = curDir.buscaDiretorioPeloNome(path[i]);
    	}
     	
     	//lista conteúdo do diretório
	    if(listMode) {
     		for(int i = 0; i < curDir.getFilhos().size(); i++) {
     			result = result.concat(curDir.getFilhos().get(i).getPermissao() + " " +
     					curDir.getFilhos().get(i).getDataCriacaoFormatada() + " " +
     					curDir.getFilhos().get(i).getNome() + "\n");
         	}
     		for(int i = 0; i < curDir.getArquivos().size(); i++) {
     			result = result.concat(curDir.getArquivos().get(i).getPermissao() + " " +
     					curDir.getArquivos().get(i).getDataCriacaoFormatada() + " " +
     					curDir.getArquivos().get(i).getNome() + "\n");
         	}
     	} else {
     		for(int i = 0; i < curDir.getFilhos().size(); i++) {
         		result = result.concat(curDir.getFilhos().get(i).getNome()) + " ";
         	}
     		for(int i = 0; i < curDir.getArquivos().size(); i++) {
         		result = result.concat(curDir.getArquivos().get(i).getNome()) + " ";
         	}
     	}
     	
        return result;
    }

    public String mkdir(String parameters) {
    	String result = "";
        String[] currentDir = operatingSystem.fileSystem.FileSytemSimulator.currentDir.split("/");
        String[] path = parameters.split("/");
    	Diretorio curDir = dirRaiz;
    	
    	//encontra o diretório atual
    	for(int i = 1; i < currentDir.length; i++) {
    		curDir = curDir.buscaDiretorioPeloNome(currentDir[i]);
    	}
    	
    	//verifica existência do diretório e cria
    	for(int i = 0; i < path.length; i++) {
    		if(path[i] == "") {
    			//caminho absoluto
    			curDir = dirRaiz;
    			continue;
    		}
    		if(path[i].contains(".")) {
    			curDir = curDir.buscaDiretorioPeloNome(path[i]);
    			continue;
    		}
    		if(curDir.buscaDiretorioPeloNome(path[i]) != null) {
	    		if(i == path.length - 1) {
	    			result = "mkdir: " + path[i] + ": Diretorio já existe (Nenhum diretorio foi criado).";
	    			break;
	   			}
   			}
       		else {        			
       			curDir.criaDiretorioFilho(path[i], curDir);
    		}
    		curDir = curDir.buscaDiretorioPeloNome(path[i]);
    	}
    	
        return result;
    }

    public String cd(String parameters) {
    	String result = "";
    	String currentDir = operatingSystem.fileSystem.FileSytemSimulator.currentDir;
        String[] cDir = currentDir.split("/");
        String[] path = parameters.split("/");
    	Diretorio curDir = dirRaiz;
    	dirRaiz.setPai(dirRaiz);
    	
    	//encontra o diretório atual
    	for(int i = 1; i < cDir.length; i++) {
    		curDir = curDir.buscaDiretorioPeloNome(cDir[i]);
    	}
    	
    	//verifica se diretório do parâmetro existe
    	for(int i = 0; i < path.length; i++) {
    		if(path[i] == "") {
    			//caminho absoluto
    			curDir = dirRaiz;
    			continue;
    		}
    		if(path[i].contains(".")) {
    			curDir = curDir.buscaDiretorioPeloNome(path[i]);
    			continue;
    		}
    		if(curDir.buscaDiretorioPeloNome(path[i]) != null) {
    			curDir = curDir.buscaDiretorioPeloNome(path[i]);
   			}
       		else {        			
       			result = path[i].concat(": Diretório não existe.");
	   			return result;
    		}
    	}
    	
        //indica o novo diretório
    	for(int i = 0; i < path.length; i++) {
    		if(path[i] == "") {
    			//caminho absoluto
    			currentDir = "/";
    			continue;
    		}
    		else if(path[i].contains(".")) {
				currentDir = getCaminhoCompleto(curDir);
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

        return result;
    }

    public String rmdir(String parameters) {
        String result = "";
        String[] currentDir = operatingSystem.fileSystem.FileSytemSimulator.currentDir.split("/");
        String[] path = parameters.split("/");
    	Diretorio curDir = dirRaiz;
    	
    	//encontra o diretório atual
    	for(int i = 1; i < currentDir.length; i++) {
    		curDir = curDir.buscaDiretorioPeloNome(currentDir[i]);
    	}
    	
    	//localiza o diretório a ser removido
    	for(int i = 0; i < path.length; i++) {
    		if(path[i] == "") {
    			//caminho absoluto
    			curDir = dirRaiz;
    			continue;
    		}
    		if(path[i].contains(".")) {
    			curDir = curDir.buscaDiretorioPeloNome(path[i]);
    			continue;
    		}
    		else if (curDir.buscaDiretorioPeloNome(path[i]) == null) {
    			result = "rmdir: Diretório: " + path + " não existe. (Nada foi removido)";
    			break;
    		}
    		else {
    			curDir = curDir.buscaDiretorioPeloNome(path[i]);
    		}
    	}
    	
    	if(curDir.getFilhos().isEmpty()) {
			curDir.getPai().getFilhos().remove(curDir);
		}
		else {
			result = "rmdir: Diretório: " + parameters + " possui arquivos e/ou diretórios. (Nada foi removido)";
		}
    	
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
        String result = "";
        String[] in = parameters.split(" ");
    	String[] currentDir = operatingSystem.fileSystem.FileSytemSimulator.currentDir.split("/");
    	String[] origem = in[0].split("/");
    	String[] destino = in[1].split("/");
    	Diretorio dirOrigem = dirRaiz;
    	Diretorio dirDestino = dirRaiz;
    	Diretorio dirAux = null;
    	
    	//passa diretório atual para origem
    	for(int i = 1; i < currentDir.length; i++) {
    		dirOrigem = dirOrigem.buscaDiretorioPeloNome(currentDir[i]);
    	}
    	//encontra diretório de origem pelo parâmetro
    	for(int i = 0; i < origem.length; i++) {
    		if(dirOrigem == null) {
    			return result = "mv: Diretório origem não existe. (Nenhuma alteração foi efetuada)";
    		}
    		else if(origem[i] == "") {
    			//caminho absoluto
    			dirOrigem = dirRaiz;
    			continue;
    		}
    		else {
        		dirOrigem = dirOrigem.buscaDiretorioPeloNome(origem[i]);
    		}
    	}
    	
    	//passa diretório atual para destino
    	for(int i = 1; i < currentDir.length; i++) {
    		dirDestino = dirDestino.buscaDiretorioPeloNome(currentDir[i]);
    	}
    	//encontra diretório de destino pelo parâmetro
    	for(int i = 0; i < destino.length; i++) {
    		if(dirDestino == null) {
    			if(i == destino.length - 1) {
    				dirDestino = dirDestino.buscaDiretorioPeloNome(destino[destino.length-1]); 
    			}
    			else {
    				result = "mv: Diretório destino não existe. (Nenhuma alteração foi efetuada)";
    			}
    		}
    		else if(destino[i] == "") {
    			//caminho absoluto
    			dirDestino = dirRaiz;
    			continue;
    		}
    		else {
        		dirDestino = dirDestino.buscaDiretorioPeloNome(destino[i]);
    		}
    	}
    	
    	if(dirOrigem == dirDestino) {
    		//renomear
    		dirOrigem.setNome(destino[destino.length-1]);
    	}
    	else {
    		//mover
    		for(int i = 0; i < dirOrigem.getPai().getFilhos().size(); i++) {
        		if(dirOrigem.getPai().getFilhos().get(i) == dirOrigem) {
        			dirAux = dirOrigem.getPai().getFilhos().remove(i);
        		}
        	}
        	dirDestino.getFilhos().add(dirAux);
    	}
    	
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
        String result = "";
    	String[] currentDir = operatingSystem.fileSystem.FileSytemSimulator.currentDir.split("/");
        String[] in = parameters.split(" ");
        String[] path;
    	Diretorio curDir = dirRaiz;
    	Arquivo file = null;
    	
    	//seta flags opcionais - modificam o fluxo do programa
        boolean recursiveMode = false;
        if (in[0].equals("-R")) recursiveMode = true; // usamos a posição 0 porque o -R só pode aparecer nesta posição
    	
        //verifica parâmetros
    	path = in[in.length-1].split("/");
        
    	//encontra o diretório atual
    	for(int i = 1; i < currentDir.length; i++) {
    		curDir = curDir.buscaDiretorioPeloNome(currentDir[i]);
    	}
    	
    	//localiza o objeto a ser alterado
    	for(int i = 0; i < path.length; i++) {
    		if (curDir.buscaDiretorioPeloNome(path[i]) == null) {
    			result = "chmod: Diretório: " + path[i] + " não existe. (Nada foi alterado)";
    			break;
    		}
    		else {
    			curDir = curDir.buscaDiretorioPeloNome(path[i]);
    		}
    	}
    	
    	String newMod = recursiveMode == true ? in[1] : in[0];
		int digits[] = new int[3];
		int READ = 4, WRITE = 2, EXECUTE = 1;
		String oldPer = curDir.getPermissao();
		String newPer = Character.toString(oldPer.charAt(0));
		
		digits[0] = Character.digit(newMod.charAt(0), 10);
		digits[1] = Character.digit(newMod.charAt(1), 10);
		digits[2] = Character.digit(newMod.charAt(2), 10);
		
		//seta nova permissão
		for(int i = 0; i < 3; i++) {
			if((digits[i] & READ) == READ){
    			newPer = newPer.concat("r");
    		}
			else {
    			newPer = newPer.concat("-");
    		}
   			if((digits[i] & WRITE) == WRITE) {
   				newPer = newPer.concat("w");
   			}
   			else {
   				newPer = newPer.concat("-");
    		}
   			if((digits[i] & EXECUTE) == EXECUTE) {
   				newPer = newPer.concat("x");
    		}
   					else {
    			newPer = newPer.concat("-");
    		}
		}
		curDir.setPermissao(newPer);
		
		//seta nova permissão recursivamente
		if (recursiveMode) {
    		for (int i = 0; i < curDir.getFilhos().size(); i++) {
    			curDir.getFilhos().get(i).setPermissao(newPer);
    		}
    	}
        
        return result;
    }

    public String createfile(String parameters) {
    	String result = "";
        String[] currentDir = operatingSystem.fileSystem.FileSytemSimulator.currentDir.split("/");
        Diretorio curDir = dirRaiz;
        String[] param = parameters.split(" ", 2);
        String path[] = param[0].split("/");
        String content = param[1];
        
        //encontra o diretório atual
    	for(int i = 1; i < currentDir.length; i++) {
    		curDir = curDir.buscaDiretorioPeloNome(currentDir[i]);
    	}
    	
    	//verifica e cria arquivo
        for(int i = 0; i < path.length; i++) {
        	if(path[i] == "") {
    			//caminho absoluto
    			curDir = dirRaiz;
    			continue;
    		}
        	else if (path[i].matches(regexArq)) {
        		//arquivo localizado
        		if(curDir.buscaArquivoPeloNome(path[i]) != null) {
        			return result = "createfile: Arquivo já existe. Não foi possível cria-lo";
        		}
        		else {
        			curDir.criaArquivo(path[i], curDir, content);
        		}
            } 
        	else {
        		curDir = curDir.buscaDiretorioPeloNome(path[i]);
        	}
        }
    	
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
   
        //inicio
        //fim
        
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
