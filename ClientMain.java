package assignment7;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;


public class ClientMain extends Application {
	GridPane grid1;
	GridPane grid2;
	Stage groupmessage;
	Stage privatemessage;
	TextArea outgoingGroup;
	TextArea outgoingPrivate;
	TextField incomingGroup;
	TextField incomingPrivate;
	Button sendGroup;
	Button sendPrivate;
	ChoiceBox<String> choiceBoxPrivate;
	ComboBox<String> combobox;
	private BufferedReader reader;
	private PrintWriter writer;
	String userName;
	public Boolean privateflag = false;
	
	
	

	@Override
	public void start(Stage primaryStage) throws Exception {
		new ClientMain().login();
	}
	
	public void login() throws Exception{
		setUpNetworking();
		Stage login = new Stage();
		login.setTitle("Login");
		GridPane grid= new GridPane();
		grid.setPadding(new Insets(10,10,10,10));
		grid.setVgap(8);
		grid.setVgap(10);
	
		//name label
		
		Label usernamelabel = new Label("Username");
		GridPane.setConstraints(usernamelabel,0,0);
		
		TextField entry = new TextField();
		GridPane.setConstraints(entry,2,1);
		
		Button loginbutton  = new Button();
		loginbutton.setText("Login");
		GridPane.setConstraints(loginbutton, 2, 2);
		loginbutton.setOnAction(e -> {
			try {
				login.close();
				userName = entry.getText();
				ServerMain.usernames.add(userName);
				chatWindow();
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		});
		grid.getChildren().addAll(usernamelabel, entry, loginbutton);
		
		Scene scene = new Scene(grid, 300,120);
		login.setScene(scene);
		login.show();
		
		
	}
	
	public void chatWindow() throws Exception{
		groupmessage = new Stage();
		privatemessage = new Stage();
		groupmessage.setTitle("Group Chat(Signed in as "+ userName+")");
		privatemessage.setTitle("Private Chat(Signed in as "+ userName +")");
		 grid1= new GridPane();
		 grid2= new GridPane();
		
		grid1.setPadding(new Insets(10,10,10,10));
		grid1.setVgap(8);
		grid1.setVgap(10);
		grid2.setPadding(new Insets(10,10,10,10));
		grid2.setVgap(8);
		grid2.setVgap(10);
		
		outgoingGroup = new TextArea();
		GridPane.setConstraints(outgoingGroup, 0, 5);
		outgoingPrivate = new TextArea();
		GridPane.setConstraints(outgoingPrivate, 0, 0);
		outgoingGroup.setWrapText(true);
		outgoingGroup.setPrefSize(500,300);
		outgoingPrivate.setWrapText(true);
		outgoingPrivate.setPrefSize(200, 200);
		
		incomingGroup = new TextField();
		GridPane.setConstraints(incomingGroup, 0,0);
		incomingGroup.setPrefSize(10, 10);
		
		incomingPrivate = new TextField();
		GridPane.setConstraints(incomingGroup, 7,0);
		incomingPrivate.setPrefSize(10, 10);
		
		sendGroup = new Button();
		GridPane.setConstraints(sendGroup,5,4);
		sendGroup.setText("Send Group Message");
		sendGroup.setOnAction(e -> {
			groupMessage();
			
		});
		sendPrivate = new Button();
		sendPrivate.setText("Send Private Message");
		sendPrivate.setOnAction(e -> {
			privateMessage();
		});
		
		choiceBoxPrivate = new ChoiceBox<String>();
		combobox = new ComboBox<String>();
		combobox.getItems().addAll(ServerMain.usernames);
		
		System.out.println(ServerMain.usernames);
		
		
		
		grid1.getChildren().addAll(outgoingGroup, incomingGroup, sendGroup);
		grid2.getChildren().addAll(outgoingPrivate, incomingPrivate, sendPrivate, combobox);
		
		Scene scene1 = new Scene(grid1, 500,450);
		Scene scene2 = new Scene(grid2, 500,400);
		groupmessage.setScene(scene1);
		privatemessage.setScene(scene2);
		
		groupmessage.show();
		privatemessage.show();
	}

	

	private void privateMessage() {
		String message = "P" + userName+ ": "+ incomingPrivate.getText();
		privateflag = true;
		writer.println(message);
		writer.flush();
		incomingPrivate.clear();
		
		
	}

	private void groupMessage() {
		String message = "G" + userName+ ": "+ incomingGroup.getText();
		//String message = incomingGroup.getText();
		writer.println(message);
		writer.flush();
		incomingGroup.clear();
		
		
	}

	private void setUpNetworking() throws Exception {
		@SuppressWarnings("resource")
		Socket sock = new Socket("127.0.0.1", 4248);
		InputStreamReader streamReader = new InputStreamReader(sock.getInputStream());
		reader = new BufferedReader(streamReader);
		writer = new PrintWriter(sock.getOutputStream());
		System.out.println("networking established");
		Thread readerThread = new Thread(new IncomingReader());
		readerThread.start();
	}


	public static void main(String[] args) {
		launch(args);	
	}

	class IncomingReader implements Runnable {
		public void run() {
			String message;
			try {
				while ((message = reader.readLine()) != null) {
					if(message.charAt(0) == 'G'){
						StringBuilder str = new StringBuilder();
						str.append(message.substring(1));
						outgoingGroup.appendText(str + "\n");
					}else if(message.charAt(0) == 'P'){
						StringBuilder str = new StringBuilder();
						str.append(message.substring(1));
						outgoingPrivate.appendText(str + "\n");;
					}
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}
}
