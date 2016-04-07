/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package serverside;
import javax.swing.*;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/**
 *
 * @author Reborn
 */
public class TCPServer extends Thread
{
     public static final int SERVERPORT = 4444;
    // while this is true the server will run
    private boolean running = false;
    // used to send messages
    private PrintWriter bufferSender;
    // callback used to notify new messages received
    private OnMessageReceived messageListener;
    private ServerSocket serverSocket;
    private Socket client;
 
    /**
     * Constructor of the class
     *
     * @param messageListener listens for the messages
     */
    public TCPServer(OnMessageReceived messageListener) {
        this.messageListener = messageListener;
    }
 
    public static void main(String[] args) {
 
        //opens the window where the messages will be received and sent
        MainScreen frame = new MainScreen();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
 
    }
 
    /**
     * Close the server
     */
    public void close() {
 
        running = false;
 
        if (bufferSender != null) {
            bufferSender.flush();
            bufferSender.close();
            bufferSender = null;
        }
 
        try {
            client.close();
            serverSocket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
 
        System.out.println("S: Done.");
        serverSocket = null;
        client = null;
 
    }
 
    /**
     * Method to send the messages from server to client
     *
     * @param message the message sent by the server
     */
    public void sendMessage(String message) {
        if (bufferSender != null && !bufferSender.checkError()) {
            bufferSender.println(message);
            bufferSender.flush();
        }
    }
 
    public boolean hasCommand(String message) {
     
        if(message!=null){
            if(message.compareTo("End")!=0)
                return false;
            else
            {
                runServer();
                return true;
            }
        }
        return false;
     }
 
    /**
     * Builds a new server connection
     */
    private void runServer() {
        running = true;
 
        try {
            System.out.println("S: Connecting...");
 
            //create a server socket. A server socket waits for requests to come in over the network.
            serverSocket = new ServerSocket(SERVERPORT);
 
            //create client socket... the method accept() listens for a connection to be made to this socket and accepts it.
            client = serverSocket.accept();
 
            System.out.println("S: Receiving...");
 
            try {
 
                //sends the message to the client
                bufferSender = new PrintWriter(new BufferedWriter(new OutputStreamWriter(client.getOutputStream())), true);
 
                //read the message received from client
                BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
 
                //in this while we wait to receive messages from client (it's an infinite loop)
                //this while it's like a listener for messages
                while (running) {
 
                    String message = null;
                    try {
                        message = in.readLine();
                    } catch (IOException e) {
                        System.out.println("Error reading message: " + e.getMessage());
                    }
 
                    if (hasCommand(message)) {
                        continue;
                    }
 
                    if (message != null && messageListener != null) {
                        //call the method messageReceived from ServerBoard class
                        messageListener.messageReceived(message);
                    }
                }
 
            } catch (Exception e) {
                System.out.println("S: Error in Running");
                e.printStackTrace();
            }
 
        } catch (Exception e) {
            System.out.println("S: Error in Run Server");
            e.printStackTrace();
        }
    }
 
    @Override
    public void run() {
        super.run();
 
        runServer();
 
    }
    
 
    //Declare the interface. The method messageReceived(String message) will must be implemented in the ServerBoard
    //class at on startServer button click
    public interface OnMessageReceived {
        public void messageReceived(String message);
    }
 
}
