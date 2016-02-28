package domain.server;

import java.util.ArrayList;
import java.util.HashMap;

public class User {

	private String username;
	private String pass;
	private ArrayList <String> contacts;
	private HashMap<String, ArrayList<Message>> contacts_messages;
	private ArrayList <String> groups;
	private Message Lastmessage;
	
	public User (String username, String pass){
		this.username = username;
		this.pass = pass;
		contacts_messages = new HashMap<String, ArrayList<Message>>();
		contacts = new ArrayList <> ();
		groups = new ArrayList <> ();
	}
	
	public String getUsername (){
		return this.username;
	}
	public String getPass (){
		return this.pass;
	}
	public boolean addContact (String username){
		if (username == null || contacts.contains(username))
			return false;
		contacts.add(username);
		contacts_messages.put(username, new ArrayList<Message>());
		return true;
	}
	
	public boolean rmContact (String username){
		if (username == null || !contacts.contains(username))
			return false;
		contacts.remove(username);
		return true;
	}
	
	public boolean addGroup (String group){
		if (group == null || groups.contains(group))
			return false;
		groups.add(group);
		return true;
	}
	
	public boolean rmGroup (String group){
		if (group == null || !groups.contains(group))
			return false;
		groups.remove(group);
		return true;
	}


}


