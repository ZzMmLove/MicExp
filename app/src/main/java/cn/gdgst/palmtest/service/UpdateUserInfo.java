package cn.gdgst.palmtest.service;

import android.content.Context;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.orhanobut.logger.Logger;
import cn.gdgst.palmtest.utils.HttpUtil;
import cn.gdgst.palmtest.utils.NetworkDialogUtils;

import java.util.Map;
import java.util.Set;

public class UpdateUserInfo {
	private String message;
	public UpdateUserInfo(Context context) throws Exception {

	}
	public Boolean getUpdateUserInfo(Map<String, String> rawParams) {
		String urlStr="http://www.shiyan360.cn/index.php/api/user_profile_update";

		Set<String> get = rawParams.keySet();
		for (String test:get) {
			Log.d("UpdateUserInfo", "键值对日志输出:"+test+","+rawParams.get(test));
			//System.out.println(test+","+rawParams.get(test));
		}

		// 设置请求参数项 发送请求返回json
		String json = HttpUtil.postRequest(urlStr, rawParams);
		Logger.json(json);
		// 解析json数据
		com.alibaba.fastjson.JSONObject jsonobj = JSON.parseObject(json);
		Boolean response = (Boolean) jsonobj.get("success");
		message = (String) jsonobj.get("message");
		return response;
	}

	public String getMessage() {
		return message;
	}
}
