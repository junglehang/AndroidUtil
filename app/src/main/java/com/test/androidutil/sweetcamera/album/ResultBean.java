package com.test.androidutil.sweetcamera.album;

/**
 * Created by 604406650 on 2016/10/27.
 */
public class ResultBean {
    /*{
	    "code": "0",
	    "result": "48-5B-39-7E-6B-94",
	    "textResult": "相同的产品各地都有。了解这一点，才会明白为什么早些年各地会同时引进100多条电视机生产线"
	}*/
    private String code;
    private String result;
    private String textResult;
    private static String imagePath;

    public String getTextResult() {
        return textResult;
    }

    public void setTextResult(String textResult) {
        this.textResult = textResult;
    }

    public static String getImagePath() {
        return imagePath;
    }

    public static void setImagePath(String mPath) {
        imagePath = mPath;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    @Override
    public String toString() {
        return "ResultBean [code=" + code + ", result=" + result + ", textResult=" + textResult + "]";
    }
}
