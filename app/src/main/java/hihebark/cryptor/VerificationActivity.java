package hihebark.cryptor;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import com.takwolf.android.lock9.Lock9View;

public class VerificationActivity extends AppCompatActivity {

    DbTool database = new DbTool(this);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.onActivityCreateSetTheme(this);
        setContentView(R.layout.activity_verification);
        final Lock9View lock9View = (Lock9View) findViewById(R.id.lock_9_view);
        TextView textView = (TextView) findViewById(R.id.txtupverlock);
        final Intent intent = getIntent();
        final Intent intentGo = new Intent(getApplication(), MainActivity.class);
        String message = intent.getStringExtra("TypeOperation");
        switch(message) {
            case "Changer Password":
                textView.setText("Dessiner votre nouveau mot de pass!");
                lock9View.setCallBack( new Lock9View.CallBack() {
                    @Override
                    public void onFinish(String password) {
                        if(database.UpdateUserPassword(password)){
                            Toast.makeText(VerificationActivity.this, "Mot de pass changer"
                                    , Toast.LENGTH_SHORT).show();
                            startActivity(intentGo);
                        }else{
                            Toast.makeText(VerificationActivity.this, "Mot de pass non changer error!"
                                    , Toast.LENGTH_SHORT).show();
                        }
                    }
                } );
                break;
            case "Mot de pass oublier":
                textView.setText("Dessiner votre nouveau mot de pass!");
                lock9View.setCallBack( new Lock9View.CallBack() {
                    @Override
                    public void onFinish(String password) {
                        if(database.UpdateUserPassword(password)){
                            Toast.makeText(VerificationActivity.this, "Mot de pass changer"
                                    , Toast.LENGTH_SHORT).show();
                            startActivity(intentGo);
                        }else{
                            Toast.makeText(VerificationActivity.this, "Mot de pass de recovery faux"
                                    , Toast.LENGTH_SHORT).show();
                        }
                    }
                } );
                break;
            case "VerificationPassword":

        }
    }
}