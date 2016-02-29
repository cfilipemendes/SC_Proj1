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

	private server_skell skell;
	
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
		
		//cria um skell do servidor
		skell = new server_skell();

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

		ServerThread(Socket inSoc, server_skell skell) {
			socket = inSoc;
			this.skell = skell;
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
				
				int numArgs = (int) inStream.readObject();
				String username = (String) inStream.readObject();
				
				//Validar e confirmar que se encontra tudo correcto
				
				
				String aux;
				//recepcao de parametros do client
				for(int i = 0; i < numArgs; i++){
					aux = (String) inStream.readObject();
					switch (aux) {
					case "-p":
						skell.authenticate((String)inStream.readObject(), username);
					}
				}
				
				outStream.close();
				inStream.close();

				socket.close();

			} catch (IOException | ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
	}

}
