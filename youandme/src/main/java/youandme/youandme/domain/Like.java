package youandme.youandme.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "likes")
@Getter @Setter
public class Like {

    @Id @GeneratedValue
    private Long like_num;

    private Long mentee_index;

    private Long mentor_index;

//    @OneToOne
//    @JoinColumn(name ="likeList_index")
//    private LikeList likeList;
}
