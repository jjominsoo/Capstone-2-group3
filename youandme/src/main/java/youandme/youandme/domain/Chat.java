package youandme.youandme.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Getter @Setter
public class Chat {
    @Id @GeneratedValue
    private Long chat_num;

    private Long sender_index;
    private Long receiver_index;
    private String text;
    private LocalDateTime date;
//    private String introduce;
}
