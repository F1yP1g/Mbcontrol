package com.lu.mbcontrol;

/**
 * Created by LU on 2018/5/10.
 */

public class tejiaUser {
    private String phone, pwd, version,phonetype;

    public tejiaUser(String a,String b,String c,String d){
        this.phone=a;
        this.pwd=b;
        this.version=c;
        this.phonetype=d;
    }
    public String getPhone() {
        return phone;
    }

    public String getPwd() {
        return pwd;
    }

    public String getVersion() {
        return version;
    }

    public String getPhonetype() {
        return phonetype;
    }

    public void setPhone(String phone) {this.phone = phone;}

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public void setPhonetype(String phonetype) {
        this.phonetype = phonetype;
    }
}
