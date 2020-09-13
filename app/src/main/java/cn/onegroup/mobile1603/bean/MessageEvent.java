package cn.onegroup.mobile1603.bean;

import java.io.Serializable;

/**
 * Created by CaostGrace on 2017/12/14 0014.
 * mail:caostgrace@163.com
 * github:https://github.com/CaostGrace
 * 简书:http://www.jianshu.com/u/b252a19d88f3
 * 内容：
 */

public class MessageEvent implements Serializable{
    public Object obj;
    public String action;
    public long exittime;
    public MessageEvent(Object obj,String action){
        this.obj = obj;
        this.action = action;
    }

    public MessageEvent(String action, long exittime) {
        this.action = action;
        this.exittime = exittime;
    }
}
