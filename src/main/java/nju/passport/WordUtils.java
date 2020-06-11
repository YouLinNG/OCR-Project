package nju.passport;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created with IDEA
 * author:yanghaolin
 * Date:2020/5/5
 * Time:23:37
 * Description：
 */
public class WordUtils {

    // 声母
    static String[] sm = {"b", "p", "m", "f", "d", "t", "n", "l", "g", "k", "h", "j", "q", "x", "zh", "ch", "sh", "r", "z", "c", "s", "y", "w"};

    // 韵母
    static String[] ym = {"a", "o", "e", "i", "u", "v", "ai", "ei", "ui", "ao", "ou", "iu", "ie", "ve", "er", "an", "en", "in", "un", "vn", "ang", "eng", "ing", "ong"};

    // 整体连读
    static String[] zt = {"zhi", "chi", "shi", "ri", "wu", "yu", "ye", "yue", "yuan", "yin", "yun", "ying","lyu"};

    // 声母韵母
    static String[] smym = {"a", "o", "e", "ai", "ei", "ao", "ou", "er", "an", "en", "ang", "eng"};

    // 声母简拼 声母韵母简拼
    static String[] jp = {"b", "p", "m", "f", "d", "t", "n", "l", "g", "k", "h", "j", "q", "x", "r", "z", "c", "s", "y", "w", "a", "o", "e"};

    /**
     * @Description: 检测 字符串 类型
     * @Param: 【1.纯中文 2.纯拼音全拼 3.纯拼音缩写 4.全拼+简拼 5.中文+拼音 】
     * @return:
     * @Author: wangwensheng@yxj.org.cn
     * @Date: 2020/4/27
     */
    public static int discernWordType(String word) {
        word = word.replaceAll(" ", "");
        // 按数字剔除 不要影响算法，如果放到后面处理会很麻烦
        word = Pattern.compile("[\\d]")
                .matcher(word)
                .replaceAll("");
        // 默认 4
        int wordType = 4;
        if (isCNWord(word)) {
            wordType = 1;
        } else if (isPYWord(word)) {
            Boolean pyqpWord = isPYQPWord(word);
            if (pyqpWord == null) {
                // 这里对 单音节 韵母 可作为全拼 or 简写 ，做特殊处理 再加一层判断
                if (isPYSXWord(word)) {
                    wordType = 3;
                } else {
                    wordType = 4;
                }
            } else if (pyqpWord) {
                wordType = 2;
            } else {
                wordType = 3;
            }
        } else {
            // 这种情况就是 既不是 全中文 也不是全拼音 ，即中拼混合
            wordType = 5;
        }
        return wordType;
    }

    /**
     * @Description: 1.是否全是中文
     */
    private static boolean isCNWord(String word) {
        boolean isCNWord = false;
        Pattern p_str = Pattern.compile("^[0-9\\u4E00-\\u9FA5]+$");
        Matcher m = p_str.matcher(word);
        if (m.find() && m.group(0)
                .equals(word)) {
            isCNWord = true;
        }
        return isCNWord;
    }

    /**
     * @Description: 2.是否全是英文
     */
    private static boolean isPYWord(String word) {
        boolean isPYWord = false;
        Pattern p_str = Pattern.compile("^[a-zA-Z0-9]+$");
        Matcher m = p_str.matcher(word);
        if (m.find() && m.group(0)
                .equals(word)) {
            isPYWord = true;
        }
        return isPYWord;
    }

    /**
     * @Description: 3.是否全是拼音 全拼 (最难判断 考虑性能这里要)
     */
    public static Boolean isPYQPWord(String word) {
        word = word.toLowerCase();
        Boolean isCNWord = false;
        // 所有字符拆开
        String[] split = word.split("");
        // 找声母
        if (split.length < 2) {
            String smymWord = split[0];
            isCNWord = smymFind(smymWord);
            return isCNWord;
        } else {
            // 首次 取最大 5个字符
            isCNWord = recursionFindWordPinYin(split, 0);
        }
        return isCNWord;
    }

    // 递归
    // true是全拼， false不是全拼， null 拼音加简写
    private static Boolean recursionFindWordPinYin(String[] split, int i) {
        Boolean isPinYin = null;
        String wordFind = "";
        int maxLength = 5;
        for (int x = 0; x < maxLength && (x + i) < split.length; x++) {
            wordFind += split[x + i];
        }
        if (split.length < 2) {
            return smymFind(wordFind);
        } else {
            int nextI = lengPinYin(wordFind);
            if (nextI == -1) {
                return false;
            } else if (nextI == wordFind.length() && split.length == (nextI + i)) {
                return true;
            } else {
                return recursionFindWordPinYin(split, nextI + i);
            }
        }
    }

    // 拼音的规格是固定的 1-5位
    // 先排除特殊情况是不是整体连读 整体连读 分为 2，3，4 位 三种类型
    // 取第一个字符 判断是不是 声母 ，如果是 找韵母，找到 即认为是拼音
    // 取第一个字符 判断是不是 声母 ，如果不是 是不是整体认读 如果是 是拼
    private static int lengPinYin(String word) {
        boolean isPy = false;
        String[] split = word.split("");
        int wordLength = split.length;
        // 第一个拼音的 长度 （1-5）
        int tempWordLength = -1;
        // 一、先排除特殊情况是不是整体连读 整体连读 分为 2，3，4 位 三种类型
        if (wordLength > 1) {
            if (!isPy && wordLength > 3) {
                String ztWord4 = split[0] + split[1] + split[2] + split[3];
                isPy = ztFind(ztWord4);
                if (isPy) {
                    tempWordLength = 4;
                }
            }
            if (!isPy && wordLength > 2) {
                String ztWord3 = split[0] + split[1] + split[2];
                isPy = ztFind(ztWord3);
                if (isPy) {
                    tempWordLength = 3;
                }
            }
            if (!isPy && wordLength > 1) {
                String ztWord2 = split[0] + split[1];
                isPy = ztFind(ztWord2);
                if (isPy) {
                    tempWordLength = 2;
                }
            }
        }

        if (!isPy) {
            // 二、不用考虑连读的情况 寻找声母
            // 1.单音节声母 2.双音节声母 3.不是声母
            int type = 3;
            boolean b = false;
            // 先判断双音节
            if (!b && wordLength > 1) {
                // 双音节声母
                String smFind2 = split[0] + split[1];
                b = smFind(smFind2);
                if (b) {
                    type = 2;
                }
            }
            // 判断单音节
            if (!b && wordLength > 0) {
                // 双音节声母
                String smFind1 = split[0];
                b = smFind(smFind1);
                if (b) {
                    type = 1;
                }
            }

            if (type != 3) {
                // 三、找到声母 找韵母
                int indexStart = 1;
                if (type == 2) {
                    // 双音节 声母 往后找一位
                    indexStart += 1;
                }
                // 韵母共有 1,2,3 位 三种
                if (!isPy && wordLength > (indexStart + 2)) {
                    String ymWord3 = split[indexStart] + split[indexStart + 1] + split[indexStart + 2];
                    isPy = ymFind(ymWord3);
                    if (isPy) {
                        tempWordLength = indexStart + 3;
                    }
                }
                if (!isPy && wordLength > (indexStart + 1)) {
                    String ymWord2 = split[indexStart] + split[indexStart + 1];
                    isPy = ymFind(ymWord2);
                    if (isPy) {
                        tempWordLength = indexStart + 2;
                    }
                }
                if (!isPy && wordLength > indexStart) {
                    String ymWord1 = split[indexStart];
                    isPy = ymFind(ymWord1);
                    if (isPy) {
                        tempWordLength = indexStart + 1;
                    }
                }
            } else {
                // 四、声母未找到 直接找韵母
                int indexStart = 0;
                // 韵母共有 1,2,3 位 三种
                if (!isPy && wordLength > (indexStart + 2)) {
                    String smymWord3 = split[indexStart] + split[indexStart + 1] + split[indexStart + 2];
                    isPy = smymFind(smymWord3);
                    if (isPy) {
                        tempWordLength = indexStart + 3;
                    }
                }
                if (!isPy && wordLength > (indexStart + 1)) {
                    String smymWord2 = split[indexStart] + split[indexStart + 1];
                    isPy = smymFind(smymWord2);
                    if (isPy) {
                        tempWordLength = indexStart + 2;
                    }
                }

                // 这里比较特殊 如果是单音节 韵母 可作为简拼 也可作为全拼！！！
                if (!isPy && wordLength > indexStart) {
                    String smymWord1 = split[indexStart];
                    isPy = smymFind(smymWord1);
                    if (isPy) {
                        tempWordLength = indexStart + 1;
                    }
                }
            }
        }
        if (!isPy) {
            tempWordLength = -1;
        }
        return tempWordLength;
    }

    /**
     * @Description: 4.是否全是拼音 缩写
     */
    private static boolean isPYSXWord(String word) {
        boolean isCNWord = false;
        // 每个字符都是 声母简拼 声母韵母简拼
        String[] split = word.split("");
        for (int index = 0; index < split.length; index++) {
            isCNWord = jpFind(split[index]);
            if (!isCNWord) {
                return isCNWord;
            }
        }
        return isCNWord;
    }

    /**
     * @Description: 声母 是否存在
     */
    private static boolean smFind(String word) {
        boolean isFind = false;
        for (int index = 0; index < sm.length; index++) {
            if (word.equals(sm[index])) {
                isFind = true;
                return isFind;
            }
        }
        return isFind;
    }

    /**
     * @Description: 韵母 是否存在
     */
    private static boolean ymFind(String word) {
        boolean isFind = false;
        for (int index = 0; index < ym.length; index++) {
            if (word.equals(ym[index])) {
                isFind = true;
                return isFind;
            }
        }
        return isFind;
    }

    /**
     * @Description: 韵母 是否存在
     */
    private static boolean ztFind(String word) {
        boolean isFind = false;
        for (int index = 0; index < zt.length; index++) {
            if (word.equals(zt[index])) {
                isFind = true;
                return isFind;
            }
        }
        return isFind;
    }

    /**
     * @Description: 声母韵母 是否存在
     */
    private static boolean smymFind(String word) {
        boolean isFind = false;
        for (int index = 0; index < smym.length; index++) {
            if (word.equals(smym[index])) {
                isFind = true;
                return isFind;
            }
        }
        return isFind;
    }

    /**
     * @Description: 声母简拼 声母韵母简拼
     */
    private static boolean jpFind(String word) {
        boolean isFind = false;
        for (int index = 0; index < jp.length; index++) {
            if (word.equals(jp[index])) {
                isFind = true;
                return isFind;
            }
        }
        return isFind;
    }


    public static void main(String[] args) {
        String str = "WANGGENGGEN";
        String str1 = "yangxiuyuam";
        String str2 = "yangxiuy";


        List<String> strings = Arrays.asList(str, str1, str2);

        strings.forEach(word -> {
            // 1.纯中文 2.纯拼音全拼 3.纯拼音缩写 4.全拼+简拼 5.中文+拼音
            long million = System.currentTimeMillis();
            int i = discernWordType(word);
            if(isPYQPWord(word)) System.out.println(word+"符合拼音规则");
            else System.err.println(word+"不符合拼音规则");

        });
    }

}
