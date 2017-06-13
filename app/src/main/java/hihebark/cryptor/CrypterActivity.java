package hihebark.cryptor;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.io.File;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import javax.crypto.NoSuchPaddingException;

public class CrypterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crypter);
        Intent intent = getIntent();
        String[] files = intent.getStringArrayExtra("File");
        final String Type_Operation = intent.getStringExtra("TypeOperation");
        final String PasswordStrongBox = intent.getStringExtra("PasswordStrongBox");
        BackgroundTask backgroundTask = new BackgroundTask(Type_Operation, files, PasswordStrongBox, this);
        backgroundTask.execute();
    }
}
class BackgroundTask extends AsyncTask<String[], String, String> {

    private ProgressDialog progressDialog;
    private String[]files;
    private String Type_Operation,PasswordStrongBox;
    private FileOperation fileOperation;
    private DbTool database;
    private AlertDialog.Builder alertDialog;
    private Intent Goto;
    private Context context;

    BackgroundTask(String Type_Operation, String[] files, String PasswordStrongBox, Context context){
        this.Type_Operation=Type_Operation;
        this.PasswordStrongBox=PasswordStrongBox;
        this.files=files;
        database = new DbTool(context);
        fileOperation = new FileOperation(PasswordStrongBox);
        alertDialog = new AlertDialog.Builder(context);
        Goto = new Intent(context, MainActivity.class);
        this.context = context;
        this.progressDialog = new ProgressDialog(context);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressDialog.setMessage("Veuillez patienter quelques instants opération en cours ...");
        progressDialog.setIndeterminate(false);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false);
        progressDialog.show();
    }
    @Override
    protected String doInBackground(String[]... params) {
        String messageFile;
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.FRANCE);
        if(Type_Operation.equals("Encrypt")){
            messageFile = "\tFile(s) encrypted:\n";
            for(int i=0; i<files.length; i++){
                File filename = new File(files[i]);
                if(isItFileOrFolder(files[i])){
                    try {
                        fileOperation.EncryptionFile(files[i]);
                        database.InsertFile(filename.getName(), files[i]);
                        fileOperation.writeToFile("E:/"+filename.getName()+" "+dateFormat.format(new Date())+"\n");
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (NoSuchAlgorithmException e) {
                        e.printStackTrace();
                    } catch (NoSuchPaddingException e) {
                        e.printStackTrace();
                    } catch (InvalidKeyException e) {
                        e.printStackTrace();
                    }
                    messageFile += "File: "+filename.getName().toString()+" Encrypted\n";
                }else{
                    try {
                        fileOperation.EncryptionDirectory(files[i]);
                        messageFile += "Folder: "+filename.getName().toString()+" Encrypted\n";
                        fileOperation.writeToFile("E:/"+filename.getName()+" "+dateFormat.format(new Date())+"\n");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }else{
            messageFile = "File(s) Decrypted:\n";
            String Filename;
            try {
                for(int i=0; i<files.length;i++){
                    File fileNameD = new File(files[i]);
                    Filename = fileNameD.getName();
                    String filePath = database.getPathFile(Filename.split("\\.ct")[0]);
                    fileOperation.DecryptionFile(files[i], filePath);
                    messageFile += "File :"+Filename.split("\\.ct")[0]+" Decrypted\n";
                    database.DeleteFile(Filename.split("\\.ct")[0]);
                    fileOperation.writeToFile("D:/"+fileNameD.getName()+" "+dateFormat.format(new Date())+"\n");
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (NoSuchPaddingException e) {
                e.printStackTrace();
            } catch (InvalidKeyException e) {
                e.printStackTrace();
            }
        }
        return messageFile;
    }
    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        progressDialog.dismiss();
        if(result.endsWith("Decrypted")){
            alertDialog.setTitle("Decryption terminer");
        }else{
            alertDialog.setTitle("Encryption terminer");
        }

        alertDialog.setMessage(result);
        alertDialog.setCancelable(false);
        alertDialog.setOnDismissListener( new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                context.startActivity(Goto);
            }
        } );
        alertDialog.setPositiveButton( "Terminer", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                context.startActivity(Goto);
            }
        } );
        alertDialog.show();
    }
    public boolean isItFileOrFolder(String file){
        File fileTest = new File(file);
        if(fileTest.isFile()) return true;
        return false;
    }
}