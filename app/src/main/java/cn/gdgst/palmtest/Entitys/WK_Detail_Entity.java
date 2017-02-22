package cn.gdgst.palmtest.Entitys;

import java.io.Serializable;

public class WK_Detail_Entity implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 544101527290373840L;

	private String id;

	private String cateid;

	private String title;

	private String img_url;

	private String img_url_s;

	private String file_url;

	private String view_count;
	
	private String remark;

	private String time;

	public WK_Detail_Entity(String id, String cateid, String title, String img_url, String img_url_s, String file_url,
			String view_count, String remark, String time) {
		super();
		this.id = id;
		this.cateid = cateid;
		this.title = title;
		this.img_url = img_url;
		this.img_url_s = img_url_s;
		this.file_url = file_url;
		this.view_count = view_count;
		this.remark = remark;
		this.time = time;
	}
	public WK_Detail_Entity(){
		
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getCateid() {
		return cateid;
	}

	public void setCateid(String cateid) {
		this.cateid = cateid;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getImg_url() {
		return img_url;
	}

	public void setImg_url(String img_url) {
		this.img_url = img_url;
	}

	public String getImg_url_s() {
		return img_url_s;
	}

	public void setImg_url_s(String img_url_s) {
		this.img_url_s = img_url_s;
	}

	public String getFile_url() {
		return file_url;
	}

	public void setFile_url(String file_url) {
		this.file_url = file_url;
	}

	public String getView_count() {
		return view_count;
	}

	public void setView_count(String view_count) {
		this.view_count = view_count;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}
}
