package youandme.youandme.form;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MobileMenteeJoinForm {

    private Long index;
    private String ID;
    private String name;
    private String school;
    private Float grade;
    private String subject;
    private String profileFilePath;
    private boolean status;
    private String text;
    //모바일로 보내는 멘티 폼
}
