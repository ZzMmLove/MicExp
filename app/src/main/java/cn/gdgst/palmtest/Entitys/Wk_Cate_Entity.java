package cn.gdgst.palmtest.Entitys;

public class Wk_Cate_Entity {
	private String id;

	private String name;

	private String status;

	private String sort;

	private String img_url;

	private String href;

	public Wk_Cate_Entity(String id, String name, String status, String sort, String img_url, String href) {
		super();
		this.id = id;
		this.name = name;
		this.status = status;
		this.sort = sort;
		this.img_url = img_url;
		this.href = href;
	}
	public Wk_Cate_Entity(){
		
	}
	public void setId(String id){
	this.id = id;
	}
	public String getId(){
	return this.id;
	}
	public void setName(String name){
	this.name = name;
	}
	public String getName(){
	return this.name;
	}
	public void setStatus(String status){
	this.status = status;
	}
	public String getStatus(){
	return this.status;
	}
	public void setSort(String sort){
	this.sort = sort;
	}
	public String getSort(){
	return this.sort;
	}
	public void setImg_url(String img_url){
	this.img_url = img_url;
	}
	public String getImg_url(){
	return this.img_url;
	}
	public void setHref(String href){
	this.href = href;
	}
	public String getHref(){
	return this.href;
	}
}
