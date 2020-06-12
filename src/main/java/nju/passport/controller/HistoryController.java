package nju.passport.controller;

import nju.passport.dao.PhotoDao;
import nju.passport.model.Account;
import nju.passport.model.Photo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Date;
import java.util.List;

/**
 * Created with IDEA
 * author:yanghaolin
 * Date:2020/6/12
 * Time:18:34
 * Description：
 */
@RestController
@RequestMapping("/History")
@CrossOrigin
public class HistoryController {

    @Autowired
    private PhotoDao photoDao;
    @RequestMapping(value = "/")
    public List<Photo> getHistory(@RequestParam(value = "from") Date from, @RequestParam(value = "to")Date to) throws IOException {

        List<Photo> res = photoDao.findByInsertDateBetween(from,to);


        return res;

    }
}
