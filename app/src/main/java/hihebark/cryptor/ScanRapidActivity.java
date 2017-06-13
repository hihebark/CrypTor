package hihebark.cryptor;

import android.app.ActivityManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ScanRapidActivity extends AppCompatActivity {
    Button btnscan;
    ProgressBar progressbarscan;
    TextView txtupprgs, txtscan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences preferences = getSharedPreferences("theme", MODE_PRIVATE);
        Boolean themeis = preferences.getBoolean("Dark", false);
        if(themeis) setTheme(R.style.DarkTheme);
        setContentView(R.layout.activity_scan_rapid);
        btnscan = (Button) findViewById(R.id.btnscan);
        final ImageView imgrocket= (ImageView) findViewById(R.id.imgrocket);
        progressbarscan = (ProgressBar) findViewById(R.id.progressbarscan);
        txtupprgs = (TextView) findViewById(R.id.txtupprgsbr);
        txtscan = (TextView) findViewById(R.id.txtscan);
        final ArrayList<String> mList = new ArrayList<>();
        btnscan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Glide.with(ScanRapidActivity.this)
                        .load(R.drawable.rocketgif)
                        .asGif()
                        .into(imgrocket);
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            progressbarscan.setProgress(0);
                        }
                        txtupprgs.setVisibility(View.VISIBLE);
                        progressbarscan.setVisibility(View.VISIBLE);
                        txtupprgs.setText("Scanne des processus");
                        ScanForBadProcess(ScanRapidActivity.this);
                        txtupprgs.setText("Scanne Terminer");
                        txtupprgs.setText("Clearing some cash");
                        FreeMemory();
                        ListView listView = new ListView(ScanRapidActivity.this);
                        final AlertDialog.Builder alertdialog = new AlertDialog.Builder(ScanRapidActivity.this);
                        alertdialog.setTitle("Detection des virus");
                        alertdialog.setView(listView);
                        ArrayList arrayList = VirusDectore(Environment.getExternalStorageDirectory(), 0, mList);
                        ArrayAdapter arrayAdapter = new ArrayAdapter(ScanRapidActivity.this,
                                android.R.layout.select_dialog_item, arrayList);
                        listView.setAdapter(arrayAdapter);
                        if(arrayList.isEmpty()) arrayAdapter.add("Nothing");
                        alertdialog.setNegativeButton("Terminer", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                startActivity(new Intent(getApplication(), MainActivity.class));
                            }
                        });
                        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                            @Override
                            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                                final String itemString = (String) parent.getItemAtPosition(position);
                                Toast.makeText(ScanRapidActivity.this, "Sooon"+itemString, Toast.LENGTH_SHORT).show();
                                return false;
                            }
                        });
                        alertdialog.show();
                        txtupprgs.setText("Terminer");
                    }
                }, 2000);
            }
        });
    }
    public void ScanForBadProcess(Context context){
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> runningProcesses = manager.getRunningAppProcesses();
        if (runningProcesses != null && runningProcesses.size() > 0) {
            for (ActivityManager.RunningAppProcessInfo process : runningProcesses){
                if (process.processName.contains("com.metasploit") ||           //the best way is to get this with a regex it's better
                        process.processName.contains("Backdoor") ||             //for search all over the processName
                        process.processName.contains("Keylogger") ||            //a tester!
                        process.processName.contains("FakePlayer") ||
                        process.processName.contains("AndroidOS_Droisnake.A") ||
                        process.processName.contains("Trojan") ||
                        process.processName.contains("Zeahache") ||
                        process.processName.contains("Geinimi")){
                    Toast.makeText(context, process.processName +" is running!"
                            , Toast.LENGTH_LONG).show();
                    manager.killBackgroundProcesses(process.processName);
                    Toast.makeText(context, "Process Killed : " + Arrays.toString(process.pkgList)
                            , Toast.LENGTH_SHORT).show();
                }
            }
        } else {
            Toast.makeText(context, "No application is running", Toast.LENGTH_SHORT).show();
        }
    }
    public void FreeMemory(){
        System.runFinalization();
        Runtime.getRuntime().gc();
        Runtime.getRuntime().freeMemory();
        System.gc();
    }
    public ArrayList<String> VirusDectore(File dir, int j, ArrayList<String> arrayList){
        String[] extension = new String[]{"bat", "vbs", "dvr", "lnk", "PS1"};           //toAdd more extension
        File[] listFile = dir.listFiles();                                              //or to bring theme from the internet
        if (listFile != null) {                                                         //exemple virus total
            for (int i = j; i < listFile.length; i++) {                                 //so you need only the MD5 of the file no need for extension
                if (listFile[i].isDirectory()) {
                    VirusDectore(listFile[i], i, arrayList);
                } else {
                    if (listFile[i].getName().endsWith(extension[0]) ||
                            listFile[i].getName().endsWith(extension[1]) ||
                            listFile[i].getName().contains(extension[2]) ||
                            listFile[i].getName().contains(extension[3]) ||
                            listFile[i].getName().endsWith(extension[4])){
                        arrayList.add(listFile[i].getName());
                    }
                }
            }
        }
        return arrayList;
    }
}