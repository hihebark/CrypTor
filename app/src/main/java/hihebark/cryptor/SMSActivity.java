package hihebark.cryptor;

import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.List;

import static android.Manifest.permission.SEND_SMS;

public class SMSActivity extends AppCompatActivity {
    private static final int RESULT_PICK_CONTACT = 2;
    private static final int REQUEST_SMS = 0;
    EditText phoneNbr, sms_txt;
    SharedPreferences preferences;
    BroadcastReceiver sentStatusReceiver, deliveredStatusReceiver;
    FileOperation operation;
    CustomSMSAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        preferences = getSharedPreferences("theme", MODE_PRIVATE);
        Boolean themeis = preferences.getBoolean("Dark", false);
        setTheme(themeis ? R.style.DarkTheme : R.style.AppTheme);
        setContentView( R.layout.activity_sms );
        sms_txt = (EditText) findViewById(R.id.smstxt);
        phoneNbr = (EditText) findViewById(R.id.phonenbr);
        ImageButton btn_send = (ImageButton) findViewById(R.id.btnsend);
        ImageButton btn_contact = (ImageButton) findViewById(R.id.btncontact);
        final ListView listView = (ListView) findViewById(R.id.listsms);
        final DbTool database = new DbTool(this);
        final AlertDialog.Builder showmessage = new AlertDialog.Builder(this);
        ArrayList<String> arrayList;
        Intent intentresult = getIntent();
        operation = new FileOperation(intentresult.getStringExtra("passwordSms"));
        String number = intentresult.getStringExtra("number");
        Boolean SmsVide = intentresult.getBooleanExtra("SmsVide", false);
        if(!SmsVide){
            phoneNbr.setText(number);
            arrayList = database.getSms(number);
        }else{
            arrayList  = new ArrayList<>();
        }
        adapter = new CustomSMSAdapter(this, arrayList);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String itemString = (String) parent.getItemAtPosition(position);
                itemString = itemString.startsWith("CrypTO:") ? itemString.split("CrypTO: ")[1] : itemString.split("CrypFROM: ")[1];
                try {
                    showmessage.setMessage(operation.decryptionString(itemString));
                    showmessage.create().show();
                    //Toast.makeText(SMSActivity.this, operation.decryptionString(itemString), Toast.LENGTH_LONG).show();
                } catch (Exception e) {
                    Toast.makeText(SMSActivity.this, "password entrer FAUX!", Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
            }
        });
        btn_contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent contactPickerIntent = new Intent(Intent.ACTION_PICK,
                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
                startActivityForResult(contactPickerIntent, RESULT_PICK_CONTACT);
            }
        } );
        btn_send.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                    int hasSMSPermission = checkSelfPermission( Manifest.permission.SEND_SMS);
                    if (hasSMSPermission != PackageManager.PERMISSION_GRANTED) {
                        if (!shouldShowRequestPermissionRationale(Manifest.permission.SEND_SMS)) {
                            showMessageOKCancel("You need to allow access to Send SMS",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                requestPermissions(new String[] {Manifest.permission.SEND_SMS},
                                                        REQUEST_SMS);
                                                try {
                                                    if(database.InsertSms("CrypTO: "+operation.EncryptionString(sms_txt.getText().toString()),
                                                            phoneNbr.getText().toString())){
                                                        String message = operation.EncryptionString(sms_txt.getText().toString());
                                                        sendMySMS("CrypTO: "+message, phoneNbr.getText().toString());
                                                        adapter.add("CrypTO: "+message);
                                                    } else Toast.makeText(SMSActivity.this, "Didn't insert", Toast.LENGTH_LONG).show();
                                                } catch (Exception e) {e.printStackTrace();}
                                            }
                                        }
                                    });
                            return;
                        }
                        requestPermissions(new String[] {Manifest.permission.SEND_SMS},
                                REQUEST_SMS);
                        return;
                    }
                }else{
                    try {
                        if(database.InsertSms("CrypTO: "+operation.EncryptionString(sms_txt.getText().toString()),
                                phoneNbr.getText().toString())){
                            String message = operation.EncryptionString(sms_txt.getText().toString());
                            sendMySMS("CrypTO: "+message, phoneNbr.getText().toString());
                            adapter.add("CrypTO: "+message);
                        } else Toast.makeText(SMSActivity.this, "Didn't insert", Toast.LENGTH_LONG).show();
                    } catch (Exception e) {e.printStackTrace();}
                }
            }
        } );
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case RESULT_PICK_CONTACT:
                    Cursor cursor;
                    try {
                        String phoneNo;
                        Uri uri = data.getData();
                        cursor = getContentResolver().query(uri, null, null, null, null);
                        cursor.moveToFirst();
                        int  phoneIndex =cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                        phoneNo = cursor.getString(phoneIndex);
                        phoneNbr.setText(phoneNo);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
            }
        } else {
            return;
        }
    }
    public void sendMySMS(String message, String phone) {
        if (phone.isEmpty() && message.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Please Enter a Valid Phone Number", Toast.LENGTH_SHORT).show();
        } else {
            SmsManager sms = SmsManager.getDefault();
            List<String> messages = sms.divideMessage(message);
            for (String msg : messages) {
                PendingIntent sentIntent = PendingIntent.getBroadcast(this, 0, new Intent("SMS_SENT"), 0);
                PendingIntent deliveredIntent = PendingIntent.getBroadcast(this, 0, new Intent("SMS_DELIVERED"), 0);
                sms.sendTextMessage(phone, null, msg, sentIntent, deliveredIntent);

            }
        }
    }
    public void onResume() {
        super.onResume();
        sentStatusReceiver=new BroadcastReceiver() {

            @Override
            public void onReceive(Context arg0, Intent arg1) {
                String s = "Unknown Error";
                switch (getResultCode()) {
                    case Activity.RESULT_OK:
                        s = "Message Sent Successfully !!";
                        break;
                    case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
                        s = "Generic Failure Error";
                        break;
                    case SmsManager.RESULT_ERROR_NO_SERVICE:
                        s = "Error : No Service Available";
                        break;
                    case SmsManager.RESULT_ERROR_NULL_PDU:
                        s = "Error : Null PDU";
                        break;
                    case SmsManager.RESULT_ERROR_RADIO_OFF:
                        s = "Error : Radio is off";
                        break;
                    default:
                        break;
                }
                Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();

            }
        };
        deliveredStatusReceiver=new BroadcastReceiver() {

            @Override
            public void onReceive(Context arg0, Intent arg1) {
                String s = "Message Not Delivered";
                switch(getResultCode()) {
                    case Activity.RESULT_OK:
                        s = "Message Delivered Successfully";
                        sms_txt.setText("");
                        break;
                    case Activity.RESULT_CANCELED:
                        break;
                }
                Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
            }
        };
        registerReceiver(sentStatusReceiver, new IntentFilter("SMS_SENT"));
        registerReceiver(deliveredStatusReceiver, new IntentFilter("SMS_DELIVERED"));
    }


    public void onPause() {
        super.onPause();
        unregisterReceiver(sentStatusReceiver);
        unregisterReceiver(deliveredStatusReceiver);
    }
    private boolean checkPermission() {
        return ( ContextCompat.checkSelfPermission(getApplicationContext(), SEND_SMS ) == PackageManager.PERMISSION_GRANTED);
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{SEND_SMS}, REQUEST_SMS);
    }

    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_SMS:
                if (grantResults.length > 0 &&  grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(getApplicationContext(), "Permission Granted, Now you can access sms", Toast.LENGTH_SHORT).show();
                    //sendMySMS();

                }else {
                    Toast.makeText(getApplicationContext(), "Permission Denied, You cannot access and sms", Toast.LENGTH_SHORT).show();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (shouldShowRequestPermissionRationale(SEND_SMS)) {
                            showMessageOKCancel("You need to allow access to both the permissions",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                requestPermissions(new String[]{SEND_SMS},
                                                        REQUEST_SMS);
                                            }
                                        }
                                    });
                            return;
                        }
                    }
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(this, ListSmsActivity.class));
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new android.support.v7.app.AlertDialog.Builder(SMSActivity.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }
}