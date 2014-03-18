package ServerSide;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

public class Server {
	public static final char NEW_USER_COME_TO_SERVER=1;
	public static final char ALL_USERS_SEND=5;
	public static final char NEW_USER_WRITING_TO_YOU=3;
	
	private ServerSocket mainSock;
	HashMap<String,DataOutputStream> dataBase;
	public Server(int port){
		try {
			mainSock=new ServerSocket(port);
		} catch (IOException e) {
			System.out.println("Не удалось создать ServerSocket");
			e.printStackTrace();
		}
		dataBase=new HashMap<String,DataOutputStream>();
		listen(port);
	}
	public DataOutputStream getClientOutputStream(String name){
		//System.out.println("Вот что спрашивает:"+name);
		return dataBase.get(name);
	}
	public void disconnectUser(String name){
		dataBase.remove(name);
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
	private void sendNewUserNameForEveryone(String name){
		for(DataOutputStream out:dataBase.values()){
			try {
				out.writeUTF(Server.NEW_USER_COME_TO_SERVER+name);
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
				sendNewUserNameForEveryone(name);
				dataBase.put(name, new DataOutputStream(newUser.getOutputStream()));
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
