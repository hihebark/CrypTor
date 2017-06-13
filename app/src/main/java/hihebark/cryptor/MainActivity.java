package hihebark.cryptor;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import java.io.File;
import ru.bartwell.exfilepicker.ExFilePicker;
import ru.bartwell.exfilepicker.data.ExFilePickerResult;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private BroadcastReceiver mBatInfoReceiver;
    private static final int EX_FILE_PICKER_RESULT = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences preferences;
        preferences = getSharedPreferences("theme", MODE_PRIVATE);
        Boolean themeis = preferences.getBoolean("Dark", false);
        setTheme(themeis ? R.style.DarkTheme : R.style.AppTheme);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        final DbTool database = new DbTool(this);
        final Boolean passwordStrongbox = database.isSpasswordEmpty();
        final AlertDialog.Builder StrongBoxPassword = new AlertDialog.Builder(MainActivity.this, R.style.DarkThemeDialog);
        StrongBoxPassword.setTitle("StrongBox");
        StrongBoxPassword.setIcon(R.drawable.ic_vpn_key_black_24dp);
        StrongBoxPassword.setMessage("Cree un mot de pass pour votre Strongbox");
        final EditText PasswordAlertDialog = new EditText(MainActivity.this);
        PasswordAlertDialog.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        PasswordAlertDialog.setTransformationMethod(PasswordTransformationMethod.getInstance());
        StrongBoxPassword.setView(PasswordAlertDialog);
        freeRamMemorySize();
        Button btncrypt = (Button) findViewById(R.id.crypter);
        Button btndecrypt = (Button) findViewById(R.id.decrypter);
        btndecrypt.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ExFilePicker exFilePicker = new ExFilePicker();
                exFilePicker.setChoiceType(ExFilePicker.ChoiceType.ALL);
                exFilePicker.setQuitButtonEnabled(true);
                exFilePicker.setNewFolderButtonDisabled(true);
                exFilePicker.setStartDirectory(Environment.getExternalStorageDirectory().toString()+"/Strongbox/");
                exFilePicker.setQuitButtonEnabled(true);
                exFilePicker.setUseFirstItemAsUpEnabled(false);
                if(passwordStrongbox){
                    StrongBoxPassword.setPositiveButton("Confirmer", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if(database.UpdateUserpassStrongbox(PasswordAlertDialog.getText().toString())){
                                exFilePicker.start(MainActivity.this, EX_FILE_PICKER_RESULT);
                            }
                        }
                    });
                    StrongBoxPassword.show();
                }else{
                    exFilePicker.start(MainActivity.this, EX_FILE_PICKER_RESULT);
                }
            }
        } );
        btncrypt.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ExFilePicker exFilePicker = new ExFilePicker();
                exFilePicker.setChoiceType( ExFilePicker.ChoiceType.ALL);
                exFilePicker.setQuitButtonEnabled( true );
                exFilePicker.setStartDirectory(Environment.getExternalStorageDirectory().toString());
                if(passwordStrongbox){
                    StrongBoxPassword.setPositiveButton("Confirmer", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if(database.UpdateUserpassStrongbox(PasswordAlertDialog.getText().toString())){
                                exFilePicker.start(MainActivity.this, EX_FILE_PICKER_RESULT);
                            }
                        }
                    });
                    StrongBoxPassword.show();
                }else{
                    exFilePicker.start(MainActivity.this, EX_FILE_PICKER_RESULT);
                }
            }
        } );
        setSupportActionBar(toolbar);
        SystemActivity system = new SystemActivity();
        long memorysize = system.getAvailableInternalMemorySize()*100/system.getTotalInternalMemorySize();
        long totalRamValue = totalRamMemorySize();
        long freeRamValue = freeRamMemorySize()*100/totalRamValue;
        long usedRamValue = (totalRamValue - freeRamValue)*100/totalRamValue;
        ProgressBar progressBarfree = (ProgressBar) findViewById(R.id.progressBarfree);
        ProgressBar progressBartotal = (ProgressBar) findViewById(R.id.progressBartot);
        final ProgressBar progressBarbatterry = (ProgressBar) findViewById(R.id.progressbattery);
        ProgressBar progressmemory = (ProgressBar) findViewById(R.id.progressmemory);
        progressBarfree.setProgress((int)freeRamValue);
        progressmemory.setProgress((int)memorysize);
        progressBartotal.setProgress((int)usedRamValue);
        mBatInfoReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
                progressBarbatterry.setProgress(level);
                if(level<50){
                    progressBarbatterry.getProgressDrawable().setColorFilter(Color.RED,
                            android.graphics.PorterDuff.Mode.SRC_IN);
                }
            }
        };
        this.registerReceiver(this.mBatInfoReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            moveTaskToBack(true);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        item.setIcon(R.drawable.ic_settings_black_24dp);
        if (id == R.id.action_settings) {
            Intent intent = new Intent(getApplication(), SettingsActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.scanner) {
            Intent intent = new Intent(getApplication(), ScanRapidActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_manage) {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(this, R.style.DarkThemeDialog);
            alertDialog.setMessage("slectionner un theme");
            alertDialog.setTitle("Changer le theme:");
            alertDialog.setPositiveButton( "Dark", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    SharedPreferences.Editor editor = getSharedPreferences("theme", MODE_PRIVATE).edit();
                    editor.putBoolean("Dark", true);
                    editor.apply();
                    Utils.changeToTheme(MainActivity.this, 1);
                }
            } );
            alertDialog.setNegativeButton("Light", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    SharedPreferences.Editor editor = getSharedPreferences("theme", MODE_PRIVATE).edit();
                    editor.putBoolean("Dark", false);
                    editor.apply();
                    Utils.changeToTheme(MainActivity.this, 0);
                }
            });
            alertDialog.show();
        } else if (id == R.id.nav_share) {
            Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
            sharingIntent.setType("text/plain");
            sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Share");
            sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, "Securisé vos données avec"+
                    " #Cryptor https://cryptor.com/");
            startActivity(Intent.createChooser(sharingIntent, "Partager l'application"));
        }else if(id == R.id.nav_smscrypt){
            startActivity(new Intent(MainActivity.this, ListSmsActivity.class));
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    private long freeRamMemorySize(){
        ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
        ActivityManager activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        activityManager.getMemoryInfo(mi);
        return mi.availMem / 1048576L;
    }
    private long totalRamMemorySize() {
        ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
        ActivityManager activityManager = (ActivityManager)getSystemService(ACTIVITY_SERVICE);
        activityManager.getMemoryInfo(mi);
        return mi.totalMem / 1048576L;
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(this, R.style.DarkThemeDialog);
        final AlertDialog.Builder alertDialogPassword = new AlertDialog.Builder(this, R.style.DarkThemeDialog);
        final EditText PasswordAlertDialog = new EditText(MainActivity.this);
        final DbTool database = new DbTool(this);
        PasswordAlertDialog.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        PasswordAlertDialog.setTransformationMethod(PasswordTransformationMethod.getInstance());
        if (requestCode == EX_FILE_PICKER_RESULT) {
            final ExFilePickerResult result = ExFilePickerResult.getFromIntent(data);
            if (result != null && result.getCount() > 0) {
                alertDialog.setTitle("Avertissement!");
                alertDialog.setMessage("Voulez vous vraiment procéder a ces modifications sur le(s) " +
                        "fichier(s)!");
                alertDialog.setPositiveButton("Oui", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        alertDialogPassword.setMessage("Entrer votre mot de pass!");
                        alertDialogPassword.setView(PasswordAlertDialog);
                        alertDialogPassword.setCancelable(false);
                        PasswordAlertDialog.setFocusable(true);
                        alertDialogPassword.setPositiveButton("Confirmer", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                        alertDialogPassword.setNegativeButton("Annuler", null);
                        final AlertDialog mAlertDialog = alertDialogPassword.create();
                        mAlertDialog.show();
                        mAlertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (database.GetPasswordR(PasswordAlertDialog.getText().toString())) {
                                    File readFile = new File(result.getPath() + result.getNames().get(0));
                                    if (readFile.canRead() && readFile.canWrite()) {
                                        String[] fileresult = new String[result.getCount()];
                                        for (int i = 0; i < result.getCount(); i++) {
                                            fileresult[i] = result.getPath() + "" + result.getNames().get(i);
                                        }
                                        Intent intent = new Intent(getApplication(), CrypterActivity.class);
                                        intent.putExtra("File", fileresult);
                                        intent.putExtra("TypeOperation", fileresult[0].endsWith(".ct") ? "Decrypt" : "Encrypt");
                                        intent.putExtra("PasswordStrongBox", PasswordAlertDialog.getText().toString());
                                        mAlertDialog.dismiss();
                                        startActivityForResult(intent, 0);
                                    } else {
                                        Toast.makeText(MainActivity.this, "Vous ne pouver pas faire" +
                                                        "cette operation sur les fichiers system"
                                                , Toast.LENGTH_SHORT).show();
                                    }

                                } else {
                                    Toast.makeText(MainActivity.this, "password faux!",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                });
                alertDialog.setNegativeButton("Non", null);
                alertDialog.show();
            }
        }
    }
}
