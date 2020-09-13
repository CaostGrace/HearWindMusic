package cn.onegroup.mobile1603.base;

import android.support.v7.app.AppCompatActivity;
import android.view.View;

/**
 * Created by CaostGrace on 2017/12/13 0013.
 * mail:caostgrace@163.com
 * github:https://github.com/CaostGrace
 * 简书:http://www.jianshu.com/u/b252a19d88f3
 * 内容：
 */

public class BaseActivity extends AppCompatActivity {

    public <T extends View> T $(int id){
        return (T) findViewById(id);
    }

    public <T extends View> T $(View v,int id) {
        return (T) v.findViewById(id);
    }


}
