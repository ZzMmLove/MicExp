package cn.gdgst.palmtest.bean;

/**
 * Created by liukun on 16/3/5.
 */
public class HttpResult<T> {
    private boolean success;

    private int error_code;

    private String message;

    private T data ;

    public void setSuccess(boolean success){
        this.success = success;
    }
    public boolean getSuccess(){
        return this.success;
    }
    public void setError_code(int error_code){
        this.error_code = error_code;
    }
    public int getError_code(){
        return this.error_code;
    }
    public void setMessage(String message){
        this.message = message;
    }
    public String getMessage(){
        return this.message;
    }
    public void setData(T data){
        this.data = data;
    }
    public T getData(){
        return this.data;
    }
}
