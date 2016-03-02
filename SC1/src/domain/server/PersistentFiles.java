package domain.server;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;

public class PersistentFiles {

	
	private BufferedReader br;
	private File users;
	private String groups;
	private Date data;
	private SimpleDateFormat sdf;

	public PersistentFiles(String usersFile, String groupsDir) {
		users = new File(usersFile + ".txt");
		sdf = new SimpleDateFormat("dd-MM-yyyy_HH-mm-ss");
		groups = groupsDir;
		if(!users.exists())
			try {
				users.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		File dir = new File(groupsDir);
		if (!dir.exists())
			dir.mkdir();
	}

	public boolean checkUserPwd(String pwd, String username) throws IOException {
		br = new BufferedReader(new FileReader(users));
		String line;
		while((line = br.readLine()) != null){
			if(line.split(":")[0].equals(username) && line.split(":")[1].equals(pwd)){
				br.close();
				return true;
			}
		}
		br.close();
		return false;
	}

	public String hasUser(String username) throws IOException {
		br = new BufferedReader(new FileReader(users));
		String line;
		while((line = br.readLine()) != null){
			if(line.split(":")[0].equals(username)){
				br.close();
				return line.split(":")[1];
			}
		}
		br.close();
		return null;
	}

	public void addUser(String username, String password) {
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(users,true));
			bw.append(username + ":" + password);
			bw.newLine();
			bw.flush();
			bw.close();
			File dir = new File(username);
			if (!dir.exists())
				dir.mkdir();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void newMessage(String to, String mess, String from) {
		try {
			data = GregorianCalendar.getInstance().getTime();
			File message = new File (new File(".").getAbsolutePath() + "//" + from + "//" + from + "_" + to + "_" + sdf.format(data) + ".txt");
			if (!message.exists())
				message.createNewFile();
			BufferedWriter bw = new BufferedWriter(new FileWriter(message));	
			bw.write(mess);
			bw.flush();
			bw.close();
			File messageTo = new File (new File(".").getAbsolutePath() + "//" + to + "//" + from + "_" + to + "_" + sdf.format(data) + ".txt");
			if (!messageTo.exists())
				messageTo.createNewFile();
			bw = new BufferedWriter(new FileWriter(messageTo));	
			bw.write(mess);
			bw.flush();
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void createGroup (String groupname, String creator){
		File group = new File (new File(".").getAbsolutePath() + groups + "//" + groupname + "//.txt");
		if (!group.exists())
			try {
				group.createNewFile();
				BufferedWriter bw = new BufferedWriter(new FileWriter(group));	
				bw.write(creator);
				bw.flush();
				bw.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
	}
	public boolean addUserToGroup (String groupname, String user){
		return false;
	}
	public String hasGroup (){
		return groups;
		//TO-DO autp generated metoid , yes
	}


}
