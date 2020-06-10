package nju.passport;

public class PassnumUtils {
    public static boolean judgePassport(String number) {

        char[] chars = number.toCharArray();
        if(chars[0] == 'E'){
            if(!Character.isDigit(chars[1])){
                for(int i = 2;i < 9;i++){
                    if(!Character.isDigit(chars[i])){
                        return false;
                    }
                }
            }
            return true;
        }
        if (chars[0] == 'E' || chars[0] == 'G') {
            for (int i = 1; i < 9; i++) {
                if (!Character.isDigit(chars[i])) {
                    return false;
                }
            }
            return true;
        }
        if (chars[0] == 'H') {
            if (chars[1] == 'J') {
                for (int i = 2; i < 8; i++) {
                    if (!Character.isDigit(chars[i])) {
                        System.err.println("护照号："+number+" 格式不符");
                        return false;
                    }
                }
                return true;
            } else {
                for (int i = 1; i < 8; i++) {
                    if (!Character.isDigit(chars[i])) {
                        System.err.println("护照号："+number+" 格式不符");
                        return false;
                    }
                }
                return true;
            }
        }
        if (chars[0] == 'M' && (chars[1] == 'B' || chars[1] == 'A')) {
            for (int i = 2; i < 8; i++) {
                if (!Character.isDigit(chars[i])) {
                    System.err.println("护照号："+number+" 格式不符");
                    return false;
                }
            }
            return true;
        }
        System.out.println("护照号："+number+" 格式正确");
        return false;
    }

    public static void main(String[] args) {
        judgePassport("E17686872");
        judgePassport("ED7686872");
        judgePassport("H17686872");
        judgePassport("HJ7686872");
        judgePassport("HA7686872");
    }
}
