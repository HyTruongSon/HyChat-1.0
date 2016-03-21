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
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;

public class Client extends JFrame {
    
    // Constants/Variables for Socket Programming
    private Socket socket = null;
    private Scanner input = null;
    private PrintWriter output = null;
    
    private final int port; // 1024-65535
    private final String computer;
    
    // Constants/Variables for Graphical User Interface
    private final String frameTitle;
    private final String username;
    private final int frameWidth = 800;
    private final int frameHeight = 600;
    private final int textHeight = 300;
    private final int frameMargin = 20;
    private final int labelWidth = 100;
    private final int buttonWidth = 120;
    private final int componentHeight = 30;
    
    private JLabel usernameLabel;
    private JTextField usernameText;
    private JButton logoutButton;
    private JLabel targetLabel;
    private JTextField targetText;
    private JButton filterButton;
    private JLabel currentLabel;
    private JTextArea currentText;
    private JScrollPane currentScroll;
    private JLabel historyLabel;
    private JTextArea historyText;
    private JScrollPane historyScroll;
    private JLabel messageLabel;
    private JTextField messageText;
    private JButton sendButton;
    
    private ArrayList<Message> messageList = new ArrayList<>();
    
    public Client(String username, String password, String computer, int port, boolean needCreated) {
        this.username = username;
        frameTitle = this.username;
        this.computer = computer;
        this.port = port;
        
        if (!startConnected(username, password, needCreated)) {
            return;
        }
        
        setTitle(frameTitle);
        setSize(frameWidth, frameHeight);
        setResizable(false);
        setLayout(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        initialize_gui();
        
        setVisible(true);
        
        new Thread() {
            @Override
            public void run() {
                chatting();
            }
        }.start();
    }
    
    private void chatting() {
        while (input.hasNext()) {
            String command = input.next();
            
            if (command.equals("msg")) {
                if (!input.hasNext()) {
                    break;
                }
                String from = input.next();
                if (!input.hasNext()) {
                    break;
                }
                String to = input.next();
                if (!input.hasNextLine()) {
                    break;
                }
                String message = removeFirstSpaces(input.nextLine());
                insertMessage(from, to, message);
            }
        }
        
        input.close();
        output.close();
        try {
            socket.close();
        } catch (Exception exc) {
            System.err.println(exc.toString());
        }
    }
    
    private String removeFirstSpaces(String str) {
        int i = str.length();
        for (int j = 0; j < str.length(); ++j) {
            if (str.charAt(j) != ' ') {
                i = j;
                break;
            }
        }
        String res = new String();
        for (int j = i; j < str.length(); ++j) {
            res += str.charAt(j);
        }
        return res;
    }
    
    private void sendMessage() {
        String target = targetText.getText();
        if (target.length() == 0) {
            JOptionPane.showMessageDialog(null, "Declare target first!");
            return;
        }
        
        String message = messageText.getText();
        if (message.length() == 0) {
            JOptionPane.showMessageDialog(null, "Type some message first!");
            return;
        }
        messageText.setText(new String());

        String from = username;
        String to = target;
        
        messageList.add(new Message(from, to, message));
        historyPrinting(from + "->" + to + ": " + message);
        messageFilter();
        
        String str = "msg " + from + " " + to + " " + message;
        output.println(str);
        output.flush();
    }
    
    private void messageFilter() {
        String target = targetText.getText();
        String current = new String();
        
        for (int i = 0; i < messageList.size(); ++i) {
            String from = messageList.get(i).getFrom();
            String to = messageList.get(i).getTo();
            String message = messageList.get(i).getMessage();
            
            if (from.equals(target) || target.equals("all")) {
                current += from + ": " + message + "\n";
                continue;
            }
            
            if (to.equals(target) || target.equals("all")) {
                current += from + ": " + message + "\n";
            }
        }
        
        final String current_ = current;
        new Thread() {
            @Override
            public void run() {
                SwingUtilities.invokeLater(new Runnable(){
                    public void run(){
                        currentText.setText(current_);
                    }
                }); 
            }
        }.start();
    }
    
    private void insertMessage(String from, String to, String message) {
        String target = targetText.getText();
        
        if (from.equals(target) || target.equals("all")) {
            currentPrinting(from + ": " + message);
        }
        
        historyPrinting(from + "->" + to + ": " + message);
        
        messageList.add(new Message(from, to, message));
    }
    
    private void currentPrinting(String line) {
        final String line_ = line + "\n";
        new Thread() {
            @Override
            public void run() {
                SwingUtilities.invokeLater(new Runnable(){
                    public void run(){
                        currentText.append(line_);
                    }
                }); 
            }
        }.start();
    }
    
    private void historyPrinting(String line) {
        final String line_ = line + "\n";
        new Thread() {
            @Override
            public void run() {
                SwingUtilities.invokeLater(new Runnable(){
                    public void run(){
                        historyText.append(line_);
                    }
                }); 
            }
        }.start();
    }
    
    private boolean startConnected(String username, String password, boolean needCreated) {
        try {
            System.out.println(computer + " " + port);
            socket = new Socket(computer, port);
            input = new Scanner(socket.getInputStream());
            output = new PrintWriter(socket.getOutputStream());
        } catch (Exception exc) {
            JOptionPane.showMessageDialog(null, exc.toString());
            return false;
        }
        
        if (needCreated) {
            output.println("create " + username + " " + password);
        } else {
            output.println("login " + username + " " + password);
        }
        output.flush();
        
        while (input.hasNext()) {
            String response = input.next();
            if (response.equals("accept")) {
                return true;
            }
            break;
        }
        
        JOptionPane.showMessageDialog(null, "The Server rejected your connection!");
        
        return false;
    }
    
    private void initialize_gui() {
        int x = frameMargin;
        int y = frameMargin;
        
        usernameLabel = new JLabel();
        usernameLabel.setText("Username:");
        usernameLabel.setBounds(x, y, labelWidth, componentHeight);
        usernameLabel.setForeground(Color.blue);
        add(usernameLabel);
        
        int textLength = frameWidth - 4 * frameMargin - labelWidth - buttonWidth;
        x += labelWidth + frameMargin;
        
        usernameText = new JTextField();
        usernameText.setText(frameTitle);
        usernameText.setBounds(x, y, textLength, componentHeight);
        usernameText.setEditable(false);
        add(usernameText);
        
        x += textLength + frameMargin;
        
        logoutButton = new JButton();
        logoutButton.setText("Log out");
        logoutButton.setBounds(x, y, buttonWidth, componentHeight);
        add(logoutButton);
        
        logoutButton.addActionListener(LogOutActionListener);
        
        x = frameMargin;
        y += componentHeight + frameMargin;
        
        targetLabel = new JLabel();
        targetLabel.setText("Target to:");
        targetLabel.setBounds(x, y, labelWidth, componentHeight);
        targetLabel.setForeground(Color.blue);
        add(targetLabel);
        
        x += labelWidth + frameMargin;
        
        targetText = new JTextField();
        targetText.setText("all");
        targetText.setBounds(x, y, textLength, componentHeight);
        targetText.addKeyListener(TargetKeyListener);
        add(targetText);
        
        x += textLength + frameMargin;
        
        filterButton = new JButton();
        filterButton.setText("Filter");
        filterButton.setBounds(x, y, buttonWidth, componentHeight);
        add(filterButton);
        
        filterButton.addActionListener(FilterActionListener);
        
        x = frameMargin;
        y += componentHeight + frameMargin;
        
        int textWidth = (frameWidth - 3 * frameMargin) / 2;
        
        currentLabel = new JLabel();
        currentLabel.setText("Current chat window");
        currentLabel.setBounds(x, y, textWidth, componentHeight);
        currentLabel.setForeground(Color.blue);
        add(currentLabel);
        
        x += textWidth + frameMargin;
        
        historyLabel = new JLabel();
        historyLabel.setText("Chat history");
        historyLabel.setBounds(x, y, textWidth, componentHeight);
        historyLabel.setForeground(Color.blue);
        add(historyLabel);
        
        x = frameMargin;
        y += componentHeight;
        
        currentText = new JTextArea();
        currentText.setEditable(false);
        currentText.setBounds(x, y, textWidth, textHeight);
        currentText.setLineWrap(true);
        
        currentScroll = new JScrollPane (currentText);
        currentScroll.setBounds(x, y, textWidth, textHeight);
        currentScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        
        add(currentScroll);
        // add(currentText);
     
        x += textWidth + frameMargin;
        
        historyText = new JTextArea();
        historyText.setEditable(false);
        historyText.setBounds(x, y, textWidth, textHeight);
        historyText.setLineWrap(true);
        
        historyScroll = new JScrollPane (historyText);
        historyScroll.setBounds(x, y, textWidth, textHeight);
        historyScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        
        add(historyScroll);
        // add(historyText);
        
        x = frameMargin;
        y += textHeight + frameMargin;
        
        messageLabel = new JLabel();
        messageLabel.setText("Message:");
        messageLabel.setBounds(x, y, labelWidth, componentHeight);
        messageLabel.setForeground(Color.blue);
        add(messageLabel);
        
        y += componentHeight;
        
        textLength = frameWidth - buttonWidth - 3 * frameMargin;
        
        messageText = new JTextField();
        messageText.setBounds(x, y, textLength, componentHeight);
        add(messageText);
        
        messageText.addKeyListener(MessageKeyListener);
        
        x += textLength + frameMargin;
        
        sendButton = new JButton();
        sendButton.setText("Send");
        sendButton.setBounds(x, y, buttonWidth, componentHeight);
        add(sendButton);
        
        sendButton.addActionListener(SendActionListener);
    }
    
    private ActionListener LogOutActionListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            System.out.println("Closing socket");
            output.flush();
            // input.close();
            // output.close();
            
            try {
                socket.close();
            } catch (Exception exc) {
                System.err.println(exc.toString());
                JOptionPane.showMessageDialog(null, exc.toString());
            }

            new Thread() {
                @Override
                public void run() {
                    Login login = new Login();
                }
            }.start();
        
            new Thread() {
                @Override
                public void run() {
                    SwingUtilities.invokeLater(new Runnable(){
                        public void run() {
                            System.out.println("Logout");
                            setVisible(false);
                            dispose();
                        }
                    }); 
                }
            }.start();
        }
    };
    
    private ActionListener FilterActionListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            messageFilter();
        }
    };
    
    private ActionListener SendActionListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            sendMessage();
        }
    };
    
    private KeyListener MessageKeyListener = new KeyListener() {
        @Override
        public void keyPressed(KeyEvent e){
            if (e.getKeyCode() == 10) {
                sendMessage();
            }
        }

        @Override
        public void keyTyped(KeyEvent e) {
        }

        @Override
        public void keyReleased(KeyEvent e) {
        }
    };
    
    private KeyListener TargetKeyListener = new KeyListener() {
        @Override
        public void keyPressed(KeyEvent e){
            if (e.getKeyCode() == 10) {
                messageFilter();
            }
        }

        @Override
        public void keyTyped(KeyEvent e) {
        }

        @Override
        public void keyReleased(KeyEvent e) {
        }
    };
    
}
