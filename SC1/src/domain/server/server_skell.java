package domain.server;

import java.util.HashMap;

public class server_skell {
	
	private HashMap <String,User> userMap;
	private HashMap <String,Group> groupMap;
	
	public server_skell (){
		userMap = new HashMap <> ();
		groupMap = new HashMap <> ();
	}

	public HashMap<String, User> getUserMap() {
		return userMap;
	}

	public HashMap<String, Group> getGroupMap() {
		return groupMap;
	}
	
	public boolean authenticate (String pwd, String username){
		return false;
	}

	
	/*
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
	
	private boolean verifyPw(String user, String newPw) {
			return newPw.equals(skell.getUserMap().get(user).getPass());
		}*/
}
