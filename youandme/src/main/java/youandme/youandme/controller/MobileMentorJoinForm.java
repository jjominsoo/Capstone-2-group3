package youandme.youandme.controller;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class MobileMentorJoinForm {
    private String name;
    private String school;
    private Float grade;
    private String subject;
    private String company;
    private String profileFilePath;
    private boolean status;
    private boolean pass;

    //모바일로 보내는 멘토 폼
}
