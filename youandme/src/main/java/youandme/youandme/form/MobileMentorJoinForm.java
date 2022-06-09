package youandme.youandme.form;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class MobileMentorJoinForm {
    private Long index;
    private String ID;
    private String name;
    private String school;
    private Float grade;
    private String subject;
    private String company;
    private String profileFilePath;
    private boolean status;
    private boolean pass;
    private String shortIntroduce;
    private String longIntroduce;
    private String text;
    //모바일로 보내는 멘토 폼
}
