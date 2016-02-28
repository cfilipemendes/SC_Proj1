package domain.server;

import java.util.ArrayList;

public class Group {

	private String groupname;
	private ArrayList <String> elements;
	private ArrayList <Message> mensagens;
	private Message Lastmess;
	
	
	public Group(String groupname) {
		this.groupname = groupname;
	}
	
	public String getGroupname() {
		return groupname;
	}

	public ArrayList<String> getElements() {
		return elements;
	}
	
	public ArrayList<Message> getMensagens() {
		return mensagens;
	}
	
	public Message getLastmess() {
		return Lastmess;
	}
	
	public void setLastmess(Message lastmess) {
		Lastmess = lastmess;
	}
	
	public boolean addContact(String username){
		if(elements.contains(username) || username == null)
			return false;
		elements.add(username);
		return true;
	}
	
	public boolean rmContact(String username){
		if(!elements.contains(username) || username == null)
			return false;
		else
			elements.remove(username);
		return true;
	}
}
