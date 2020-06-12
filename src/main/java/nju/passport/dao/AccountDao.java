package nju.passport.dao;

import nju.passport.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Created with IDEA
 * author:yanghaolin
 * Date:2020/6/12
 * Time:16:43
 * Description：
 */
@Repository
public interface AccountDao extends JpaRepository<Account, String> {

    Account findByAccount(String account);
}
