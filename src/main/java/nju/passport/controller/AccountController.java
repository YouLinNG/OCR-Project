package nju.passport.controller;

import nju.passport.dao.AccountDao;
import nju.passport.model.Account;
import nju.passport.model.Photo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

/**
 * Created with IDEA
 * author:yanghaolin
 * Date:2020/6/12
 * Time:16:30
 * Description：
 */

@RestController
@RequestMapping("/Account")
@CrossOrigin
public class AccountController {

    @Autowired
    private AccountDao accountDao;

    @RequestMapping(value = "/Create")

    public boolean create(@RequestParam(value = "username") String name,@RequestParam(value = "password")String password) throws IOException {


        Account account = new Account();
        account.setAccount(name);
        account.setPassnum(password);
        accountDao.save(account);

        return true;

    }

    @RequestMapping(value = "/Login")

    public boolean login(@RequestParam(value = "username") String name,@RequestParam(value = "password")String password) throws IOException {

        Account account = accountDao.findByAccount(name);
        if(account == null ) {
            return false;
        }else{
            if(account.getPassnum().equals(password)){
                return true;
            }
        }

        return false;

    }

}
