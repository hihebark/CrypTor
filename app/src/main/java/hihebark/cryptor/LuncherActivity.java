package hihebark.cryptor;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.takwolf.android.lock9.Lock9View;
import java.io.File;

public class LuncherActivity extends AppCompatActivity {
    DbTool database;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_luncher);
        database = new DbTool(this);
        TextView txt_top_lock = (TextView) findViewById(R.id.txtuplock);
        final Lock9View lock9View = (Lock9View) findViewById(R.id.lock_9_view);
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        final AlertDialog.Builder alertDialogBlock = new AlertDialog.Builder(this);
        final Handler handler = new Handler();
        alertDialogBlock.setTitle("Avertissement!");
        alertDialogBlock.setMessage("\nIl faut attendre 10 seconds\navant un autre essai\n");
        alertDialogBlock.setCancelable(false);
        final AlertDialog.Builder alertDialogpassword = new AlertDialog.Builder(this);
        final Button btn_mdp_oublier = (Button)findViewById(R.id.passoub);
        final Button btn_password = (Button)findViewById(R.id.password);
        final EditText txt_question = new EditText(LuncherActivity.this);
        if (database.GetCountTable()==1) {
            txt_top_lock.setText(R.string.up_lock_connexion);
        } else {
            btn_mdp_oublier.setVisibility(View.GONE);
            btn_password.setVisibility(View.GONE);
            txt_top_lock.setText(R.string.up_lock_inscription);
        }
        lock9View.setCallBack(new Lock9View.CallBack() {
            int counter = 0;
            @Override
            public void onFinish(final String password) {
                if(database.GetCountTable()==1){
                    if(connection(password)){
                        Toast.makeText(LuncherActivity.this, "connexion établie avec succes",
                                Toast.LENGTH_SHORT).show();
                        File StrongboxFolder = new File(Environment.getExternalStorageDirectory()+"/Strongbox");
                        if(!StrongboxFolder.exists()) StrongboxFolder.mkdir();
                        counter = 0;
                        Intent intent = new Intent(getApplication(), MainActivity.class);
                        startActivity(intent);
                    }else{
                        counter++;
                        if(counter<3){
                            Toast.makeText(LuncherActivity.this, "Tentative: "+counter, Toast.LENGTH_SHORT).show();
                        }else{
                            final Dialog alertDialogg = alertDialogBlock.show();
                            handler.postDelayed(new Runnable() {
                                public void run() {
                                    // Actions to do after 10 seconds
                                    alertDialogg.dismiss();
                                    counter = 0;
                                }
                            }, 10000);
                        }
                    }
                }else{

                    alertDialog.setTitle("Question secret");
                    alertDialog.setMessage("Question secret!\nExemple: votre ami d'enfance:");
                    alertDialog.setView(txt_question);
                    alertDialog.setIcon(R.drawable.ic_vpn_key_black_24dp);
                    alertDialog.setPositiveButton( "Suivant!", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            final EditText PasswordAlertDialog = new EditText(LuncherActivity.this);
                            PasswordAlertDialog.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                            PasswordAlertDialog.setTransformationMethod(PasswordTransformationMethod.getInstance());
                            alertDialog.setTitle("Just une autre étape");
                            alertDialog.setMessage("Ajouter un mot de pass");
                            alertDialog.setView(PasswordAlertDialog);
                            alertDialog.setIcon(R.drawable.ic_vpn_key_black_24dp);
                            alertDialog.setPositiveButton("Terminer", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if(PasswordAlertDialog.getText().length() < 3){
                                        Toast.makeText(LuncherActivity.this,
                                                "mot de pass petit try again", Toast.LENGTH_SHORT).show();
                                    }else{
                                        if(inscription(password, txt_question.getText().toString(),
                                                PasswordAlertDialog.getText().toString())){
                                            File root = new File(Environment.getExternalStorageDirectory()+"/Strongbox");
                                            if(!root.exists()) root.mkdir();
                                            Toast.makeText(LuncherActivity.this, "inscription réussie",
                                                    Toast.LENGTH_SHORT).show();
                                            startActivity(new Intent(LuncherActivity.this, MainActivity.class));
                                        }else{
                                            Toast.makeText(LuncherActivity.this,
                                                    "Error! inscription refusé", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }
                            });
                            alertDialog.show();
                        }
                    } );
                    alertDialog.show();
                }
            }
        });
        btn_mdp_oublier.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.setMessage("Entrer votre password de recovery");
                alertDialog.setTitle("Mot de pass oublier!");
                alertDialog.setView(txt_question);
                alertDialog.setPositiveButton( "Conffirmer", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(database.GetPasswordR(txt_question.getText().toString())){
                            Intent intent = new Intent(getApplication(), VerificationActivity.class);
                            intent.putExtra("TypeOperation", "Mot de pass oublier");
                            startActivityForResult(intent, 0);
                        }else{
                            Toast.makeText(LuncherActivity.this, "Mot de pass faux! Réessayer"
                                    , Toast.LENGTH_SHORT).show();
                        }
                    }
                } );
                alertDialog.show();
            }
        } );
        btn_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText PasswordAlertDialog = new EditText(LuncherActivity.this);
                PasswordAlertDialog.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                PasswordAlertDialog.setTransformationMethod(PasswordTransformationMethod.getInstance());
                PasswordAlertDialog.setHint("Password");
                alertDialogpassword.setTitle("Entrer votre mot de pass");
                alertDialogpassword.setView(PasswordAlertDialog);
                final int[] counter = {0};
                alertDialogpassword.setPositiveButton("confirmer", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                alertDialogpassword.setNegativeButton("Annuler", null);
                final AlertDialog mAlertDialog = alertDialogpassword.create();
                mAlertDialog.show();
                mAlertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(database.GetMDPassword(PasswordAlertDialog.getText().toString())){
                            startActivity(new Intent(LuncherActivity.this, MainActivity.class));
                        }else{
                            counter[0]++;
                            if(counter[0] <3){
                                Toast.makeText(LuncherActivity.this, "Tentative: "+ counter[0], Toast.LENGTH_SHORT).show();
                            }else{
                                final Dialog alertDialogg = alertDialogBlock.show();
                                handler.postDelayed(new Runnable() {
                                    public void run() {
                                        // Actions to do after 10 seconds
                                        alertDialogg.dismiss();
                                        counter[0] = 0;
                                    }
                                }, 10000);
                            }
                            PasswordAlertDialog.setText("");
                        }
                    }
                });
            }
        });
    }
    public boolean inscription(String password, String passwordR, String mdpassword){
        return database.InsertCustomer("User", password, passwordR, mdpassword);
    }
    public boolean connection(String password){
        return database.VerifyPassword(password);
    }
}