package cn.gdgst.palmtest.bean;

import java.util.List;

/**
 * Created by Don on 2016/7/5.
 */
public class CategoryListSub_Entity {
    private String id;

    private String diyname;

    private String name;

    private String imgurl;

    private String pid;

    private String sort;

    private String total;

    private String ishot;

    private String isnew;

    private String pagetitle;

    private String pagekey;

    private String pagedec;

    private String remark;

    private int deep;

    private String key;

    private List<CategoryList_Entity> sub ;

    private int subcount;

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
    public void setTotal(String total){
        this.total = total;
    }
    public String getTotal(){
        return this.total;
    }
    public void setIshot(String ishot){
        this.ishot = ishot;
    }
    public String getIshot(){
        return this.ishot;
    }
    public void setIsnew(String isnew){
        this.isnew = isnew;
    }
    public String getIsnew(){
        return this.isnew;
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
    public void setSub(List<CategoryList_Entity> sub){
        this.sub = sub;
    }
    public List<CategoryList_Entity> getSub(){
        return this.sub;
    }
    public void setSubcount(int subcount){
        this.subcount = subcount;
    }
    public int getSubcount(){
        return this.subcount;
    }
}
