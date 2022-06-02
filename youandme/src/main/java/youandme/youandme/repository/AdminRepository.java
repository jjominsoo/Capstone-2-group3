package youandme.youandme.repository;

import youandme.youandme.domain.Administrator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class AdminRepository {

    private final EntityManager em;

    public void save(Administrator admin){
        em.persist(admin);
    }

    public Administrator findID(int admin_index){
        return em.find(Administrator.class, admin_index);
    }
}

