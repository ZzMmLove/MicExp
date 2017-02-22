package cn.gdgst.palmtest.servers;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONPObject;
import cn.gdgst.palmtest.Entitys.UserEntity;
import cn.gdgst.palmtest.utils.HttpUtil;

import java.util.List;
import java.util.Map;

public class GetUserList {
	public static List<UserEntity> getUsers(String url,Map<String, String>requestparams){
		String UsersJson= HttpUtil.postRequest(url, requestparams);
		com.alibaba.fastjson.JSONObject jsonobj=(JSONObject) JSON.toJSON(UsersJson);
		com.alibaba.fastjson.JSONPObject userdata=(JSONPObject) jsonobj.get("data");
		String data=userdata.toJSONString();
		List<UserEntity> userList=JSON.parseArray(data, UserEntity.class);

		return userList;

	}

}
