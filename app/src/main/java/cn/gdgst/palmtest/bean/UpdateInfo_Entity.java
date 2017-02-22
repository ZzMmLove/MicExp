package cn.gdgst.palmtest.bean;

/**
 * Created by Don on 2016/6/24.
 */
public class UpdateInfo_Entity {
    private int is_upgrade;

    private String upgrade_url;

    private String upgrade_remark;

    public void setIs_upgrade(int is_upgrade){
        this.is_upgrade = is_upgrade;
    }
    public int getIs_upgrade(){
        return this.is_upgrade;
    }
    public void setUpgrade_url(String upgrade_url){
        this.upgrade_url = upgrade_url;
    }
    public String getUpgrade_url(){
        return this.upgrade_url;
    }
    public void setUpgrade_remark(String upgrade_remark){
        this.upgrade_remark = upgrade_remark;
    }
    public String getUpgrade_remark(){
        return this.upgrade_remark;
    }

}