package main;

import java.util.ArrayList;
import java.util.Stack;

import fileFunctions.FileManager;
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
    			result = "rmdir: Diretório não existe. (Nada foi removido)";
    			break;
    		}
    		else {
    			curDir = curDir.buscaDiretorioPeloNome(path[i]);
    		}
    	}
    	
    	if(curDir.getFilhos().isEmpty() && curDir.getArquivos().isEmpty()) {
			curDir.getPai().getFilhos().remove(curDir);
		}
		else {
			result = "rmdir: Diretório possui arquivos e/ou diretórios. (Nada foi removido)";
		}
    	
        return result;
    }

    public String cp(String parameters) {
    	String result = "";
        String[] in = parameters.split(" ");
    	String[] currentDir = operatingSystem.fileSystem.FileSytemSimulator.currentDir.split("/");
    	String[] origem = in[0].split("/");
    	String[] destino = in[1].split("/");
    	Diretorio dirOrigem = dirRaiz;
    	Diretorio dirDestino = dirRaiz;
    	Arquivo arqOrigem = null;
    	String nomeArq = null;
    	
    	//passa diretório atual para origem
    	for(int i = 1; i < currentDir.length; i++) {
    		dirOrigem = dirOrigem.buscaDiretorioPeloNome(currentDir[i]);
    	}
    	
    	//encontra diretório de origem pelo parâmetro
    	for(int i = 0; i < origem.length; i++) {
    		if(origem[i] == "") {
    			//caminho absoluto
    			dirOrigem = dirRaiz;
    			continue;
    		}
    		else if(origem[i].matches(regexArq)) {
    			if(dirOrigem.buscaArquivoPeloNome(origem[i]) != null) {
    				arqOrigem = dirOrigem.buscaArquivoPeloNome(origem[i]);
    			}
    			else {
    				return result = "mv: Arquivo origem não existe. (Nenhuma alteração foi efetuada)";
    			}
    		}
    		else {
    			if(dirOrigem.buscaDiretorioPeloNome(origem[i]) != null) {
    				dirOrigem = dirOrigem.buscaDiretorioPeloNome(origem[i]);
    			}
    			else {
    				return result = "mv: Diretorio origem não existe. (Nenhuma alteração foi efetuada)";
    			}
    		}
    	}
    	
    	//passa diretório atual para destino
    	for(int i = 1; i < currentDir.length; i++) {
    		dirDestino = dirDestino.buscaDiretorioPeloNome(currentDir[i]);
    	}
    	
    	//encontra diretório de destino pelo parâmetro
    	for(int i = 0; i < destino.length; i++) {
    		if(destino[i] == "") {
    			//caminho absoluto
    			dirDestino = dirRaiz;
    			continue;
    		}
    		else if(destino[i].matches(regexArq)) {
    			if(dirDestino.buscaArquivoPeloNome(destino[i]) == null) {
    				nomeArq = destino[i]; 
    			}
    			else {
    				return result = "mv: nome ja existente. (Nenhuma alteração foi efetuada)";
    			}
    		}
    		else {
    			if(dirDestino.buscaDiretorioPeloNome(destino[i]) != null) {
    				dirDestino = dirDestino.buscaDiretorioPeloNome(destino[i]);
    			}
    			else {
    				if(i == destino.length - 1) {
    					dirDestino = dirDestino.buscaDiretorioPeloNome(origem[origem.length-1]);
        			}
        			else {
        				return result = "mv: Diretorio origem não existe. (Nenhuma alteração foi efetuada)";
        			}
    			}
    		}
    	}
    	
    	//remove ou move objetos
    	if(arqOrigem != null) {
    		if(nomeArq != null) {
        		//renomeia arquivo
        		arqOrigem.setNome(nomeArq);
        	}
    		else {
    			//copia arquivo
    			 try {
                     Arquivo cloneArq = (Arquivo) arqOrigem.clone();
                     cloneArq.setDirPai(dirDestino);
                     dirDestino.getArquivos().add(cloneArq);
                 } catch (CloneNotSupportedException e) {
                     return result = "Erro ao copiar arquivo";
                 }
    		}
    	}
    	else {
    		if(dirOrigem == dirDestino) {
        		//renomeia diretório
        		dirOrigem.setNome(destino[destino.length-1]);
        	}
        	else {
        		//copia diretório
        	    try {
                    Diretorio cloneDir = (Diretorio) dirOrigem.clone();
                    cloneDir.setPai(dirDestino);
                    dirDestino.getFilhos().add(cloneDir);
                } catch (CloneNotSupportedException e) {
                    return result = "Erro ao copiar diretorio";
                }
        	}
    	}
    	
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
    	Arquivo arqOrigem = null;
    	Arquivo arqAux = null;
    	String nomeArq = null;
    	
    	//passa diretório atual para origem
    	for(int i = 1; i < currentDir.length; i++) {
    		dirOrigem = dirOrigem.buscaDiretorioPeloNome(currentDir[i]);
    	}
    	
    	//encontra diretório de origem pelo parâmetro
    	for(int i = 0; i < origem.length; i++) {
    		if(origem[i] == "") {
    			//caminho absoluto
    			dirOrigem = dirRaiz;
    			continue;
    		}
    		else if(origem[i].matches(regexArq)) {
    			if(dirOrigem.buscaArquivoPeloNome(origem[i]) != null) {
    				arqOrigem = dirOrigem.buscaArquivoPeloNome(origem[i]);
    			}
    			else {
    				return result = "mv: Arquivo origem não existe. (Nenhuma alteração foi efetuada)";
    			}
    		}
    		else {
    			if(dirOrigem.buscaDiretorioPeloNome(origem[i]) != null) {
    				dirOrigem = dirOrigem.buscaDiretorioPeloNome(origem[i]);
    			}
    			else {
    				return result = "mv: Diretorio origem não existe. (Nenhuma alteração foi efetuada)";
    			}
    		}
    	}
    	
    	//passa diretório atual para destino
    	for(int i = 1; i < currentDir.length; i++) {
    		dirDestino = dirDestino.buscaDiretorioPeloNome(currentDir[i]);
    	}
    	
    	//encontra diretório de destino pelo parâmetro
    	for(int i = 0; i < destino.length; i++) {
    		if(destino[i] == "") {
    			//caminho absoluto
    			dirDestino = dirRaiz;
    			continue;
    		}
    		else if(destino[i].matches(regexArq)) {
    			if(dirDestino.buscaArquivoPeloNome(destino[i]) == null) {
    				nomeArq = destino[i]; 
    			}
    			else {
    				return result = "mv: nome ja existente. (Nenhuma alteração foi efetuada)";
    			}
    		}
    		else {
    			if(dirDestino.buscaDiretorioPeloNome(destino[i]) != null) {
    				dirDestino = dirDestino.buscaDiretorioPeloNome(destino[i]);
    			}
    			else {
    				if(i == destino.length - 1) {
    					dirDestino = dirDestino.buscaDiretorioPeloNome(origem[origem.length-1]);
        			}
        			else {
        				return result = "mv: Diretorio origem não existe. (Nenhuma alteração foi efetuada)";
        			}
    			}
    		}
    	}
    	
    	//remove ou move objetos
    	if(arqOrigem != null) {
    		if(nomeArq != null) {
        		//renomeia arquivo
        		arqOrigem.setNome(nomeArq);
        	}
    		else {
    			//move arquivo
    			for(int i = 0; i < dirOrigem.getArquivos().size(); i++) {
        			if(dirOrigem.getArquivos().get(i).getNome().equals(arqOrigem.getNome())) {
        				arqAux = dirOrigem.getArquivos().remove(i);
        			}
        		}
        		dirDestino.getArquivos().add(arqAux);
    		}
    	}
    	else {
    		if(dirOrigem == dirDestino) {
        		//renomeia diretório
        		dirOrigem.setNome(destino[destino.length-1]);
        	}
        	else {
        		//move diretório
        		for(int i = 0; i < dirOrigem.getPai().getFilhos().size(); i++) {
            		if(dirOrigem.getPai().getFilhos().get(i) == dirOrigem) {
            			dirAux = dirOrigem.getPai().getFilhos().remove(i);
            		}
            	}
            	dirDestino.getFilhos().add(dirAux);
        	}
    	}
    	
        return result;
    }

    public String rm(String parameters) {
        String result = "";
    	String[] currentDir = operatingSystem.fileSystem.FileSytemSimulator.currentDir.split("/");
        String[] in = parameters.split(" ");
        String[] path;
    	Diretorio curDir = dirRaiz;
    	
    	//seta flags opcionais - modificam o fluxo do programa
        boolean removeDirMode = false;
        if (in[0].equals("-R")) removeDirMode = true; // usamos a posição 0 porque o -R só pode aparecer nesta posição
    	
        //verifica parâmetros
    	path = in[in.length-1].split("/");
        
    	//encontra o diretório atual
    	for(int i = 1; i < currentDir.length; i++) {
    		curDir = curDir.buscaDiretorioPeloNome(currentDir[i]);
    	}
    	
    	//localiza o objeto a ser alterado
    	for(int i = 0; i < path.length; i++) {
        	if(path[i] == "") {
    			//caminho absoluto
    			curDir = dirRaiz;
    			continue;
    		}
        	else if (path[i].matches(regexArq)) {
        		//arquivo localizado
        		if(curDir.buscaArquivoPeloNome(path[i]) != null) {
        			//remove arquivo
        			for(int j = 0; j < curDir.getArquivos().size(); j++) {
        				if(curDir.getArquivos().get(j).getNome().equals(path[i])) {
        					curDir.getArquivos().remove(j);
        				}
        			}
        		}
        		else {
        			return result = "rm: Arquivo não existe (Nenhum arquivo ou diretório foi removido)";
        		}
            } 
        	else if(curDir.buscaDiretorioPeloNome(path[i]) != null) {
        		curDir = curDir.buscaDiretorioPeloNome(path[i]);
        		if(i == path.length - 1) {
        			if(removeDirMode) {
        				//remove diretório e todo seu conteúdo, caso flag ativada
        				curDir.getPai().getFilhos().remove(curDir);
            			break;
        			}
        			else {
        				return result = "rm: " + path[i] + ": é um diretorio (Nenhum arquivo ou diretório foi removido)";
        			}
        		}
        	}
        	else {
        		return result = "rm: Diretório nao existe (Nenhum arquivo ou diretório foi removido)";
        	}
        }
    	return result;
    }

    public String chmod(String parameters) {
        String result = "";
    	String[] currentDir = operatingSystem.fileSystem.FileSytemSimulator.currentDir.split("/");
        String[] in = parameters.split(" ");
        String[] path;
    	Diretorio curDir = dirRaiz;
    	String oldPer = "";
    	String newPer = "";
    	String objeto = null;
    	
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
        	if(path[i] == "") {
    			//caminho absoluto
    			curDir = dirRaiz;
    			continue;
    		}
        	else if (path[i].matches(regexArq)) {
        		//arquivo localizado
        		if(curDir.buscaArquivoPeloNome(path[i]) != null) {
        			//para mudar permissão do arquivo
        			for(int j = 0; j < curDir.getArquivos().size(); j++) {
        				if(curDir.getArquivos().get(j).getNome().equals(path[i])) {
        					oldPer = curDir.getArquivos().get(j).getPermissao();
        				}
        			}
        			objeto = "arq";
        		}
        		else {
        			return result = "chmod: Arquivo não existe. (Nada foi alterado)";
        		}
            } 
        	else if(curDir.buscaDiretorioPeloNome(path[i]) != null) {
        		curDir = curDir.buscaDiretorioPeloNome(path[i]);
        		if(i == path.length - 1) {
    				//para mudar permissão do diretório
    				oldPer = curDir.getPermissao();
    				objeto = "dir";
        		}
        	}
        	else {
        		return result = "chmod: Diretório não existe. (Nada foi alterado)";
        	}
        }
    	
    	String newMod = recursiveMode == true ? in[1] : in[0];
    	newPer = Character.toString(oldPer.charAt(0));
		int digits[] = new int[3];
		int READ = 4, WRITE = 2, EXECUTE = 1;
		
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
		
		if(objeto.equals("arq")) {
			for(int j = 0; j < curDir.getArquivos().size(); j++) {
				if(curDir.getArquivos().get(j).getNome().equals(path[path.length-1])) {
					curDir.getArquivos().get(j).setPermissao(newPer);
				}
			}
			if(recursiveMode) {
				return result = "chmod: Não é possível aplicar recursividade na permissão de arquivos (Somente permissão do arquivo alterada)";
			}
		}
		else {
			curDir.setPermissao(newPer);
			//seta nova permissão recursivamente
			if (recursiveMode) {
	    		for (int i = 0; i < curDir.getFilhos().size(); i++) {
	    			curDir.getFilhos().get(i).setPermissao(newPer);
	    		}
	    		for (int i = 0; i < curDir.getArquivos().size(); i++) {
	    			newPer = "-" + newPer.substring(1);
	    			curDir.getArquivos().get(i).setPermissao(newPer);
	    		}
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
    	String result = "";
        String[] currentDir = operatingSystem.fileSystem.FileSytemSimulator.currentDir.split("/");
        Diretorio curDir = dirRaiz;
        String[] path = parameters.split("/");
        
        //encontra o diretório atual
    	for(int i = 1; i < currentDir.length; i++) {
    		curDir = curDir.buscaDiretorioPeloNome(currentDir[i]);
    	}
    	
    	//verifica e imprime conteúdo do arquivo
        for(int i = 0; i < path.length; i++) {
        	if(path[i] == "") {
    			//caminho absoluto
    			curDir = dirRaiz;
    			continue;
    		}
        	else if (path[i].matches(regexArq)) {
        		//arquivo localizado
        		if(curDir.buscaArquivoPeloNome(path[i]) != null) {
        			for(int j = 0; j < curDir.getArquivos().size(); j++) {
        				if(curDir.getArquivos().get(j).getNome().equals(path[path.length-1])) {
        					return result = result.concat(curDir.getArquivos().get(j).getConteudo());
        				}
        			}
        		}
        		else {
        			return result = "cat: Arquivo não existe.";
        		}
            } 
        	else {
        		curDir = curDir.buscaDiretorioPeloNome(path[i]);
        	}
        }
    	
        return result;
    }

    public String batch(String parameters) {
        String result = "";
        
        //abre o arquivo
        //ArrayList<String> arquivo = FileManager.stringReader(parameters);
        ArrayList<String> file = FileManager.stringReader("C:\\Users\\cferr\\workspace\\comandos.txt");
       
        //verifica leitura do arquivo
        if (file == null) {
        	return result = "Arquivo não existe.";
        }
        
        //executa comandos do arquivo
        for(int i = 0; i < file.size(); i++) {
        	String[] line = file.get(i).split(" ", 2);
        	String comando = line[0];
        	String param = null;
        	
        	if(line.length > 1) {
        		param = line[1];
        	}
        	
        	switch(comando) {
	        	case "ls":
	        		ls(param);
	        		break;
	        	case "mkdir":
	        		mkdir(param);
	        		break;
	        	case "cd":
	        		cd(param);
	        		break;
	        	case "rmdir":
	        		rmdir(param);
	        		break;
	        	case "cp":
	        		cp(param);
	        		break;
	        	case "mv":
	        		mv(param);
	        		break;
	        	case "rm":
	        		rm(param);
	        		break;
	        	case "chmod":
	        		chmod(param);
	        		break;
	        	case "createfile":
	        		createfile(param);
	        		break;
	        	case "cat":
	        		cat(param);
	        		break;
	        	case "batch":
	        		batch(param);
	        		break;
	        	case "dump":
	        		dump(param);
	        		break;
	        	case "info":
	        		info();
	        		break;
        	}
        }
        
        return result = "Comandos executados.";
    }

    public String dump(String parameters) {
        String result = "";
        Diretorio curDir = dirRaiz;
        
        //FileManager.writer(parameters, result);
        FileManager.writer("C:\\Users\\cferr\\workspace\\dump.txt", result);
        
        //chama função recursiva
        recursiveDump(curDir, "", parameters);
        
        return result;
    }
    
    //percorre o sistema de arquivos e monta o dump
    public void recursiveDump(Diretorio node, String curPath, String parameters) {
    	 String textCom = "";
         String textPer = "";
         Stack<String> pilha = new Stack<String>();
    	
    	//condição de parada da recursão
    	if(node == null ) {
    		 for(int i = 0; i < pilha.size(); i++) {
    				FileManager.writerAppend("C:\\Users\\cferr\\workspace\\dump.txt", pilha.pop() + "\n");
    		 }
    		return;
    	}
    	
    	curPath += node.getNome();
    	
    	//verifica se o diretório atual tem arquivos
    	if(!node.getArquivos().isEmpty()) {
    		for(Arquivo arq : node.getArquivos()) {
    			//cria arquivo
    			if(curPath.equals("/")) {
    				textCom = "createfile " + curPath + arq.getNome() + " " + arq.getConteudo();
    			}
    			else {
    				textCom = "createfile " + curPath + "/" + arq.getNome() + " " + arq.getConteudo();
    			}
    			
    			pilha.push(textCom);
    			//FileManager.writerAppend(parameters, textCom + "\n");
            	//FileManager.writerAppend("C:\\Users\\cferr\\workspace\\dump.txt", textCom + "\n");
            	
            	//verifica permissão do arquivo
            	if(!arq.getPermissao().equals("-rw-r--r--")) {
            		String auxPer = arq.getPermissao();
            		String per = "";
            		int cont = 0;
            		
            		for(int i = 1; i < auxPer.length(); i += 3) {
            		    String subPer = auxPer.substring(i, i + 3);
            		    if(subPer.contains("r")) {
            		    	cont = cont + 4;
            		    }
            		    if(subPer.contains("w")) {
            		    	cont = cont + 2;
            		    }
            		    if(subPer.contains("x")) {
            		    	cont = cont + 1;
            		    }
            		    per = per + cont;
            		    cont = 0;
            		}
            		textPer = "chmod " + per + " " + curPath + "/" + arq.getNome();
            		
            		pilha.push(textPer);
            		//FileManager.writerAppend(parameters, textPer + "\n");
            		//FileManager.writerAppend("C:\\Users\\cferr\\workspace\\dump.txt", textPer + "\n");
            	}
    		}
    	}
    	
    	//verifica se o diretório atual tem diretórios filhos
    	if(node.getFilhos().isEmpty()) {
    		//cria diretório filho
    		textCom = "mkdir " + curPath + "/" + node.getNome();
    		pilha.push(textCom);
    		
    		//FileManager.writerAppend(parameters, textCom + "\n");
        	//FileManager.writerAppend("C:\\Users\\cferr\\workspace\\dump.txt", textCom + "\n");
        	
        	//verifica permissão do diretório
        	if(!node.getPermissao().equals("drwxr-xr-x")) {
        		String auxPer = node.getPermissao();
        		String per = "";
        		int cont = 0;
        		
        		for(int i = 1; i < auxPer.length(); i += 3) {
        		    String subPer = auxPer.substring(i, i + 3);
        		    if(subPer.contains("r")) {
        		    	cont = cont + 4;
        		    }
        		    if(subPer.contains("w")) {
        		    	cont = cont + 2;
        		    }
        		    if(subPer.contains("x")) {
        		    	cont = cont + 1;
        		    }
        		    per = per + cont;
        		    cont = 0;
        		}
        		textPer = "chmod " + per + " " + curPath + "/" + node.getNome();
        		
        		pilha.push(textPer);
        		//FileManager.writerAppend(parameters, textPer + "\n");
        		//FileManager.writerAppend("C:\\Users\\cferr\\workspace\\dump.txt", textPer + "\n");
        	}
	    }
    	else if(!node.getNome().equals("/")) {
    		curPath += "/";
    	}
    	
    	//percorre o sistema de arquivos com recursão
    	for(int i = 0; i < node.getFilhos().size(); i++) {
    		recursiveDump(node.getFilhos().get(i), curPath, parameters);
    	}
    	
    }

    public String info() {
    	String result = "";
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