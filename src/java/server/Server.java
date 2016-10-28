package java.server;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class Server extends JFrame {
	
	private static final long serialVersionUID = 1L;
	
	private JTextField userText;
	private JTextArea chatWindow;
	private ObjectOutputStream output;
	private ObjectInputStream input;
	private ServerSocket server;
	private Socket connection;
	
	public Server(){
		super("Instant Messaging");
		userText = new JTextField();
		userText.setEditable(false);
		userText.addActionListener(
				new ActionListener(){
					public void actionPerformed(ActionEvent e) {
						sendMessage(e.getActionCommand());
						userText.setText("");	
					}	
				}
		);
		add(userText, BorderLayout.NORTH);
		chatWindow = new JTextArea();
		add(new JScrollPane(chatWindow));
		setSize(300,150);
		setVisible(true);
	}
	
	public void startRunning(){
		try{
			server = new ServerSocket(6789, 100);
			while(true){
				try{
					waitForConnections();
					setupStreams();
					whileChatting();
				}catch(EOFException e){
					showMessage(" /n Server has ended connection! ");
				}finally{
					closeDown();
				}
			}
		}catch(IOException e){
			
		}
	}
	
	private void sendMessage(String message){
		try {
			output.writeObject("SERVER -" + message);
			output.flush();
			showMessage("/n SERVER - " + message);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void closeDown() {
		showMessage("/n Closing connection..");
		ableToType(false);
		try{
			output.close();
			input.close();
			connection.close();
		}catch(IOException e){
			e.printStackTrace();
		}
		
	}

	private void whileChatting() throws IOException {
		String message = " You are now connected! ";
		sendMessage(message);
		ableToType(true);
		do{
			try{
				message = (String) input.readObject();
			}catch(ClassNotFoundException e){
				showMessage(" /n I don't know what the user sent! ");
			}
			
		}while(!message.equals("CLIENT - END"));
				
	}

	private void setupStreams() throws IOException {
		
		output = new ObjectOutputStream(connection.getOutputStream());
		output.flush();
		input = new ObjectInputStream(connection.getInputStream());
		showMessage(" /n Start talking! /n ");
				
	}

	private void waitForConnections() throws IOException {
		showMessage(" Waiting for connection... ");
		connection = server.accept();
		showMessage(" Now connected to " + connection.getInetAddress().getHostAddress());
				
	}
	
	private void showMessage(final String text){
		SwingUtilities.invokeLater(
				new Runnable(){
					public void run(){
						chatWindow.append(text);
					}
				}
		);
	}
	
	private void ableToType(final boolean tof){
		SwingUtilities.invokeLater(
				new Runnable(){
					public void run(){
						userText.setEditable(tof);
					}
				}
		);
	}
	

}
