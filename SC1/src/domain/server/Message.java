package domain.server;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;

public class Message {

	private String from;
	private String to;
	private String mess;
	private Date date;
	
	public Message(String from, String to, String mess){
		this.from = from;
		this.to = to;
		this.mess = mess;
		setTime();
		
	}
	private void setTime() {
		date = GregorianCalendar.getInstance().getTime();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		System.out.println(sdf.format(date));
		
	}
	
	private Date getTime() {
		return date;
	}
	
	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;
	}

}
