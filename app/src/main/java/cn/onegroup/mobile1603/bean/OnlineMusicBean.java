package cn.onegroup.mobile1603.bean;

import java.io.Serializable;


public class OnlineMusicBean implements Serializable {

	/*9个属性*/


	public int id;// 歌曲id
	public String songurl;// 歌曲链接
	public int ownerid;//上传的者id
	public String nickname;// 上传者昵称
	public String songname;// 上传者昵称
	public String headpic;// 头像地址
	public int domainid;//上传的者id
	public int rq;// 人气
	public String uploadtime;// 上传时间


}