package youandme.youandme.controller;


import lombok.Getter;
import lombok.Setter;
import javax.validation.constraints.NotEmpty;


@Getter @Setter
public class MeteeForm {

    @NotEmpty(message = "회원 아이디는 필수입니다")
    private String ID;

    private String password;
    private String Name;
    private String school;
    private String grade;
    private String subject;

    private String profileName;
    private String profilePath;

}
