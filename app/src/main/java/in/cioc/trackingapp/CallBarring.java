package in.cioc.trackingapp;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.CallLog;
import android.support.v4.app.ActivityCompat;
import android.telephony.SmsMessage;

import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * Created by Ashish on 17/9/18.
 */

public class CallBarring extends BroadcastReceiver {
    static Context cxt;
    String phNumber, callDuration, dateString, timeString, dir, date;
    int tot_seconds;
    private static SmsListener mListener;


    @Override
    public void onReceive(final Context context, Intent intent) {
        cxt = context;
        Bundle data  = intent.getExtras();

        Object[] pdus = (Object[]) data.get("pdus");

        for(int i=0;i<pdus.length;i++){
            SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) pdus[i]);

            String sender = smsMessage.getDisplayOriginatingAddress();
            //You must check here if the sender is your provider and not another one with same text.

            String messageBody = smsMessage.getMessageBody();

            //Pass on the text to our listener.
            if (mListener == null){
                return;
            }
            mListener.messageReceived(sender+"\n "+messageBody);
        }

//        String msg = fetchInboxSms();
//        if (smsSendingListener == null){
//            return;
//        }
//        smsSendingListener.smsSending(msg);
//        init();
    }

    private void getCalldetailsNow() {
        // TODO Auto-generated method stub
        if (ActivityCompat.checkSelfPermission(cxt, Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Cursor managedCursor = cxt.getContentResolver().query(CallLog.Calls.CONTENT_URI, null, null, null, CallLog.Calls.DATE + " ASC");

        int number = managedCursor.getColumnIndex( CallLog.Calls.NUMBER );
        int duration1 = managedCursor.getColumnIndex( CallLog.Calls.DURATION);
        int type1 = managedCursor.getColumnIndex(CallLog.Calls.TYPE);
        int date1 = managedCursor.getColumnIndex(CallLog.Calls.DATE);

        if(managedCursor.moveToLast() == true) {
            phNumber = managedCursor.getString(number);
            String callDuration1 = managedCursor.getString(duration1);

            String type = managedCursor.getString(type1);
            date = managedCursor.getString(date1);
            int dircode = Integer.parseInt(type);
            dir=null;
            switch (dircode)
            {
                case CallLog.Calls.OUTGOING_TYPE:
                    dir = "OUTGOING";
                    break;
                case CallLog.Calls.INCOMING_TYPE:
                    dir = "INCOMING";
                    break;
                case CallLog.Calls.MISSED_TYPE:
                    dir = "MISSED";
                    break;
                default:
                    dir = "MISSED";
                    break;
            }

            SimpleDateFormat sdf_date = new SimpleDateFormat("dd/MM/yyyy");
            SimpleDateFormat sdf_time = new SimpleDateFormat("h:mm a");
//            SimpleDateFormat sdf_dur = new SimpleDateFormat("KK:mm:ss");

            tot_seconds = Integer.parseInt(callDuration1);
            int hours = tot_seconds / 3600;
            int minutes = (tot_seconds % 3600) / 60;
            int seconds = tot_seconds % 60;

            callDuration = String.format("%02d : %02d : %02d ", hours, minutes, seconds);

            dateString = sdf_date.format(new Date(Long.parseLong(date)));
            timeString = sdf_time.format(new Date(Long.parseLong(date)));
            //  String duration_new=sdf_dur.format(new Date(Long.parseLong(callDuration)));
        }
        managedCursor.close();
    }

    public static void bindListener(SmsListener smsListener) {
        mListener = smsListener;
    }



}
