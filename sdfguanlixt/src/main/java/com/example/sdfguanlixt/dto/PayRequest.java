package com.example.sdfguanlixt.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

public class PayRequest {

    @NotBlank(message = "请选择支付方式")
    private String payMethod;

    @NotBlank(message = "请输入支付密码")
    @Pattern(regexp = "^\\d{6}$", message = "支付密码为 6 位数字")
    private String payPassword;

    public String getPayMethod() {
        return payMethod;
    }

    public void setPayMethod(String payMethod) {
        this.payMethod = payMethod;
    }

    public String getPayPassword() {
        return payPassword;
    }

    public void setPayPassword(String payPassword) {
        this.payPassword = payPassword;
    }
}
