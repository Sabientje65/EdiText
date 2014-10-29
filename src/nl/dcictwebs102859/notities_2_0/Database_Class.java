package nl.dcictwebs102859.notities_2_0;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import javax.swing.*;
import java.io.*;

public class Database_Class {
    static ResultSet res;
    static JCheckBox adminStatus;
    static JTextField usernameTextfield, passwordTextfield;
    static int userId;
    static String oldUsername;
    static JTextArea blobText;
    
    /**
    Template
    
    
    
    
    
    try(
            Connection con = DriverManager.getConnection("jdbc:derby://localhost:1527/blobbie");
            Statement st = con.createStatement()
        ){
            Class.forName("org.apache.derby.jdbc.ClientDriver");
            
            //code goes here
            
            con.close();
    }catch(SQLException sqle){
            JOptionPane.showMessageDialog(null, "SQL error, terminating program..."); //optional
            
            //code goes here
            
            System.exit(-1); //optional
    }catch(ClassNotFoundException cnfe){
            JOptionPane.showMessageDialog(null, "An error occurred, terminating program..."); //optional
            
            //code goes here
            
            System.exit(-1); //optional
    }
    
    
    
    
    
    */
    
    static boolean loginCheck(String username, String password){
        int result = 0;
        
        try(
            Connection con = DriverManager.getConnection("jdbc:derby://localhost:1527/blobbie");
            Statement st = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, 
                                               ResultSet.CONCUR_READ_ONLY)
        ){
            Class.forName("org.apache.derby.jdbc.ClientDriver");
            
            username = validation(username);
            password = validation(password);
            
            res = st.executeQuery("SELECT *"
                + "FROM APP.\"users\""
                + "WHERE \"username\" = '" + username + "'"
                + "AND \"password\" = '" + password + "'"
            );
            
            while(res.next()){
                Login_Class.admin = res.getBoolean(4);
                Login_Class.loginId = res.getInt(1);
                result++;
            }
            
            con.close();
            
            return result == 1;
            
        }catch(SQLException sqle){
            JOptionPane.showMessageDialog(null, "An SQL error occurred, terminating program...");
            
            System.exit(-1);
            return false;
        } catch(ClassNotFoundException cnfe){
            JOptionPane.showMessageDialog(null, "An error occurred, terminating program...");
            
            System.exit(-1);
            return false;
        }
    }
    
    static boolean createAccount(String username, String password){
        try(
            Connection con = DriverManager.getConnection("jdbc:derby://localhost:1527/blobbie");
            Statement st = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, 
                                               ResultSet.CONCUR_READ_ONLY)
        ){
            Class.forName("org.apache.derby.jdbc.ClientDriver");
            
            int usrLength = username.length();
            int passLength = password.length();
            
            username = validation(username);
            password = validation(password);
            
            res = st.executeQuery("SELECT *"
                + "FROM APP.\"users\""
                + "WHERE \"username\" = '" + username + "'"
            );
            
            if(passLength != password.length() | usrLength != username.length()){
                JOptionPane.showMessageDialog(null, "Your desired username or password contained illegal characters.");
                return false;
            }
            
            if(res.next())
                return false;
            
            st.executeUpdate("INSERT INTO APP.\"users\""
                    + "(\"username\", \"password\")"
                    + "VALUES ('" + username + "', '" + password + "')"
            );
            
            con.close();
            
            return true;
        }catch(SQLException sqle){
            JOptionPane.showMessageDialog(null, "An SQL error occurred, terminating program...");
            
            System.exit(-1);
            
            return false;
        } catch(ClassNotFoundException cnfe){
            JOptionPane.showMessageDialog(null, "An error occurred, terminating program...");
            
            System.exit(-1);
            
            return false;
        }
    }
    
    static boolean delete(String delUsername){
        try(Connection con = DriverManager.getConnection("jdbc:derby://localhost:1527/blobbie");
            Statement st = con.createStatement()
            ){
                Class.forName("org.apache.derby.jdbc.ClientDriver");
            
                if("Danny".equals(delUsername) | "DannyBackup".equals(delUsername)){
                    JOptionPane.showMessageDialog(null, "Error can't remove user \"Danny\" ", "Error" ,JOptionPane.ERROR_MESSAGE);
                    return false;
                }
                else{
                    int rows = 0;
                    
                    st.executeUpdate("DELETE "
                            + "FROM APP.\"users\""
                            + "WHERE \"username\" = '" + delUsername + "'"
                    );
                    
                    res = st.executeQuery("SELECT *"
                            + "FROM APP.\"blobjes\""
                            + "WHERE \"username\" = '" + delUsername + "'"
                    );
                    
                    while(res.next())
                        rows++;
                    
                    if (rows >= 1){
                        st.executeUpdate("DELETE "
                                + "FROM APP.\"blobjes\""
                                + "WHERE \"username\" = '" + delUsername + "'"
                        );
                    }
                    JOptionPane.showMessageDialog(null, "Account deleted!");
                }
            con.close();
            
            if (delUsername.equals(Login_Class.username)){
                JOptionPane.showMessageDialog(null, "The account that's currently in use has been deleted, terminating program...");
                System.exit(-1);
            }
            
            return true;
        }catch(SQLException sqle){
            JOptionPane.showMessageDialog(null, "SQL error, terminating program...");
            
            System.exit(-1);
            
            return false;
        }catch(ClassNotFoundException cnfe){
            JOptionPane.showMessageDialog(null, "An error occurred, terminating program...");
            
            System.exit(-1);
            
            return false;
        }
    }
    
    static int createBlob(){
        try(
            Connection con = DriverManager.getConnection("jdbc:derby://localhost:1527/blobbie");
            Statement st = con.createStatement()
        ){
            String newBlobUsername = "";
            Class.forName("org.apache.derby.jdbc.ClientDriver");
            
            java.util.Date today = new java.util.Date();

            res = st.executeQuery("SELECT \"username\""
                + "FROM APP.\"users\""
                + "WHERE \"id\" = " + Login_Class.loginId);
            
            while(res.next())
                newBlobUsername = res.getString(1);
            
            st.executeUpdate("INSERT INTO APP.\"blobjes\""
                + "(\"username\", \"LAST_EDITED\") "
                + "VALUES ('" + newBlobUsername + "', (CURRENT_TIMESTAMP))");

            res = st.executeQuery("SELECT \"id\""
                    + "FROM APP.\"blobjes\""
                    + "WHERE \"username\" = '" + newBlobUsername + "'"
                    + "ORDER BY \"LAST_EDITED\" DESC"
            );

            res.next();

            int newBlobId = res.getInt(1);

            JOptionPane.showMessageDialog(null, "New blob created succesfully!");
            
            con.close();
            
            return newBlobId;
        }catch(SQLException sqle){
            JOptionPane.showMessageDialog(null, "SQL error, terminating program...");
            
            System.out.println("Message: " + sqle.getMessage());
            
            System.exit(-1);
            
            return 0;
        }catch(ClassNotFoundException cnfe){
            JOptionPane.showMessageDialog(null, "An error occurred, terminating program...");
            
            System.exit(-1);
            
            return 0;
        }
    }
    
    static int mobileLoginCheck(String info){
        try(
                Connection con = DriverManager.getConnection("jdbc:derby://localhost:1527/blobbie");
                Statement st = con.createStatement()
            ){
                Class.forName("org.apache.derby.jdbc.ClientDriver");
                
                int id = 0;
                
                String username = info.substring(4, info.indexOf("@pass"));
                String password = info.substring(info.indexOf("@pass") + 5, info.length());
                
                res = st.executeQuery("SELECT \"id\""
                        + "FROM APP.\"users\""
                        + "WHERE \"username\" = '" + username + "'"
                        + "AND \"password\" = '" + password + "'"
                );
                
                while(res.next())
                    id = res.getInt(1);

                con.close();
                
                return id;
        }catch(SQLException | ClassNotFoundException exc){
                return 0;
        }
    }
    
    static String mobileGetBlobs(String info){
        try(
                Connection con = DriverManager.getConnection("jdbc:derby://localhost:1527/blobbie");
                Statement st = con.createStatement()
            ){
                Class.forName("org.apache.derby.jdbc.ClientDriver");

                String output = "";
                
                res = st.executeQuery("SELECT \"username\""
                    + "FROM APP.\"users\""
                    + "WHERE \"id\" = " + Integer.parseInt(info.substring(5)));
                
                if(res.next()){          
                    res = st.executeQuery("SELECT \"id\""
                        + "FROM APP.\"blobjes\""
                        + "WHERE \"username\" = '" + res.getString(1) + "'"
                        + "ORDER BY \"LAST_EDITED\" DESC");

                    while(res.next())
                        output += "@id" + Integer.toString(res.getInt(1));
                    
                    if(output.equals(""))
                        output = "@id0";
                }
                else
                    output = "@id-1";
                
                con.close();
                
                return output;
        }catch(SQLException | ClassNotFoundException sqle){
            return "@id0";
        }
    }
    
    static String mobileBlobView(String info){
        try(
                Connection con = DriverManager.getConnection("jdbc:derby://localhost:1527/blobbie");
                Statement st = con.createStatement()
            ){
                Class.forName("org.apache.derby.jdbc.ClientDriver");
                
                boolean exists = true;

                int mobileId = Integer.parseInt(info.substring(5));
                
                String outputLine = "@text1";
                
                res = st.executeQuery("SELECT \"blobje\""
                    + "FROM APP.\"blobjes\""
                    + "WHERE \"id\" = " + mobileId);
                
                if(res.next()){
                    Clob blobbie = res.getClob(1);
                    
                    //File clobText = new File("mClob" + mobileId);
                    
                    try(BufferedReader buff = new BufferedReader(blobbie.getCharacterStream())){
                        
                        while(true){
                            String line = buff.readLine();
                            
                            if(line == null)
                                break;
                            
                            outputLine += line + "\n";
                        }
                        
                    }catch(IOException ioe){
                        exists = false;
                    }catch(NullPointerException npe){
                        
                    }      
                }
                else
                    exists = false;
                
                if(!exists)
                    outputLine = "@text0";
                
                con.close();
                
                return outputLine;
        }catch(SQLException | ClassNotFoundException sqle){
                return "";
        }
    }
    
    static String saveMobileBlob(String info){
        try(
                Connection con = DriverManager.getConnection("jdbc:derby://localhost:1527/blobbie");
                Statement st = con.createStatement()
            ){
                Class.forName("org.apache.derby.jdbc.ClientDriver");

                int selectedMobileBlobId = Integer.parseInt(info.substring(5, info.indexOf("@id")));
                String newBlobText = info.substring(info.indexOf("@id") + 3).replaceAll("@nLine", "\n");
                
                
                res = st.executeQuery("SELECT *"
                    + "FROM APP.\"blobjes\""
                    + "WHERE \"id\" = " + selectedMobileBlobId
                );
                
                if(!res.next())
                    return"0";
                
                st.executeUpdate("UPDATE APP.\"blobjes\""
                    + "SET \"blobje\" = '" + newBlobText + "',"
                    + "\"LAST_EDITED\" = (CURRENT_TIMESTAMP)"
                    + "WHERE \"id\" = " + selectedMobileBlobId
                );

                con.close();
                
                return "1";
        }catch(SQLException | ClassNotFoundException sqle){
            return "-1";
        }
    }
    
    static String createMobileBlob(String info){
        try(
        Connection con = DriverManager.getConnection("jdbc:derby://localhost:1527/blobbie");
        Statement st = con.createStatement()
        ){
            Class.forName("org.apache.derby.jdbc.ClientDriver");

            int mobileUserId = Integer.parseInt(info.substring(7));

            res = st.executeQuery("SELECT \"username\""
                    + "FROM APP.\"users\""
                    + "WHERE \"id\" = " + mobileUserId
            );
            
            if(!res.next())
                return "-1";
            
            String mobileUsername = res.getString(1);
            
            st.executeUpdate("INSERT INTO APP.\"blobjes\""
                + "(\"username\", \"LAST_EDITED\") "
                + "VALUES('" + mobileUsername + "', (CURRENT_TIMESTAMP))");
            
            con.close();
            
            return "1";
        }catch(SQLException | ClassNotFoundException sqle){
            return "-2";
        }
    }
    
    static String deleteMobileBlob(String info){
        try(
                Connection con = DriverManager.getConnection("jdbc:derby://localhost:1527/blobbie");
                Statement st = con.createStatement()
            ){
                Class.forName("org.apache.derby.jdbc.ClientDriver");

                int deleteId = Integer.parseInt(info.substring(7));
                
                res = st.executeQuery("SELECT \"id\""
                    + "FROM APP.\"blobjes\""
                    + "WHERE \"id\" = " + deleteId);
                
                if(!res.next())
                    return "-1";
                
                st.executeUpdate("DELETE "
                    + "FROM APP.\"blobjes\""
                    + "WHERE \"id\" = " + deleteId);

                con.close();
                
                return "1";
        }catch(SQLException | ClassNotFoundException sqle){
            return "0";
        }
    }
    
    static String createMobileAccount(String info){
        try(
                Connection con = DriverManager.getConnection("jdbc:derby://localhost:1527/blobbie");
                Statement st = con.createStatement()
            ){
                Class.forName("org.apache.derby.jdbc.ClientDriver");

                String createUser = info.substring(4, info.indexOf("@pass"));
                String createPass = info.substring(info.indexOf("@pass") + 5);
                
                res = st.executeQuery("SELECT \"username\""
                    + "FROM APP.\"users\""
                    + "WHERE \"username\" = '" + createUser + "'");
                
                if(res.next())
                    return "id -1";
                
                st.executeUpdate("INSERT INTO APP.\"users\""
                    + "(\"username\", \"password\") "
                    + "VALUES ('" + createUser + "', '" + createPass + "')");

                con.close();
                
                return "id 0";
        }catch(SQLException | ClassNotFoundException sqle){
                return "id -1";
        }
    }
    
    static JPanel getBlobs(boolean admin){
        int rows = 0;
        JButton blobId, newBlob;
        
        try(
            Connection con = DriverManager.getConnection("jdbc:derby://localhost:1527/blobbie");
            Statement st = con.createStatement()
        ){
            Class.forName("org.apache.derby.jdbc.ClientDriver");
            JPanel pane = new JPanel();
            JPanel northPane, centerPane;
            centerPane = new JPanel();
            northPane = new JPanel();
            
            if(!admin)
                res = st.executeQuery("SELECT *"
                      + "FROM APP.\"blobjes\""
                      + "WHERE \"username\" = '" + Login_Class.username + "'"
                      + "ORDER BY \"LAST_EDITED\" DESC"
                );
            else
                res = st.executeQuery("SELECT *"
                      + "FROM APP.\"blobjes\""
                      + "ORDER BY \"LAST_EDITED\" DESC"
                );

            
            if(!admin){
                newBlob = new JButton("Create new blob");
                newBlob.addActionListener(GUI_Class.createBlob);
            
                northPane.add(newBlob);
            }
            
            
            while(res.next()){
                blobId = new JButton("Blob id: " + res.getString(1));
                blobId.addActionListener(GUI_Class.blobSelecter);
                
                centerPane.add(blobId);
                centerPane.add(new JLabel("Username: " + res.getString(2)));
                centerPane.add(new JLabel("Last edited: " + res.getString(4)));
                rows ++;
            }
            
            
            centerPane.setLayout(new GridLayout(rows, 3, 0, 0));
            
            pane.setLayout(new BorderLayout());
            pane.add(northPane, BorderLayout.NORTH);
            pane.add(centerPane, BorderLayout.CENTER);
            
            
            con.close();
            
            return pane;
            
        } catch(SQLException sqle){
            JOptionPane.showMessageDialog(null, "An SQL error occurred, terminating program...");
            
            System.exit(-1);
            
            return new JPanel();
        }catch(ClassNotFoundException cnfe){
            JOptionPane.showMessageDialog(null, "An error occurred, terminating program...");
            
            System.exit(-1);
            
            return new JPanel();
        }
    }
    
    static JPanel getUsers(){
        int rows = 0;
        JCheckBox adminBox[];
        
        try(
            Connection con = DriverManager.getConnection("jdbc:derby://localhost:1527/blobbie");
            Statement st = con.createStatement()
        ){
            Class.forName("org.apache.derby.jdbc.ClientDriver");
            
            JPanel pane = new JPanel();
            JButton id;
            
            res = st.executeQuery("SELECT *"
                  + "FROM APP.\"users\""
                  + "ORDER BY \"id\""
            );
            
            while(res.next()){
                rows ++;
            }

            adminBox = new JCheckBox[rows];

            rows = 0;

            res = st.executeQuery("SELECT *"
                  + "FROM APP.\"users\""
                  + "ORDER BY \"id\""
            );

            while(res.next()){
                if (res.getBoolean(4))
                    adminBox[rows] = new JCheckBox("Admin", true);
                else
                    adminBox[rows] = new JCheckBox("Admin", false);
                adminBox[rows].setEnabled(false);
                pane.add(id = new JButton("User id: " + res.getString(1)));
                pane.add(new JLabel("Username: " + res.getString(2)));
                pane.add(new JLabel("Password: " + res.getString(3)));
                pane.add(adminBox[rows]);
                id.addActionListener(GUI_Class.buttonListener);
                rows++;
            }
            
            pane.setLayout(new GridLayout(rows, 4, 5, 10));
            
            con.close();
            
            return pane;
            
        } catch(SQLException sqle){
            JOptionPane.showMessageDialog(null, "An SQL error occurred, terminating program...");
            
            System.exit(-1);
            
            return new JPanel();
        }catch(ClassNotFoundException cnfe){
            JOptionPane.showMessageDialog(null, "An error occurred, terminating program...");
            
            System.exit(-1);
            
            return new JPanel();
        }
    }
    
    static JPanel setUserPane(String id){
        JPanel pane = new JPanel();
        JPanel subPane = new JPanel();
        JLabel usernameLabel, passwordLabel;


        JButton save, delete;
        
        try(
            Connection con = DriverManager.getConnection("jdbc:derby://localhost:1527/blobbie");
            Statement st = con.createStatement()
        ){  
            Class.forName("org.apache.derby.jdbc.ClientDriver");
            
            usernameTextfield = new JTextField (50);
            passwordTextfield = new JTextField (50);
            usernameLabel = new JLabel("Username: ");
            passwordLabel = new JLabel("Password: ");
            userId = Integer.parseInt(id.substring(9));
            
            res = st.executeQuery("SELECT *"
                + "FROM APP.\"users\""
                + "WHERE \"id\" = " + userId
            );
            
            while (res.next()){
                pane.add(new JLabel("ID: " + id));
                
                adminStatus = new JCheckBox("Admin: ", res.getBoolean(4));
                
                usernameTextfield.setText(res.getString(2));
                passwordTextfield.setText(res.getString(3));
                
                pane.setLayout(new BorderLayout());
                
                pane.add(adminStatus, BorderLayout.NORTH);
                
                subPane.setLayout(new GridLayout(3, 2, 5, 5));
                subPane.add(usernameLabel);
                oldUsername = usernameTextfield.getText();
                subPane.add(usernameTextfield);
                
                if(usernameTextfield.getText().equals("Danny"))
                    usernameTextfield.setEditable(false);

                subPane.add(passwordLabel);
                subPane.add(passwordTextfield);
                
                if(usernameTextfield.getText().equals("DannyBackup")){
                    usernameTextfield.setEditable(false);
                    passwordTextfield.setEditable(false);
                }
                
                save = new JButton("Save");
                delete = new JButton("Delete");
                
                save.addActionListener(GUI_Class.SDButton);
                delete.addActionListener(GUI_Class.SDButton);
                
                subPane.add(save);
                subPane.add(delete);
                
                pane.add(subPane, BorderLayout.CENTER);
            }
            
            con.close();
            
            return pane;
        }catch(SQLException sqle){
            JOptionPane.showMessageDialog(null, "SQL error, terminating program...");
            
            System.exit(-1);
            
            return pane;
        }catch(NumberFormatException nfe){
            JOptionPane.showMessageDialog(null, "Error, terminating program...");
            System.out.println(nfe.getMessage());
            
            System.exit(-1);
            
            return pane;
        }catch(ClassNotFoundException cnfe){
            JOptionPane.showMessageDialog(null, "An error occurred, terminating program...");
            
            System.exit(-1);
            
            return new JPanel();
        }
    }
    
    static JPanel viewBlob(int id){
        try(
                Connection con = DriverManager.getConnection("jdbc:derby://localhost:1527/blobbie");
                Statement st = con.createStatement()
            ){
                JPanel pane = new JPanel();
                JPanel subPane = new JPanel();
                JButton save, clear, load;
                blobText = new JTextArea(10, 10);
            
                Class.forName("org.apache.derby.jdbc.ClientDriver");
                

                res = st.executeQuery("SELECT \"blobje\""
                        + "FROM APP.\"blobjes\""
                        + "WHERE \"id\" = " + id
                );

                if(res.next()){
                    
                    Clob clob = res.getClob(1);

                    //File clobText = new File("Clob" + id);

                    try(BufferedReader buff = new BufferedReader(clob.getCharacterStream())){
                        int x = 0;

                        while(true){
                            String line = buff.readLine();
                            if(line == null)
                                break;
                            else if(x == 0)
                                blobText.setText(line);
                            else if (x != 0)
                                blobText.setText(blobText.getText() + line);

                            blobText.setText(blobText.getText() + "\n");

                            x++;
                        }

                        if(x!=0)
                            blobText.setText(blobText.getText().substring(0, blobText.getText().length() - 1));

                        buff.close();
                        clob.free();

                    }catch(IOException ioe){
                        JOptionPane.showMessageDialog(null, "An error occurred, terminating program...");

                        System.exit(-1);
                    }catch(NullPointerException npe){

                    }

                    save = new JButton ("Save");
                    clear = new JButton ("Clear");
                    load = new JButton("Load last saved instance of this blob");

                    save.addActionListener(GUI_Class.saveBlob);
                    clear.addActionListener(new ActionListener(){
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            blobText.setText("");
                        }
                    });

                    load.addActionListener(new ActionListener(){
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            GUI_Class.viewBlob(id);
                        }
                    });

                    pane.setLayout(new BorderLayout());

                    subPane.add(save);
                    subPane.add(load);
                    subPane.add(clear);

                    pane.add(blobText, BorderLayout.CENTER);
                    pane.add(subPane, BorderLayout.SOUTH);

                    con.close();

                    //clobText.delete();

                    return pane;
                }
                else{
                    return new JPanel();
                }
        }catch(SQLException sqle){
                JOptionPane.showMessageDialog(null, "SQL error, terminating program...");
                
                System.exit(-1);
                
                return new JPanel();
        }catch(ClassNotFoundException cnfe){
                JOptionPane.showMessageDialog(null, "An error occurred, terminating program...");

                System.exit(-1);

                return new JPanel();
        }
        
    }
    
    static void save(boolean admin, String newPassword, String newUsername){
        try(Connection con = DriverManager.getConnection("jdbc:derby://localhost:1527/blobbie");
            Statement st = con.createStatement()
            ){
            Class.forName("org.apache.derby.jdbc.ClientDriver");
            
            newUsername = validation(newUsername);
            newPassword = validation(newPassword);
            
            if (("Danny".equals(oldUsername) | "DannyBackup".equals(oldUsername)) && admin == false){
                JOptionPane.showMessageDialog(null, "Error can't remove admin status from user \"Danny\" ", "Error" ,JOptionPane.ERROR_MESSAGE);
            }
            else{
                boolean adminChange = true;
                int rows = 0;
                
                if(admin == false){
                    res = st.executeQuery("SELECT * FROM APP.\"users\" WHERE \"admin\" = true");

                    while(res.next())
                        rows++;

                    if(rows <= 1){
                        JOptionPane.showMessageDialog(null, "Error failed to remove admin status from selected user.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                    else
                        adminChange = true;
                }

                if(!newUsername.equals(oldUsername)){
                    res = st.executeQuery("SELECT * FROM APP.\"users\" WHERE \"username\" = '" + newUsername + "'");

                    rows = 0;

                    while(res.next()){
                        rows++;
                    }

                    if (rows != 0){
                        JOptionPane.showMessageDialog(null, "Error, username already exists.", "Error", JOptionPane.ERROR_MESSAGE);
                        adminChange = false;
                    }
                }
                
                if (newUsername.length() == 0 | newUsername.length() > 50){
                    JOptionPane.showMessageDialog(null, "Invalid username");
                    adminChange = false;
                }
                
                if (newPassword.length() == 0 | newPassword.length() > 50){
                    JOptionPane.showMessageDialog(null, "Invalid password");
                    adminChange = false;
                }
                
                if(adminChange){
                    st.executeUpdate("UPDATE APP.\"users\""
                            + "SET "
                            + "\"username\" = '" + newUsername + "',"
                            + "\"password\" = '" + newPassword + "',"
                            + "\"admin\" = " + admin
                            + " WHERE \"id\" = " + userId
                    );
                    
                    st.executeUpdate("UPDATE APP.\"blobjes\""
                            + "SET "
                            + "\"username\" = '" + newUsername + "'"
                            + "WHERE \"username\" = '" + oldUsername + "'"
                    );
                    
                    res = st.executeQuery("SELECT \"admin\""
                        + "FROM APP.\"users\""
                        + "WHERE \"id\" = " + Login_Class.loginId);
                    
                    oldUsername = newUsername;
                    
                    JOptionPane.showMessageDialog(null, "Changes applied successfully!");
                    
                    while(res.next())
                        if(!res.getBoolean(1)){
                            Login_Class.admin = false;
                            GUI_Class.tabs.removeTabAt(5);
                            for(int i = 0; i < 3; i++)
                                GUI_Class.tabs.removeTabAt(0);
                            JOptionPane.showMessageDialog(null, "Admin status lost.");
                        }
                    
                }
            }

            con.close();
        }catch(SQLException sqle){
            JOptionPane.showMessageDialog(null, "SQL error, terminating program...");
            System.out.println(sqle.getCause().toString());
            System.out.println(sqle.getMessage());
            System.out.println(sqle.getSQLState());
            System.out.println(sqle.getErrorCode());
            
            System.exit(-1);
        }catch(ClassNotFoundException cnfe){
            JOptionPane.showMessageDialog(null, "An error occurred, terminating program...");
            
            System.exit(-1);
        }
    }
    
    static void saveBlob(){
        try(
                Connection con = DriverManager.getConnection("jdbc:derby://localhost:1527/blobbie");
                Statement st = con.createStatement()
            ){
                Class.forName("org.apache.derby.jdbc.ClientDriver");
                
                String blobString = validation(blobText.getText());
                
                st.executeUpdate("UPDATE APP.\"blobjes\""
                        + "SET \"blobje\" = '" + blobString + "',"
                        + "\"LAST_EDITED\" = (CURRENT_TIMESTAMP)"
                        + "WHERE \"id\" = " + GUI_Class.currentBlobId
                );

                con.close();
        }catch(SQLException sqle){
                JOptionPane.showMessageDialog(null, "SQL error, terminating program...");

                System.exit(-1);
        }catch(ClassNotFoundException cnfe){
                JOptionPane.showMessageDialog(null, "An error occurred, terminating program...");

                System.exit(-1);
        }
    }
    
    static void deleteBlob(int id){
        try(
                Connection con = DriverManager.getConnection("jdbc:derby://localhost:1527/blobbie");
                Statement st = con.createStatement()
            ){
                Class.forName("org.apache.derby.jdbc.ClientDriver");
                
                st.executeUpdate("DELETE "
                    + "FROM APP.\"blobjes\""
                    + "WHERE \"id\" = " + id);

                con.close();
        }catch(SQLException sqle){
                JOptionPane.showMessageDialog(null, "SQL error, terminating program...");

                System.exit(-1);
        }catch(ClassNotFoundException cnfe){
                JOptionPane.showMessageDialog(null, "An error occurred, terminating program...");

                System.exit(-1);
        }
    }
    
    
    
    
    
    
    
    
    
    
    static private String validation(String string){
        String[] illegalStrings = {"'", "\\\\", "@usr", "@pass"};

        while(true) {
            for(String i : illegalStrings)
                string = string.replaceAll(i, "");
            
            boolean again = false;

            for(String i : illegalStrings)
                if(string.contains(i))
                    again = true;
            
            if(!again)
                break;
        }

        System.out.println(string);

        return string;
    }
}