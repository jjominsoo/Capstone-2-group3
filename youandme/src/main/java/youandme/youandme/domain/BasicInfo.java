package youandme.youandme.domain;


import lombok.Getter;

import javax.persistence.Embeddable;

@Embeddable
@Getter
public class BasicInfo {

    private String password;
    private String school;
    private String grade;
    private String subject;

    protected BasicInfo(){
    }

    public BasicInfo(String password, String school, String grade, String subject){
        this.password = password;
        this.school = school;
        this.grade = grade;
        this.subject = subject;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
