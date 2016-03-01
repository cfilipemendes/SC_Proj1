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

//Servidor do servico myWhatsServer

public class myWhatsServer{

	private final String USERS_PWS_FILE = "usersAndPws";
	private final String GROUPS_FILE = "usersAndPws";
	private final int PW_ERROR = -66;
	private server_skell skell;
	
	public static void main(String[] args) {
		System.out.println("servidor: main");
		myWhatsServer server = new myWhatsServer();
		server.startServer(Integer.parseInt(args[0]));
	}

	@SuppressWarnings("resource")
	public void startServer (int port){
		ServerSocket sSoc = null;
		try {
			sSoc = new ServerSocket(port);
		} catch (IOException e) {
			System.err.println(e.getMessage());
			System.exit(-1);
		}
		
		//cria um skell do servidor
		skell = new server_skell(USERS_PWS_FILE,GROUPS_FILE);

		while(true) {
			try {
				Socket inSoc = sSoc.accept();
				ServerThread newServerThread = new ServerThread(inSoc,skell);
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
		private server_skell skell;
		ObjectOutputStream outStream;
		ObjectInputStream inStream;

		ServerThread(Socket inSoc, server_skell skell) {
			socket = inSoc;
			this.skell = skell;
			System.out.println("thread do server para cada cliente");
		}

		public void run(){
			try {
				outStream = new ObjectOutputStream(socket.getOutputStream());
				inStream = new ObjectInputStream(socket.getInputStream());
				int numArgs;
				String username,password;
				try {
					
					
					username = (String) inStream.readObject();
					password = (String) inStream.readObject();
					String pwAux;
					//Primeiro verifica que se nao houver user ele eh criado
					if((pwAux = skell.isUser(username)) == null){
						skell.createUser(username,password);
					}else{ // senao, como EXISTE USER faz autenticacao
						while(!pwAux.equals(password)){
							System.out.println("Nao fez a autenticacao, user:" + username + " : " + password);
							outStream.writeObject(PW_ERROR);
							password = (String) inStream.readObject();
						}
					}
					outStream.writeObject(1);
					numArgs = (int) inStream.readObject();		
					
					
					String aux;
					//recepcao de parametros do client
					for(int i = 0; i < numArgs; i++){
						aux = (String) inStream.readObject();
					}
					
					System.out.println("thread: depois de receber os args");
				}catch (ClassNotFoundException e1) {
					e1.printStackTrace();
				}	
				outStream.writeObject(0);
				
				closeThread();

			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		private void closeThread() {
			try {
				outStream.close();
				inStream.close();

				socket.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
		}
	}

}
