package com.lekaizer.commandlinetexting.clt.model;

import android.support.v4.util.ArrayMap;


/**
 * Created by lekaizer on 10/02/15.
 */
public class Timeline {
    private ArrayMap<String, Conversation> conversations;

    /**
     * Constructor
     */
    public Timeline(){
        conversations = new ArrayMap<>();
    }

    /**
     * Add a new conversation with a new contact to your conversation feed.
     * @param conv the new Conversation that need to be added
     * @return 0 if succeed and -1 if it fails
     * @see com.lekaizer.commandlinetexting.clt.model.Conversation
     */

    public int createNewConversation(Conversation conv){
        if(!conversations.containsKey(conv.getOwner()))
            conversations.put(conv.getOwner(), conv);
        else
            return -1;

        return 0;
    }


    public boolean contains(String contactName){
        if (conversations.containsKey(contactName))
            return true;
        else
            return false;
    }

    public void delete(String key){
        conversations.remove(key);
    }

    public int size(){return conversations.size();}

    public String getKeyAt(int i) {
            return conversations.keyAt(i);
    }
}
