package cn.onegroup.mobile1603.ui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import cn.onegroup.mobile1603.R;


/**
 * Created by CaostGrace on 2017/12/5 0005.
 * mail:caostgrace@163.com
 * github:https://github.com/CaostGrace
 * 简书:http://www.jianshu.com/u/b252a19d88f3
 * 内容：两种状态的ImageView
 */

public class MyImageView extends android.support.v7.widget.AppCompatImageView {

    private boolean status;
    private int img_open;
    private int img_close;

    private int src;
    private OnClickListener listener;



    public MyImageView(Context context) {
        super(context);
    }

    public MyImageView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public MyImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray typedArray = context.getTheme().obtainStyledAttributes(attrs, R.styleable.MyImageView,defStyleAttr,0);
        int n = typedArray.getIndexCount();
        for(int i = 0;i<n;i++){
            switch (i){
                case R.styleable.MyImageView_img_status:
                    status = typedArray.getBoolean(i,false);
                    break;
                case R.styleable.MyImageView_img_close:
                    img_close = typedArray.getResourceId(i,R.mipmap.ic_launcher);
                    break;
                case R.styleable.MyImageView_img_open:
                    img_open = typedArray.getResourceId(i,R.mipmap.ic_launcher);
                    break;
                case R.styleable.MyImageView_android_src:
                    src = typedArray.getResourceId(i,R.mipmap.ic_launcher);
                    break;
            }
        }

//        change();

        if (src == R.mipmap.ic_launcher){
            change();
//            Log.i("======src == 0======",src+"");
        }else{
            this.setImageResource(src);
//            Log.i("======src != 0======",src+"");
        }
        setClickable(true);

        setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notifyDataChange();
                if(null != listener){
                    listener.Onclick(status);
                }
            }
        });

    }

    public void notifyDataChange() {
        if (status){
            this.setImageResource(img_close);
            status = false;
        }else{
            this.setImageResource(img_open);
            status = true;
        }
    }


    private void change(){
        if(status){
            this.setImageResource(img_open);
//            Log.i("======img_open======",img_open+"");
        }else{
            this.setImageResource(img_close);
//            Log.i("======img_close======",img_close+"");
        }
    }



    public void setOnClickListener(OnClickListener listener){
        this.listener = listener;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
        change();
    }

    public int getImg_open() {
        return img_open;
    }

    public void setImg_open(int img_open) {
        this.img_open = img_open;
        change();
    }

    public int getImg_close() {
        return img_close;
    }

    public void setImg_close(int img_close) {
        this.img_close = img_close;
        change();
    }


    public interface OnClickListener{
        void Onclick(boolean status);
    }
}
