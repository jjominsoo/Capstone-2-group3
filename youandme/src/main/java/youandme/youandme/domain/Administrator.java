package youandme.youandme.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Getter
@Setter
public class Administrator {

    @Id
    @GeneratedValue
    private int admin_index;

    private String ID;
    private String password;
    //멘토 로그인
}