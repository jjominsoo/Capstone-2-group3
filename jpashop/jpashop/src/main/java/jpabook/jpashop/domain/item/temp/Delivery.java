package jpabook.jpashop.domain.item.temp;

import jpabook.jpashop.domain.zzBasicInfo;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
public class Delivery {
    @Id
    @GeneratedValue
    @Column(name = "delivery_id")
    private Long id;

    @OneToOne(mappedBy = "delivery", fetch = FetchType.LAZY)
    private Order order;

    @Embedded
    private Address address;

    @Embedded
    private zzBasicInfo basicInfo;

    @Enumerated(EnumType.STRING)
    private DeliveryStatus status;
}
