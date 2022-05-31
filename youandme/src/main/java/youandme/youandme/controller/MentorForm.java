package youandme.youandme.controller;


import lombok.Getter;
import lombok.Setter;
import javax.validation.constraints.NotEmpty;
import java.util.List;


@Getter @Setter
public class MentorForm {

    @NotEmpty(message = "회원 아이디는 필수입니다")
    private String ID;

    private String password;
    private String Name;
    private String school;
    private Float grade;
    private String subject;
    private String company;

    private String profileName;
    private String profilePath;

    private String graduationFileName;
    private String graduationFilePath;

    private String companyFileName;
    private String companyFilePath;

    private boolean status;
    private boolean pass;

    private String shortIntroduce;
    private String longIntroduce;

}
