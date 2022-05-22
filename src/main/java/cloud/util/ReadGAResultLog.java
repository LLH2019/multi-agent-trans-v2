package cloud.util;

import scala.Int;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * @author ：LLH
 * @date ：Created in 2022/3/19 8:53
 * @description：读取日志
 */
public class ReadGAResultLog {
    static int totalEValue= 183777;
    static int improvedCNP = 151542;
    static int freeTimeCNP = 151395;
    public void readResultLog() {
        List<Integer> fitnessValues = new ArrayList<>();
        try { // 防止文件建立或读取失败，用catch捕捉错误并打印，也可以throw

            /* 读入TXT文件 */
            String pathname = "C:\\Users\\94076\\Desktop\\result3.txt"; // 绝对路径或相对路径都可以，这里是绝对路径，写入文件时演示相对路径
            File filename = new File(pathname); // 要读取以上路径的input。txt文件
            InputStreamReader reader = new InputStreamReader(
                    new FileInputStream(filename)); // 建立一个输入流对象reader
            BufferedReader br = new BufferedReader(reader); // 建立一个对象，它把文件内容转成计算机能读懂的语言
            String line = "";
//            line = br.readLine();
            do {
                line = br.readLine(); // 一次读入一行数据
                if (line != null) {
                    String[] strs = line.split(" ");
                    fitnessValues.add(Integer.parseInt(strs[1]));

                }
            } while (line != null);
        } catch (Exception e) {
            e.printStackTrace();
        }

        StringBuffer numSb = new StringBuffer();
        for (int i=1; i<=100; i++) {
            numSb.append(i);
            numSb.append(',');
        }
        System.out.println(numSb.toString());

        StringBuffer sb = new StringBuffer();
        for (int value: fitnessValues) {
            sb.append(totalEValue-value);
            sb.append(',');
        }
        System.out.println(sb.toString());

        StringBuffer sbC = new StringBuffer();
        for (int i=0;i<100; i++) {
            sbC.append(improvedCNP);
            sbC.append(",");
        }
        System.out.println(sbC.toString());

        StringBuffer sbD = new StringBuffer();
        for (int i=0;i<100; i++) {
            sbD.append(freeTimeCNP);
            sbD.append(",");
        }
        System.out.println(sbD.toString());

    }

    public static void main(String[] args) {
        ReadGAResultLog readLog = new ReadGAResultLog();
        readLog.readResultLog();
    }

}
