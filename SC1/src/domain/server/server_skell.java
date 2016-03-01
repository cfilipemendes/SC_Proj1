package domain.server;

import java.io.IOException;
import java.util.HashMap;

public class server_skell {
	
	PersistentFiles files;
	private HashMap <String,User> userMap;
	private HashMap <String,Group> groupMap;
	
	public server_skell (String usersFile, String groupsFile){
		userMap = new HashMap <> ();
		groupMap = new HashMap <> ();
		files = new PersistentFiles(usersFile,groupsFile);
	}

	public HashMap<String, User> getUserMap() {
		return userMap;
	}

	public HashMap<String, Group> getGroupMap() {
		return groupMap;
	}
	
	public boolean authenticate (String pwd, String username) throws IOException{
		return files.checkUserPwd(pwd,username);
	}

	public String isUser(String username) throws IOException {
		return files.hasUser(username);
	}

	public void createUser(String username, String password) {
		files.addUser(username,password);
		
	}
}
