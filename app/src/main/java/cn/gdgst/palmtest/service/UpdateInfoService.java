package cn.gdgst.palmtest.service;

import android.content.Context;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.orhanobut.logger.Logger;

import cn.gdgst.palmtest.API.APIWrapper;
import cn.gdgst.palmtest.Entitys.UpdateInfo;
import cn.gdgst.palmtest.bean.HttpResult;
import cn.gdgst.palmtest.utils.HttpUtil;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import java.util.List;
import java.util.Map;

public class UpdateInfoService {

	public UpdateInfoService(Context context) throws Exception {

	}


	public UpdateInfo getUpDateInfo(Map<String, String> rawParams) {
		String urlStr = "http://shiyan360.cn/index.php/api/check_version";
		// 设置请求参数项 发送请求返回json
		String json = HttpUtil.postRequest(urlStr, rawParams);
		//Logger.json(json);
		// 解析json数据
		com.alibaba.fastjson.JSONObject jsonobj = JSON.parseObject(json);
		com.alibaba.fastjson.JSONObject js = jsonobj.getJSONObject("data");
		String jo = JSON.toJSONString(js);
		UpdateInfo uf = JSON.parseObject(jo, UpdateInfo.class);
		UpdateInfo updateInfo = new UpdateInfo();
		updateInfo.setUpgrade_url(uf.getUpgrade_url());
		updateInfo.setIs_upgrade(uf.getIs_upgrade());
		updateInfo.setUpgrade_remark(uf.getUpgrade_remark());
		return updateInfo;
	}
}
