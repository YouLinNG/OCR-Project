package nju.passport.dao;

import nju.passport.model.File;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FileDao extends JpaRepository<File,String> {
    /**
     * 通过主键获取一行数据
     * @return
     */
    File getById(Long id);

    File getByMd5(String md5);
}
