package cn.gdgst.palmtest.Entitys;

public class AddCollectEntity {
	private int id;

	private String member_id;

	private int doc_id;

	private String model;

	private String name;

	private int dateline;

	public AddCollectEntity(int id, String member_id, int doc_id, String model, String name, int dateline) {
		super();
		this.id = id;
		this.member_id = member_id;
		this.doc_id = doc_id;
		this.model = model;
		this.name = name;
		this.dateline = dateline;
	}
	public AddCollectEntity(){
		
	}
	public void setId(int id){
	this.id = id;
	}
	public int getId(){
	return this.id;
	}
	public void setMember_id(String member_id){
	this.member_id = member_id;
	}
	public String getMember_id(){
	return this.member_id;
	}
	public void setDoc_id(int doc_id){
	this.doc_id = doc_id;
	}
	public int getDoc_id(){
	return this.doc_id;
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
	public void setDateline(int dateline){
	this.dateline = dateline;
	}
	public int getDateline(){
	return this.dateline;
	}
}
