package nl.dcictwebs102859.notities_2_0;

import javax.swing.*;
import java.awt.*;

public class Login_Class {
    int paneResult;
    static String username, password;
    static boolean admin;
    static int loginId;
    
    JTextField usernameInput = new JTextField(50);
    JPasswordField passwordInput = new JPasswordField(50);

    JPanel loginPane = new JPanel();
    JPanel usernamePane = new JPanel();
    JPanel passwordPane = new JPanel();

    public Login_Class(){
        GUI_Class.setLookAndFeel(loginPane);
        
        loginPane.setLayout(new BorderLayout());
        usernamePane.add(new JLabel("Username: "));
        usernamePane.add(usernameInput);

        passwordPane.add(new JLabel("Password: "));
        passwordPane.add(passwordInput);

        loginPane.add(usernamePane, BorderLayout.NORTH);
        loginPane.add(passwordPane, BorderLayout.SOUTH);
        
        accountCheck();
    }
    
    private void accountCheck(){
        paneResult = JOptionPane.showConfirmDialog(null, "Do you have an account?");
        
        if (paneResult == JOptionPane.YES_OPTION){
            login();
        }
        else if (paneResult == JOptionPane.NO_OPTION){
            paneResult = JOptionPane.showConfirmDialog(null, "Would you like to create an account?");
            
            if (paneResult == JOptionPane.YES_OPTION){
                createAccount("Please enter your desired username and password");
            }
        }
    }
    
    private void login(){
        paneResult = JOptionPane.showConfirmDialog(null, loginPane, "Please enter your log in credentials.", JOptionPane.OK_CANCEL_OPTION);

        if (paneResult == JOptionPane.OK_OPTION){
            username = usernameInput.getText();
            password = passwordInput.getText();

            if(Database_Class.loginCheck(username, password)){
                JOptionPane.showMessageDialog(null, "Hello " + username + ", you have succesfully logged in.");
                new GUI_Class();
            }
            else{
                paneResult = JOptionPane.showConfirmDialog(null, "Your log in credentials are incorrect. Would you like to try to log in again?");

                if (paneResult == JOptionPane.YES_OPTION)
                    login();
            }
        }
    }
    
    private void createAccount(String message){
        paneResult = JOptionPane.showConfirmDialog(null, loginPane, message, JOptionPane.OK_CANCEL_OPTION);

        username = usernameInput.getText();
        password = passwordInput.getText();

        if (username.length() != 0 && password.length() != 0 && username.length() <= 50 && password.length() <= 50 && paneResult == JOptionPane.YES_OPTION){
            if(Database_Class.createAccount(username, password))
                login();
            else
                createAccount("The desired username is already in use, please choose another username.");
        }
        else if (paneResult == JOptionPane.YES_OPTION)
            createAccount("Please enter a valid username and password.");
    }
    
    public static void main(String[] args){
        new Login_Class();
    }
}