package cn.onegroup.mobile1603.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import cn.onegroup.mobile1603.R;


/**
 * Created by Administrator on 2017/9/12 0012.
 */

public class DialogUitls {
    private static AlertDialog alertDialog;
    private static WindowManager.LayoutParams params;

    public static AlertDialog ShowYesNoDialog(Context context, String title, String content,
                                              String yes, String no, final DialogInterface
            .OnClickListener listener) {

        alertDialog = new AlertDialog.Builder(context).create();

        alertDialog.setCancelable(true);


        View view = LayoutInflater.from(context).inflate(R.layout.base_dialog, null);
        ((TextView) view.findViewById(R.id.title)).setText(title);
        ((TextView) view.findViewById(R.id.content)).setText(content);


        Window window = alertDialog.getWindow();
        params = window.getAttributes();
        params.windowAnimations = R.style.up_down;

        alertDialog.show();
        window.setContentView(view, params);

        TextView btnYes;
        btnYes = (TextView) view.findViewById(R.id.yes);
        btnYes.setText(yes);
        btnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onClick(alertDialog, DialogInterface.BUTTON_POSITIVE);
                alertDialog.dismiss();
            }
        });
        TextView btnNO;
        btnNO = (TextView) view.findViewById(R.id.no);
        btnNO.setText(no);
        btnNO.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onClick(alertDialog, DialogInterface.BUTTON_NEGATIVE);
                alertDialog.dismiss();
            }
        });
        return alertDialog;
    }

    public static AlertDialog ShowYesDialog(Context context, String title, String content, String
            yes, final DialogInterface.OnClickListener listener) {

        alertDialog = new AlertDialog.Builder(context).create();

        alertDialog.setCancelable(false);

        View view = LayoutInflater.from(context).inflate(R.layout.base_dialog, null);
        ((TextView) view.findViewById(R.id.title)).setText(title);
        ((TextView) view.findViewById(R.id.content)).setText(content);

        Window window = alertDialog.getWindow();

        params = window.getAttributes();
        params.windowAnimations = R.style.up_down;

        alertDialog.show();
        window.setContentView(view, params);

        TextView btnYes;
        btnYes = (TextView) view.findViewById(R.id.yes);
        btnYes.setText(yes);
        btnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onClick(alertDialog, DialogInterface.BUTTON_POSITIVE);
                alertDialog.dismiss();
            }
        });
        TextView btnNO;
        btnNO = (TextView) view.findViewById(R.id.no);
        btnNO.setVisibility(View.GONE);

        View ver_line;
        ver_line = view.findViewById(R.id.ver_line);
        ver_line.setVisibility(View.GONE);

        return alertDialog;
    }

    public static AlertDialog ShowYesNoLocationDialog(Context context, String title, String
            content, String yes, String no, final DialogInterface.OnClickListener listener) {

        final AlertDialog location = new AlertDialog.Builder(context).create();
        location.setCancelable(false);

        View view = LayoutInflater.from(context).inflate(R.layout.base_dialog, null);
        ((TextView) view.findViewById(R.id.title)).setText(title);
        ((TextView) view.findViewById(R.id.content)).setText(content);

        Window window = location.getWindow();

        WindowManager.LayoutParams params = window.getAttributes();
        params.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;

        //这个show必须在setContentView前面，不然报错。
        location.show();
        window.setContentView(view, params);


        TextView btnYes;
        btnYes = (TextView) view.findViewById(R.id.yes);
        btnYes.setText(yes);
        btnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onClick(location, DialogInterface.BUTTON_POSITIVE);
                location.dismiss();
            }
        });
        TextView btnNO;
        btnNO = (TextView) view.findViewById(R.id.no);
        btnNO.setText(no);
        btnNO.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onClick(location, DialogInterface.BUTTON_NEGATIVE);
                location.dismiss();
            }
        });
        return location;
    }

}
