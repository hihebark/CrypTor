package hihebark.cryptor;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class SettingsActivity extends AppCompatActivity {
    ListView listViewSettings;
    DbTool database = new DbTool(this);
    FileOperation operation = new FileOperation("");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences preferences = getSharedPreferences("theme", MODE_PRIVATE);
        Boolean themeis = preferences.getBoolean("Dark", false);
        setTheme(themeis ? R.style.DarkTheme : R.style.AppTheme);
        setContentView(R.layout.activity_settings);
        listViewSettings = (ListView) findViewById(R.id.list_item_setting);
        String[] liste_Item = new String[]{"Changer le schema", "Changer de theme",
                                            "Envoyer un SMS Crypter", "Scanner l'appareil",
                                            "Supprimer mon compte", "Rapport"};
        Integer[] imgid={
                R.drawable.ic_vpn_key_black_24dp,
                R.drawable.ic_style_black_24dp,
                R.drawable.ic_message_black_24dp,
                R.drawable.ic_find_replace_black_24dp,
                R.drawable.ic_delete_forever_black_24dp,
                R.drawable.ic_insert_drive_file_black_24dp
        };
        String[] des_liste_item={"Changer votre schema de connexion", "Personaliser l'application",
                                "Envoyer et recevoire des SMS", "faire un scan rapide de l'appareil",
                                "Supprimer votre compte", "Rapport sur vos dernières actions"};
        final CustomListAdapter adapter=new CustomListAdapter(this, liste_Item, imgid, des_liste_item);
        listViewSettings.setAdapter(adapter);
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        listViewSettings.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(SettingsActivity.this, (String)parent.getItemAtPosition(position),
                        Toast.LENGTH_LONG).show();
                return true;
            }
        });
        listViewSettings.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final String itemString = (String) parent.getItemAtPosition(position);

                switch (itemString){
                    case "Changer le schema":
                        Intent intent = new Intent(getApplication(), VerificationActivity.class);
                        intent.putExtra("TypeOperation", "Changer Password");
                        startActivityForResult(intent, 0);

                        break;
                    case "Changer de theme":
                        alertDialog.setMessage("slectionner un theme");
                        alertDialog.setTitle("Changer le theme:");
                        alertDialog.setPositiveButton( "Dark", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                SharedPreferences.Editor editor = getSharedPreferences("theme", MODE_PRIVATE).edit();
                                editor.putBoolean("Dark", true);
                                editor.apply();
                                Utils.changeToTheme(SettingsActivity.this, 1);
                            }
                        } );
                        alertDialog.setNegativeButton("Light", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                SharedPreferences.Editor editor = getSharedPreferences("theme", MODE_PRIVATE).edit();
                                editor.putBoolean("Dark", false);
                                editor.apply();
                                Utils.changeToTheme(SettingsActivity.this, 1);
                            }
                        });
                        alertDialog.show();
                        break;
                    case "Envoyer un SMS Crypter":
                        startActivity(new Intent(getApplication(), ListSmsActivity.class));
                        break;
                    case "Supprimer mon compte":
                        alertDialog.setMessage("Voulez vous vraiment supprimer votre compte?\n" +
                                "Tout information sur l'utilisateur sera supprimer sauf celle des "+
                                "fichiers Crypter (Si il existe des fichiers no decrypter).");
                        alertDialog.setTitle("Supprimer mon compte!");
                        alertDialog.setPositiveButton("Oui", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                                if(database.DeleteCustomer()){
                                    Toast.makeText(SettingsActivity.this,"Compte supprimer"
                                            ,Toast.LENGTH_LONG).show();
                                    Handler handler = new Handler();
                                    handler.postDelayed( new Runnable() {
                                        @Override
                                        public void run() {
                                            Intent intent = new Intent(getApplication(), LuncherActivity.class);
                                            startActivity(intent);
                                        }
                                    } ,700);
                                }else{
                                    Toast.makeText(SettingsActivity.this, "Error! Connexion au serveur no établie"
                                            , Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                        alertDialog.setNegativeButton( "Non", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                return;
                            }
                        } );
                        alertDialog.show();
                        break;
                    case "Rapport":
                        AlertDialog.Builder mDialog = new AlertDialog.Builder(SettingsActivity.this, R.style.DarkThemeDialog);
                        ListView rapportList = new ListView(SettingsActivity.this);
                        ArrayList<String> arrayList = new ArrayList<>();
                        try {
                            arrayList = operation.readFromFile();
                        } catch (IOException e) {
                            arrayList.add("rien a afficher");
                            e.printStackTrace();
                        }
                        ArrayAdapter adapterrapport = new ArrayAdapter(SettingsActivity.this,
                                android.R.layout.simple_list_item_1, arrayList);
                        rapportList.setAdapter(adapterrapport);
                        mDialog.setTitle("Rapport:");
                        mDialog.setMessage("E:/ = Encryption\nD:/ Decryption");
                        mDialog.setNeutralButton("Supprimer", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                File fileToDelete = new File(Environment.getExternalStorageDirectory()+"/log.log");
                                if(fileToDelete.exists()) fileToDelete.delete();
                            }
                        });
                        mDialog.setView(rapportList);
                        mDialog.setIcon(R.drawable.ic_insert_drive_file_black_24dp);
                        mDialog.show();
                        break;
                    case "Scanner l'appareil":
                        startActivity(new Intent(getApplicationContext(), ScanRapidActivity.class));
                        break;
                    default:
                        break;
                }
            }
        });
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(this, MainActivity.class));
    }
}