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

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

public class ServerNoGUI {
    
    // Constants/Variables for Socket Programming
    private int port; // 1024-65535
    private ServerSocket serverSocket;
    
    private final String clientDataFileName = "users.dat";
    
    private ArrayList<ClientData> clientList = new ArrayList<>();
    
    public ServerNoGUI() {
        try (Scanner scanner = new Scanner(System.in)) {
            System.out.print("Port: ");
            port = scanner.nextInt();
        }
        
        new Thread() {
            public void run() {
                ServerService();
            }
        }.start();
    }
    
    private void terminalPrinting(String line) {
        final Object syncObj = System.out;
        synchronized (syncObj) {
            System.out.println(line);
        }
    }
    
    private void ServerService() {
        terminalPrinting("Server starts");
        try {
            serverSocket = new ServerSocket(port);
        } catch (Exception exc) {
            terminalPrinting(exc.toString());
            return;
        }
        terminalPrinting("Successfully initialized the server socket. Port: " + Integer.toString(port));
        
        boolean stop = false;
        while (true) {
            Socket socket = null;
            
            try {
                socket = serverSocket.accept();
            } catch (Exception exc) {
                terminalPrinting(exc.toString());
                continue;
            }
         
            new Thread(new MultiThreadServer(socket)).start();
            
            if (stop) {
                break;
            }
        }
        
        try {
            serverSocket.close();
        } catch (IOException exc) {
            terminalPrinting(exc.toString());
        }
    }
    
    private class MultiThreadServer implements Runnable {
        private Socket socket;
        private Scanner socketInput;
        private PrintWriter socketOutput;
        
        MultiThreadServer(Socket socket) {
            this.socket = socket;
        }
        
        @Override
        public void run() {
            try {
                socketInput = new Scanner(socket.getInputStream());
            } catch (Exception exc) {
                terminalPrinting(exc.toString());
                return;
            }
            
            try {
                socketOutput = new PrintWriter(socket.getOutputStream());
            } catch (Exception exc) {
                terminalPrinting(exc.toString());
                return;
            }
            
            // Varify the connection by comparing the username and password
            boolean success = false;
            String username = new String();
            String password = new String();
            
            while (true) {
                if (!socketInput.hasNext()) {
                    break;
                }
                
                String command = socketInput.next();
                
                if (command.equals("create")) {
                    if (!socketInput.hasNext()) {
                        continue;
                    }
                    username = socketInput.next();
                    
                    if (!socketInput.hasNext()) {
                        continue;
                    }
                    password = socketInput.next();
                    
                    terminalPrinting("create " + username);
                    
                    if (!existUser(username)) {
                        insertUser(username, password);
                        success = true;
                    } 
                }
                
                if (command.equals("login")) {
                    if (!socketInput.hasNext()) {
                        continue;
                    }
                    username = socketInput.next();
                    
                    if (!socketInput.hasNext()) {
                        continue;
                    }
                    password = socketInput.next();
                    
                    terminalPrinting("login " + username);
                    
                    if (existUser(username)) {
                        if (getPasswordUser(username).equals(password)) {
                            success = true;
                        }
                    } 
                }
                
                break;
            }
            
            if (success) {
                for (int i = 0; i < clientList.size(); ++i) {
                    if (clientList.get(i).getUsername().equals(username)) {
                        if (clientList.get(i).isAlive()) {
                            socketOutput.println("reject");
                            socketOutput.flush();
                            
                            socketInput.close();
                            socketOutput.close();
                            try {
                                socket.close();
                            } catch (Exception exc) {
                                terminalPrinting(exc.toString());
                            }
                            return;
                        }
                    }
                }
                
                clientList.add(new ClientData(username, socket, socketInput, socketOutput));
                
                socketOutput.println("accept");
                socketOutput.flush();
                
                terminalPrinting("accept " + username);
                
                // Send all history back to the client
                Scanner file = null;
                try {
                    file = new Scanner(new File(username));
                } catch (IOException exc) {
                    terminalPrinting(exc.toString());
                }
                
                if (file != null) {
                    while (file.hasNextLine()) {
                        socketOutput.println(file.nextLine());
                    }
                    file.close();
                }
                
                socketOutput.flush();
            } else {
                socketOutput.println("reject");
                socketOutput.flush();
                
                terminalPrinting("reject " + username);
                
                socketInput.close();
                socketOutput.close();
                try {
                    socket.close();
                } catch (Exception exc) {
                    terminalPrinting(exc.toString());
                }
                return;
            }
            
            while (true) {
                if (!socketInput.hasNext()) {
                    break;
                }
                
                String command = socketInput.next();
                
                if (command.equals("msg")) {
                    if (!socketInput.hasNext()) {
                        break;
                    }
                    String from = socketInput.next();
                    
                    if (!socketInput.hasNext()) {
                        break;
                    }
                    String to = socketInput.next();
                    
                    if (!socketInput.hasNextLine()) {
                        break;
                    }
                    String message = removeFirstSpaces(socketInput.nextLine());
                    
                    terminalPrinting("msg " + from + " " + to + " " + message);
                    
                    sendMessage(from, to, message);
                }
            }
            
            for (int i = 0; i < clientList.size(); ++i) {
                if (clientList.get(i).getUsername().equals(username)) {
                    clientList.get(i).setAlive(false);
                }
            }
            
            terminalPrinting("exit " + username);
            
            socketInput.close();
            socketOutput.close();
            try {
                socket.close();
            } catch (Exception exc) {
                terminalPrinting(exc.toString());
            }
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
    
    private void sendMessage(String from, String to, String message) {
        String str = "msg " + from + " " + to + " " + message;
        
        // Write to the history of from
        PrintWriter file = null;
        try {
            file = new PrintWriter(new FileWriter(from, true));
        } catch (IOException exc) {
            terminalPrinting(exc.toString());
            return;
        }
        file.println(str);
        file.close();
        
        // Write to the history of to
        file = null;
        try {
            file = new PrintWriter(new FileWriter(to, true));
        } catch (IOException exc) {
            terminalPrinting(exc.toString());
            return;
        }
        file.println(str);
        file.close();
        
        // Notice to
        if (to.equals("all")) {
            for (int i = 0; i < clientList.size(); ++i) {
                if (clientList.get(i).isAlive()) {
                    clientList.get(i).getSocketOutput().println(str);
                    clientList.get(i).getSocketOutput().flush();
                }
            }
        } else {
            for (int i = 0; i < clientList.size(); ++i) {
                if (clientList.get(i).getUsername().equals(to)) {
                    if (clientList.get(i).isAlive()) {
                        clientList.get(i).getSocketOutput().println(str);
                        clientList.get(i).getSocketOutput().flush();
                    }
                }
            }
        }
    }
    
    private boolean existUser(String username) {
        Scanner scanner = null;
        
        try {
            scanner = new Scanner(new File(clientDataFileName));
        } catch (IOException exc) {
            terminalPrinting(exc.toString());
            return false;
        }
        
        while (scanner.hasNextLine()) {
            String username_ = scanner.nextLine();
            if (username_.equals(username)) {
                return true;
            }
            if (!scanner.hasNextLine()) {
                break;
            }
            scanner.nextLine();
        }
        
        return false;
    }
    
    private void insertUser(String username, String password) {
        PrintWriter file = null;
        try {   
            file = new PrintWriter(new FileWriter(clientDataFileName, true));
        } catch (IOException exc) {
            terminalPrinting(exc.toString());
            return;
        }
        
        file.println(username);
        file.println(password);
        file.close();
        
        file = null;
        try {   
            file = new PrintWriter(new FileWriter(username, true));
        } catch (IOException exc) {
            terminalPrinting(exc.toString());
            return;
        }
        file.close();
    }
    
    private String getPasswordUser(String username) {
        Scanner scanner = null;
        
        try {
            scanner = new Scanner(new File(clientDataFileName));
        } catch (IOException exc) {
            terminalPrinting(exc.toString());
            return new String();
        }
        
        while (scanner.hasNextLine()) {
            String username_ = scanner.nextLine();
            if (!scanner.hasNextLine()) {
                break;
            }
            String password = scanner.nextLine();
            if (username_.equals(username)) {
                return password;
            }
        }
        
        return new String();
    }
}
