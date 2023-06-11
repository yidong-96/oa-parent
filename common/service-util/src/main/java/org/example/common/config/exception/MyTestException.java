package org.example.common.config.exception;

public class MyTestException extends RuntimeException {
    private Integer code; // 状态码
    private String msg; // 描述信息

    public MyTestException(Integer code,String msg) {
        super(msg);
        this.code = code;
        this.msg = msg;
    }

    @Override
    public String toString() {
        return "MyTestException{" +
                "code=" + code +
                ", msg='" + msg + '\'' +
                '}';
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
