package hihebark.cryptor;

import android.app.Activity;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.ArrayList;

public class CustomSMSAdapter extends ArrayAdapter<String> {
    private final Activity context;
    private final ArrayList<String> itemname;

    public CustomSMSAdapter(Activity context, ArrayList<String> itemname) {
        super(context, R.layout.smslayout, itemname);
        this.context = context;
        this.itemname = itemname;
    }
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.smslayout, null, true);
        TextView txtTitle = (TextView) rowView.findViewById(R.id.message_text);
        LinearLayout linearLayout = (LinearLayout) rowView.findViewById(R.id.smsLinearlayout);
        if(!itemname.isEmpty()){
            if(itemname.get(position).startsWith("CrypTO:")){
                linearLayout.setGravity(Gravity.START);
                txtTitle.setBackground(ContextCompat.getDrawable(context, R.drawable.out_message_bg));
            }
            txtTitle.setText(itemname.get(position));
        }
        return rowView;
    }
}