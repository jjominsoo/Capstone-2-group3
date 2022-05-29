package youandme.youandme.repository;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import youandme.youandme.domain.Chat;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class ChatRepository {
    private final EntityManager em;

    public void save(Chat chat){
        em.persist(chat);
    }

    public List<Chat> findSender(Long sender_index){
        return em.createQuery("select m from Chat m where m.sender_index = :sender_index", Chat.class)
                .setParameter("sender_index", sender_index)
                .getResultList();

    }

    public List<Chat> findReceiver(Long receiver_index){
        return em.createQuery("select m from Chat m where m.receiver_index = :receiver_index", Chat.class)
                .setParameter("receiver_index", receiver_index)
                .getResultList();

    }
}
