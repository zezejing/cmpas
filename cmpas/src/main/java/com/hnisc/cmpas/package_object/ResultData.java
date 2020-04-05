package com.hnisc.cmpas.package_object;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
public class ResultData {
    private String Result;

    public String getResult() {
        return Result;
    }

    public void setResult(String result) {
        Result = result;
    }
}
