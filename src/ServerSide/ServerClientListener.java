package ServerSide;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Arrays;

public class ServerClientListener extends Thread{
	private Socket whoAmI;
	private Server myServer;
	private DataInputStream listenToMe;
	private DataOutputStream whoListenToMe;
	private String myName;
	private String companionName;
	private boolean finish=false;
	public ServerClientListener(Server serv,Socket newUser) {
		whoAmI=newUser;
		myServer=serv;
		try {
			listenToMe=new DataInputStream(whoAmI.getInputStream());
			whoListenToMe=new DataOutputStream(whoAmI.getOutputStream());
			detectName();
		} catch (IOException e) {
			System.out.println("Стримы не открылись");
			e.printStackTrace();
		}
		
	}
	public void sendClients(String[] names){
		try {
			for(int i=0;i<names.length-1;++i){
					myServer.getClientOutputStream(myName).writeUTF(names[i]);
			}
			
			if(names!=null && names.length>0){
				System.out.println(Arrays.toString(names));
				myServer.getClientOutputStream(myName).writeUTF(names[names.length-1]+Server.ALL_USERS_SEND);
			}
			start();
		} catch (IOException e) {
			System.out.println("Ошибка в посылке списка пользователей");
			e.printStackTrace();
		}
	}
	private void detectName() {
		String name;
		while(true){
			try {
				name=listenToMe.readUTF();
				if(name!=null){
					myName=name;
					break;
				}
			} catch (IOException e) {
				System.out.println("Имя не прочиталось");
				e.printStackTrace();
			}
		}
	}
	public String getUserName(){
		return myName;
	}
	private void registerNewListener(String name){
		whoListenToMe=(DataOutputStream) myServer.getClientOutputStream(name);
		companionName=name;
		try {
			whoListenToMe.writeUTF(Server.NEW_USER_WRITING_TO_YOU+myName);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	private void leaveThat(){
		try {			
			myServer.getClientOutputStream(myName).writeUTF((char)4+"");
		    //myServer.getClientOutputStream(companionName).writeUTF(Server.NEW_USER_WRITING_TO_YOU+companionName);
			listenToMe.close();
			//whoListenToMe.close();
			whoAmI.close();
			myServer.disconnectUser(myName);
		} catch (IOException e) {
			System.out.println("Юзер не удалился");
			e.printStackTrace();
		}
		
	}
	@Override
	public void run(){
		try {
			while(!finish){		
					String message=listenToMe.readUTF();
					if(message!=null && message.charAt(0)==Server.NEW_USER_COME_TO_SERVER) registerNewListener(message.substring(1));
					else if(message.charAt(0)==Server.USER_LEFT_SERVER){
						leaveThat();
						finish=true;
						break;
					}
					else whoListenToMe.writeUTF(message);				
			}
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			//leaveThat();
		}
	}

}
