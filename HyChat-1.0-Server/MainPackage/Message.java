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

public class Message {

    private String from;
    private String to;
    private String message;

    public Message(String from, String to, String message) {
        this.from = from;
        this.to = to;
        this.message = message;
    }
    
    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

    public String getMessage() {
        return message;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
