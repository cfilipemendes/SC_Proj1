package domain.client;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;
import java.util.regex.Pattern;

public class myWhats {

	private static StringBuilder y;
	private static final String flags = "-p-m-f-r-a-d";
	private static final Pattern PATTERN = Pattern.compile(
			"^(([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.){3}([01]?\\d\\d?|2[0-4]\\d|25[0-5])$");

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

		String pwd = null;
		if (valid == -10){
			pwd = retryPwd();
			if (pwd.equals("q"))
				return;
		}

		//Ligacao socket
		//Socket soc = new Socket(ip, Integer.parseInt(port));

		//Abertura das Streams
		//ObjectInputStream in = new ObjectInputStream(soc.getInputStream());
		//ObjectOutputStream out = new ObjectOutputStream(soc.getOutputStream());

		//Modelizacao do array de envio ao servidor!
		String [] argsFinal;
		if (pwd != null){
			argsFinal = new String [args.length];
			argsFinal[0] = "-p";
			argsFinal[1] = pwd;
			int x = 2;
			for (int i = 2; i < argsFinal.length; i++){
				argsFinal[i] = args[x];
				x++;
			}
		}
		else{
			argsFinal = new String [args.length-2];
			int x = 2;
			for (int i = 0; i < argsFinal.length; i++){
				argsFinal[i] = args[x];
				x++;
			}
		}

		System.out.print("O argsFinal eh: ");
		for (int i = 0; i < argsFinal.length; i++) {
			System.out.print(argsFinal[i] + " + ");
		}
		System.out.println();
		/*
		//envia o numero de parametros
		out.writeObject(argsFinal.length);
		//envia o username
		out.writeObject(args[1]);

		int fromServer = (int) in.readObject();
		if (fromServer < 0){
			System.out.println("Oistras NO gambas!");
		}
		else
			System.out.println("Oistras YES gambas!");

		in.close();
		out.close();
		soc.close();*/
	}

	private static String retryPwd(){
		Scanner sc = new Scanner(System.in);
		System.out.println("Por favor insira a PASSWORD (ou 'Q' para sair):");
		String pwd = sc.nextLine();
		pwd = pwd.toLowerCase();
		sc.close();
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

	//onde eu quiser
	//-1 x == null
	//-2 flag -p n e a primeira
	//-3 argumento da pass e uma flag
	//-4 argumentos insuficientes para a pass
	//-5 flag invalida a seguir ao -p
	//-6 ordem errada da flag
	//-7 argumentos das flag invalidos
	//-10 falta password
	public static int validate (String [] x){
		y = new StringBuilder ();
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
				if(!arguments(i, x, size))
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
				if(!arguments(i, x, size))
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
				if (!arguments(i, x, size) || !argument(i, x, size) || size == i)
					return -7;
				y.append('r');
				break;
			case "-a":
				if (y.toString().contains("m") || 
						y.toString().contains("f") ||
						y.toString().contains("r") ||
						y.toString().contains("a") ||
						y.toString().contains("d"))
					return -6;
				if(!arguments(i, x, size))
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
				if(!arguments(i, x, size))
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
	private static boolean arguments(int i , String [] args, int size){
		if(i+2 > size)
			return false;
		if (flags.contains(args[i+1]) || flags.contains(args[i+2]))
			return false;
		return true;
	}
	//Se tiver um unico argumento ah frente da flag
	private static boolean argument(int i, String [] args, int size){
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
