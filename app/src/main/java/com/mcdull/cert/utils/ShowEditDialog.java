package com.mcdull.cert.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.mcdull.cert.R;

/**
 * Created by mcdull on 15/8/3.
 */
public class ShowEditDialog {

    private Context context;
    private AlertDialog alertDialog;
    private String title;
    private CallBack callBack;

    public ShowEditDialog(Context context, String title, CallBack callBack) {
        this.context = context;
        this.title = title;
        this.callBack = callBack;
    }

    public void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View view = View.inflate(context, R.layout.edit_dialog, null);

        final EditText editText = (EditText) view.findViewById(R.id.et_tooltip);

        Button btYes = (Button) view.findViewById(R.id.bt_yes);
        Button btNo = (Button) view.findViewById(R.id.bt_no);
        ((TextView) view.findViewById(R.id.tv_tooltip_title)).setText(title);

        btYes.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                callBack.CallBack(editText);
            }
        });

        btNo.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        alertDialog = builder.create();
        alertDialog.setView(view, 0, 0, 0, 0);
        alertDialog.show();

    }

    public void dismiss() {
        if (alertDialog != null)
            alertDialog.dismiss();
    }

    public interface CallBack {
        public void CallBack(EditText editText);
    }
}
