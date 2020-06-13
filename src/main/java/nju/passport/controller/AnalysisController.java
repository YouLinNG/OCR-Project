package nju.passport.controller;

import nju.passport.dao.PhotoDao;
import nju.passport.dao.VisaDao;
import nju.passport.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.*;

/**
 * Created with IDEA
 * author:yanghaolin
 * Date:2020/6/12
 * Time:22:29
 * Description：
 */
@RestController
@RequestMapping("/Analysis")
@CrossOrigin
public class AnalysisController {

    @Autowired
    private PhotoDao photoDao;

    @Autowired
    private VisaDao visaDao;

    @RequestMapping(value = "/MonthPhoto")
    public List<MonthPhoto> getMonthPhoto() throws IOException {
        List<MonthPhoto> res = new ArrayList<>();
        for (int i = 1; i < 13; i++) {
            MonthPhoto monthPhoto = new MonthPhoto();
            monthPhoto.setMonth(i + "月");
            monthPhoto.setNum(0);
            res.add(monthPhoto);
        }
        List<Photo> photos = photoDao.findAll();

        for (Photo photo : photos) {
            Date insertDate = photo.getInsertDate();

            res.get(insertDate.getMonth()).setNum(res.get(insertDate.getMonth()).getNum() + 1);
        }

        return res;

    }

//    @RequestMapping(value = "/MonthVisa")
//    public List<MonthVisa> getMonthVisa() throws IOException {
//        List<MonthVisa> res = new ArrayList<>();
//
//
//        List<Visa> res = visaDao.findAll();
//
//        return res;
//
//    }


    @RequestMapping(value = "/Year")
    public List<Year> getLastVisaWeek() throws IOException {

        List<Year> res = new ArrayList<>();

        Map<String, Integer> temp = new HashMap<>();
        List<Photo> photos = photoDao.findAll();
        for (Photo photo : photos) {
            String birth = photo.getBirth();
            String year = "";
            if(birth != null) year = birth.split("/")[2];
            else continue;
            try {
                int yearInt = Integer.parseInt(year);
                if (yearInt < 1950) {
                    temp.put("1950前", temp.getOrDefault("1950前", 0) + 1);
                }
                if (yearInt >= 1960 && yearInt <= 1969) {
                    temp.put("1960-1969", temp.getOrDefault("1960-1969", 0) + 1);
                }
                if (yearInt >= 1970 && yearInt <= 1979) {
                    temp.put("1970-1979", temp.getOrDefault("1970-1979", 0) + 1);
                }
                if (yearInt >= 1980 && yearInt <= 1989) {
                    temp.put("1980-1989", temp.getOrDefault("1980-1989", 0) + 1);
                }
                if (yearInt >= 1990 && yearInt <= 1999) {
                    temp.put("1990-1999", temp.getOrDefault("1990-1999", 0) + 1);
                }
                if (yearInt >= 2000) {
                    temp.put("2000后", temp.getOrDefault("2000后", 0) + 1);
                }
            } catch (NumberFormatException e) {
                continue;
            }

        }

        Year year1 = new Year();
        year1.setItem("1950前");
        year1.setCount(temp.getOrDefault("1950前",0));res.add(year1);
        Year year2 = new Year();
        year2.setItem("1960-1969");
        year2.setCount(temp.getOrDefault("1960-1969",0));res.add(year2);
        Year year3 = new Year();
        year3.setItem("1970-1979");
        year3.setCount(temp.getOrDefault("1970-1979",0));res.add(year3);
        Year year4 = new Year();
        year4.setItem("1980-1989");
        year4.setCount(temp.getOrDefault("1980-1989",0));res.add(year4);
        Year year5 = new Year();
        year5.setItem("1990-1999");
        year5.setCount(temp.getOrDefault("1990-1999",0));res.add(year5);
        Year year6 = new Year();
        year6.setItem("2000后");
        year6.setCount(temp.getOrDefault("2000后",0));res.add(year6);

        return res;

    }
}
