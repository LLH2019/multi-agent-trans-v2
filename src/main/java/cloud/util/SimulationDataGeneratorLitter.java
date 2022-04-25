package cloud.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @author ：LLH
 * @date ：Created in 2022/3/16 15:08
 * @description：仿真数据生成
 */
public class SimulationDataGeneratorLitter {
//    private int taskSize =10;
//    private int resourceSize = 20;

    public List<List<int[]>> generateData(int taskSize, int resourceSize) {
        List<List<int[]>> tasks = new ArrayList<>();
        Random random = new Random();

        for (int i=0; i<taskSize; i++) {
            List<int[]> curTask = new ArrayList<>();
//            int processNum = random.nextInt(5)+3;
            int processNum = 8;
            for (int j=0; j<processNum; j++) {
                // processTime 数据0-resourceSize为处理时间， resourceSize-2*resourceSize为加工成本情况
                int[] processTime = new int[resourceSize*2];
                for (int k = 0; k < resourceSize; k++) {
                    processTime[k] = random.nextInt(10) + 195;
                    int randomN = random.nextInt(10);
                    if (randomN <= 1) {
                        processTime[j] = 9999;
                    }
                }

                for (int k=resourceSize; k<2*resourceSize; k++) {
                    processTime[k] = random.nextInt(20)+10;
                }

                curTask.add(processTime);
            }
            tasks.add(curTask);
        }

//        for (int k=0; k<total*0.1; k++) {
//            int i = random.nextInt(100);
//            int j = random.nextInt(10);
//
//            tasks.get(i)[j%tasks.get(i).length] = 9999;
//        }
//
//        int [][] resourceDis = new int[resourceSize][resourceSize];
//        for(int i=0; i<resourceSize; i++) {
//            for (int j=0; j<i; j++) {
//                resourceDis[i][j] = random.nextInt(10)+5;
////                if (i<=4 || (i<=9 && j>=5) || (i<=14 && j>=10) || (i<=19 && j>=15)) {
////                    resourceDis[i][j] = random.nextInt(3);
////                } else {
////                    resourceDis[i][j] = random.nextInt(3)+3;
////                }
//            }
//        }
//        for (int i=0; i<resourceSize; i++) {
//            for (int j=i+1; j<resourceSize; j++) {
//                resourceDis[i][j] = resourceDis[j][i];
//            }
//        }
//
//        printData(tasks,resourceSize);
//        for (int i=0; i<resourceSize; i++) {
//            for (int j=0; j<resourceSize; j++) {
//                System.out.print(resourceDis[i][j] + " ");
//            }
//            System.out.println();
//        }

        return tasks;
    }

    private int[][] generateResourceDis(int resourceSize, int ratio) {
        Random random = new Random();
        int [][] resourceDis = new int[resourceSize][resourceSize];
        for(int i=0; i<resourceSize; i++) {
            for (int j=0; j<i; j++) {
//                if (ratio == 0) {
//                    resourceDis[i][j] = 0;
//                } else {
                    resourceDis[i][j] = ratio;
//                }
//                if (i<=4 || (i<=9 && j>=5) || (i<=14 && j>=10) || (i<=19 && j>=15)) {
//                    resourceDis[i][j] = random.nextInt(3);
//                } else {
//                    resourceDis[i][j] = random.nextInt(3)+3;
//                }
            }
        }
        for (int i=0; i<resourceSize; i++) {
            for (int j=i+1; j<resourceSize; j++) {
                resourceDis[i][j] = resourceDis[j][i];
            }
        }
        return resourceDis;
    }

    private void printData(List<List<int[]>> tasks, int resourceSize) {
        for (int i=0; i<tasks.size(); i++) {
            System.out.println( i + "  " + tasks.get(i).size());
            for (int j=0; j<tasks.get(i).size(); j++) {
                for (int k=0; k<2*resourceSize; k++) {
                    System.out.print(tasks.get(i).get(j)[k] + " ");
                }
                System.out.println();
            }

        }
    }


    public static void main(String[] args) {

        SimulationDataGeneratorLitter simulationDataGenerator = new SimulationDataGeneratorLitter();

        for(int taskNum=1; taskNum<=10; taskNum++) {
            for(int ratio=0; ratio<=20; ratio++) {
                for (int rapid = 1; rapid <= 10; rapid++) {

                    int taskSize = 20;
                    int resourceSize = 20;

                    List<List<int[]>> datas = simulationDataGenerator.generateData(taskSize * taskNum, resourceSize);
                    int[][] resourceDis = simulationDataGenerator.generateResourceDis(resourceSize, ratio);
                    try {
                        File writeName = new File("D:\\Coding\\JavaProject\\multi-agent-trans-v1\\data\\trans-litter\\write-with-cost-and-trans" + resourceSize + "-" + (taskSize * taskNum) + "-" + ratio + "-" + rapid + ".txt"); // 相对路径，如果没有则要建立一个新的output.txt文件
                        if (!writeName.exists()) {
                            writeName.createNewFile(); // 创建新文件,有同名的文件的话直接覆盖
                        }
                        BufferedWriter out = new BufferedWriter(new FileWriter(writeName));
                        for (int i = 0; i < datas.size(); i++) {
                            out.write(String.valueOf(datas.get(i).size()));
                            out.newLine();
                            for (int j = 0; j < datas.get(i).size(); j++) {
                                StringBuffer sb = new StringBuffer();
                                for (int k = 0; k < datas.get(i).get(j).length; k++) {
                                    sb.append(datas.get(i).get(j)[k]);
                                    sb.append(',');
                                }

                                sb.deleteCharAt(sb.length() - 1);
                                out.write(sb.toString());
                                out.newLine();
                            }
                        }

                        for (int i = 0; i < resourceDis.length; i++) {
                            StringBuffer sb = new StringBuffer();
                            for (int j = 0; j < resourceDis[i].length; j++) {
                                sb.append(resourceDis[i][j]);
                                sb.append(',');
                            }
                            sb.deleteCharAt(sb.length() - 1);
                            out.write(sb.toString());
                            out.newLine();
                        }

                        out.flush(); // 把缓存区内容压入文件
                        out.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

}
