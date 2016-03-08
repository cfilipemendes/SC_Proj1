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
	private final String GROUPS_DIR = "groups";
	private final String USERS_DIR = "users";
	private final int PW_ERROR = -66;
	private final int ARGS_ERROR = -67;
	private final int REG_ERROR = -68;
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
		skell = new server_skell(USERS_PWS_FILE,GROUPS_DIR, USERS_DIR);

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
						if (skell.isGroup(username) == null){
							skell.createUser(username,password);
						}
						else{
							outStream.writeObject(REG_ERROR);
							closeThread();
							return;
						}
					}
					else{ // senao, como EXISTE USER faz autenticacao
						while(!pwAux.equals(password)){
							System.out.println("Nao fez a autenticacao, user:" + username + " : " + password);
							outStream.writeObject(PW_ERROR);
							password = (String) inStream.readObject();
						}
					}
					outStream.writeObject(1);//correu tudo bem com a autenticacao
					
					numArgs = (int) inStream.readObject();


					String [] arguments = new String [numArgs];
					//recepcao de parametros do client
					for(int i = 0; i < numArgs; i++){
						arguments [i]= (String) inStream.readObject();
					}

					//Se a recepcao de parametros nao for fiavel
					if (skell.validate (arguments) != 1){
						outStream.writeObject(ARGS_ERROR);
						closeThread();
						return;
					}

					else{
						int confirm = 1;
						outStream.writeObject(1);//correu tudo bem com os argumentos recebidos
						if (arguments.length != 0){
							switch(arguments[0]){
							case "-m":
								if (skell.isUser(arguments[1]) != null)
									skell.doMoperation(arguments[1],arguments[2],username);
								else if (skell.isGroup(arguments[1]) != null)
									skell.doMGroupOperation(arguments[1],arguments[2],username);
								else
									confirm = -1;
								break;
							case "-f":
								skell.doFoperation(arguments[1],arguments[2]);
								break;
							case "-r":
								if (numArgs == 1)
									skell.doR0operation();
								else if (numArgs == 2)
									skell.doR1operation(arguments[1]);
								else
									skell.doR2operation(arguments[1],arguments[2]);
								break;
							case "-a":
								if (skell.isUser(arguments[1]) != null){
									if (skell.isUser(arguments[2]) == null){
										confirm = skell.doAoperation(arguments[1],arguments[2],username);
									}
									else
										confirm = REG_ERROR;
								}
								else
									confirm = -1;
								break;
							case "-d":
								confirm = skell.doDoperation(arguments[1],arguments[2],username);
								break;
							}
							System.out.println("Confirm = " + confirm);

						}
					}

				}catch (ClassNotFoundException e1) {
					e1.printStackTrace();
				}	


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
				e.printStackTrace();
			}


		}
	}

}
