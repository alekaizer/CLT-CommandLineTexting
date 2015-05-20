package com.lekaizer.commandlinetexting.clt.model;

import java.util.ArrayList;

/**
 * Created by lekaizer on 10/02/15.
 */
public class Conversation {
        private String owner;
        private ArrayList<String> conversation;

    /**
     * Constructor that create a new conversation in you text's feed.
     * @param name String the name of your opponent.
     */
    public Conversation(String name){
        owner = name;
        conversation = new ArrayList<>();
    }

    /**
     * addMessage adds a new message(msg) to the conversation
     * @param msg String that will be added to the conversation
     */
    public void addMessage(String msg){
        conversation.add(msg);
    }

    /**
     * getMsgList returns list of the text you have exchanged with this opponent
     * @return ArrayList<String> conversation the list of messages
     */
    public ArrayList<String> getMsgList(){return conversation;}

    /**
     * getConversationSize returns the number of messages exchanged with this contact
     * @return int size the number of messages exchanged with this contact
     */
    public int getConversationSize(){
        return conversation.size();
    }

    /**
     * This returns the name of the contact
     * @return owner the name of the contact
     */
    public String getOwner(){return owner;}
}
