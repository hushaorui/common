package com.hushaorui.common;

public class CommonResult {
    private static final CommonResult systemError = new CommonResult(WebConstants.ERROR, "系统错误，请检查日志！");
    private static final CommonResult dataCannotBeNullError = new CommonResult(WebConstants.ERROR, "数据不可为空！");
    private static final CommonResult addSuccess = new CommonResult(WebConstants.SUCCESS, "添加成功！");
    private static final CommonResult updateSuccess = new CommonResult(WebConstants.SUCCESS, "修改成功！");
    private static final CommonResult saveSuccess = new CommonResult(WebConstants.SUCCESS, "保存成功！");
    private static final CommonResult deleteSuccess = new CommonResult(WebConstants.SUCCESS, "删除成功！");
    private String result;
    private String message;
    private Object data;

    public CommonResult(String result, String message) {
        this.result = result;
        this.message = message;
    }

    public CommonResult(String result, Object data) {
        this.result = result;
        this.data = data;
    }

    public static CommonResult success(String message) {
        return new CommonResult(WebConstants.SUCCESS, message);
    }
    public static CommonResult addSuccess() {
        return addSuccess;
    }
    public static CommonResult updateSuccess() {
        return updateSuccess;
    }
    public static CommonResult saveSuccess() {
        return saveSuccess;
    }
    public static CommonResult deleteSuccess() {
        return deleteSuccess;
    }
    public static CommonResult dataCannotBeNullError() {
        return dataCannotBeNullError;
    }
    public static CommonResult error(String message) {
        return new CommonResult(WebConstants.ERROR, message);
    }

    public static CommonResult systemError() {
        return systemError;
    }

    public static CommonResult successData(Object data) {
        return new CommonResult(WebConstants.SUCCESS, data);
    }
    public String getResult() {
        return result;
    }

    public String getMessage() {
        return message;
    }

    public Object getData() {
        return data;
    }
}
