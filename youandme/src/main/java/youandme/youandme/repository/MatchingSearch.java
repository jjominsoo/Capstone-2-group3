package youandme.youandme.repository;

import youandme.youandme.domain.item.temp.OrderStatus;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class MatchingSearch {
    private String memberName;// 회원이름
    private OrderStatus orderStatus; //ORDER / CANCEL
}
