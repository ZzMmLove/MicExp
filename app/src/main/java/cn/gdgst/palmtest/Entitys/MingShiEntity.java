package cn.gdgst.palmtest.Entitys;

import java.io.Serializable;

public class MingShiEntity implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 5220569378108646981L;

	private String id;

	private String cateid;

	private String gradeid;

	private String name;

	private String img_url;

	private String img_url_s;

	private String video_url;

	private String view_count;

	private String remark;

	private String time; 

	public MingShiEntity(String id, String cateid, String gradeid, String name, String img_url, String img_url_s,
			String video_url, String view_count, String remark, String time) {
		super();
		this.id = id;
		this.cateid = cateid;
		this.gradeid = gradeid;
		this.name = name;
		this.img_url = img_url;
		this.img_url_s = img_url_s;
		this.video_url = video_url;
		this.view_count = view_count;
		this.remark = remark;
		this.time = time;
	}
	public MingShiEntity(){
		
	}
	public void setId(String id){
	this.id = id;
	}
	public String getId(){
	return this.id;
	}
	public void setCateid(String cateid){
	this.cateid = cateid;
	}
	public String getCateid(){
	return this.cateid;
	}
	public void setGradeid(String gradeid){
	this.gradeid = gradeid;
	}
	public String getGradeid(){
	return this.gradeid;
	}
	public void setName(String name){
	this.name = name;
	}
	public String getName(){
	return this.name;
	}
	public void setImg_url(String img_url){
	this.img_url = img_url;
	}
	public String getImg_url(){
	return this.img_url;
	}
	public void setImg_url_s(String img_url_s){
	this.img_url_s = img_url_s;
	}
	public String getImg_url_s(){
	return this.img_url_s;
	}
	public void setVideo_url(String video_url){
	this.video_url = video_url;
	}
	public String getVideo_url(){
	return this.video_url;
	}
	public void setView_count(String view_count){
	this.view_count = view_count;
	}
	public String getView_count(){
	return this.view_count;
	}
	public void setRemark(String remark){
	this.remark = remark;
	}
	public String getRemark(){
	return this.remark;
	}
	public void setTime(String time){
	this.time = time;
	}
	public String getTime(){
	return this.time;
	}
}
