package com.lekaizer.commandlinetexting.clt;

import com.lekaizer.commandlinetexting.clt.model.Conversation;

import junit.framework.TestCase;

/**
 * Created by lekaizer on 16/02/15.
 */
public class ConversationTest extends TestCase {
    Conversation conversation;

    @Override
    public void setUp() throws Exception {
        super.setUp();
    }

    public void test() throws Exception {
        assertNotNull(new Conversation("Achille"));
    }

    @Override
    public void tearDown() throws Exception {
        super.tearDown();
    }
}
