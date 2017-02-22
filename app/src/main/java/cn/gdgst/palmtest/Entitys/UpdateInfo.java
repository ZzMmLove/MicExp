package cn.gdgst.palmtest.Entitys;

public class UpdateInfo {
	private int is_upgrade;

	private String upgrade_url;

	private String upgrade_remark;

	public UpdateInfo(int is_upgrade, String upgrade_url, String upgrade_remark) {
		super();
		this.is_upgrade = is_upgrade;
		this.upgrade_url = upgrade_url;
		this.upgrade_remark = upgrade_remark;
	}
	public UpdateInfo(){
		
	}
	public int getIs_upgrade() {
		return is_upgrade;
	}

	public void setIs_upgrade(int is_upgrade) {
		this.is_upgrade = is_upgrade;
	}

	public String getUpgrade_url() {
		return upgrade_url;
	}

	public void setUpgrade_url(String upgrade_url) {
		this.upgrade_url = upgrade_url;
	}

	public String getUpgrade_remark() {
		return upgrade_remark;
	}

	public void setUpgrade_remark(String upgrade_remark) {
		this.upgrade_remark = upgrade_remark;
	}
	
}
