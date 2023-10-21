package de.loegler.jstone.client.gui;

import de.loegler.jstone.client.main.FriendManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class FriendFrame {
    private JFrame frame;
    private JScrollPane requestScroll, listScroll;
    private JPanel requestPanel,listPanel,wrapper,wrapper2;
    private JTextField userToAdd;
    private JButton requestFriend,requestEcken;
    private FriendManager manager;
    private ActionListener friendListener = new FriendListener();

    public FriendFrame(FriendManager client){
        this.manager=client;
        frame = new JFrame("JStone - Freunde");
        frame.setSize(450,350);
        frame.setLocationRelativeTo(null);
        GridLayout requestLayout = new GridLayout(0,1);
        requestLayout.setVgap(10);
        requestPanel = new JPanel(requestLayout);
        GridLayout listLayout = new GridLayout(0, 1);
        listLayout.setVgap(10);
        listPanel = new JPanel(listLayout);
        requestScroll = new JScrollPane(requestPanel);
        listScroll = new JScrollPane(listPanel);
        listPanel.add(new JLabel("Deine Freunde:"));
        requestPanel.add(new JLabel("Freundschaftsanfragen:"));
        GridLayout mgr = new GridLayout(0, 2);
        mgr.setHgap(50);
        mgr.setVgap(20);
        frame.getContentPane().setLayout(mgr);

        frame.getContentPane().add(requestScroll);
        frame.getContentPane().add(listScroll);
        wrapper=new JPanel(new GridLayout(0,1));
        wrapper2=new JPanel(new GridLayout(0,1));
        userToAdd = new JTextField();
        requestFriend=new JButton("Füge als Freund hinzu");
        requestFriend.addActionListener(this.friendListener);
        requestEcken = new JButton("Um wie viele Ecken befreundet?");
        requestEcken.addActionListener(this.friendListener);
        wrapper.add(new JLabel("Nutzername:"));
        wrapper2.add(userToAdd);
        wrapper.add(requestFriend);
        wrapper2.add(requestEcken);
        frame.getContentPane().add(wrapper);
        frame.getContentPane().add(wrapper2);

        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }

    public void dispose(){
        frame.dispose();
    }

    public void addFreundschaftsanfrage(String name){
       JPanel panel = new FriendPanel(name);
       panel.add(new JLabel(name));
       JButton accept = new JButton("J");
       JButton deny = new JButton("X");
       panel.add(accept);
       panel.add(deny);
       accept.addActionListener(this.friendListener);
       deny.addActionListener(this.friendListener);
       requestPanel.add(panel);
       requestPanel.revalidate();
       requestPanel.repaint();
    }

    public void addFriend(String name){
        JPanel panel = new FriendPanel(name);
        panel.add(new JLabel(name));
        listPanel.add(panel);
        listPanel.revalidate();
        listPanel.repaint();
    }

    private static class FriendPanel extends JPanel{
        private String username;

        public FriendPanel(String username){
            super();
            this.username=username;
        }
        public String getUsername() {
            return username;
        }
    }

    private class FriendListener implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e) {
            Object source = e.getSource();
            if(source instanceof JButton){
                JButton button =  (JButton) source;
                if(button == requestFriend){
                    String username = userToAdd.getText();
                    if(username!=null&&!username.isEmpty()){
                        manager.sendFriendshipRequest(username);
                    }
                }else if(button== requestEcken){
                    String username= userToAdd.getText();
                    if(username!=null&&!username.isEmpty())
                        manager.requestEcken(username);
                }

                Object parent = button.getParent();
                if(parent instanceof FriendPanel){
                    FriendPanel p = (FriendPanel) parent;
                    String friendName = p.getUsername();
                    final String clicked = button.getText();
                    if(clicked.equalsIgnoreCase("J")){
                        //Freundschaft annehmen
                        manager.acceptFriendship(friendName);

                    }else if(clicked.equalsIgnoreCase("X")){
                        //Freundschaft auflösen/ ablehnen
                        manager.denyFriendship(friendName);
                    }else{
                        //GGF: Chat
                    }
                }
            }
        }
    }
}
