package cn.gdgst.palmtest.service;

import android.content.Context;
import android.support.annotation.VisibleForTesting;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.orhanobut.logger.Logger;

import cn.gdgst.palmtest.API.APIWrapper;
import cn.gdgst.palmtest.Entitys.CollectEntity;
import cn.gdgst.palmtest.bean.HttpResult;
import cn.gdgst.palmtest.utils.HttpUtil;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import java.util.List;
import java.util.Map;

public class CollectService {

	public CollectService(Context context) throws Exception {
	}

	public int getaddInfo(Map<String, String> rawParams) {
		String urlStr = "http://shiyan360.cn/index.php/api/user_collect_add";
		// 设置请求参数项 发送请求返回json
		String json = HttpUtil.postRequest(urlStr, rawParams);
		Logger.json(json);
		// 解析json数据
		com.alibaba.fastjson.JSONObject jsonobj = JSON.parseObject(json);
		Boolean response = (Boolean) jsonobj.get("success");
		int error_code=(int) jsonobj.get("error_code");
		Logger.i("error_code"+error_code);
		return error_code;

	}

	public Boolean getDeleteInfo(Map<String, String> rawParams) {
		String urlStr = "http://shiyan360.cn/index.php/api/user_collect_delete";
		String json = HttpUtil.postRequest(urlStr, rawParams);
		Logger.json(json);
		// 解析json数据
		com.alibaba.fastjson.JSONObject jsonobj = JSONObject.parseObject(json);
		Boolean response = (Boolean) jsonobj.get("success");

		return response;

	}

	public static List<CollectEntity> getCollectList(Map<String,String>rawParams){
		String urlStr="http://shiyan360.cn/index.php/api/user_collect";
		String json=HttpUtil.postRequest(urlStr, rawParams);
		Logger.json(json);
		//解析json数据
		com.alibaba.fastjson.JSONObject jsonobj=JSON.parseObject(json);
		com.alibaba.fastjson.JSONArray jsondata=jsonobj.getJSONArray("data");
		String array=JSON.toJSONString(jsondata);
		List<CollectEntity> CollectList=JSON.parseArray(array, CollectEntity.class);
		return CollectList;
	}

}
