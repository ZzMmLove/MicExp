package cn.gdgst.palmtest.Entitys;

import java.io.Serializable;
/**
 * 实现UserEntity接口实现对象数据的序列化
 * @author Don
 *
 */
public class UserEntity implements Serializable{
	private static final long serialVersionUID = -540126402116610455L;
	private String  id;
	private String  nickname;
	private String  name;
	private String  avatar;
	private int  sex;
	private String  type;
	private String  school;
	private int   status;
	private String  accessToken;

	public UserEntity(String id, String nickname, String name, String avatar, int sex, String type, String school,
					  int status, String accessToken) {
		super();
		this.id = id;
		this.nickname = nickname;
		this.name = name;
		this.avatar = avatar;
		this.sex = sex;
		this.type = type;
		this.school = school;
		this.status = status;
		this.accessToken = accessToken;
	}
	public UserEntity(){

	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getNickname() {
		return nickname;
	}
	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getAvatar() {
		return avatar;
	}
	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}
	public int getSex() {
		return sex;
	}
	public void setSex(int sex) {
		this.sex = sex;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getSchool() {
		return school;
	}
	public void setSchool(String school) {
		this.school = school;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public String getAccessToken() {
		return accessToken;
	}
	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	@Override
	public String toString() {
		return "UserEntity{" +
				"id='" + id + '\'' +
				", nickname='" + nickname + '\'' +
				", name='" + name + '\'' +
				", avatar='" + avatar + '\'' +
				", sex=" + sex +
				", type='" + type + '\'' +
				", school='" + school + '\'' +
				", status=" + status +
				", accessToken='" + accessToken + '\'' +
				'}';
	}
}