package in.cioc.trackingapp;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.provider.Telephony;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import java.lang.ref.WeakReference;

/**
 * Created by admin on 17/09/18.
 */

public class BackgroundReceiver extends BroadcastReceiver {
    private static final String TAG =
            BackgroundReceiver.class.getSimpleName();
    public static Context cxt;
    private static SmsSendingListener smsSendingListener;
    static String txt="";
    private static ProgressDialog progressDialogInbox;
    private static CustomHandler customHandler;

    @Override
    public void onReceive(Context context, Intent intent) {
        cxt = context;
//        Bundle data = intent.getExtras();
//        if (intent.getAction().equals(Telephony.Sms.Intents.SMS_RECEIVED_ACTION)) {
//            String smsSender = "";
//            String smsBody = "";

//        if (data != null) {
//
//            Object[] pdus = (Object[]) data.get("pdus");
//
//            for(int i=0;i<pdus.length;i++){
//                SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) pdus[i]);
//
//                String sender = smsMessage.getDisplayOriginatingAddress();
//                //You must check here if the sender is your provider and not another one with same text.
//
//                String messageBody = smsMessage.getMessageBody();
//
//                //Pass on the text to our listener.
//                if (mListener == null){
//                    return;
//                }
//                mListener.messageReceived(messageBody);
//            }
//        }
//                    Object[] pdus = (Object[]) data.get("pdus");
//
//                    for (int i = 0; i < pdus.length; i++) {
//                        SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) pdus[i]);
//
//                        String sender = smsMessage.getDisplayOriginatingAddress();
//
//                        String messageBody = smsMessage.getMessageBody();
//
//                        //Pass on the text to our listener.
//
////                    Object[] pdus = (Object[]) intent.getExtras().get("pdus");
//////                    for (int i = 0; i < pdus.length; i++) {
//////                        SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) pdus[i]);
//////                        String sender = smsMessage.getDisplayOriginatingAddress();
//////                        String messageBody = smsMessage.getMessageBody();
//////                        Toast.makeText(context, "sender " + sender + "\nmessageBody: " + messageBody, Toast.LENGTH_SHORT).show();
//////                    }
////                            if (pdus == null) {
////                                // Display some error to the user
////                                Log.e("", "SmsBundle had no pdus key");
////                                return;
////                            }
////                            SmsMessage[] messages = new SmsMessage[pdus.length];
////                            for (int i = 0; i < messages.length; i++) {
////                                messages[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
////                                smsBody += messages[i].getMessageBody();
////                            }
////                            smsSender = messages[0].getOriginatingAddress();
//                        if (mListener == null) {
//                            return;
//                        }
//                        mListener.messageReceived("smsSender " + sender + "\nsmsBody: " + messageBody);
//                        context.startService(new Intent(context, BackgroundService.class));
//                    }
//                }

//            }

//                    SmsMessage[] msgs;
//                    String strMessage = "";
//                    String format = data.getString("format");
//                    // Retrieve the SMS message received.
//                    Object[] pdus = (Object[]) data.get("data");
//                    if (pdus != null) {
//                        // Check the Android version.
//                        boolean isVersionM =
//                                (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M);
//                        // Fill the msgs array.
//                        msgs = new SmsMessage[pdus.length];
//                        for (int i = 0; i < msgs.length; i++) {
//                            // Check Android version and use appropriate createFromPdu.
//                            if (isVersionM) {
//                                // If Android version M or newer:
//                                msgs[i] = SmsMessage.createFromPdu((byte[]) pdus[i], format);
//                            } else {
//                                // If Android version L or older:
//                                msgs[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
//                            }
//                            // Build the message to show.
//                            strMessage += "SMS from " + msgs[i].getOriginatingAddress();
//                            strMessage += " :" + msgs[i].getMessageBody() + "\n";
//                            // Log and display the SMS message.
//                            Log.d(TAG, "onReceive: " + strMessage);
//
//                            if (mListener == null) {
//                                return;
//                            }
//                            mListener.messageReceived(strMessage);
//                        }
//                    }
//                }
//        context.startService(new Intent(context, BackgroundService.class));

        if (!intent.getAction().equals("android.intent.action.PHONE_STATE")) {
            return;
        } else {
            String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);
            String incomingNo = intent.getStringExtra(TelephonyManager.EXTRA_INCOMING_NUMBER);
            if (state.equals(TelephonyManager.EXTRA_STATE_RINGING)||state.equals(TelephonyManager.EXTRA_STATE_IDLE)) {
                if (incomingNo != null) {
                    String mobNo = incomingNo.replace("+91", "");
                    Toast.makeText(context, "Incoming Call State " + mobNo, Toast.LENGTH_SHORT).show();
                    context.startService(new Intent(context, BackgroundService.class).putExtra("mob", mobNo));
                }
            }
            if (state.equals(TelephonyManager.EXTRA_STATE_OFFHOOK)) {
                Toast.makeText(context, "Call Received State", Toast.LENGTH_SHORT).show();
            }
            if (state.equals(TelephonyManager.EXTRA_STATE_IDLE)){
                String number = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
                Toast.makeText(context,"Call Idle State - "+number,Toast.LENGTH_SHORT).show();
            }
        }

        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            context.startService(new Intent(context, BackgroundService.class));
        }

        if (intent.getAction().equals(Telephony.Sms.Intents.RESULT_SMS_HANDLED)){
            Toast.makeText(context, "sms handled", Toast.LENGTH_SHORT).show();
        }

//        init();
    }

    void init(){
        customHandler = new CustomHandler(new BackgroundReceiver());
        progressDialogInbox = new ProgressDialog(cxt);
        populateMessageList();
    }

    private void showProgressDialog(String message) {
        progressDialogInbox.setMessage(message);
        progressDialogInbox.setIndeterminate(true);
        progressDialogInbox.setCancelable(true);
        progressDialogInbox.show();
    }
    private void populateMessageList() {
        showProgressDialog("Fetching Inbox Messages...");
        startThread();
    }

    public class FetchMessageThread extends Thread {

        public int tag = -1;

        public FetchMessageThread(int tag) {
            this.tag = tag;
        }

        @Override
        public void run() {

            fetchInboxSms();
            if (smsSendingListener==null){
                return;
            }
            smsSendingListener.smsSending(txt);
            customHandler.sendEmptyMessage(0);

        }
    }

    public String fetchInboxSms(){
        Uri uriSms = Uri.parse("content://sms/sent");

        Cursor cursor = cxt.getContentResolver()
                .query(uriSms,
                        new String[] { "_id", "address", "date", "body",
                                "type", "read" }, null, null,
                        "date" + " COLLATE LOCALIZED ASC");
        if (cursor != null) {
            cursor.moveToLast();
            if (cursor.getCount() > 0) {

                txt = cursor.getString(cursor
                        .getColumnIndex("address")) +" \n"+cursor.getString(cursor
                        .getColumnIndex("body"));
//                Toast.makeText(cxt, "call rcver"+txt, Toast.LENGTH_SHORT).show();
            }
        }
        return txt;
    }

    private static FetchMessageThread fetchMessageThread;

    private static int currentCount = 0;

    public synchronized void startThread() {
        if (fetchMessageThread == null) {
            fetchMessageThread = new FetchMessageThread(currentCount);
            fetchMessageThread.start();
        }
    }

    public synchronized void stopThread() {
        if (fetchMessageThread != null) {
            Log.i("Cancel thread", "stop thread");
            FetchMessageThread moribund = fetchMessageThread;
            currentCount = fetchMessageThread.tag == 0 ? 1 : 0;
            fetchMessageThread = null;
            moribund.interrupt();
        }
    }

    public static void sentSMS(SmsSendingListener listener){
        smsSendingListener = listener;
    }


    static class CustomHandler extends Handler {
        private final WeakReference<BackgroundReceiver> activityHolder;

        CustomHandler(BackgroundReceiver inboxListActivity) {
            activityHolder = new WeakReference<BackgroundReceiver>(inboxListActivity);
        }

        @Override
        public void handleMessage(android.os.Message msg) {

            BackgroundReceiver inboxListActivity = activityHolder.get();
            if (fetchMessageThread != null
                    && currentCount == fetchMessageThread.tag) {
                Log.i("received result", "received result");
                fetchMessageThread = null;

                if (smsSendingListener==null){
                    return;
                }
                smsSendingListener.smsSending(txt);
                Toast.makeText(cxt, txt, Toast.LENGTH_SHORT).show();

                progressDialogInbox.dismiss();
            }
        }
    }

    private DialogInterface.OnCancelListener dialogCancelListener = new DialogInterface.OnCancelListener() {

        @Override
        public void onCancel(DialogInterface dialog) {
            stopThread();
        }

    };


}