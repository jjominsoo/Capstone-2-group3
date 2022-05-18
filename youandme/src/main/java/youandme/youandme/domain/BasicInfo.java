package youandme.youandme.domain;


import lombok.Getter;

import javax.persistence.Embeddable;

@Embeddable
@Getter
public class BasicInfo {
    private String ID;
    private String password;
    private String school;
    private String grade;
    private String subject;

    protected BasicInfo(){
    }

    public BasicInfo(String ID, String password, String school, String grade, String subject){
        this.ID = ID;
        this.password = password;
        this.school = school;
        this.grade = grade;
        this.subject = subject;
    }
}
