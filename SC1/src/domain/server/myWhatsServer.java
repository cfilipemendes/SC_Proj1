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
	
	//flag da password fica a true se houver pass
	private static boolean pass = false;
	private static String flags = "-p-m-f-r-a-d";
	private static StringBuilder y;
	//onde eu quiser
	//-1 x == null
	//-2 flag -p n e a primeira
	//-3 argumento da pass e uma flag
	//-4 argumentos insuficientes para a pass
	//-5 flag invalida a seguir ao -p
	//-6 ordem errada da flag
	//-7 argumentos das flag invalidos
	public static int validate (String [] x){
		y = new StringBuilder ();
		if (x == null)
			return -1;
		
		int size = x.length;

		for (int i = 3; i < size; i++){
			switch(x[i]){
			case "-p":
				if(y.length() != 0)
					return -2;
				if(i+1 < size){
					if(flags.contains(x[i+1])){
						return -3;
					}
				}else
					return -4;
							
				if(i+2 < size)
					if(!flags.contains(x[i+2]))
						return -5;
				y.append('p');
				pass = true;
				break;
			case "-m":
				if (y.toString().contains("m") || 
					y.toString().contains("f") ||
					y.toString().contains("r") ||
					y.toString().contains("a") ||
					y.toString().contains("d"))
					return -6;
				if(!arguments(i, x, size))
					return -7;
				y.append('m');
				break;
			case "-f":
				if ( y.toString().contains("f") ||
					 y.toString().contains("r") ||
					 y.toString().contains("a") ||
					 y.toString().contains("d"))
					return -6;
				if(!arguments(i, x, size))
					return -7;
				y.append('f');
				break;
			case "-r":
				if (y.toString().contains("r") ||
					y.toString().contains("a") ||
					y.toString().contains("d"))
					return -6;
				y.append('r');
				break;
			case "-a":
				if (y.toString().contains("a") ||
					y.toString().contains("d"))
					return -6;
				if(!arguments(i, x, size))
					return -7;
				y.append('a');
				break;
			case "-d":
				if (y.toString().contains("d"))
					return -6;
				if(!arguments(i, x, size))
					return -7;
				y.append('d');
				break;
			}
	}			
		return 1;
	}		
	

	private static boolean arguments(int i , String [] args, int size){
		if(i+2 >= size)
			return false;
		if (flags.contains(args[i+1]) || flags.contains(args[i+2]))
			return false;
		
		if(i+3 < size)
			if(!flags.contains(args[i+3]))
				return false;
		
		return true;
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
 			
				//TODO: refazer
				//este codigo apenas exemplifica a comunicao entre o cliente e o servidor
				//nao faz qualquer tipo de autenticao
			
				outStream.writeObject(validate(args));
				
				

				outStream.close();
				inStream.close();
 			
				socket.close();

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
