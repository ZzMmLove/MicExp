package cn.gdgst.palmtest.Entitys;

import java.io.Serializable;
/**
 * 实现UserEntity接口实现对象数据的序列化
 * @author Don
 *
 */
public class UserEntity implements Serializable{
	private static final long serialVersionUID = -540126402116610455L;
	/**
	 * 用户ID
	 */
	private String  id;
	/**
	 * 用户昵称
	 */
	private String  nickname;
	/**
	 * 用户真实姓名
	 */
	private String  name;
	/**
	 * 用户头像的路径
	 */
	private String  avatar;
	/**
	 * 性别
	 */
	private int  sex;
	/**
	 * 用户的身份(老师，学生，家长)
	 */
	private String  type;
	/**
	 * 用户所在学校
	 */
	private String  school;
	/**
	 * 用户状态
	 */
	private int   status;
	/**
	 * 用户访问服务器数据的令牌
	 */
	private String  accessToken;
	/**
	 * 学生所在班级
	 */
	private String banji;
	/**
	 * 学生所属老师
	 */
	private String teacher;

	public UserEntity(String id, String nickname, String name, String avatar, int sex, String type, String school,
					  int status, String accessToken, String banji, String teacher) {
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
		this.banji = banji;
		this.teacher = teacher;
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
	public void setBanji(String banji) { this.banji = banji;}
	public String getBanji() { return banji;}
	public void setTeacher(String teacher) { this.teacher = teacher;}
	public String getTeacher() {
		return teacher;
	}

	@Override
	public String toString() {
		return "UserEntity{" +
				"accessToken='" + accessToken + '\'' +
				", id='" + id + '\'' +
				", nickname='" + nickname + '\'' +
				", name='" + name + '\'' +
				", avatar='" + avatar + '\'' +
				", sex=" + sex +
				", type='" + type + '\'' +
				", school='" + school + '\'' +
				", status=" + status +
				", banji='" + banji + '\'' +
				", teacher='" + teacher + '\'' +
				'}';
	}
}