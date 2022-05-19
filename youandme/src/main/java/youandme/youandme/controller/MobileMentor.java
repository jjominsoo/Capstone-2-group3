package youandme.youandme.controller;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class MobileMentor {
    private Long index;

    private String ID;
    private String password;
    private String name;
    private String school;
    private String grade;
    private String subject;
    private String profileFilePath;
    private String graduationFilePath;
    private String companyFilePath;
}
