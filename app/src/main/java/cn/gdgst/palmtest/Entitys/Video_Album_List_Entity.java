package cn.gdgst.palmtest.Entitys;

import java.io.Serializable;

/**
 * 实现Video_Album_List_Entity接口实现对象数据的序列化 视频专辑列表实体类
 *
 * @author Don
 *
 */
public class Video_Album_List_Entity implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = -1854661955488718000L;

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

	public Video_Album_List_Entity(String id, String cateid, String gradeid, String name, String img_url,
								   String img_url_s, String video_url, String view_count, String remark, String time) {
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

	public Video_Album_List_Entity() {

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

	public String getGradeid() {
		return gradeid;
	}

	public void setGradeid(String gradeid) {
		this.gradeid = gradeid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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

	public String getVideo_url() {
		return video_url;
	}

	public void setVideo_url(String video_url) {
		this.video_url = video_url;
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

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

}
