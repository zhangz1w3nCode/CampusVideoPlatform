package com.zzw.domain;
//返回的信息类
public class jsonResponse<T> {
    private String code;
    private String msg;
    private T data;

    public jsonResponse(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public jsonResponse(T data) {
        this.data = data;
        code = "0";
        msg = "成功";
    }
    //返回成功
    public static jsonResponse<String> success(){
        return new jsonResponse(null);
    }
    //默认 成功信息
    public static jsonResponse<String> success(String data){
        return new jsonResponse(data);
    }
    // 写死的失败情况的
    public static jsonResponse<String> fail(){
        return new jsonResponse("1","失败");
    }
    //定制的 失败情况的 code 和 msg
    public static jsonResponse<String> fail(String code, String msg){
        return new jsonResponse(code,msg);
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
