package cn.gdgst.palmtest.utils;

/**
 * Created by Administrator on 7/3 0003.
 */

public class RegExUtils {

    /**
     * 判断是不是QQ号码
     * @param qqStr
     * @return
     */
    public static boolean isQQ(String qqStr){
        // 1 首位不能是0  ^[1-9]
        // 2 必须是 [5, 11] 位的数字  \d{4, 9}
        String reg = "/^[1-9][0-9]{4,9}$/gim";
        return qqStr.matches(reg) ? true: false;
    }

    /**
     * 是否是电话号码
     * @param phoneStr
     * @return
     */
    public static boolean isPhone(String phoneStr){
        String reg = "/^(0|86|17951)?(13[0-9]|15[012356789]|18[0-9]|14[57]|17[678])[0-9]{8}$/";
        return phoneStr.matches(reg)? true: false;
    }

    /**
     * 时候为合法的Email
     * @param emailStr
     * @return
     */
    public static boolean isEmail(String emailStr){
        //String reg = "/^\w+((-\w+)|(\.\w+))*\@[A-Za-z0-9]+((\.|-)[A-Za-z0-9]+)*\.[A-Za-z0-9]+$/";
        String reg1 = "^\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$";
        return emailStr.matches(reg1)? true: false;
    }
}
