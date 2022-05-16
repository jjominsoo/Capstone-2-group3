package youandme.youandme.service;

import youandme.youandme.domain.Member;
import youandme.youandme.domain.item.Book;
import youandme.youandme.domain.item.Item;
import youandme.youandme.exception.NotEnoughStockException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class OrderServiceTest {

    @Autowired EntityManager em;
    @Autowired OrderService orderService;
    @Autowired
    OrderRepository orderRepository;

    @Test
    public void 상품주문() throws Exception{
        //given
        Member member = createMember();

        Item book = createBook("HelloWorld JPA", 10000, 10);
        int orderCount = 2;

        //when
        Long orderId = orderService.order(member.getId(), book.getId(), orderCount);
        
        //then
        Order getOrder = orderRepository.findOne(orderId);

        assertEquals("상품 주문시 상태는 ORDER", OrderStatus.ORDER, getOrder.getStatus());
        assertEquals("주문한 상품 종류 수가 정확해야함",1,getOrder.getOrderItems().size());
        assertEquals("주문가격은 수량 곱하기 가격",10000*orderCount,getOrder.getTotalPrice());
        assertEquals("주문수량만큼 재고 줄어야한다.",8,book.getStockQuantity());
    }


    @Test
    public void 주문취소() throws Exception{
        //given
        Member member = createMember();
        Item book = createBook("HelloWorld JPA", 10000, 10);
        int orderCount = 2;
        Long orderId = orderService.order(member.getId(), book.getId(), orderCount);
        //when
        orderService.cancelOrder(orderId);
        //then
        Order getOrder = orderRepository.findOne(orderId);

        assertEquals("상태는 CANCEL",OrderStatus.CANCEL,getOrder.getStatus());
        assertEquals("재고 다시 증가",10,book.getStockQuantity());
    }

    @Test(expected = NotEnoughStockException.class)
    public void 재고수량초과() throws Exception{
        //given
        Member member = createMember();
        Item book = createBook("HelloWorld JPA", 10000, 10);
        int orderCount = 11;
        //when
        orderService.order(member.getId(), book.getId(), orderCount);
        //then
        fail("재고수량부족 예외 발생해야함");
    }

    private Item createBook(String name, int price, int stockQuantity) {
        Item book = new Book();
        book.setName(name);
        book.setPrice(price);
        book.setStockQuantity(stockQuantity);
        em.persist(book);
        return book;
    }

    private Member createMember() {
        Member member = new Member();
        member.setName("number1");
        member.setAddress(new Address("Gyeonggido", "Yongsuri", "67-40"));
        em.persist(member);
        return member;
    }

}