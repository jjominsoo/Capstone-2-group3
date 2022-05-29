package youandme.youandme.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
public class Mentee {
    
    @Id @GeneratedValue
    @Column(name = "mentee_id")
    private Long index;

    private String ID;
    private String password;
    private String name;
    private String school;
    private Float grade;
    private String subject;

    @Embedded
    private Profiles profiles;

//    private String introduce;
}
