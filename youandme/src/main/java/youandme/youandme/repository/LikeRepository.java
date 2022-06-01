package youandme.youandme.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import youandme.youandme.domain.Like;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class LikeRepository {

    private final EntityManager em;

    public void save(Like like){
        em.persist(like);
    }

    public List<Like> findLike(Long mentee_index){
        return em.createQuery("select m from Like m where m.mentee_index = :mentee_index", Like.class)
                .setParameter("mentee_index", mentee_index)
                .getResultList();
    }


    public List<Like> findLiked(Long mentor_index){
        return em.createQuery("select m from Like m where m.mentor_index = :mentor_index", Like.class)
                .setParameter("mentor_index", mentor_index)
                .getResultList();
    }

    public List<Like> findUnliked(Long mentee_index, Long mentor_index){
        return em.createQuery("select m from Like m where m.mentee_index = :mentee_index and m.mentor_index = :mentor_index", Like.class)
                .setParameter("mentee_index", mentee_index)
                .setParameter("mentor_index",mentor_index)
                .getResultList();

    }

//    public List<Like> unliked(Long mentee_index, Long mentor_index){
//        return em.createQuery("delete from Like m where m.mentee_index = :mentee_index and m.mentor_index = :mentor_index",Like.class)
//                .setParameter("mentee_index", mentee_index)
//                .setParameter("mentor_index",mentor_index)
//                .getResultList();
//    }

    public void unliked(Like unlike){
        em.remove(unlike);
    }
}
