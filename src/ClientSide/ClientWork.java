package ClientSide;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import ServerSide.Server;

public class ClientWork extends Shell {
	private Socket mySocket;
	private DataOutputStream toServer;
	private DataInputStream fromServer;
	private String currentListeningСompanion;
	private String myName;
	private String[] contacts;
	private HashMap<String, StringBuilder> history;
	private HashMap<Integer, String> writers;

	/**
	 * Create the composite.
	 * 
	 * @param parent
	 * @param style
	 */

	public ClientWork(Display parent, String name, String host, int port) {
		super(parent);
		history = new HashMap<String, StringBuilder>();
		writers = new HashMap<Integer, String>();
		try {
			mySocket = new Socket(host, port);
			toServer = new DataOutputStream(mySocket.getOutputStream());
			fromServer = new DataInputStream(mySocket.getInputStream());
			myName = name;
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

	public void setToWriters(int num, String name) {
		System.out.println("Вставил:" + name);
		writers.put(num, name);
	}

	public String getFromWriters(int num) {
		return writers.get(num);
	}

	public void removeFromWriters(int num) {
		writers.remove(num);
	}

	public void getOnlineUsers() {
		ArrayList<String> data = new ArrayList<String>();
		String message;
		while (true) {
			try {
				message = fromServer.readUTF();
				System.out.println("Message:" + message);
				if (message.charAt(0) == Server.ALL_USERS_SEND) { // сделать
																	// первым
					data.add(message.substring(1));
					break;
				}
				data.add(message);
			} catch (IOException e) {
				System.out.print("Имена не пришли");
				e.printStackTrace();
			}

		}
		contacts = new String[data.size()];
		for (int i = 0; i < data.size(); ++i) {
			System.out.println(data.get(i));
			history.put(data.get(i), new StringBuilder());
			contacts[i] = data.get(i);
		}

	}

	public void sendNormalMessage(String message) {
		try {
			toServer.writeUTF(message);
		} catch (IOException e) {
			System.out.println("На сервер не пошло");
			e.printStackTrace();
		}
	}

	public String getMyName() {
		return myName;
	}

	private void sendName(String s) {
		sendNormalMessage(s);
	}

	public void registerNewListeningCompanion(String name) {
		currentListeningСompanion = name;
		sendNormalMessage(Server.WANT_WRITE_TO + name);
	}

	public void appendInHistory(String name, String message) {
		if (!history.containsKey(name))
			history.put(name, new StringBuilder().append(message));
		else
			history.get(name).append(message);
	}

	public String getFromHistory(String username) {
		String h = history.get(username).toString();
		return h == null ? "" : h;
	}

	public String readFromServer() {
		try {
			String s = null;
			if (!mySocket.isClosed()) {
				s = fromServer.readUTF();
			}
			return s;
		} catch (IOException e) {
			System.out.println("Не пришло от сервера");
			e.printStackTrace();
		}
		return null;
	}

	public void closeAll() {
		try {
			toServer.close();
			fromServer.close();
			mySocket.close();
		} catch (IOException e) {
			System.out.println("Не закрылись");
			e.printStackTrace();
		}

	}

	public String getCurrentListeningCompanion() {
		return currentListeningСompanion;
	}

	public String[] getCurrentFriends() {
		return contacts;
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

}
