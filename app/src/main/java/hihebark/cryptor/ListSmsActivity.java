package hihebark.cryptor;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class ListSmsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        SharedPreferences preferences = getSharedPreferences("theme", MODE_PRIVATE);
        Boolean themeis = preferences.getBoolean("Dark", false);
        if(themeis) setTheme(R.style.DarkTheme);
        setContentView( R.layout.activity_list_sms );
        DbTool database = new DbTool(this);
        final AlertDialog.Builder alertdialog = new AlertDialog.Builder(this, R.style.DarkThemeDialog);
        final ListView listViewContact = (ListView) findViewById(R.id.listContact);
        Button btnSendMessage = (Button) findViewById(R.id.btnsendSMS);
        TextView txtNothingtoShow = (TextView) findViewById(R.id.textNothingSend);
        final EditText passwordSms = new EditText(ListSmsActivity.this);
        if(passwordSms.getParent()!=null){
            ((ViewGroup)passwordSms.getParent()).removeView(passwordSms);
        }
        alertdialog.setTitle("");
        alertdialog.setView(passwordSms);
        alertdialog.setMessage("Entrer un mot de pass pour crypter vos sms.\n si le correspondant " +
                "n'a pas le meme password le message ne sera pas dechiffrer.");
        btnSendMessage.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertdialog.setPositiveButton("Conffirmer", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(getApplicationContext(), SMSActivity.class);
                        intent.putExtra("passwordSms", passwordSms.getText().toString());
                        intent.putExtra("SmsVide", true);
                        startActivity(intent);
                    }
                });
                alertdialog.setNegativeButton("Annuler", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                alertdialog.show();
            }
        } );
        if(database.GetCountSms()>0){
            ArrayList<String> arrayList = database.getAllSMSnumber();
            ArrayAdapter adapter = new ArrayAdapter(this,
                    android.R.layout.simple_selectable_list_item, arrayList);
            listViewContact.setAdapter(adapter);
            listViewContact.setOnItemClickListener( new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    final String itemString = (String) parent.getItemAtPosition(position);
                    alertdialog.setPositiveButton("Conffirmer", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(getApplicationContext(), SMSActivity.class);
                            intent.putExtra("number", itemString);
                            intent.putExtra("passwordSms", passwordSms.getText().toString());
                            intent.putExtra("SmsVide", false);
                            startActivity(intent);
                        }
                    });
                    alertdialog.setNegativeButton("Annuler", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    alertdialog.show();
                }
            } );
        }else{
            listViewContact.setVisibility(View.GONE);
            txtNothingtoShow.setVisibility(View.VISIBLE);
        }
    }
}