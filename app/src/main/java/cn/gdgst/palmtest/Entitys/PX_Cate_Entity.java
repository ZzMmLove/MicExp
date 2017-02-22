package cn.gdgst.palmtest.Entitys;

import java.io.Serializable;

public class PX_Cate_Entity implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -5451055200836468333L;

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

}
