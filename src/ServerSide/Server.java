package ServerSide;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

public class Server {
	public static final char NEW_USER_COME_TO_SERVER=16;
	public static final char USER_LEFT_SERVER=17;
	public static final char ALL_USERS_SEND=18;
	public static final char WANT_WRITE_TO=19;
	public static final char YOU_CAN_CLOSE=20;
	public static final char USER_WRITING_TO_YOU=21;
	public static final char USER_FINISH_WRITING=22;
	
	private ServerSocket mainSock;
	HashMap<String,UserOutputStream> dataBase;
	public Server(int port){
		try {
			mainSock=new ServerSocket(port);
		} catch (IOException e) {
			System.out.println("Не удалось создать ServerSocket");
			e.printStackTrace();
		}
		dataBase=new HashMap<String,UserOutputStream>();
		listen(port);
	}
	public DataOutputStream getClientOutputStream(String name){
		return dataBase.get(name);
	}
	public void disconnectUser(String name){
		System.out.println("Дисконекчу : "+name);
		try {
			dataBase.get(name).close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		dataBase.remove(name);
		sendMessageForEveryone(Server.USER_LEFT_SERVER+name);
	}
	public String[] getAllOnlineUsers(){
		int len=dataBase.size();
		String[] data=new String[len];
		int i=0;
		for(String s:dataBase.keySet()){
			data[i++]=s;
		}
		return data;
	}
	private void sendMessageForEveryone(String msg){
		for(DataOutputStream out:dataBase.values()){
			try {
				out.writeUTF(msg);
			} catch (IOException e) {
				System.out.println("Имя до всех не дошло");
				e.printStackTrace();
			}
		}
	}
	private void listen(int port){
		while(true){
			try {
				Socket newUser=mainSock.accept();
				ServerClientListener usr=new ServerClientListener(this,newUser);
				String name=usr.getUserName();
				if(dataBase.containsKey(name)) continue;
				sendMessageForEveryone(Server.NEW_USER_COME_TO_SERVER+name);
				dataBase.put(name, new UserOutputStream(newUser.getOutputStream()));
				usr.sendClients(getAllOnlineUsers());
			} catch (IOException e) {
				System.out.println("Сокет не получен");
				e.printStackTrace();
			}
		}
	}
	public static void main(String[] args){
		new Server(5000);
	}
}
