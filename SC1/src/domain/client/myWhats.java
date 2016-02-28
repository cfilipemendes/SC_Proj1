package domain.client;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.regex.Pattern;

public class myWhats {


	private static final String flags = "-p-m-f-r-a-d";
	private static final Pattern PATTERN = Pattern.compile(
			"^(([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.){3}([01]?\\d\\d?|2[0-4]\\d|25[0-5])$");

	public static void main (String [] args) throws UnknownHostException, IOException, ClassNotFoundException{
		if (args.length < 3){
			System.err.println("Input insuficiente");
			return;
		}
		if (args.length > 3)
			if (!flags.contains(args[3])){
				System.err.println("Input incorrecto");
				return;
			}

		String userName = args[1];
		String ip = args[2].split(":")[0];
		String port = args[2].split(":")[1];

		if (!validIP(ip)){
			System.err.println("IP invalido");
			return;
		}

		//Ligacao socket
		Socket soc = new Socket(ip, Integer.parseInt(port));

		//Abertura das Streams
		ObjectInputStream in = new ObjectInputStream(soc.getInputStream());
		ObjectOutputStream out = new ObjectOutputStream(soc.getOutputStream());


		// Validate

		out.writeObject(args);
		int fromServer = (int) in.readObject();
		verifyOutput(fromServer);
		
		in.close();
		out.close();
		soc.close();
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

	public static boolean validIP(final String ip) {
		return PATTERN.matcher(ip).matches();
	}
}
