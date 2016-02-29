package domain.server;

/***************************************************************************
 *  
 *
 *
 ***************************************************************************/

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

//Servidor do servico myWhatsServer

public class myWhatsServer{

	private HashMap <String,User> userMap;
	private HashMap <String,Group> groupMap;

	public static void main(String[] args) {
		System.out.println("servidor: main");
		myWhatsServer server = new myWhatsServer();
		server.startServer();
	}

	public void startServer (){
		ServerSocket sSoc = null;

		try {
			sSoc = new ServerSocket(23456);
		} catch (IOException e) {
			System.err.println(e.getMessage());
			System.exit(-1);
		}

		userMap = new HashMap <> ();
		groupMap = new HashMap <> ();

		while(true) {
			try {
				Socket inSoc = sSoc.accept();
				ServerThread newServerThread = new ServerThread(inSoc);
				newServerThread.start();
			}
			catch (IOException e) {
				e.printStackTrace();
			}

		}
		//sSoc.close();
	}


	//Threads utilizadas para comunicao com os clientes
	class ServerThread extends Thread {

		private Socket socket = null;

		ServerThread(Socket inSoc) {
			socket = inSoc;
			System.out.println("thread do server para cada cliente");
		}

		public void run(){
			try {
				ObjectOutputStream outStream = new ObjectOutputStream(socket.getOutputStream());
				ObjectInputStream inStream = new ObjectInputStream(socket.getInputStream());

				String [] args = null;

				try {
					args = (String [])inStream.readObject();
					System.out.println("thread: depois de receber os args");
				}catch (ClassNotFoundException e1) {
					e1.printStackTrace();
				}
				
				String newPw = "";
				
				outStream.writeObject(test);
				
				//verifica que ha erro ou ha falta de pass
				if (test != 1 || test != -10){
					outStream.writeObject(test);
					this.currentThread().interrupt();
					return;
				}
				//verifica se nao existe password
				else if (test == -10){
					while(true){	
						outStream.writeObject(test);
						newPw = (String)inStream.readObject();
						//verifica se o hashmap nao contem o utilizador indicado
						if (!userMap.containsKey(args[1])){
							userMap.put(args[1], new User (args[1],newPw));
							break;
						}
						//pass incorrecta
						if (!verifyPw(args[1],newPw))
							test = -11;
						//tudo correcto
						else{
							break;
						}
					}
				}
				else{
					if (!userMap.containsKey(args[1]))
						userMap.put(args[1], new User (args[1],args[4]));
					//pass incorrecta
					else if (!verifyPw(args[1],args[4])){
						while(true){
							//-11 significa pass incorrecta
							outStream.writeObject(-11);
							newPw = (String)inStream.readObject();
							if (verifyPw(args[1],newPw))
								break;
						}
					}
				}
				//esta correcto, fez login com sucesso
				//realiza a doOperations(args)
				doOperations(args);

				
				outStream.close();
				inStream.close();

				socket.close();

			} catch (IOException | ClassNotFoundException e) {
				e.printStackTrace();
			}
		}

		private void doOperations(String[] args) {
			
		}

		private boolean verifyPw(String user, String newPw) {
			return newPw.equals(userMap.get(user).getPass());
		}
	}

}
