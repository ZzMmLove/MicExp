package cn.gdgst.palmtest.operate;

import java.io.Serializable;

@SuppressWarnings("serial")
public class RequestHeader implements Serializable{
	/**
	 * 客户端当前系统时间，以毫秒做为单位，如与服务器相差大于15分钟则返回异常，每个方法都会做此验证。
	 */
	private String a_curDateTime;
	/**
	 * 用户登陆后获取的方法签名对象的Id，如果用户没有登陆请返回“”空字符串
	 */
	private String a_cid;
	/**
	 * 签名信息，用户登陆后获取的方法签名对象的Id加上用户的登陆密码进行MD5加密，服务器通过a_cid存留信息与之加密对照验证
	 */
	private String a_singInfo;
	/**
	 * 手机的IMEI编码
	 */
	private String a_imei;
	/**
	 * 手机卡的IMSI编码
	 */
	private String a_imsi;
	/**
	 * 手机号码，如取不到请返回“”空字符串
	 */
	private String a_phone;
	/**
	 * 经度
	 */
	private String  a_lng;
	/**
	 * 纬度
	 */
	private String  a_lat;
	public String getA_curDateTime() {
		return a_curDateTime;
	}
	public void setA_curDateTime(String a_curDateTime) {
		this.a_curDateTime = a_curDateTime;
	}
	public String getA_cid() {
		return a_cid;
	}
	public void setA_cid(String a_cid) {
		this.a_cid = a_cid;
	}
	public String getA_singInfo() {
		return a_singInfo;
	}
	public void setA_singInfo(String a_singInfo) {
		this.a_singInfo = a_singInfo;
	}
	public String getA_imei() {
		return a_imei;
	}
	public void setA_imei(String a_imei) {
		this.a_imei = a_imei;
	}
	public String getA_imsi() {
		return a_imsi;
	}
	public void setA_imsi(String a_imsi) {
		this.a_imsi = a_imsi;
	}
	public String getA_phone() {
		return a_phone;
	}
	public void setA_phone(String a_phone) {
		this.a_phone = a_phone;
	}
	public String getA_lng() {
		return a_lng;
	}
	public void setA_lng(String a_lng) {
		this.a_lng = a_lng;
	}
	public String getA_lat() {
		return a_lat;
	}
	public void setA_lat(String a_lat) {
		this.a_lat = a_lat;
	}


}
