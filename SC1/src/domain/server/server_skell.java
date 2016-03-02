package domain.server;

import java.io.IOException;
import java.util.HashMap;

public class server_skell {

	PersistentFiles files;
	private HashMap <String,User> userMap;
	private HashMap <String,Group> groupMap;
	private static final String flags = "-p-m-f-r-a-d";

	public server_skell (String usersFile, String groupsFile){
		userMap = new HashMap <> ();
		groupMap = new HashMap <> ();
		files = new PersistentFiles(usersFile,groupsFile);
	}

	public HashMap<String, User> getUserMap() {
		return userMap;
	}

	public HashMap<String, Group> getGroupMap() {
		return groupMap;
	}

	public boolean authenticate (String pwd, String username) throws IOException{
		return files.checkUserPwd(pwd,username);
	}

	public String isUser(String username) throws IOException {
		return files.hasUser(username);
	}

	public void createUser(String username, String password) {
		files.addUser(username,password);

	}

	//-1 x == null
	//-2 flag -p n e a primeira
	//-3 argumento da pass e uma flag
	//-4 argumentos insuficientes para a pass
	//-5 flag invalida a seguir ao -p
	//-6 ordem errada da flag
	//-7 argumentos das flag invalidos
	//-10 falta password
	/**
	 * 
	 * @param arguments string de argumentos enviados do cliente ao servidor
	 * @return < 0 em caso de erro ou 1 em caso de sucesso
	 */
	public int validate(String[] arguments) {
		StringBuilder confirm = new StringBuilder ();
		if (arguments == null)
			return -1;
		int size = arguments.length-1;
		if (size > 2)
			return -8;
		if (size == -1)
			return 1;

		for (int i = 0; i <= size; i++){
			switch(arguments[i]){
			case "-m":
				if (confirm.toString().contains("m") || 
						confirm.toString().contains("f") ||
						confirm.toString().contains("r") ||
						confirm.toString().contains("a") ||
						confirm.toString().contains("d"))
					return -6;
				if(!argTwo(i, arguments, size))
					return -7;
				confirm.append('m');
				break;
			case "-f":
				if (confirm.toString().contains("m") || 
						confirm.toString().contains("f") ||
						confirm.toString().contains("r") ||
						confirm.toString().contains("a") ||
						confirm.toString().contains("d"))
					return -6;
				if(!argTwo(i, arguments, size))
					return -7;
				confirm.append('f');
				break;
			case "-r":
				if (confirm.toString().contains("m") || 
						confirm.toString().contains("f") ||
						confirm.toString().contains("r") ||
						confirm.toString().contains("a") ||
						confirm.toString().contains("d"))
					return -6;
				if (!argTwo(i, arguments, size) && !argOne(i, arguments, size) && size != i)
					return -7;
				confirm.append('r');
				break;
			case "-a":
				if (confirm.toString().contains("m") || 
						confirm.toString().contains("f") ||
						confirm.toString().contains("r") ||
						confirm.toString().contains("a") ||
						confirm.toString().contains("d"))
					return -6;
				if(!argTwo(i, arguments, size))
					return -7;
				confirm.append('a');
				break;
			case "-d":
				if (confirm.toString().contains("m") || 
						confirm.toString().contains("f") ||
						confirm.toString().contains("r") ||
						confirm.toString().contains("a") ||
						confirm.toString().contains("d"))
					return -6;
				if(!argTwo(i, arguments, size))
					return -7;
				confirm.append('d');
				break;
			}
		}
		return 1;
	}
	private static boolean argTwo(int i , String [] args, int size){
		if(i+2 > size)
			return false;
		if (flags.contains(args[i+1]) || flags.contains(args[i+2]))
			return false;
		return true;
	}
	//Se tiver um unico argumento ah frente da flag
	private static boolean argOne(int i, String [] args, int size){
		if (i+1 > size)
			return false;
		if (flags.contains(args[i+1]))
			return false;
		return true;
	}
	/**
	 * Guarda uma file mensagem com o contacto
	 * do destinatario e com o conteudo da mensagem
	 * @param contact contacto do destinatario
	 * @param mess conteudo da mensagem
	 */
	public void doMoperation(String contact, String mess) {
		System.out.println("doMoperation com destinatario: " + contact + " e com mensagem " + mess);
	}

	/**
	 * vai buscar um ficheiro que foi enviado a um contacto
	 * @param contact contacto da pessoa que recebeu o ficheiro
	 * @param fich nome do ficheiro
	 */
	public void doFoperation(String contact, String fich) {
		System.out.println("doFoperation com contacto: " + contact + " e com ficheiro " + fich);	
	}

	/**
	 * vai buscar tudo o que foi enviado e recebido para todos os contactos
	 */
	public void doR0operation() {
		System.out.println("doR0operation");		
	}

	/**
	 * vai buscar tudo o que foi enviado e recebido para um so contacto ou grupo
	 * @param contact contacto ou grupo do qual se quer ver tudo o que foi enviado e recebido
	 */
	public void doR1operation(String contact) {
		System.out.println("doR1operation com contacto: " + contact);		
	}

	/**
	 * 
	 * @param contact
	 * @param fich
	 */
	public void doR2operation(String contact, String fich) {
		System.out.println("doR2operation com contacto: " + contact + " e com ficheiro " + fich);
	}
	
	/**
	 * adiciona um user a um grupo
	 * @param user contacto do utilizador
	 * @param group nome do grupo
	 */
	public void doAoperation(String user, String group) {
		System.out.println("doAoperation com user: " + user + " e com grupo " + group);		
	}

	/**
	 * remove um utilizador de um grupo
	 * @param user contacto do utilizador
	 * @param group nome do grupo
	 */
	public void doDoperation(String user, String group) {
		System.out.println("doDoperation com user: " + user + " e com grupo " + group);		
	}
}







