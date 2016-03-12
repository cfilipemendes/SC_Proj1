package domain.server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class server_skell {

	private static PersistentFiles files;
	private static final String flags = "-p-m-f-r-a-d";

	/**
	 * 
	 * @param usersFile
	 * @param groupsDir
	 * @param usersDir
	 */
	public server_skell (String usersFile, String groupsDir, String usersDir){
		files = new PersistentFiles(usersFile,groupsDir,usersDir);
	}

	/**
	 * 
	 * @param pwd
	 * @param username
	 * @return
	 * @throws IOException
	 */
	public boolean authenticate (String pwd, String username) throws IOException{
		return files.checkUserPwd(pwd,username);
	}

	/**
	 * 
	 * @param username
	 * @return
	 * @throws IOException
	 */
	public String isUser(String username) throws IOException {
		return files.hasUser(username);
	}

	/**
	 * 
	 * @param username
	 * @param password
	 */
	public void createUser(String username, String password) {
		files.addUser(username,password);
	}

	/**
	 * 
	 * @param groupname
	 * @return
	 * @throws IOException
	 */
	public String isGroup(String groupname) throws IOException{
		return files.hasGroup(groupname);
	}

	/**
	 * 
	 * @param groupname
	 * @param user
	 * @return
	 */
	public boolean hasUserInGroup(String groupname, String user){
		return files.hasUserInGroup(groupname, user);

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
	/**
	 * 
	 * @param i
	 * @param args
	 * @param size
	 * @return
	 */
	private static boolean argTwo(int i , String [] args, int size){
		if(i+2 > size)
			return false;
		if (flags.contains(args[i+1]) || flags.contains(args[i+2]))
			return false;
		return true;
	}
	/**
	 * 
	 * @param i
	 * @param args
	 * @param size
	 * @return
	 */
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
	public void doMoperation(String to, String mess, String from) {
		System.out.println("doMoperation com destinatario: " + to + " e com mensagem " + mess);
		files.newMessage(to, mess, from);
	}
	
	public void doMGroupOperation(String groupname, String mess, String from) {
		System.out.println("doMGroupOperation com grupo: " + groupname + " e com mensagem " + mess);
		files.newGroupMessage(groupname, mess, from);
	}

	/**
	 * recebe um ficheiro que foi enviado a um contacto
	 * @param contact contacto da pessoa que recebeu o ficheiro
	 * @param fich nome do ficheiro
	 * @param username 
	 */
	public void doFoperation(String contact, String fich, String username, int fileSize,ObjectInputStream inStream) {
		files.saveFile(contact,fich,username,fileSize,inStream);
		System.out.println("doFoperation com contacto: " + contact + " e com ficheiro " + fich);
	}
	
	public void doFoperationGroup(String contact, String fich, String username, int fileSize,ObjectInputStream inStream) {	
		files.saveFileGroup(contact,fich,username,fileSize,inStream);
		System.out.println("doFoperation com contacto: " + contact + " e com ficheiro " + fich);
	}

	/**
	 * vai buscar a ultima coisa que foi enviada ou recebida para cada contacto
	 * @param username 
	 * @param outStream 
	 */
	public void doR0operation(String username, ObjectOutputStream outStream) {
		System.out.println("doR0operation");
		files.getLatestConvs(username,outStream);
	}

	/**
	 * vai buscar tudo o que foi enviado e recebido para um so contacto ou grupo
	 * @param contact contacto ou grupo do qual se quer ver tudo o que foi enviado e recebido
	 * @param outStream 
	 * @param contact 
	 */
	public int doR1operation(String username, String contact, ObjectOutputStream outStream,boolean user) {
		System.out.println("doR1operation com contacto: " + contact);
		return files.getContactConv(username,contact,outStream,user);
	}

	/**
	 * 
	 * @param contact
	 * @param fich
	 * @return 
	 */
	public int doR2operation(String from,String contact, String fich,ObjectOutputStream outStream,boolean user) {
		System.out.println("doR2operation com contacto: " + contact + " e com ficheiro " + fich);
		return files.getFile(from,contact,fich,outStream,user);

	}

	/**
	 * adiciona um user a um grupo
	 * @param user contacto do utilizador
	 * @param group nome do grupo
	 * @throws IOException 
	 */
	public int doAoperation(String user, String group, String from) throws IOException {
		System.out.println("doAoperation com user: " + user + " e com grupo " + group);
		int confirm = 1;
		if (from.equals(user))
			return -5;
		String creator;
		if((creator = files.hasGroup(group)) != null){
			if(creator.equals(from)){
				if (!files.hasUserInGroup(group,user)){
					files.addUserToGroup(group,user);
				}
				else
					confirm = -6;

			}
			else
				confirm = -8;

		}
		else{
			files.createGroup(group,from);
			files.addUserToGroup(group,user);
		}

		return confirm;
	}

	/**
	 * remove um utilizador de um grupo
	 * @param user contacto do utilizador
	 * @param group nome do grupo
	 * @return 
	 * @throws IOException 
	 */
	public int doDoperation(String user, String group, String from) throws IOException {
		System.out.println("doDoperation com user: " + user + " e com grupo " + group);
		String creator;
		int confirm = 1;
		if((creator = files.hasGroup(group)) != null){
			if(creator.equals(from)){
				if(files.hasUserInGroup(group,user)){
					files.rmFromGroup(group,user);
				}
				else
					confirm = -7;
			}
			else
				confirm = -8;
		}
		else
			confirm = -9;
		return confirm;
	}

}







