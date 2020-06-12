package nju.passport.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * Created with IDEA
 * author:yanghaolin
 * Date:2020/5/19
 * Time:14:20
 * Description：
 */
@Entity(name = "visa")
public class Visa {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private boolean exist;

    private boolean nameExist;

    private boolean birthExist;

    private String imageName;

    public boolean isExist() {
        return exist;
    }

    public void setExist(boolean exist) {
        this.exist = exist;
    }

    public boolean isBirthExist() {
        return birthExist;
    }

    public boolean isNameExist() {
        return nameExist;
    }

    public void setBirthExist(boolean birthExist) {
        this.birthExist = birthExist;
    }

    public void setNameExist(boolean nameExist) {
        this.nameExist = nameExist;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }
}
