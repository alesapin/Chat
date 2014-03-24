package ServerSide;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.util.Arrays;

public class ServerClientListener extends Thread{
	private Socket whoAmI;
	private Server myServer;
	private DataInputStream listenToMe;
	private UserOutputStream whoListenToMe;
	private String myName;
	private int myWritingNumber;
	public ServerClientListener(Server serv,Socket newUser) {
		whoAmI=newUser;
		myServer=serv;
		try {
			listenToMe=new DataInputStream(whoAmI.getInputStream());
			whoListenToMe=new UserOutputStream(whoAmI.getOutputStream());
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
				myServer.getClientOutputStream(myName).writeUTF(Server.ALL_USERS_SEND+names[names.length-1]);
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
		try {
			whoListenToMe.freeNum(myWritingNumber);
			whoListenToMe.writeUTF(Server.USER_FINISH_WRITING+""+myWritingNumber);
		} catch (SocketException e1) {
			System.out.println("Уже закрылся");
		} catch(IOException e){
			e.printStackTrace();
		}
		whoListenToMe=(UserOutputStream) myServer.getClientOutputStream(name);
		myWritingNumber=whoListenToMe.getNum();
		try {
			whoListenToMe.writeUTF(Server.USER_WRITING_TO_YOU+""+(char)myWritingNumber+myName);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	private void leaveThat(){
		try {			
			myServer.getClientOutputStream(myName).writeUTF(Server.YOU_CAN_CLOSE+"");
			listenToMe.close();
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
			outer:while(true){		
					String message=listenToMe.readUTF();
					switch(message.charAt(0)){
						case Server.WANT_WRITE_TO:
							registerNewListener(message.substring(1));
							break;
						case Server.USER_LEFT_SERVER:
							leaveThat();
							break outer;
						default:
							whoListenToMe.writeUTF((char)myWritingNumber+message);				
							break;
					}

			}
		} catch (EOFException e) {
			myServer.disconnectUser(myName);
		} catch(IOException e){
			e.printStackTrace();
		}finally{
			//leaveThat();
		}
	}

}
