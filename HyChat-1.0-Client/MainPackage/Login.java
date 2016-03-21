/*
Name: Hy Truong Son
Major: BSc. Computer Science
Class: 2013 - 2016
Institution: Eotvos Lorand University, Budapest, Hungary
Email: sonpascal93@gmail.com
Website: http://people.inf.elte.hu/hytruongson/
Copyright 2016 (c), Hy Truong Son. All rights reserved. Only use for academic purposes.
*/

package MainPackage;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class Login extends JFrame {
    
    private final String computer_default = "157.181.166.217";
    private final String port_default = "5921";
    
    private final String frameTitle = "Login";
    private final int frameWidth = 320;
    private final int frameHeight = 480;
    private final int componentHeight = 30;
    private final int frameMargin = 20;
    
    private final String username_correctness_warning = 
            "Username can only contain 'A'..'Z', 'a'..'z', '0'..'9', '-' and '_'!";
    private final String password_correctness_warning = 
            "Password can only contain 'A'..'Z', 'a'..'z', '0'..'9', '-' and '_'!";
    private final String port_correctness_warning = 
            "Port format (number format) is not correct!";
            
    private final int username_minLength = 3;
    private final int password_minLength = 3;
    
    private final String username_length_warning = 
            "Username has to contain at least " + Integer.toString(username_minLength) + " characters!";
    private final String password_length_warning =
            "Password has to contain at least " + Integer.toString(password_minLength) + " characters!";
    private final String connection_length_warning = 
            "The connection cannot be empty!";
    
    private JLabel usernameLabel;
    private JTextField usernameText;
    private JLabel passwordLabel;
    private JPasswordField passwordText;
    private JLabel connectionLabel;
    private JTextField connectionText;
    private JLabel portLabel;
    private JTextField portText;
    private JButton loginButton;
    private JButton createButton;
    
    public Login() {
        setTitle(frameTitle);
        setSize(frameWidth, frameHeight);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(null);
        
        initialize_gui();
        
        setVisible(true);
    }
    
    private void initialize_gui() {
        int x = frameMargin;
        int y = frameMargin;
        
        usernameLabel = new JLabel();
        usernameLabel.setText("Username:");
        usernameLabel.setBounds(x, y, frameWidth - 2 * frameMargin, componentHeight);
        usernameLabel.setForeground(Color.blue);
        add(usernameLabel);
        
        y += componentHeight;
        
        usernameText = new JTextField();
        usernameText.setBounds(x, y, frameWidth - 2 * frameMargin, componentHeight);
        add(usernameText);
        
        y += componentHeight;
        
        passwordLabel = new JLabel();
        passwordLabel.setText("Password:");
        passwordLabel.setBounds(x, y, frameWidth - 2 * frameMargin, componentHeight);
        passwordLabel.setForeground(Color.blue);
        add(passwordLabel);
        
        y += componentHeight;
        
        passwordText = new JPasswordField();
        passwordText.setBounds(x, y, frameWidth - 2 * frameMargin, componentHeight);
        add(passwordText);
        
        y += componentHeight + frameMargin;
        
        connectionLabel = new JLabel();
        connectionLabel.setText("Connection:");
        connectionLabel.setForeground(Color.blue);
        connectionLabel.setBounds(x, y, frameWidth - 2 * frameMargin, componentHeight);
        add(connectionLabel);
        
        y += componentHeight;
        
        connectionText = new JTextField();
        connectionText.setText(computer_default);
        connectionText.setBounds(x, y, frameWidth - 2 * frameMargin, componentHeight);
        add(connectionText);
        
        y += componentHeight;
        
        portLabel = new JLabel();
        portLabel.setText("Port:");
        portLabel.setForeground(Color.blue);
        portLabel.setBounds(x, y, frameWidth - 2 * frameMargin, componentHeight);
        add(portLabel);
        
        y += componentHeight;
        
        portText = new JTextField();
        portText.setText(port_default);
        portText.setBounds(x, y, frameWidth - 2 * frameMargin, componentHeight);
        add(portText);
        
        y += componentHeight + frameMargin;
        
        int buttonLength = frameWidth - 2 * frameMargin;
        
        loginButton = new JButton();
        loginButton.setText("Login");
        loginButton.setBounds(x, y, buttonLength, componentHeight);
        add(loginButton);
        
        loginButton.addActionListener(LoginActionListener);
        
        y += componentHeight + frameMargin;
        
        createButton = new JButton();
        createButton.setText("Create new user");
        createButton.setBounds(x, y, buttonLength, componentHeight);
        add(createButton);
        
        createButton.addActionListener(CreateActionListener);
    }
    
    private boolean checkUsernameCorrectness(String username) {
        for (int i = 0; i < username.length(); ++i) {
            char ch = username.charAt(i);
            if ((ch >= 'A') && (ch <= 'Z')) {
                continue;
            }
            if ((ch >= 'a') && (ch <= 'z')) {
                continue;
            }
            if ((ch >= '0') && (ch <= '9')) {
                continue;
            }
            if ((ch == '-') || (ch == '_')) {
                continue;
            }
            return false;
        }
        return true;
    }
    
    private boolean checkPasswordCorrectness(char password[]) {
        for (int i = 0; i < password.length; ++i) {
            char ch = password[i];
            if ((ch >= 'A') && (ch <= 'Z')) {
                continue;
            }
            if ((ch >= 'a') && (ch <= 'z')) {
                continue;
            }
            if ((ch >= '0') && (ch <= '9')) {
                continue;
            }
            if ((ch == '-') || (ch == '_')) {
                continue;
            }
            return false;
        }
        return true;
    }
    
    private ActionListener LoginActionListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            String username = usernameText.getText();
            char password[] = passwordText.getPassword();
            
            if (username.length() < username_minLength) {
                JOptionPane.showMessageDialog(null, username_length_warning);
                return;
            }
            
            if (password.length < password_minLength) {
                JOptionPane.showMessageDialog(null, password_length_warning);
                return;
            }
            
            if (!checkUsernameCorrectness(username)) {
                JOptionPane.showMessageDialog(null, username_correctness_warning);
                return;
            }
            
            if (!checkPasswordCorrectness(password)) {
                JOptionPane.showMessageDialog(null, password_correctness_warning);
                return;
            }
            
            String computer = connectionText.getText();
            
            if (computer.length() == 0) {
                JOptionPane.showMessageDialog(null, connection_length_warning);
                return;
            }
            
            String port_str = portText.getText();
            int port = 0;
            
            try {
                port = Integer.parseInt(port_str);
            } catch (NumberFormatException exc) {
                JOptionPane.showMessageDialog(null, port_correctness_warning);
                return;
            }
            
            // Hidding the login window
            new Thread() {
                public void run() {
                    SwingUtilities.invokeLater(new Runnable(){
                        public void run(){
                            setVisible(false);
                        }
                    }); 
                }
            }.start();
            
            // Starting the client window
            new Client(username, String.valueOf(password), computer, port, false);
        }
    };

    private ActionListener CreateActionListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            String username = usernameText.getText();
            char password[] = passwordText.getPassword();
            
            if (username.length() < username_minLength) {
                JOptionPane.showMessageDialog(null, username_length_warning);
                return;
            }
            
            if (password.length < password_minLength) {
                JOptionPane.showMessageDialog(null, password_length_warning);
                return;
            }
            
            if (!checkUsernameCorrectness(username)) {
                JOptionPane.showMessageDialog(null, username_correctness_warning);
                return;
            }
            
            if (!checkPasswordCorrectness(password)) {
                JOptionPane.showMessageDialog(null, password_correctness_warning);
                return;
            }
            
            String computer = connectionText.getText();
            
            if (computer.length() == 0) {
                JOptionPane.showMessageDialog(null, connection_length_warning);
                return;
            }
            
            String port_str = portText.getText();
            int port = 0;
            
            try {
                port = Integer.parseInt(port_str);
            } catch (NumberFormatException exc) {
                JOptionPane.showMessageDialog(null, port_correctness_warning);
                return;
            }
            
            // Hidding the login window
            new Thread() {
                public void run() {
                    SwingUtilities.invokeLater(new Runnable(){
                        public void run(){
                            setVisible(false);
                        }
                    }); 
                }
            }.start();
            
            // Starting the client window
            new Client(username, String.valueOf(password), computer, port, true);
        }
    };

}
