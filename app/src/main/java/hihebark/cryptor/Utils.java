package hihebark.cryptor;

import android.app.Activity;
import android.content.Intent;

public class Utils extends Activity{
    private static int sTheme;
    public final static int THEME_MATERIAL_LIGHT = 0;
    public final static int THEME_YOUR_CUSTOM_THEME = 1;
    public static void changeToTheme(Activity activity, int theme) {
        sTheme = theme;
        activity.finish();
        activity.startActivity(new Intent(activity, activity.getClass()));
        activity.overridePendingTransition(android.R.anim.fade_in,
                android.R.anim.fade_out);
    }
    public static void onActivityCreateSetTheme(Activity activity) {
        switch (sTheme) {
            case THEME_MATERIAL_LIGHT:
                activity.setTheme(R.style.AppTheme);
                break;
            case THEME_YOUR_CUSTOM_THEME:
                activity.setTheme(R.style.DarkTheme);
                break;
            default:
                activity.setTheme(R.style.AppTheme);
                break;
        }
    }
}