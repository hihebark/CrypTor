package hihebark.cryptor;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.annotation.Nullable;

public class ServiceCommunicator extends Service {
    private SmsReceiver mSMSreceiver;
    private IntentFilter mIntentFilter;

    @Override
    public void onCreate(){
        super.onCreate();

        //SMS event receiver
        mSMSreceiver = new SmsReceiver();
        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction("android.provider.Telephony.SMS_RECEIVED");
        registerReceiver(mSMSreceiver, mIntentFilter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mSMSreceiver);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}