package jpabook.jpashop.domain;


import lombok.Getter;

import javax.persistence.Embeddable;

@Embeddable
@Getter
public class zzBasicInfo {
    private String ID;
    private String password;
    private String Rname;
    private String school;
    private String grade;
    private String subject;
    private String file;

    protected zzBasicInfo(){
    }

    public zzBasicInfo(String ID, String password, String name, String school, String grade, String subject){//,String file) {
        this.ID = ID;
        this.password = password;
        this.Rname = name;
        this.school = school;
        this.grade = grade;
        this.subject = subject;
        //this.file = file;
    }
}
