package nju.passport.dao;

import nju.passport.model.Photo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * Created with IDEA
 * author:yanghaolin
 * Date:2020/5/19
 * Time:13:17
 * Description：
 */
@Repository
public interface PhotoDao extends JpaRepository<Photo,String> {

    List<Photo> findByInsertDateBetween(Date from, Date to);

}
