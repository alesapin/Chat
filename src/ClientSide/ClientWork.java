package ClientSide;


import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class ClientWork extends Shell {
	private Socket mySocket;
	private DataOutputStream toServer;
	private DataInputStream fromServer;
	private String currentListeningСompanion;
	private String currentWritingCompanion;
	private String myName;
	private String[] contacts;
	

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */

	public ClientWork(Display parent,String name,String host,int port) {
		super(parent,SWT.SHELL_TRIM & (~SWT.RESIZE));
		try {
			mySocket=new Socket(host,port);
			toServer=new DataOutputStream(mySocket.getOutputStream());
			fromServer=new DataInputStream(mySocket.getInputStream());
			myName=name;
			sendName(myName);
			getOnlineUsers();
		} catch (UnknownHostException e) {
			System.out.println("Нет хоста");
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("Не создался сокет");
			e.printStackTrace();
		}
	}
	public void registerNewWritingCompanion(String name){
		currentWritingCompanion=name.substring(1);
	}
	public String getCurrentWritingCompanion(){
		return currentWritingCompanion;
	}
	public void getOnlineUsers(){
		ArrayList<String> data=new ArrayList<String>();
		String message;
		while(true){
			
			try {
				message=fromServer.readUTF();
				System.out.println("Message:"+message);
				
				if(message.charAt(message.length()-1)==5){
					data.add(message.substring(0, message.length()-1));
					break;
				}
				data.add(message);
			} catch (IOException e) {
				System.out.print("Имена не пришли");
				e.printStackTrace();
			}
			
		}
		contacts=new String[data.size()];
		for(int i=0;i<data.size();++i){
			contacts[i]=data.get(i);
		}
	}
	public void sendNormalMessage(String message){
		try {
			toServer.writeUTF(message);
		} catch (IOException e) {
			System.out.println("На сервер не пошло");
			e.printStackTrace();
		}
	}
	public String getMyName(){
		return myName;
	}
	private void sendName(String s){
		sendNormalMessage(s);
	}
	public void registerNewListeningCompanion(String name){
		currentListeningСompanion=name;
		StringBuilder b=new StringBuilder();
		b.append((char)1);
		b.append(name);
		sendNormalMessage(b.toString());
	}
	
	public String readFromServer(){
		try {
			return fromServer.readUTF();
		} catch (IOException e) {
			System.out.println("Не пришло от сервера");
			e.printStackTrace();
		}
		return null;
	}
	public String getCurrentCompanion(){
		return currentListeningСompanion;
	}
	public String[] getCurrentFriends(){
		return contacts;
	}
	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

}
