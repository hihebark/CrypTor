package hihebark.cryptor;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.telephony.SmsMessage;

public class SmsReceiver extends BroadcastReceiver {
    String TAG = SmsReceiver.class.getSimpleName();
    DbTool database;
    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        database = new DbTool(context);
        SmsMessage[] msgs;
        if (bundle != null) {
            Object[] pdus = (Object[]) bundle.get("pdus");
            msgs = new SmsMessage[pdus.length];
            for (int i=0; i < msgs.length; i++) {
                msgs[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                String message;
                if(msgs[i].getMessageBody().contains("CrypTO: ")){
                    message = msgs[i].getMessageBody().split("CrypTO: ")[1];
                    database.InsertSms("CrypFROM: "+message, msgs[i].getOriginatingAddress());
                    NotificationCompat.Builder builder =
                            new NotificationCompat.Builder(context)
                                    .setSmallIcon(R.drawable.ic_message_black_24dp)
                                    .setContentTitle("Sms notification")
                                    .setContentText(msgs[i].getMessageBody());

                    Intent notificationIntent = new Intent(context, SMSActivity.class);
                    PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent,
                            PendingIntent.FLAG_UPDATE_CURRENT);
                    builder.setContentIntent(contentIntent);
                    NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                    manager.notify(1, builder.build());
                }
            }
        }
    }
}