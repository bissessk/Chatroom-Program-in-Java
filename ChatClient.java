package chatclient;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.*;
import java.util.*;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

/**
 * HW 04 Chat Server
 * Chat CLIENT
 * @author kb2784
 */

public class ChatClient {
    
    static JPanel      jp              = new JPanel     ();
    static JButton     enterChat_butt  = new JButton    ("Enter Chatroom");
    static JLabel      screenName      = new JLabel     ("Screen Name :");
    static JLabel      port            = new JLabel     ("Port :");
    static JTextField  screenNameField = new JTextField ();
    static JTextField  portField       = new JTextField ();
    static JFrame      jf              = new JFrame     ("ChatServer (kb2784)");
    
    static void displayEnterChatWindow() {
        
        jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jf.setSize(600 , 200);
        jp.setLayout(new GridLayout(3,2));
        screenName.setHorizontalAlignment(SwingConstants.CENTER);
        jp.add(screenName);
        jp.add(screenNameField);
        port.setHorizontalAlignment(SwingConstants.CENTER);
        jp.add(port);
        jp.add(portField);
        enterChat_butt.addActionListener(new ButtonListener());
        jp.add(enterChat_butt);
        jf.add(jp);
        jf.setVisible(true);
    }
    
    public static void main(String[] args) {
        
        displayEnterChatWindow();

    }
}

class ButtonListener implements ActionListener {
    
    static Socket       sock;
    static PrintStream  sout;
    static Scanner      sin;
    static JButton      send_butt    = new JButton    ("Send");
    static JTextField   messageField = new JTextField ();
    static JTextArea    chatRoom     = new JTextArea  ();
    static JFrame       jf           = new JFrame     ("ChatServer (kb2784)");
    
    @Override
    public void actionPerformed(ActionEvent arg0) {
        JButton jb = (JButton)arg0.getSource();
        
        if (jb == ChatClient.enterChat_butt) {
            
            ChatClient.enterChat_butt.setEnabled(false);
            
            try {
                sock = new Socket("localhost",5190);
                if (sock.isConnected()) {
                    
                    sout = new PrintStream(sock.getOutputStream());
                    sin  = new Scanner(sock.getInputStream());

                    jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                    jf.setSize(600 , 600);

                    JPanel jp1 = new JPanel ();
                    jp1.setLayout(new BorderLayout());

                    chatRoom.setEditable(false);
                    sout.print(ChatClient.screenNameField.getText() + "\r\n");

                    JPanel jp2 = new JPanel ();
                    jp2.setLayout(new BorderLayout());

                    send_butt.addActionListener(new ButtonListener2());

                    jp2.add(messageField, BorderLayout.CENTER);
                    jp2.add(send_butt, BorderLayout.EAST);
                    jp2.setPreferredSize(new Dimension(600,60));
                    jp1.add(chatRoom, BorderLayout.CENTER);
                    jp1.add(jp2,BorderLayout.SOUTH);
                    jf.add(jp1);
                    jf.setVisible(true);

                    Thread uc = new UpdateChat(sin);
                    uc.start();
                }
            }
            
            catch (UnknownHostException e) {System.out.println("Unknown Host: " + e.toString());}
            catch (IOException ex) {System.out.println("IO Error: " + ex.toString());}
        }
    }
}

class ButtonListener2 implements ActionListener {
    @Override
    public void actionPerformed(ActionEvent arg0) {
        JButton jb  = (JButton)arg0.getSource();
        String exit = "EXIT";
        if (jb == ButtonListener.send_butt) {
            
            String mess = ButtonListener.messageField.getText();
            ButtonListener.sout.print(mess+"\r\n");
            ButtonListener.messageField.setText("");
            
            if (mess.equals(exit)==true) {
                ButtonListener.jf.dispose();
                ChatClient.jf.dispose();
            }   
        }
    }
}

class UpdateChat extends Thread {
    Scanner sin;
    
    UpdateChat (Scanner sin) { this.sin = sin; }
    
    @Override
    public void run() {
        while (sin.hasNext()) {
            ButtonListener.chatRoom.append(sin.nextLine()  + "\r\n");
        }
    }
}

