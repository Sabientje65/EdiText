package nl.dcictwebs102859.notities_2_0;

import java.awt.event.*;
import java.io.*;
import java.net.*;
import javax.swing.*;

public class Server_Class{
    private ServerSocket serverSock;
    private Socket connection;
    private BufferedOutputStream out;
    private BufferedReader in;
    private final Thread t;
    boolean started;
    
    JFrame frame;
    JButton start;
    
    Server_Class(){
        t = new Thread(new startRunning());
        
        if (Login_Class.loginId == 1 | Login_Class.loginId == 2){
            frame = new JFrame("Server");
            frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
            
            start = new JButton("Start server");
            start.addActionListener(new ActionListener(){
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (!started){
                        try{
                            serverSock = new ServerSocket(6666);
                            t.start();
                            JOptionPane.showMessageDialog(null, "Server started successfully!");
                            started = true;
                        }catch(IOException ioe){
                            JOptionPane.showMessageDialog(null, "Error, failed to create server socket.");
                        }
                    }
                    else
                        JOptionPane.showMessageDialog(null, "The server is already running, you wanker.");
                }
            });
            
            frame.add(start);
            frame.setSize(150, 100);
            frame.setResizable(false);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        }
    }
    
    class startRunning implements Runnable{
        
        public void run(){
            while(true){
                if(serverSock == null)
                    return;
                
                try{
                    try{
                        connection = serverSock.accept();
                        
                        out = new BufferedOutputStream(connection.getOutputStream());
                        out.flush();
                        in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                        
                        PrintWriter write = new PrintWriter(out, true);
                        
                        String inputLine = null;
                        String outputLine = null;

                        while(true){
                            if (inputLine != null)
                                break;
                            
                            inputLine = in.readLine();
                        }
                        

                        if(inputLine != null){
                            if(inputLine.contains("@usr")){
                                if (inputLine.substring(0, 4).equals("@usr")){
                                    if(inputLine.contains("\\") || inputLine.contains("'") || !inputLine.contains("@pass")){
                                        outputLine = "Fuck off, wanker";
                                        write.println(outputLine);
                                    }
                                    else{
                                        outputLine = "id " + Database_Class.mobileLoginCheck(inputLine);
                                        write.println(outputLine);
                                    }
                                }
                            }
                            
                            if(inputLine.contains("@bids")){
                                if(inputLine.substring(0, 5).equals("@bids")){
                                    outputLine = Database_Class.mobileGetBlobs(inputLine);
                                    write.println(outputLine);
                                }
                            }
                            
                            if(inputLine.contains("@view")){
                                if(inputLine.substring(0, 5).equals("@view")){
                                    outputLine = Database_Class.mobileBlobView(inputLine);
                                    write.println(outputLine);
                                }
                            }
                            
                            if(inputLine.contains("@save")){
                                if(inputLine.substring(0, 5).equals("@save")){
                                    outputLine = Database_Class.saveMobileBlob(inputLine);
                                    write.println(outputLine);
                                }
                            }
                            
                            if(inputLine.contains("@create")){
                                if(inputLine.substring(0, 7).equals("@create")){
                                    outputLine = Database_Class.createMobileBlob(inputLine);
                                    write.println(outputLine);
                                }
                            }
                            
                            if(inputLine.contains("@delete")){
                                if(inputLine.substring(0, 7).equals("@delete")){
                                    outputLine = Database_Class.deleteMobileBlob(inputLine);
                                    write.println(outputLine);
                                }
                            }
                            
                            if(inputLine.contains("@reg")){
                                if(inputLine.substring(0, 4).equals("@reg")){
                                    outputLine = Database_Class.createMobileAccount(inputLine);
                                    write.println(outputLine);
                                }
                            }
                        }
                        
                        System.out.println("Received: " + inputLine);
                        System.out.println("Send: " + outputLine);
                        
                        write.close();
                        out.close();
                        in.close();
                        connection.close();
                    }catch(EOFException eofe){
                        errorPane("Error, end of file exception.");
                    }
                }catch(IOException ioe){

                }
            }
        }
    }
    
    private void errorPane(String st){
        JOptionPane.showMessageDialog(null, st);
    }
}