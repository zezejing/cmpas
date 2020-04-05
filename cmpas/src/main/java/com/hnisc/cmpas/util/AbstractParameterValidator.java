package com.hnisc.cmpas.util;

import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

@Component
public class AbstractParameterValidator {
    //校验通过标识
    public static final String SUCESS = "SUCCESS";
    //是否输出所有错误参数
    private boolean outputAllError = false;
    //是否输出日志
    private boolean outputLog=true;
    //多个错误原因间隔符
    private String seperator="!";
    public boolean isOutputAllError() {
        return outputAllError;
    }
    public void setOutputAllError(boolean outputAllError) {
        this.outputAllError = outputAllError;
    }
    public boolean isOutputLog() {
        return outputLog;
    }
    public void setOutputLog(boolean outputLog) {
        this.outputLog = outputLog;
    }
    public String getSeperator() {
        return seperator;
    }
    public void setSeperator(String seperator) {
        this.seperator = seperator;
    }
    private void log(String fieldName, Object object, String result)
    {
        if(outputLog)
            System.out.println("字段:"+fieldName+" 值:"+object.toString()+" 校验结果:"+result);
    }
    public String validate(Object... objects) {
        StringBuilder stringBuilder = new StringBuilder();
        for (Object object : objects)
            if (object!=null)
            for (Field field : object.getClass().getDeclaredFields()) {
                field.setAccessible(true);
                Method method = null;
                try {
                    method = this.getClass().getDeclaredMethod(field.getName(), String.class);
                } catch (Exception e) { }
                if (method != null) {
                    String result =null;
                    try{
                        if(field.get(object)!=null)
                        result=(String) method.invoke(this, field.get(object));
                        //输出日志
                        log(field.getName(),field.get(object),result);
                    }catch (Exception e){}
                    if (result!=null&&(!result.equals(SUCESS))){
                        if (stringBuilder.length() > 0 && (!outputAllError))
                            return stringBuilder.toString();
                        stringBuilder.append(result);
                        stringBuilder.append(seperator);
                    }
                }
            }
        return stringBuilder.length() == 0 ? SUCESS : stringBuilder.toString();
    }
    public boolean checkResult(String result)
    {
        return result.equals(SUCESS);
    }
}
