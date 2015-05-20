package com.lekaizer.commandlinetexting.clt;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.ActionBarActivity;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Patterns;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lekaizer.commandlinetexting.clt.model.Conversation;
import com.lekaizer.commandlinetexting.clt.model.Timeline;


public class CLI extends ActionBarActivity implements View.OnKeyListener{
    private String currentConversation;
    private boolean conversationAlreadyOpened;
    Timeline feed = new Timeline();
    protected BroadcastReceiver smsReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            SmsMessage[] msgs = null;
            String str = "";
            if (bundle != null)
            {
                //---retrieve the SMS message received---
                Object[] pdus = (Object[]) bundle.get("pdus");
                msgs = new SmsMessage[pdus.length];
                for (int i=0; i<msgs.length; i++){
                    msgs[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                    str += getContactPhoneName(msgs[i].getOriginatingAddress());
                    //if(msgs[i].getOriginatingAddress().contains(currentConversation)) {
                        NotificationCompat.Builder notif = new NotificationCompat.Builder(getApplicationContext());
                        notif.setContentTitle(getContactPhoneName(msgs[i].getOriginatingAddress()));
                        notif.setContentText(msgs[i].getMessageBody().toString());
                        notif.setSmallIcon(R.drawable.abc_ic_go_search_api_mtrl_alpha);
                        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                        notificationManager.notify(001,notif.build());

                    //}
                    str += "> ";
                    str += msgs[i].getMessageBody().toString();
                    str += "\n";
                }

                TextView text = new TextView(getApplicationContext());
                text.setText(str);
                text.setTextColor(Color.WHITE);
                text.setTextSize(12);
                ((LinearLayout) findViewById(R.id.screen)).addView(text);
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cli);

        currentConversation = null;
        conversationAlreadyOpened = false;

        EditText entry = (EditText)findViewById(R.id.entry);

        entry.setOnKeyListener(this);
    }

    /**
     * Called after {@link #onStop} when the current activity is being
     * re-displayed to the user (the user has navigated back to it).  It will
     * be followed by {@link #onStart} and then {@link #onResume}.
     * <p/>
     * <p>For activities that are using raw {@link android.database.Cursor} objects (instead of
     * creating them through
     * {@link #managedQuery(android.net.Uri, String[], String, String[], String)},
     * this is usually the place
     * where the cursor should be requeried (because you had deactivated it in
     * {@link #onStop}.
     * <p/>
     * <p><em>Derived classes must call through to the super class's
     * implementation of this method.  If they do not, an exception will be
     * thrown.</em></p>
     *
     * @see #onStop
     * @see #onStart
     * @see #onResume
     */
    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStart(){
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.provider.Telephony.SMS_RECEIVED");
        registerReceiver(smsReceiver, filter);
        super.onStart();
    }

    protected void onStop(){
        unregisterReceiver(smsReceiver);
        super.onStop();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_cli, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Called when a hardware key is dispatched to a view. This allows listeners to
     * get a chance to respond before the target view.
     * <p>Key presses in software keyboards will generally NOT trigger this method,
     * although some may elect to do so in some situations. Do not assume a
     * software input method has to be key-based; even if it is, it may use key presses
     * in a different way than you expect, so there is no way to reliably catch soft
     * input key presses.
     *
     * @param v       The view the key has been dispatched to.
     * @param keyCode The code for the physical key that was pressed
     * @param event   The KeyEvent object containing full information about
     *                the event.
     * @return True if the listener has consumed the event, false otherwise.
     */
    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        if((event.getAction()==KeyEvent.ACTION_DOWN)  && (keyCode==KeyEvent.KEYCODE_ENTER)){

               String r = commandHandler(((EditText) findViewById(v.getId())).getText().toString());
               if (r != null) {
                   switch (r){
                       case "open_error":
                           showToast(getApplicationContext(), "You should close current conversation first");
                           break;
                       case "ls_error":
                           showToast(getApplicationContext(), "Can't execute this command on this run level");
                           showToast(getApplicationContext(), "Close conversation first");
                           break;
                       case "closed":
                           showToast(getApplicationContext(), "Conversation with \"" + currentConversation + "\" is closed");
                           currentConversation = null;
                           break;
                       case "deleted":
                           showToast(getApplicationContext(), "Conversation with \"" + currentConversation + "\" is deleted");
                           currentConversation = null;
                           break;
                       case "command_error":
                           showToast(getApplicationContext(),"UNKNOWN COMMAND");
                           break;
                       default:

                           TextView text = new TextView(this);
                           text.setText("You> "+r);
                           text.setTextColor(Color.WHITE);
                           text.setTextSize(12);
                           ((LinearLayout) findViewById(R.id.screen)).addView(text);
                   }
               }

        }


        ((EditText)findViewById(v.getId())).setText("");
        return false;
    }

    public String commandHandler(String command){
        String arg0 = command.split(" ")[0];


            switch (arg0){
                case "open":
                        return open(command);
                case "ls":
                        return ls(command);
                case "delete":
                        return delete();
                case "del":
                        return  delete();
                case "close":
                    return close();
                default:
                    if(conversationAlreadyOpened)
                         return textHandler(command);
                    else
                        return  "command_error";
            }

    }


    /**
     * TextHandler is the method to send the specified message, this method call the sendSMS(String)
     * @param msg the text to be sent
     * @return the msg sent to be displayed on the screen
     */
    public String textHandler(String msg){
        String destinator ="";
        if(isValidMobile(currentConversation))
            destinator=currentConversation;
        else {
            destinator = getContactPhoneNumber(currentConversation);
        }
        sendSMS(destinator,msg);
        return msg;
    }


    private boolean isValidMobile(String phoneNumber) {

        if (Patterns.PHONE.matcher(phoneNumber).matches()){
            if (phoneNumber.length() < 6 || phoneNumber.length() > 13) {
                return false;
            } else {
                return true;
            }
        }
        return false;
    }

    /**
     * Method to send SMS using SMSManager
     * @param phoneNumber the phone number to send the text to
     * @param message the text to be sent
     */
     private void sendSMS(String phoneNumber, String message)
    {
        PendingIntent pi = PendingIntent.getActivity(this, 0,
            new Intent(""), 0);
        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(phoneNumber, null, message, pi, null);
    }

    private void showToast(Context context, String message){
        Toast.makeText(context,message,Toast.LENGTH_SHORT).show();
    }
    /**
     * Open (or create a new conversation if it doesn't exist) a new conversation with the contact name specified
     * @param command the command and the argument to execute
     * @return current contact with who you are talking with
     */
    public String open(String command){
        String arg = command.substring(command.indexOf(" "),command.length()).trim();
         if(!conversationAlreadyOpened) {

             if(!android.util.Patterns.PHONE.matcher(arg).matches()) {
                 Cursor cursor = getApplicationContext().getContentResolver().
                         query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
                 String contactList = "";
                 int numberOfMatchedContacts =0;
                 while(cursor.moveToNext()){
                      int name_idx = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
                      String name = cursor.getString(name_idx);
                      if(name.toLowerCase().contains(arg.toLowerCase())){
                        contactList += "\n"+name;
                        numberOfMatchedContacts++;
                      }
                 }
                 if(numberOfMatchedContacts > 1){
                     contactList = command+"\nCLT: ambiguous contacts" +contactList;
                 } else
                    if (!feed.contains(arg))
                     feed.createNewConversation(new Conversation(contactList.replace("\n","")));
                 currentConversation = arg;
                 conversationAlreadyOpened = true;
                 return contactList;
             } else
                 if (!feed.contains(arg))
                     feed.createNewConversation(new Conversation(arg));

                 currentConversation = arg;
                 conversationAlreadyOpened = true;
             return currentConversation;
         } else {
             return "open_error";
         }

    }


    public String getContactPhoneNumber(String contactName){
        Cursor cursor = getApplicationContext().getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,null,null,null);
        while (cursor.moveToNext()){
            int phone_idx = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
             int name_idx = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
            String _contactName = cursor.getString(name_idx);
            if(_contactName.equalsIgnoreCase(contactName))
                return cursor.getString(phone_idx);
        }

        return "";
    }

    public String getContactPhoneName(String contactPhone){
          Cursor cursor = getApplicationContext().getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,null,null,null);
        while (cursor.moveToNext()){
            int phone_idx = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
             int name_idx = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
            String _contactPhone = cursor.getString(phone_idx);
            if((_contactPhone.equalsIgnoreCase(contactPhone)) || (_contactPhone.contains(contactPhone)))
                return cursor.getString(name_idx);
        }

        return contactPhone;
    }

    public String ls(String command){
        if(!conversationAlreadyOpened) {
            String tmpString = command;
            for (int i = 0; i < feed.size(); i++) {

                if (i % 2 == 0)
                    tmpString += "\n";
                tmpString += feed.getKeyAt(i) + " ";
            }
            return tmpString;
        } else {
            return "ls_error";
        }

    }

    public String delete(){
        feed.delete(currentConversation);
        conversationAlreadyOpened = false;
        return "deleted";
    }

    public String close(){
        conversationAlreadyOpened = false;
        return "closed";
    }
}
