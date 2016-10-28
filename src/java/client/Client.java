package java.client;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class Client extends JFrame {
	
	private JTextField userText;
	private JTextArea chatWindow;
	private ObjectOutputStream output;
	private ObjectInputStream input;
	private Socket connection;
	private String serverIp;
	private String message = "";
	
	public Client(String host){
		super("Cliet");
		serverIp = host;
		userText = new JTextField();
		userText.setEditable(false);
		userText.addActionListener(
				new ActionListener(){

					@Override
					public void actionPerformed(ActionEvent e) {
						sendMessage(e.getActionCommand());
						userText.setText("");
					}
					
				}
		);
		add(userText, BorderLayout.NORTH);
		chatWindow = new JTextArea();
		add(new JScrollPane(chatWindow), BorderLayout.CENTER);
		setSize(300, 150);
		setVisible(true);
	}

	public void startRunning(){
		try{
			connectToServer();
			setupStreams();
			whileChatting();
		}catch(IOException e) {
			showMessage(" Server terminated connection... ");
		}finally{
			closeDown();
		}
	}

	private void closeDown() {
		showMessage(" /n Closing down... ");
		ableToType(false);
		
		try{
			output.close();
			input.close();
			connection.close();
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	
	private void sendMessage(String message) {
		
		try{
			output.writeObject("CLIENT - " + message);
			output.flush();
			showMessage("/nCLIENT - " + message);
		}catch(IOException e){
			chatWindow.append("Oops..something went wrong.");
		}
		
	}

	private void showMessage(final String message) {
		SwingUtilities.invokeLater(
				new Runnable(){

					@Override
					public void run() {
						chatWindow.append(message);						
					}
					
				}
		);
				
	}

	private void whileChatting() throws IOException {
		ableToType(true);
		do{
			try{
				message = (String) input.readObject();
				showMessage(" /n " + message);
			}catch(ClassNotFoundException e){
				showMessage(" Well this is awkward... ");
			}
		}while(!message.equals("SERVER - END"));
	}

	private void setupStreams() throws IOException {
		output = new ObjectOutputStream(connection.getOutputStream());
		output.flush();
		input = new ObjectInputStream(connection.getInputStream());
		
				
	}

	private void connectToServer() throws IOException {
		showMessage(" Attempting to connect to the server..... ");
		connection = new Socket(InetAddress.getByName(serverIp), 6789);
		showMessage("Connected to " + connection.getInetAddress().getHostAddress());
				
	}
	
	private void ableToType(final boolean tof){
		SwingUtilities.invokeLater(
				new Runnable(){

					@Override
					public void run() {
						chatWindow.setEditable(tof);
					}
					
				}
		);
	}
	

}
