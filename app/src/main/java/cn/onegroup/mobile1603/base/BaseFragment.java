package cn.onegroup.mobile1603.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by CaostGrace on 2017/12/13 0013.
 * mail:caostgrace@163.com
 * github:https://github.com/CaostGrace
 * 简书:http://www.jianshu.com/u/b252a19d88f3
 * 内容：
 */

public abstract class BaseFragment extends Fragment {
    private View parent;

    public <T extends View> T $(int id) {
        return (T) parent.findViewById(id);
    }
    public <T extends View> T $(View v,int id) {
        return (T) v.findViewById(id);
    }


    public abstract int putLayout();

    public abstract void nextOnCreat(@Nullable Bundle savedInstanceState);

    public View getView() {
        return parent;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable
            Bundle savedInstanceState) {
        parent = inflater.inflate(putLayout(), null);
        nextOnCreat(savedInstanceState);
        return parent;
    }
}
