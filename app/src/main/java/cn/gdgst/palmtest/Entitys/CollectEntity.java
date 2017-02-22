package cn.gdgst.palmtest.Entitys;

public class CollectEntity {
	private String id;
	private String video_id;
	private String name;
	private String img_url;
	private String img_url_s;
	private String video_url;
	private String view_count;
	public CollectEntity(String id, String video_id, String name, String img_url, String img_url_s, String video_url,
			String view_count) {
		super();
		this.id = id;
		this.video_id = video_id;
		this.name = name;
		this.img_url = img_url;
		this.img_url_s = img_url_s;
		this.video_url = video_url;
		this.view_count = view_count;
	}
	public CollectEntity(){
		
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getVideo_id() {
		return video_id;
	}
	public void setVideo_id(String video_id) {
		this.video_id = video_id;
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

	
}
