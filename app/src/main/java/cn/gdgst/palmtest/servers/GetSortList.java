package cn.gdgst.palmtest.servers;

import com.alibaba.fastjson.JSON;
import com.orhanobut.logger.Logger;
import cn.gdgst.palmtest.Entitys.CK_Cate_Entity;
import cn.gdgst.palmtest.Entitys.Category_firstlist_Entity;
import cn.gdgst.palmtest.Entitys.GradeEntity;
import cn.gdgst.palmtest.Entitys.PX_Cate_Entity;
import cn.gdgst.palmtest.Entitys.Sub;
import cn.gdgst.palmtest.Entitys.Wk_Cate_Entity;
import cn.gdgst.palmtest.utils.HttpUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;



public class GetSortList {
	/**获取视频分类/试验分类列表 （一级分类：高中、初中、小学）
	 * @param url
	 * @param requestparams
	 * @return
	 */
	public static List<Category_firstlist_Entity> getcategory_firstlist_Entities(String url,
																				 Map<String, String> requestparams) {
		String Cate_firstlist = HttpUtil.postRequest(url, requestparams);
		com.alibaba.fastjson.JSONObject jsonobj=JSON.parseObject(Cate_firstlist);
		com.alibaba.fastjson.JSONArray userdata = jsonobj.getJSONArray("data");
		String data=JSON.toJSONString(userdata);
		List<Category_firstlist_Entity> catefirstlist = JSON.parseArray(data, Category_firstlist_Entity.class);

		return catefirstlist;

	}
	/**获取年级分类列表 高三 高二 高一 初三 初二 初一 小六 小五 小四 小三
	 * @param url
	 * @param requestparams
	 * @return
	 */
	public static List<GradeEntity> getGradelist (String url,Map<String, String>requestparams){
		String UsersJson=HttpUtil.postRequest(url, requestparams);
		com.alibaba.fastjson.JSONObject jsonobj=JSON.parseObject(UsersJson);
		com.alibaba.fastjson.JSONArray userdata = jsonobj.getJSONArray("data");
		com.alibaba.fastjson.JSONObject jo =userdata.getJSONObject(0);
		com.alibaba.fastjson.JSONArray jsonarr =jo.getJSONArray("sub");
		String array=JSON.toJSONString(jsonarr);
		List<GradeEntity> gradelist	=JSON.parseArray(array, GradeEntity.class);
		return gradelist;
	}
	/**获取视频分类/试验分类列表
	 * （二级分类：高中物理、高中化学、高中生物、通用技术、科技活动、机器人、信息技术、数字地理、创新实验室）
	 * @param url
	 * @param requestparams
	 * @return
	 */
	public static List<Sub> getSubList(String url,Map<String, String> requestparams){
		String Cate_firstlist = HttpUtil.postRequest(url, requestparams);
		com.alibaba.fastjson.JSONObject jsonobj=JSON.parseObject(Cate_firstlist);
		com.alibaba.fastjson.JSONArray userdata = jsonobj.getJSONArray("data");
		String data=JSON.toJSONString(userdata);
		//获取data里面的内容后解析赋值Category_firstlist_Entity 实体类，
		List<Category_firstlist_Entity> catefirstlist = JSON.parseArray(data, Category_firstlist_Entity.class);
		List<Sub> sublist = new ArrayList<Sub>();
		if (catefirstlist.isEmpty()) {
			Logger.i("catefirstlist is Empty");
		} else {
			for (int i = 0; i < catefirstlist.size(); i++) {
				for (int j = 0; j < catefirstlist.get(i).getSub().size(); j++) {
					Sub sub=new Sub();
					sub.setName(catefirstlist.get(i).getSub().get(j).getName());
					sub.setId(catefirstlist.get(i).getSub().get(j).getId());
					sublist.add(sub);
				}
			}
		}
		return sublist;
	}

	public static List<GradeEntity> getGradeList(String url, Map<String, String> requestparams){
		String Cate_firstlist = HttpUtil.postRequest(url, requestparams);
		com.alibaba.fastjson.JSONObject jsonobj=JSON.parseObject(Cate_firstlist);
		com.alibaba.fastjson.JSONArray userdata = jsonobj.getJSONArray("data");
		String data=JSON.toJSONString(userdata);
		//获取data里面的内容后解析赋值Category_firstlist_Entity 实体类，
		List<GradeEntity> gradelist = JSON.parseArray(data, GradeEntity.class);
		return gradelist;

	}

	public static List<CK_Cate_Entity> getckcateList(String url, Map<String, String>requestparams){
		String Json=HttpUtil.postRequest(url, requestparams);
		com.alibaba.fastjson.JSONObject jsonobj=JSON.parseObject(Json);
		com.alibaba.fastjson.JSONArray userdata = jsonobj.getJSONArray("data");
		String data=JSON.toJSONString(userdata);
		List<CK_Cate_Entity> ckcatelist	=JSON.parseArray(data, CK_Cate_Entity.class);
		return ckcatelist;

	}
	public static List<Wk_Cate_Entity> getwkcateList(String url, Map<String, String>requestparams){
		String Json=HttpUtil.postRequest(url, requestparams);
		com.alibaba.fastjson.JSONObject jsonobj=JSON.parseObject(Json);
		com.alibaba.fastjson.JSONArray userdata = jsonobj.getJSONArray("data");
		String data=JSON.toJSONString(userdata);
		List<Wk_Cate_Entity> wkcatelist	=JSON.parseArray(data, Wk_Cate_Entity.class);
		return wkcatelist;

	}

	public static List<PX_Cate_Entity> getpxcateList(String url, Map<String, String> requestparams){
		String Json=HttpUtil.postRequest(url, requestparams);
		com.alibaba.fastjson.JSONObject jsonobj=JSON.parseObject(Json);
		com.alibaba.fastjson.JSONArray userdata = jsonobj.getJSONArray("data");
		String data=JSON.toJSONString(userdata);
		List<PX_Cate_Entity> wkcatelist	=JSON.parseArray(data, PX_Cate_Entity.class);
		return wkcatelist;

	}
}
