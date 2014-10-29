package nl.dcictwebs102859.notities_2_0;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class GUI_Class {
    JFrame frame;
    static JTabbedPane tabs;
    static JPanel userPane, usersPane, allBlobPane, editorPane, notesPane;
    static int currentBlobId = 0;
    
    public GUI_Class(){
        frame = new JFrame("Notes");
        tabs = new JTabbedPane();
        
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        if(Login_Class.admin){
            allBlobPane = Database_Class.getBlobs(Login_Class.admin);
            usersPane   = Database_Class.getUsers();
            tabs.addTab("All blobs", new JScrollPane(allBlobPane));
            tabs.addTab("All users", new JScrollPane(usersPane));
            tabs.addTab("This user", userPane);
            
            tabs.setEnabledAt(2, false);
        }
        
        notesPane = Database_Class.getBlobs(false);
        tabs.addTab("My Notes", new JScrollPane(notesPane));
        tabs.addTab("Editor", new JScrollPane(editorPane));
        
        if(Login_Class.admin)
            tabs.setEnabledAt(4, false);
        else
            tabs.setEnabledAt(1, false);
        
        tabs.addChangeListener(spareSomeChange);
        
        frame.add(tabs);
        
        frame.setResizable(false);
        frame.pack();
        frame.setSize(frame.getWidth(), 300);
        frame.setLocationRelativeTo(null);
        setLookAndFeel(frame);
        
        frame.setVisible(true);
        
        
        
        if(Login_Class.loginId == 1 | Login_Class.loginId == 2){
            Server_Class server = new Server_Class();
            System.out.println(Login_Class.loginId);
        }
    }
    
    public static void setLookAndFeel(Component comp){
        try{
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
            SwingUtilities.updateComponentTreeUI(comp);
        }catch(Exception exc){
            JOptionPane.showMessageDialog(null, "Error setting look and feel.");
        }
    }
    
    public ChangeListener spareSomeChange = new ChangeListener(){
        @Override
        public void stateChanged(ChangeEvent e) {
            int index = tabs.getSelectedIndex();
            
            if(Login_Class.admin)
                if(index >= 0 & index <= 3){
                    frame.pack();
                    frame.setSize(frame.getWidth(), 300);
                    frame.setResizable(false);
                    frame.setLocationRelativeTo(null);
                }
                else{
                    frame.setSize(600, 600);
                    frame.setResizable(true);
                    frame.setLocationRelativeTo(null);
                }
            else
                if (index == 0){
                    frame.pack();
                    frame.setSize(frame.getWidth(), 300);
                    frame.setResizable(false);
                    frame.setLocationRelativeTo(null);
                }
                else{
                    frame.setSize(600, 600);
                    frame.setResizable(true);
                    frame.setLocationRelativeTo(null);
                }
        }
    };
    
    static ActionListener buttonListener = new ActionListener(){
        @Override
        public void actionPerformed(ActionEvent e) {
            JButton sauce = (JButton) e.getSource();
            userPane  = (Database_Class.setUserPane(sauce.getText()));
            tabs.remove(2);
            tabs.add(userPane, 2);
            tabs.setTitleAt(2, "This user");
        }
    };
    
    static ActionListener SDButton = new ActionListener(){
        @Override
        public void actionPerformed(ActionEvent e) {
            JButton sauce = (JButton) e.getSource();
            if (sauce.getText() == "Save")
                save();
            else{ 
                int answer = JOptionPane.showConfirmDialog(null, "Are you absolutely sure you want to delete this user?");
                if (answer == JOptionPane.YES_OPTION){
                    delete();
                    tabs.setSelectedIndex(1);
                    userPane = null;
                    tabs.setTabComponentAt(2, userPane);
                }
            }
        }
    };
    
    static ActionListener blobSelecter = new ActionListener(){
        @Override
        public void actionPerformed(ActionEvent e) {
            JButton delete, view, cancel;
            JButton sauce = (JButton) e.getSource();
            JPanel newPane = new JPanel();
            JFrame newFrame = new JFrame("Do you want to view, or delete the selected blob?");
            
            final String txt = sauce.getText().substring(sauce.getText().indexOf(":") + 2);
            
            view = new JButton("View blob");
            delete = new JButton("Delete blob");
            cancel = new JButton("Cancel");
            
            newPane.add(view);
            newPane.add(delete);
            newPane.add(cancel);
            
            newFrame.add(newPane);
            
            newFrame.pack();
            newFrame.setLocationRelativeTo(null);
            newFrame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
            
            view.addActionListener(new ActionListener(){
                @Override
                public void actionPerformed(ActionEvent e) {
                    viewBlob(Integer.parseInt(txt));
                    newFrame.dispose();
                }
            });
            
            delete.addActionListener(new ActionListener(){
                @Override
                public void actionPerformed(ActionEvent e) {
                    if(JOptionPane.showConfirmDialog(null, "Are you sure you want to delete this blob?") == JOptionPane.YES_OPTION)
                        deleteBlob(Integer.parseInt(txt));
                    newFrame.dispose();
                }
            });
            
            cancel.addActionListener(new ActionListener(){
                @Override
                public void actionPerformed(ActionEvent e){
                    newFrame.dispose();
                }
            });
            
            newFrame.setVisible(true);
        }
    };
    
    static ActionListener saveBlob = new ActionListener(){
        @Override
        public void actionPerformed(ActionEvent e) {
            Database_Class.saveBlob();
            
            notesPane = Database_Class.getBlobs(false);
            
            tabs.removeTabAt(0);
            
            if(Login_Class.admin){
                allBlobPane = Database_Class.getBlobs(true);
                tabs.removeTabAt(2);
                
                tabs.add(new JScrollPane(allBlobPane), 0);
                tabs.setTitleAt(0, "All Blobs");
                
                tabs.add(new JScrollPane(notesPane), 3);
                tabs.setTitleAt(3, "My Notes");
                
                tabs.setSelectedIndex(4);
            }
            else{
                tabs.add(new JScrollPane(notesPane), 0);
                tabs.setTitleAt(0, "My Notes");
                
                tabs.setSelectedIndex(1);
            }
            
            JOptionPane.showMessageDialog(null, "Blob saved successfully!");
        }
    };
    
    static ActionListener createBlob = new ActionListener(){
        @Override
        public void actionPerformed(ActionEvent e) {
            int newBlobId = Database_Class.createBlob();
            
            notesPane = Database_Class.getBlobs(false);
            
            if(Login_Class.admin){
                allBlobPane = Database_Class.getBlobs(true);
                
                tabs.removeTabAt(0);
                
                tabs.add(allBlobPane, 0);
                tabs.setTitleAt(0, "All blobs");
                
                tabs.removeTabAt(3);
                
                tabs.add(new JScrollPane(notesPane), 3);
                tabs.setTitleAt(3, "My notes");
                
                tabs.setSelectedIndex(3);
            }
            else{
                tabs.removeTabAt(0);
                
                tabs.add(notesPane, 0);
                tabs.setTitleAt(0, "My notes");
                
                tabs.setSelectedIndex(0);
            }
            
            
            int answer = JOptionPane.showConfirmDialog(null, "Do you want to view your new blob now?"
                + "\nThis will also delete all unsaved data if you have a blob opened!");
            
            if (answer == JOptionPane.YES_OPTION){
                viewBlob(newBlobId);
            }
        }
    };
    
    static private void save(){
        if(Database_Class.adminStatus.isSelected())
            Database_Class.save(true, Database_Class.passwordTextfield.getText(), Database_Class.usernameTextfield.getText());
        else
            Database_Class.save(false, Database_Class.passwordTextfield.getText(), Database_Class.usernameTextfield.getText());
        
        if(Login_Class.admin){
            tabs.removeTabAt(1);
            usersPane = Database_Class.getUsers();
            tabs.add(new JScrollPane(usersPane), 1);
            tabs.setTitleAt(1, "All users");

            tabs.removeTabAt(0);
            allBlobPane = Database_Class.getBlobs(true);
            tabs.add(new JScrollPane(allBlobPane), 0);
            tabs.setTitleAt(0, "All blobs");
        }
    }
    
    static private void delete(){
        if(Database_Class.delete(Database_Class.usernameTextfield.getText())){
            tabs.removeTabAt(1);
            usersPane = Database_Class.getUsers();
            tabs.add(new JScrollPane(usersPane), 1);
            tabs.setTitleAt(1, "All users");
            tabs.setEnabledAt(2, false);

            tabs.removeTabAt(0);
            allBlobPane = Database_Class.getBlobs(true);
            tabs.add(new JScrollPane(allBlobPane), 0);
            tabs.setTitleAt(0, "All blobs");
        }
    }
    
    static void viewBlob(int id){
        if(Login_Class.admin)
            tabs.removeTabAt(4);
        else
            tabs.removeTabAt(1);
        
        editorPane = Database_Class.viewBlob(id);
        currentBlobId = id;
        
        
        tabs.addTab("Editor", new JScrollPane(editorPane));
            
        if(editorPane.getComponentCount() > 1){
            
            if(Login_Class.admin)
                tabs.setSelectedIndex(4);
            else
                tabs.setSelectedIndex(1);

            JOptionPane.showMessageDialog(null, "Blob selected!");
        }
        else{
            JOptionPane.showMessageDialog(null, "Error, blob doesn't exist.");
            if(Login_Class.admin)
                tabs.setEnabledAt(4, false);
            else
                tabs.setEnabledAt(1, false);
        }
    }
    
    static private void deleteBlob(int id){
        Database_Class.deleteBlob(id);
        
        int selectedTab = tabs.getSelectedIndex();
        
        notesPane = Database_Class.getBlobs(false);
            
        if (Login_Class.admin){
            tabs.removeTabAt(3);
            tabs.add(new JScrollPane(notesPane), 3);
            tabs.setTitleAt(3, "My notes");

            allBlobPane = Database_Class.getBlobs(true);

            tabs.removeTabAt(0);
            tabs.add(new JScrollPane(allBlobPane), 0);
            tabs.setTitleAt(0, "All blobs");
            
            if(id == currentBlobId)
                tabs.setEnabledAt(4, false);
            
            tabs.setSelectedIndex(selectedTab);
        }
        else{
            tabs.removeTabAt(0);
            tabs.add(new JScrollPane(notesPane), 0);
            tabs.setTitleAt(0, "My notes");
            
            if(id == currentBlobId)
                tabs.setEnabledAt(1, false);
            
            tabs.setSelectedIndex(selectedTab);
        }
        
        JOptionPane.showMessageDialog(null, "Blob deleted!");
    }
}