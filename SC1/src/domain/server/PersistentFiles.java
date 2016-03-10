package domain.server;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;

public class PersistentFiles {


	private static final int PACKET_SIZE = 1024;
	private BufferedReader br;
	private File users;
	private String groupsDir;
	private String usersDir;
	private Date data;
	private SimpleDateFormat sdf;

	public PersistentFiles(String usersFile, String groupsDir, String usersDir) {
		users = new File(usersFile + ".txt");
		sdf = new SimpleDateFormat("dd-MM-yyyy_HH-mm-ss");
		this.groupsDir = groupsDir;
		this.usersDir = usersDir;
		if(!users.exists())
			try {
				users.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		File dir = new File(usersDir);
		if (!dir.exists())
			dir.mkdir();
		dir = new File(groupsDir);
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
			File dir = new File (new File(".").getAbsolutePath() + "//" + usersDir + "//" + username);
			if (!dir.exists())
				dir.mkdir();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void newMessage(String to, String mess, String from) {
		File dir = new File (new File(".").getAbsolutePath() + "//" + usersDir + "//" + from + "//" + to);
		if (!dir.exists())
			dir.mkdir();
		try {
			data = GregorianCalendar.getInstance().getTime();
			File message = new File (new File(".").getAbsolutePath() + "//" + usersDir + "//" + from + "//" + to + "//" + from + "_" + to + "_" + sdf.format(data) + ".txt");
			message.createNewFile();
			BufferedWriter bw = new BufferedWriter(new FileWriter(message));	
			bw.write(mess);
			bw.flush();
			bw.close();

			dir = new File (new File(".").getAbsolutePath() + "//" + usersDir + "//" + to + "//" + from);
			if (!dir.exists())
				dir.mkdir();
			File messageTo = new File (new File(".").getAbsolutePath() + "//" + usersDir + "//" + to + "//" + from + "//" + from + "_" + to + "_" + sdf.format(data) + ".txt");
			messageTo.createNewFile();
			bw = new BufferedWriter(new FileWriter(messageTo));	
			bw.write(mess);
			bw.flush();
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void newGroupMessage(String groupname, String mess, String from) {
		try {
			data = GregorianCalendar.getInstance().getTime();
			File message = new File (new File(".").getAbsolutePath() + "//" + groupsDir + "//" + groupname + "//" + from + "_" + groupname + "_" + sdf.format(data) + ".txt");
			message.createNewFile();
			BufferedWriter bw = new BufferedWriter(new FileWriter(message));	
			bw.write(mess);
			bw.flush();
			bw.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}


	}

	public void createGroup (String groupname, String creator){
		File dir = new File (new File(".").getAbsolutePath() + "//" + groupsDir + "//" + groupname);
		if (!dir.exists())
			dir.mkdir();
		File group = new File (new File(".").getAbsolutePath() + "//" + groupsDir + "//" + groupname + "//" + groupname + ".txt");


		try {
			if (!group.exists()){
				group.createNewFile();
				BufferedWriter bw = new BufferedWriter(new FileWriter(group));	
				bw.write(creator);
				bw.newLine();
				bw.flush();
				bw.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void addUserToGroup (String groupname, String user){
		File group = new File(new File(".").getAbsolutePath() + "//" + groupsDir + "//" + groupname + "//" + groupname + ".txt");
		BufferedWriter bw;
		try {
			bw = new BufferedWriter(new FileWriter(group,true));
			bw.append(user);
			bw.newLine();
			bw.flush();
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public String creatorOfGroup (String groupname){
		File group = new File(new File(".").getAbsolutePath() + "//" + groupsDir + "//" + groupname + "//" + groupname + ".txt");
		String creator = null;
		try {
			br = new BufferedReader(new FileReader(group));
			creator = br.readLine();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return creator;
	}

	public void rmFromGroup(String groupname, String user){
		File group = new File(new File(".").getAbsolutePath() + "//" + groupsDir + "//" + groupname + "//" + groupname + ".txt");
		if (creatorOfGroup (groupname).equals(user)){
			File groupDir = new File(new File(".").getAbsolutePath() + "//" + groupsDir + "//" + groupname);
			cleanDir(groupDir);
			groupDir.delete();
		}
		else{
			File temp = new File(new File(".").getAbsolutePath() + "//" + groupsDir + "//" + groupname + "//temp.txt");
			BufferedWriter bw;
			String line;
			try {
				br = new BufferedReader(new FileReader(group));
				bw = new BufferedWriter(new FileWriter(temp,true));
				while((line = br.readLine()) != null){
					if(!line.equals(user)){
						bw.append(line);
						bw.newLine();
						bw.flush();
					}
				}
				bw.close();
				br.close();
				if(group.delete())
					temp.renameTo(group);

			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void cleanDir (File dir) {
		for(File f : dir.listFiles()){
			f.delete();
		}
	}

	public boolean hasUserInGroup(String groupname, String user){
		File group = new File(new File(".").getAbsolutePath() + "//" + groupsDir + "//" + groupname + "//" + groupname + ".txt");
		String line = null;
		try {
			br = new BufferedReader(new FileReader(group));
			while((line = br.readLine()) != null){
				if(line.equals(user)){
					br.close();
					return true;
				}
			}
			br.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return false;
	}

	public String hasGroup (String groupname) throws IOException{
		File group = new File (new File(".").getAbsolutePath()+ "//" + groupsDir + "//" + groupname + "//" + groupname + ".txt");
		String readLine = null;
		if(group.exists()){
			br = new BufferedReader(new FileReader(group));
			readLine = br.readLine();
			br.close();
			return readLine;
		}
		return null;

	}

	public void saveFile(String contact, String fich, String username, int fileSize, ObjectInputStream inStream) {
		try {
			byte [] byteArray = new byte [fileSize];
			FileOutputStream fosFrom = new FileOutputStream(new File(".").getAbsolutePath() + 
					"//" + usersDir + "//"+ username + "//" + contact + "//" + username + "_" + contact + "_" + fich);
			BufferedOutputStream bosFrom = new BufferedOutputStream(fosFrom);
			FileOutputStream fosTo = new FileOutputStream(new File(".").getAbsolutePath() + 
					"//" + usersDir + "//" + contact + "//" + username + "//" + username + "_" + contact + "_" + fich);
			BufferedOutputStream bosTo = new BufferedOutputStream(fosTo);

			int current = 0;
			int bytesRead;
			int nCiclo = fileSize/PACKET_SIZE;
			int resto = fileSize%PACKET_SIZE;

			for (int i = 0; i < nCiclo; i++){
				bytesRead = inStream.read(byteArray, current,PACKET_SIZE);
				bosFrom.write(byteArray,current,bytesRead);
				bosFrom.flush();
				bosTo.write(byteArray,current,bytesRead);
				bosTo.flush();
				if (bytesRead > 0)
					current += bytesRead;
			}

			if (resto > 0){
				bytesRead = inStream.read(byteArray, current,resto);
				bosFrom.write(byteArray,current,bytesRead);
				bosFrom.flush();
				bosTo.write(byteArray,current,bytesRead);
				bosTo.flush();
			}
			bosFrom.close();
			bosTo.close();
			fosFrom.close();
			fosTo.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void saveFileGroup(String contact, String fich, String username, int fileSize, ObjectInputStream inStream) {
		try {
			byte [] byteArray = new byte [fileSize];
			FileOutputStream fos = new FileOutputStream(new File(".").getAbsolutePath() + 
					"//" + groupsDir + "//"+ contact + "//" + username + "_" + contact + "_" + fich);
			BufferedOutputStream bos = new BufferedOutputStream(fos);

			int current = 0;
			int bytesRead;
			int nCiclo = fileSize/PACKET_SIZE;
			int resto = fileSize%PACKET_SIZE;

			for (int i = 0; i < nCiclo; i++){
				bytesRead = inStream.read(byteArray, current,PACKET_SIZE);
				bos.write(byteArray,current,bytesRead);
				bos.flush();
				if (bytesRead > 0)
					current += bytesRead;
			}

			if (resto > 0){
				bytesRead = inStream.read(byteArray, current,resto);
				bos.write(byteArray,current,bytesRead);
				bos.flush();
			}
			bos.close();
			bos.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void getFile(String from,String contact, String fich, ObjectOutputStream outStream) {
		File myFile = new File (new File(".").getAbsolutePath() + 
				"//" + usersDir + "//" + contact + "//" +  fich);
		//File myFile = new File (new File(".").getAbsolutePath() + 
			//	"//" + usersDir + "//" + contact + "//" + from + "_" + contact + "_" + fich);
		int fileSize = (int) myFile.length();
		byte [] byteArray = new byte [fileSize];
		try {
			outStream.writeObject(fileSize);
			FileInputStream fis = new FileInputStream (myFile);
			BufferedInputStream bis = new BufferedInputStream (fis);
			int bytesRead;
			int current = 0; 


			int nCiclo = fileSize/PACKET_SIZE;
			int resto = fileSize%PACKET_SIZE;

			for (int i = 0; i < nCiclo; i++){
				bytesRead = bis.read(byteArray,current,PACKET_SIZE);
				outStream.write(byteArray,current,bytesRead);

				outStream.flush();

				if (bytesRead > 0)
					current += bytesRead;
			}
			if (resto > 0){
				bytesRead = bis.read(byteArray,current,resto);
				outStream.write(byteArray,current,bytesRead);
				outStream.flush();
			}

			bis.close();
			fis.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}




