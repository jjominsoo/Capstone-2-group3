package youandme.youandme.controller;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MobileMenteeJoinForm {
    private String name;
    private String school;
    private Float grade;
    private String subject;
    private String profileFilePath;
    private boolean status;

    //모바일로 보내는 멘티 폼
}
