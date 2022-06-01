package youandme.youandme.service;

import org.springframework.data.jpa.repository.Modifying;
import youandme.youandme.domain.Like;
import youandme.youandme.domain.Mentee;
import youandme.youandme.repository.LikeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class LikeService {


    private final LikeRepository likeRepository;

    @Transactional
    public void save(Like like){
//        validateDuplicateLike(like);
        likeRepository.save(like);
    }

    @Transactional
    public List<Like> findLiked(Long mentee_index){

        return likeRepository.findLiked(mentee_index);
    }


//    @Transactional
//    public List<Like> unliked(Long mentee_index, Long mentor_index){
//        return likeRepository.unliked(mentee_index,mentor_index);
//    }


    public List<Like> findUnliked(Long mentee_id, Long mentor_id){
        return likeRepository.findUnliked(mentee_id, mentor_id);
    }

    @Transactional
    public void unliked(Like unlike){
        likeRepository.unliked(unlike);
    }


//    private void validateDuplicateLike(Like like) {
//        List<Like> findLikes = likeRepository.findLiked(like.getMentee_index());
//        System.out.println("ss = " + findLikes.get(0).getMentor_index() );
//        if(findLikes.get(0).getMentor_index() ==  like.getMentor_index()){
//            throw new IllegalStateException("Already Liked!");
//        }
//    } 좋아요 중복으로 누르면 그대로 저장됨 (전부)

}
