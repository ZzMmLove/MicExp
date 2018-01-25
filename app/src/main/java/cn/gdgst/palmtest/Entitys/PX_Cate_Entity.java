package cn.gdgst.palmtest.Entitys;

import java.io.Serializable;

public class PX_Cate_Entity implements Serializable{

	private static final long serialVersionUID = -5451055200836468333L;

	/**
	 * "success":true,
	 "error_code":0,
	 "message":"数据加载完毕",
	 "data":[
	 {
	 "id":"137",
	 "name":"物理实验设备",
	 "tpl":"index_pic",
	 "sort":"11"
	 },
	 */

	private String id;

	private String name;

	private String tpl;

	private String sort;

	public PX_Cate_Entity(String id, String name, String tpl, String sort) {
		super();
		this.id = id;
		this.name = name;
		this.tpl = tpl;
		this.sort = sort;
	}
	public PX_Cate_Entity(){
		
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
	public void setTpl(String tpl){
	this.tpl = tpl;
	}
	public String getTpl(){
	return this.tpl;
	}
	public void setSort(String sort){
	this.sort = sort;
	}
	public String getSort(){
	return this.sort;
	}

	@Override
	public String toString() {
		return "PX_Cate_Entity{" +
				"id='" + id + '\'' +
				", name='" + name + '\'' +
				", tpl='" + tpl + '\'' +
				", sort='" + sort + '\'' +
				'}';
	}
}
