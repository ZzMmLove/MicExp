package cn.gdgst.palmtest.Entitys;

import java.io.Serializable;

public class WenKuEntity implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 3513674947708737782L;

	private String id;

	private String cateid;

	private String title;

	private String img_url;

	private String img_url_s;

	private String file_url;

	private String view_count;

	private String time;

	public WenKuEntity(String id, String cateid, String title, String img_url, String img_url_s, String file_url,
			String view_count, String time) {
		super();
		this.id = id;
		this.cateid = cateid;
		this.title = title;
		this.img_url = img_url;
		this.img_url_s = img_url_s;
		this.file_url = file_url;
		this.view_count = view_count;
		this.time = time;
	}
	public WenKuEntity(){
		
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
	public void setTitle(String title){
	this.title = title;
	}
	public String getTitle(){
	return this.title;
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
	public void setFile_url(String file_url){
	this.file_url = file_url;
	}
	public String getFile_url(){
	return this.file_url;
	}
	public void setView_count(String view_count){
	this.view_count = view_count;
	}
	public String getView_count(){
	return this.view_count;
	}
	public void setTime(String time){
	this.time = time;
	}
	public String getTime(){
	return this.time;
	}
}
