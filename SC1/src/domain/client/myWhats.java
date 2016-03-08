package domain.client;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;
import java.util.regex.Pattern;

public class myWhats {

	private static Scanner sc;
	private static Socket soc;
	private static ObjectInputStream in;
	private static ObjectOutputStream out;
	private static final String flags = "-p-m-f-r-a-d";
	private static final Pattern PATTERN = Pattern.compile(
			"^(([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.){3}([01]?\\d\\d?|2[0-4]\\d|25[0-5])$");
	private final static int PW_ERROR = -66;
	private final static int ARGS_ERROR = -67;
	private final static int REG_ERROR = -68;

	public static void main (String [] args) throws UnknownHostException, IOException, ClassNotFoundException{
		
		if (args.length < 2){
			System.err.println("Input insuficiente");
			return;
		}
		else if (args.length > 2)
			if (!flags.contains(args[2])){
				System.err.println("Input incorrecto");
				return;
			}

		if (args.length > 7)
			System.err.println("Input excede o esperado");

		String userName = args[0];
		String ip = args[1].split(":")[0];
		String port = args[1].split(":")[1];
		//verifica se o IP eh vahlido!
		if (!validIP(ip)){
			System.err.println("IP invalido");
			return;
		}

		int valid = validate(args);
		if (valid != 1 && valid != -10){
			verifyOutput(valid);
			return;
		}
		
		sc = new Scanner (System.in);

		String pwd = null;
		if (valid == -10)
			pwd = retryPwd(sc);

		//Ligacao socket
		soc = new Socket(ip, Integer.parseInt(port));

		//Abertura das Streams
		in = new ObjectInputStream(soc.getInputStream());
		out = new ObjectOutputStream(soc.getOutputStream());

		//Modelizacao do array de envio ao servidor!
		String [] argsFinal;
		if (pwd != null){
			argsFinal = new String [args.length-2];
			int x = 2;
			for (int i = 0; i < argsFinal.length; i++){
				argsFinal[i] = args[x];
				x++;
			}
		}
		else{
			argsFinal = new String [args.length-4];
			pwd=args[3];
			int x = 4;
			for (int i = 0; i < argsFinal.length; i++){
				argsFinal[i] = args[x];
				x++;
			}
		}
		
		//envia o username
		out.writeObject(userName);
		out.writeObject(pwd);
		int fromServer = (int) in.readObject();
		while(fromServer == PW_ERROR){
			System.err.print("Password ERRADA! ");
			pwd = retryPwd(sc);
			out.writeObject(pwd);
			fromServer = (int) in.readObject();
		}
		if (fromServer == REG_ERROR){
			System.err.println("Nome ja existente!");
			closeCon();
			return;
		}

		//envia o numero de argumentos
		out.writeObject(argsFinal.length);
		//envia todos os argumentos
		for (int i = 0; i < argsFinal.length; i++){
			out.writeObject(argsFinal[i]);
		}
		
		//verifica se os dados foram bem recebidos pelo servidor
		fromServer = (int) in.readObject();
		if (fromServer == ARGS_ERROR){
			System.err.println("O servidor recebeu dados CORROMPIDOS!");
			closeCon();
			return;
		}
		else if (fromServer == REG_ERROR){
			System.err.println("Nome ja existente!");
			closeCon();
			return;
		}
	
		//Continuar aqui!!!!!!!
		//
		//
		//
		//
		if (argsFinal [0].equals("-f")){
			
		}
		
		closeCon();
	}
	
	//close connection
	private static void closeCon (){
		try {
			out.close();
			in.close();
			sc.close();
			soc.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private static String retryPwd(Scanner sc){
		System.out.println("Por favor insira a PASSWORD:");
		String pwd = null;
		pwd = sc.nextLine();
		return pwd;
	}

	private static void verifyOutput(int fromServer) {
		switch(fromServer){
		case 1:
			System.out.println("Oistras gambas!");
			break;
		case -1:
			System.out.println("Argumentos recebidos a null!");
			break;
		case -2:
			System.out.println("Ordem da flag -p invalida");
			break;
		case -3:
			System.out.println("Argumento da pass e uma flag!");
			break;
		case -4:
			System.out.println("Argumentos insuficientes para a password!");
			break;
		case -5:
			System.out.println("Flag invalida a seguir ao -p!");
			break;
		case -6:
			System.out.println("Ordem errada das flags");
			break;
		case -7:
			System.out.println("Argumentos das flags invalidos!");
			break;
		}

	}

	//-1 x == null
	//-2 flag -p n e a primeira
	//-3 argumento da pass e uma flag
	//-4 argumentos insuficientes para a pass
	//-5 flag invalida a seguir ao -p
	//-6 ordem errada da flag
	//-7 argumentos das flag invalidos
	//-10 falta password
	public static int validate (String [] x){
		StringBuilder y = new StringBuilder ();
		if (x == null)
			return -1;

		int size = x.length-1;

		for (int i = 2; i <= size; i++){
			switch(x[i]){
			case "-p":
				if(y.length() != 0)
					return -2;
				if(i+1 <= size){
					if(flags.contains(x[i+1])){
						return -3;
					}
				}else
					return -4;

				if(i+2 < size)
					if(!flags.contains(x[i+2]))
						return -5;
				y.append('p');
				break;
			case "-m":
				if (y.toString().contains("m") || 
						y.toString().contains("f") ||
						y.toString().contains("r") ||
						y.toString().contains("a") ||
						y.toString().contains("d"))
					return -6;
				if(!argTwo(i, x, size))
					return -7;
				y.append('m');
				break;
			case "-f":
				if (y.toString().contains("m") || 
						y.toString().contains("f") ||
						y.toString().contains("r") ||
						y.toString().contains("a") ||
						y.toString().contains("d"))
					return -6;
				if(!argTwo(i, x, size))
					return -7;
				y.append('f');
				break;
			case "-r":
				if (y.toString().contains("m") || 
						y.toString().contains("f") ||
						y.toString().contains("r") ||
						y.toString().contains("a") ||
						y.toString().contains("d"))
					return -6;
				if (!argTwo(i, x, size) && !argOne(i, x, size) && size != i){
					return -7;
				}
				y.append('r');
				break;
			case "-a":
				if (y.toString().contains("m") || 
						y.toString().contains("f") ||
						y.toString().contains("r") ||
						y.toString().contains("a") ||
						y.toString().contains("d"))
					return -6;
				if(!argTwo(i, x, size))
					return -7;
				y.append('a');
				break;
			case "-d":
				if (y.toString().contains("m") || 
						y.toString().contains("f") ||
						y.toString().contains("r") ||
						y.toString().contains("a") ||
						y.toString().contains("d"))
					return -6;
				if(!argTwo(i, x, size))
					return -7;
				y.append('d');
				break;
			}
		}
		if (!y.toString().contains("p"))
			return -10;
		return 1;
	}		

	//Se tiver dois argumentos ah frente da flag
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

	//Verifica o IP dado pelo utilizador
	public static boolean validIP(final String ip) {
		return PATTERN.matcher(ip).matches();
	}
}
