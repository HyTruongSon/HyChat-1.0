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

import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class ClientData {

    private String username;
    private Socket socket;
    private Scanner socketInput;
    private PrintWriter socketOutput;
    private boolean alive;

    ClientData(String username, Socket socket, Scanner socketInput, PrintWriter socketOutput) {
        this.username = username;
        this.socket = socket;
        this.socketInput = socketInput;
        this.socketOutput = socketOutput;
        alive = true;
    }

    public String getUsername() {
        return username;
    }

    public Socket getSocket() {
        return socket;
    }

    public Scanner getSocketInput() {
        return socketInput;
    }

    public PrintWriter getSocketOutput() {
        return socketOutput;
    }

    public boolean isAlive() {
        return alive;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setSocket(Socket socket) {
        this.socket = socket;
    }

    public void setSocketInput(Scanner socketInput) {
        this.socketInput = socketInput;
    }

    public void setSocketOutput(PrintWriter socketOutput) {
        this.socketOutput = socketOutput;
    }

    public void setAlive(boolean alive) {
        this.alive = alive;
    }
}
