package cn.gdgst.palmtest.Entitys;

import java.io.Serializable;

public class CK_Cate_Entity implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -1499884062107388660L;

	private String id;

	private String name;

	public CK_Cate_Entity(String id, String name) {
		super();
		this.id = id;
		this.name = name;
	}
	public CK_Cate_Entity(){
		
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
}
