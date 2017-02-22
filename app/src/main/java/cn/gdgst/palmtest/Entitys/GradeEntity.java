package cn.gdgst.palmtest.Entitys;

import java.io.Serializable;
import java.util.List;

/**年级分类列表
 * 高三 高二 高一 初三 初二 初一 小六 小五 小四 小三
 * @author Don
 *
 */
public class GradeEntity implements Serializable {
	/**
	 *
	 */
	private static final long serialVersionUID = -5199493050193364587L;

	private String id;

	private String diyname;

	private String name;

	private String imgurl;

	private String pid;

	private String sort;

	private String ishot;

	private String pagetitle;

	private String pagekey;

	private String pagedec;

	private String remark;

	private int deep;

	private String key;

	private List<Sub> sub ;

	private int subcount;

	public GradeEntity(String id, String diyname, String name, String imgurl, String pid, String sort, String ishot,
					   String pagetitle, String pagekey, String pagedec, String remark, int deep, String key, List<Sub> sub,
					   int subcount) {
		super();
		this.id = id;
		this.diyname = diyname;
		this.name = name;
		this.imgurl = imgurl;
		this.pid = pid;
		this.sort = sort;
		this.ishot = ishot;
		this.pagetitle = pagetitle;
		this.pagekey = pagekey;
		this.pagedec = pagedec;
		this.remark = remark;
		this.deep = deep;
		this.key = key;
		this.sub = sub;
		this.subcount = subcount;
	}
	public GradeEntity(){

	}
	public void setId(String id){
		this.id = id;
	}
	public String getId(){
		return this.id;
	}
	public void setDiyname(String diyname){
		this.diyname = diyname;
	}
	public String getDiyname(){
		return this.diyname;
	}
	public void setName(String name){
		this.name = name;
	}
	public String getName(){
		return this.name;
	}
	public void setImgurl(String imgurl){
		this.imgurl = imgurl;
	}
	public String getImgurl(){
		return this.imgurl;
	}
	public void setPid(String pid){
		this.pid = pid;
	}
	public String getPid(){
		return this.pid;
	}
	public void setSort(String sort){
		this.sort = sort;
	}
	public String getSort(){
		return this.sort;
	}
	public void setIshot(String ishot){
		this.ishot = ishot;
	}
	public String getIshot(){
		return this.ishot;
	}
	public void setPagetitle(String pagetitle){
		this.pagetitle = pagetitle;
	}
	public String getPagetitle(){
		return this.pagetitle;
	}
	public void setPagekey(String pagekey){
		this.pagekey = pagekey;
	}
	public String getPagekey(){
		return this.pagekey;
	}
	public void setPagedec(String pagedec){
		this.pagedec = pagedec;
	}
	public String getPagedec(){
		return this.pagedec;
	}
	public void setRemark(String remark){
		this.remark = remark;
	}
	public String getRemark(){
		return this.remark;
	}
	public void setDeep(int deep){
		this.deep = deep;
	}
	public int getDeep(){
		return this.deep;
	}
	public void setKey(String key){
		this.key = key;
	}
	public String getKey(){
		return this.key;
	}
	public void setSub(List<Sub> sub){
		this.sub = sub;
	}
	public List<Sub> getSub(){
		return this.sub;
	}
	public void setSubcount(int subcount){
		this.subcount = subcount;
	}
	public int getSubcount(){
		return this.subcount;
	}

}