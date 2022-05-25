package youandme.youandme.controller;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MobileMenteeJoinForm {
    private String name;
    private String school;
    private String grade;
    private String subject;
    private String profileFilePath;
    private boolean status;
}
