package cloud.util;

import com.alibaba.excel.EasyExcel;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.*;

/**
 * @author ：LLH
 * @date ：Created in 2022/3/17 18:57
 * @description：处理输出数据
 */
public class HandleTransOutputData {
    static int [][][] resourceType = new int[11][10][8];

    static int [][] averageResourceType = new int[11][8];
    public int handleOutputData(int taskNum, int ratio, int rapid, int taskSize, int resourceSize) {
        TreeMap<String, List<int[]>> resourceMaps = new TreeMap<>(new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                int i1 = Integer.parseInt(o1.substring(8));
                int i2 = Integer.parseInt(o2.substring(8));
                return i1-i2;
            }
        });

        int maxNum = 0;

        try { // 防止文件建立或读取失败，用catch捕捉错误并打印，也可以throw

            /* 读入TXT文件 */
            String pathname ="D:\\Coding\\JavaProject\\multi-agent-trans-v1\\data\\output\\output" + resourceSize + "-" + (taskSize*taskNum) +"-" + ratio  +"-" + rapid +".txt"; // 绝对路径或相对路径都可以，这里是绝对路径，写入文件时演示相对路径
            File filename = new File(pathname); // 要读取以上路径的input。txt文件
            InputStreamReader reader = new InputStreamReader(
                    new FileInputStream(filename)); // 建立一个输入流对象reader
            BufferedReader br = new BufferedReader(reader); // 建立一个对象，它把文件内容转成计算机能读懂的语言
            String line = "";
//            line = br.readLine();
            String tag = "";
            Set<String> set = new HashSet<>();
            do {
                line = br.readLine(); // 一次读入一行数据
                if (line != null) {
                    String [] strs = line.split(" ");
                    if (tag.equals(strs[2])) {
                        set.add(strs[5]);
                    } else {
                        if (!set.isEmpty()) {
                            resourceType[ratio][rapid-1][set.size()-1]++;
                        }
                        set.clear();
                        set.add(strs[5]);
                    }
                    tag = strs[2];

//                    maxNum = Math.max(maxNum, Integer.parseInt(strs[4]));
//                    if (!resourceMaps.containsKey(strs[5])) {
//                        List<int[]> timeGaps = new ArrayList<>();
//                        timeGaps.add(new int[]{Integer.parseInt(strs[3]), Integer.parseInt(strs[4])});
//                        resourceMaps.put(strs[5], timeGaps);
//                    } else {
//                        List<int[]> timeGaps = resourceMaps.get(strs[5]);
//                        timeGaps.add(new int[]{Integer.parseInt(strs[3]), Integer.parseInt(strs[4])});
//                    }
                    System.out.println(line);
                }
            } while ( line != null);
            resourceType[ratio][rapid-1][set.size()-1]++;
            set.clear();
        }catch (Exception e){
            e.printStackTrace();
        }

        for (Map.Entry<String, List<int[]>> entry : resourceMaps.entrySet()) {
            List<int[]> timeGaps = entry.getValue();
            Collections.sort(timeGaps, new Comparator<int[]>() {
                @Override
                public int compare(int[] o1, int[] o2) {
                    return o1[0]-o2[0];
                }
            });

//            System.out.print(entry.getKey() + "   ");
//            for (int[] timeGap :timeGaps) {
//                System.out.print(timeGap[0] + " " +timeGap[1] +"    ");
//            }
//            System.out.println();
        }
//        System.out.println(maxNum);
        return maxNum;
    }

    private void getAverageResourceType() {
        for (int i=0; i<11; i++) {
            int [] aveType = new int[8];
            for (int j=0; j<10; j++) {
                for (int k=0; k<8; k++) {
                    aveType[k] += resourceType[i][j][k];
                }
            }
            for (int p=0; p<8; p++) {
                averageResourceType[i][p] = aveType[p]/10;
            }
        }
    }

    public static void main(String[] args) {
        HandleTransOutputData handleOutputData = new HandleTransOutputData();

        Map<Integer, List<Integer>>  maxNumMaps =  new HashMap<>();

        List<Integer> averageList = new ArrayList<>();

        for(int taskNum=5; taskNum<=5; taskNum++) {
            if (!maxNumMaps.containsKey(taskNum)) {
                maxNumMaps.put(taskNum, new ArrayList<>());
            }
            int total = 0;
            for (int ratio=0; ratio<=10; ratio++) {
                for (int rapid = 1; rapid <= 10; rapid++) {
                    int taskSize = 20;
                    int resourceSize = 20;
                    int maxNum = handleOutputData.handleOutputData(taskNum, ratio, rapid, taskSize, resourceSize);
                    total += maxNum;
                    List<Integer> maxNumList = maxNumMaps.get(taskNum);
                    maxNumList.add(maxNum);
                }
            }
            averageList.add(total/10);
        }

        for (int i=0; i<11; i++) {
            for (int j=0; j<10; j++) {
                for (int k=0; k<8; k++) {
                    System.out.print(resourceType[i][j][k] + " ");
                }
                System.out.println();
            }
        }
        handleOutputData.getAverageResourceType();

        System.out.println("average");
        for (int i=0; i<11; i++) {
            for (int p=0; p<8; p++) {
                System.out.print(averageResourceType[i][p] + " ");
            }
            System.out.println();
        }

        List<List<Integer>> res = new ArrayList<>();
        for (int i = 0; i < 11; i++) {
            StringBuffer sb = new StringBuffer();
            res.add(new ArrayList<>());
            for (int j = 0; j < 8; j++) {
                int w = averageResourceType[i][j];
                while (w-- > 0) {
                    res.get(i).add(j);
                    sb.append(j);
                    sb.append(',');
                }
            }
            System.out.println(sb.toString());
        }
        res.get(2).add(7);
        res.get(9).add(4);
        res.get(10).add(4);
        res.get(10).add(4);
//        res.get.remove(100);
        System.out.println("res");

        List<List<Object>> list = new ArrayList<>();
//        for (int i = 0; i < 10; i++) {
//            List<Object> data = new ArrayList<>();
//            data.add("张三");
//            data.add(25);
//            data.add(new Date());
//            list.add(data);
//        }
//        return list;

        for (int j = 0; j < res.get(3).size(); j++) {
            List<Object> data = new ArrayList<>();
            data.add(j);
            for (int i = 0; i < 11; i++) {
                data.add(res.get(i).get(j));
                System.out.print(res.get(i).get(j) + " ");
            }
            list.add(data);
            System.out.println();
        }
        String fileName = "D:\\Download\\Chrome\\test.csv";
        EasyExcel.write(fileName).head(head()).sheet("模板").doWrite(list);
//        StringBuffer sb = new StringBuffer();
//
//        for (Map.Entry<Integer, List<Integer>> entry: maxNumMaps.entrySet()) {
//            System.out.print(entry.getKey() + " ");
//            for (int val : entry.getValue()) {
//                System.out.print(val + " ");
//                sb.append(val);
//                sb.append(",");
//            }
//            System.out.println();
//        }
//        System.out.println(sb.toString());
//
//        StringBuffer newSb = new StringBuffer();
//        for (int i=0; i<averageList.size(); i++) {
//            newSb.append(averageList.get(i));
//            newSb.append(',');
//        }
//        System.out.println(newSb.toString());

    }
    private static List<List<String>>  head() {
        List<List<String>> list = new ArrayList<>();
        List<String> head0 = new ArrayList<>();
        head0.add("0");
        list.add(head0);
        return list;
    }

}
