package nju.passport.model;

/**
 * Created with IDEA
 * author:yanghaolin
 * Date:2020/5/19
 * Time:14:20
 * Description：
 */
public class Visa {

    private boolean exist;

    private String name;

    private String sex;

    private String birth;

    private String passnum;

    public boolean isExist() {
        return exist;
    }

    public void setExist(boolean exist) {
        this.exist = exist;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getBirth() {
        return birth;
    }

    public void setBirth(String birth) {
        this.birth = birth;
    }

    public String getPassnum() {
        return passnum;
    }

    public void setPassnum(String passnum) {
        this.passnum = passnum;
    }
}
