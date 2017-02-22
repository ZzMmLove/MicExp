package cn.gdgst.palmtest.Entitys;

public class HistoryEntity {
	private String id;

	private String video_id;

	private String model;

	private String name;

	private String img_url;

	private String img_url_s;

	private String video_url;

	private String view_count;

	public HistoryEntity(String id, String video_id, String model, String name, String img_url, String img_url_s,
			String video_url, String view_count) {
		super();
		this.id = id;
		this.video_id = video_id;
		this.model = model;
		this.name = name;
		this.img_url = img_url;
		this.img_url_s = img_url_s;
		this.video_url = video_url;
		this.view_count = view_count;
	}
	public HistoryEntity(){
		
	}
	public void setId(String id){
	this.id = id;
	}
	public String getId(){
	return this.id;
	}
	public void setVideo_id(String video_id){
	this.video_id = video_id;
	}
	public String getVideo_id(){
	return this.video_id;
	}
	public void setModel(String model){
	this.model = model;
	}
	public String getModel(){
	return this.model;
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
}
