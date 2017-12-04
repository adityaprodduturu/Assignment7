package assignment7;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Observable;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;


public class ServerMain extends Observable {
	public static ArrayList<String> usernames = new ArrayList<String>();
	
	public static void main(String[] args) {
		try {
			new ServerMain().setUpNetworking();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void setUpNetworking() throws Exception {
		@SuppressWarnings("resource")
		ServerSocket serverSock = new ServerSocket(4248);
		while (true) {
			Socket clientSocket = serverSock.accept();
			ClientObserver writer = new ClientObserver(clientSocket.getOutputStream());
			Thread t = new Thread(new ClientHandler(clientSocket));
			t.start();
			this.addObserver(writer);
			System.out.println("got a connection");
		}
	}
	class ClientHandler implements Runnable {
		private BufferedReader reader;

		public ClientHandler(Socket clientSocket) {
			Socket sock = clientSocket;
			try {
				reader = new BufferedReader(new InputStreamReader(sock.getInputStream()));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		public void run() {
			String message;
			try {
				while ((message = reader.readLine()) != null) {
					if(message.charAt(0) == 'G'){
						group(message);
					}else if(message.charAt(0) == 'P'){
						privatem(message);
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		private void privatem(String message) {
			//write only to the sender and reciever 
			System.out.println("server read "+message);
			setChanged();
			//message = message.substring(1);
			notifyObservers(message);
			
		}

		private void group(String message) {
			System.out.println("server read "+message);
			setChanged();
			//message = message.substring(1);
			notifyObservers(message);	
		}
	}
}