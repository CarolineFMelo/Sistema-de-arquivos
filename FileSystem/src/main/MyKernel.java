package main;

import java.util.ArrayList;
import java.util.Arrays;
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
	public String regexArq = "\\S+\\.[^\\s]+";

    public MyKernel() {
    	this.dirRaiz = new Diretorio("/", null);
    	this.dirAtual = null;
    	this.dirAntigo = null;
    }
    
    //verifica se ha flags na entrada do programa
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
        if (param[0].equals("-l") || param[0].equals("-L")) listMode = true; // usamos a posicao 0 porque o -l so pode aparecer nesta posicao
    	
        //verifica parametros
    	if(param.length == 2) {
    		path = param[1].split("/");
    	}
    	else if((param.length == 1) && (!argParser(param[0], "l"))) {
    		path = param[0].split("/");
    	}
    	else {
    		path = "".split("/");
    	}
        
    	//encontra o diretorio atual
    	for(int i = 1; i < currentDir.length; i++) {
    		curDir = curDir.buscaDiretorioPeloNome(currentDir[i]);
    	}
    	
    	//encontra caminho do parametro
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
       			return result = path[i].concat(": Diretorio nao existe.");
    		}
    		curDir = curDir.buscaDiretorioPeloNome(path[i]);
    	}
	    
     	//lista conteudo do diretorio
	    if(listMode) {
     		for(int i = 0; i < curDir.getFilhos().size(); i++) {
     			result = result.concat(curDir.getFilhos().get(i).getPermissao() + " " +
     					"carol" + " " +
     					curDir.getFilhos().get(i).getDataCriacaoFormatada() + " " +
     					curDir.getFilhos().get(i).getNome() + "\n");
         	}
     		for(int i = 0; i < curDir.getArquivos().size(); i++) {
     			result = result.concat(curDir.getArquivos().get(i).getPermissao() + " " +
     					"carol" + " " +
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
    	
    	//encontra o diretorio atual
    	for(int i = 1; i < currentDir.length; i++) {
    		curDir = curDir.buscaDiretorioPeloNome(currentDir[i]);
    	}
    	
    	//verifica existencia do diretorio e cria
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
	    			return result = "mkdir: " + path[i] + ": Diretorio nao existe (Nenhum diretorio foi criado).";
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
    	
    	//encontra o diretorio atual
    	for(int i = 1; i < cDir.length; i++) {
    		curDir = curDir.buscaDiretorioPeloNome(cDir[i]);
    	}
    	
    	//verifica se diretorio do parametro existe
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
       			return result = path[i].concat(": Diretorio nao existe.");
    		}
    	}
    	
    	//indica o novo diretorio
    	currentDir = getCaminhoCompleto(curDir);
    	if(currentDir.length() > 1) {
    		currentDir = currentDir.substring(0, currentDir.length() - 1);
    	}
		
        //setando parte grafica do diretorio atual
        operatingSystem.fileSystem.FileSytemSimulator.currentDir = currentDir;

        return result;
    }
    
    public String rmdir(String parameters) {
        String result = "";
        String[] currentDir = operatingSystem.fileSystem.FileSytemSimulator.currentDir.split("/");
        String[] path = parameters.split("/");
    	Diretorio curDir = dirRaiz;
    	Diretorio dirRemoved = dirRaiz;
    	
    	//encontra o diretorio atual
    	for(int i = 1; i < currentDir.length; i++) {
    		curDir = curDir.buscaDiretorioPeloNome(currentDir[i]);
    		dirRemoved = dirRemoved.buscaDiretorioPeloNome(currentDir[i]);
    	}
    	
    	//localiza o diretorio a ser removido
    	for(int i = 0; i < path.length; i++) {
    		if(path[i] == "") {
    			//caminho absoluto
    			dirRemoved = dirRaiz;
    			continue;
    		}
    		if(path[i].contains(".")) {
    			dirRemoved = dirRemoved.buscaDiretorioPeloNome(path[i]);
    			continue;
    		}
    		else if (dirRemoved.buscaDiretorioPeloNome(path[i]) == null) {
    			return result = "rmdir: Diretorio nao existe. (Nada foi removido)";
    		}
    		else {
    			dirRemoved = dirRemoved.buscaDiretorioPeloNome(path[i]);
    		}
    	}
    	
    	if(dirRemoved == curDir) {
    		if(curDir.getPai().equals(dirRaiz)|| curDir.getPai() == null) {
    			cd("");
    		}
    		else {
    			cd(getCaminhoCompleto(curDir.getPai()));
    		}
    	}
    	
    	if(dirRemoved.getFilhos().isEmpty() && dirRemoved.getArquivos().isEmpty()) {
    		dirRemoved.getPai().getFilhos().remove(dirRemoved);
		}
		else {
			return result = "rmdir: Diretorio possui arquivos e/ou diretorios. (Nada foi removido)";
		}
    	
        return result;
    }
    
    public String cp(String parameters) {
    	String result = "";
        String[] in = parameters.split(" ");
    	String[] currentDir = operatingSystem.fileSystem.FileSytemSimulator.currentDir.split("/");
    	String[] origem = null;
    	String[] destino = null;
    	Diretorio dirOrigem = dirRaiz;
    	Diretorio dirDestino = dirRaiz;
    	Arquivo arqOrigem = null;
    	String nomeArq = null;
    	String nomeDir = null;
    	
    	//seta flags opcionais - modificam o fluxo do programa
        boolean dirMode = false;
        if (in[0].equals("-R") || in[0].equals("-r")) dirMode = true; // usamos a posicao 0 porque o -R so pode aparecer nesta posicao
    	
        //verifica parametros
    	origem = in[in.length-2].split("/");
    	destino = in[in.length-1].split("/");
    	
    	//passa diretorio atual para origem
    	for(int i = 1; i < currentDir.length; i++) {
    		dirOrigem = dirOrigem.buscaDiretorioPeloNome(currentDir[i]);
    	}
    	
    	//encontra diretorio de origem pelo parametro
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
    				return result = "cp: O arquivo não existe. (Nada foi copiado)";
    			}
    		}
    		else {
    			if(dirOrigem.buscaDiretorioPeloNome(origem[i]) != null) {
    				if(i == origem.length-1) {
        				if(dirMode == false) {
        					return result = "cp: Flag desativada. (Nenhum diretorio foi copiado)";
        				}
        			}
    				dirOrigem = dirOrigem.buscaDiretorioPeloNome(origem[i]);
    			}
    			else {
    				return result = "cp: O diretorio de origem não existe. (Nada foi copiado)";
    			}
    		}
    	}
    	
    	//passa diretorio atual para destino
    	for(int i = 1; i < currentDir.length; i++) {
    		dirDestino = dirDestino.buscaDiretorioPeloNome(currentDir[i]);
    	}
    	
    	//encontra diretorio de destino pelo parametro
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
    		}
    		else {
    			if(dirDestino.buscaDiretorioPeloNome(destino[i]) != null) {
    				dirDestino = dirDestino.buscaDiretorioPeloNome(destino[i]);
    			}
    			else {
    				if(i == destino.length - 1) {
    					if(dirMode == true) {
    						nomeDir = destino[i];
    					}
        			}
        			else {
        				return result = "cp: O diretorio de destino não existe. (Nada foi copiado)";
        			}
    			}
    		}
    	}
    	
    	//copia objetos
    	if(arqOrigem != null) {
    		//copia arquivo
    		try {
                Arquivo cloneArq = (Arquivo) arqOrigem.clone();
                cloneArq.setDirPai(dirDestino);
                if(nomeArq != null) {
                	cloneArq.setNome(nomeArq);
                }
                dirDestino.getArquivos().add(cloneArq);
    		} catch (CloneNotSupportedException e) {
                return result = "Erro ao copiar arquivo";
            }
    	}
    	else {
    		//copia diretorio
        	try {
	            Diretorio cloneDir = (Diretorio) dirOrigem.clone();
	            cloneDir.setPai(dirDestino);
	            if(nomeDir != null) {
		            cloneDir.setNome(nomeDir);
	            }
	            dirDestino.getFilhos().add(cloneDir);
            } catch (CloneNotSupportedException e) {
            	return result = "Erro ao copiar diretorio";
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
    	Diretorio curDir = dirRaiz;
    	Diretorio dirOrigem = dirRaiz;
    	Diretorio dirDestino = dirRaiz;
    	Diretorio dirAux = null;
    	Arquivo arqOrigem = null;
    	Arquivo arqAux = null;
    	String nomeArq = null;
    	
    	//pega diretorio atual
    	for(int i = 1; i < currentDir.length; i++) {
    		curDir = curDir.buscaDiretorioPeloNome(currentDir[i]);
    	}
    	
    	//passa diretorio atual para origem
    	for(int i = 1; i < currentDir.length; i++) {
    		dirOrigem = dirOrigem.buscaDiretorioPeloNome(currentDir[i]);
    	}
    	
    	//encontra diretorio de origem pelo parametro
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
    				return result = "mv: Arquivo origem nao existe. (Nenhuma alteracao foi efetuada)";
    			}
    		}
    		else {
    			if(dirOrigem.buscaDiretorioPeloNome(origem[i]) != null) {
    				dirOrigem = dirOrigem.buscaDiretorioPeloNome(origem[i]);
    			}
    			else {
    				return result = "mv: Diretorio origem nao existe. (Nenhuma alteracao foi efetuada)";
    			}
    		}
    	}
    	
    	//passa diretorio atual para destino
    	for(int i = 1; i < currentDir.length; i++) {
    		dirDestino = dirDestino.buscaDiretorioPeloNome(currentDir[i]);
    	}
    	
    	//encontra diretorio de destino pelo parametro
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
    				return result = "mv: nome ja existente. (Nenhuma alteracao foi efetuada)";
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
        				return result = "mv: Diretorio origem nao existe. (Nenhuma alteracao foi efetuada)";
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
    			Diretorio aux = dirOrigem;
        		//renomeia diretorio
        		dirOrigem.setNome(destino[destino.length-1]);
        		if(aux == curDir) {
		    		cd(getCaminhoCompleto(dirOrigem));
		    	}
        	}
        	else {
        		if(dirOrigem == curDir) {
		    		if(curDir.getPai().equals(dirRaiz) || curDir.getPai() == null) {
		    			cd("");
		    		}
		    		else {
		    			cd(getCaminhoCompleto(curDir.getPai()));
		    		}
		    	}
        		//move diretorio
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
    	Diretorio auxDir = dirRaiz;
    	
    	//seta flags opcionais - modificam o fluxo do programa
        boolean removeDirMode = false;
        if (in[0].equals("-R") || in[0].equals("-r")) removeDirMode = true; // usamos a posicao 0 porque o -R so pode aparecer nesta posicao
    	
        //verifica parametros
    	path = in[in.length-1].split("/");
        
    	//encontra o diretorio atual
    	for(int i = 1; i < currentDir.length; i++) {
    		curDir = curDir.buscaDiretorioPeloNome(currentDir[i]);
    		auxDir = auxDir.buscaDiretorioPeloNome(currentDir[i]);
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
        			return result = "rm: Arquivo nao existe (Nenhum arquivo ou diretorio foi removido)";
        		}
            } 
        	else if(curDir.buscaDiretorioPeloNome(path[i]) != null) {
        		curDir = curDir.buscaDiretorioPeloNome(path[i]);
        		if(i == path.length - 1) {
        			if(removeDirMode) {
        				if(auxDir == curDir) {
        		    		if(curDir.getPai() == dirRaiz || curDir.getPai() == null) {
        		    			cd("");
        		    		}
        		    		else {
        		    			cd(getCaminhoCompleto(curDir.getPai()));
        		    		}
        		    	}
        				//remove diretorio e todo seu conteudo, caso flag ativada
        				curDir.getPai().getFilhos().remove(curDir);
            			break;
        			}
        			else {
        				return result = "rm: " + path[i] + ": e um diretorio (Nenhum arquivo ou diretorio foi removido)";
        			}
        		}
        	}
        	else {
        		return result = "rm: Diretorio nao existe (Nenhum arquivo ou diretorio foi removido)";
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
        if (in[0].equals("-R") || in[0].equals("-r")) recursiveMode = true; // usamos a posicao 0 porque o -R so pode aparecer nesta posicao
    	
        //verifica parametros
    	path = in[in.length-1].split("/");
        
    	//encontra o diretorio atual
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
        			//para mudar permissao do arquivo
        			for(int j = 0; j < curDir.getArquivos().size(); j++) {
        				if(curDir.getArquivos().get(j).getNome().equals(path[i])) {
        					oldPer = curDir.getArquivos().get(j).getPermissao();
        				}
        			}
        			objeto = "arq";
        		}
        		else {
        			return result = "chmod: Arquivo nao existe. (Nada foi alterado)";
        		}
            } 
        	else if(curDir.buscaDiretorioPeloNome(path[i]) != null) {
        		curDir = curDir.buscaDiretorioPeloNome(path[i]);
        		if(i == path.length - 1) {
    				//para mudar permissao do diretorio
    				oldPer = curDir.getPermissao();
    				objeto = "dir";
        		}
        	}
        	else {
        		return result = "chmod: Diretorio nao existe. (Nada foi alterado)";
        	}
        }
    	
    	String newMod = recursiveMode == true ? in[1] : in[0];
    	newPer = Character.toString(oldPer.charAt(0));
		int digits[] = new int[3];
		int READ = 4, WRITE = 2, EXECUTE = 1;
		
		for(int i = 0; i < 3; i++) {
			if(Character.digit(newMod.charAt(i), 10) <= 7) {
				digits[i] = Character.digit(newMod.charAt(i), 10);
			}
			else {
				return result = "permissao invalida";
			}
		}
		
		//seta nova permissao
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
				return result = "chmod: Nao e possivel aplicar recursividade na permissao de arquivos (Somente permissao do arquivo alterada)";
			}
		}
		else {
			curDir.setPermissao(newPer);
			//seta nova permissao recursivamente
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
        
        //encontra o diretorio atual
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
        			return result = "createfile: Arquivo ja existe. Nao foi possivel cria-lo";
        		}
        		else {
        			curDir.criaArquivo(path[i], curDir, content);
        		}
            } 
        	else {
        		if(curDir.buscaDiretorioPeloNome(path[i]) == null) {
        			return result = "createfile: Diretorio nao existe. Nao foi possivel criar arquivo";
        		}
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
        
        //encontra o diretorio atual
    	for(int i = 1; i < currentDir.length; i++) {
    		curDir = curDir.buscaDiretorioPeloNome(currentDir[i]);
    	}
    	
    	//verifica e imprime conteudo do arquivo
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
        			return result = "cat: Arquivo nao existe.";
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
        ArrayList<String> file = FileManager.stringReader(parameters);
       
        //verifica leitura do arquivo
        if (file == null) {
        	return result = "Arquivo nao existe.";
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
        Stack<String> pilha = new Stack<String>();
        
        //cria e abre arquivo de dump
        FileManager.writer(parameters, result);
        
        //chama funcao recursiva
        recursiveDump(curDir, "", pilha);
        
        int pilhaSize = pilha.size();
        //desempilha os comandos e escreve no arquivo de dump
        for(int i = 0; i < pilhaSize; i++) {
        	FileManager.writerAppend(parameters, pilha.pop() + "\n");
        }
        
        return result;
    }

    //percorre o sistema de arquivos e monta o dump
    public void recursiveDump(Diretorio node, String curPath, Stack<String> pilha) {
    	 String textCom = "";
         String textPer = "";
    	
    	//condicao de parada da recursao
    	if(node == null ) {
    		return;
    	}
    	
    	curPath += node.getNome();
    	
    	//verifica se o diretorio atual tem arquivos
    	if(!node.getArquivos().isEmpty()) {
    		for(Arquivo arq : node.getArquivos()) {
            	//verifica permissao do arquivo
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
            	}
            	
            	//cria arquivo
    			if(curPath.equals("/")) {
    				textCom = "createfile " + curPath + arq.getNome() + " " + arq.getConteudo();
    			}
    			else {
    				textCom = "createfile " + curPath + "/" + arq.getNome() + " " + arq.getConteudo();
    			}
    			pilha.push(textCom);
    		}
    	}
    	
    	//verifica se o diretorio atual tem diretorios filhos
    	if(node.getFilhos().isEmpty()) {
        	//verifica permissao do diretorio
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
        		textPer = "chmod " + per + " " + curPath;
        		pilha.push(textPer);
        	}
        	
        	//cria diretorio filho
    		textCom = "mkdir " + curPath;
    		pilha.push(textCom);
	    }
    	else if(!node.getNome().equals("/")) {
    		curPath += "/";
    	}
    	
    	//percorre o sistema de arquivos com recursao
    	for(int i = 0; i < node.getFilhos().size(); i++) {
    		recursiveDump(node.getFilhos().get(i), curPath, pilha);
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