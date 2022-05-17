package youandme.youandme.controller;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class MobileMember {
    private Long ID;
    private String name;
    private String school;
    private String grade;
    private String subject;
    private String profileFilePath;
    private String schoolFilePath;
    private String companyFilePath;
}
