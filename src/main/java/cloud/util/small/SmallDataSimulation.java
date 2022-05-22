package cloud.util.small;

import cloud.util.ResourceAgent;
import cloud.util.TaskAgent;

import java.io.*;
import java.util.*;

/**
 * @author ：LLH
 * @date ：Created in 2022/4/25 15:34
 * @description：小规模数据仿真
 */
public class SmallDataSimulation {
    static int resourceSize  = 5;
    static int taskSize = 10;
    static int totalProcessNum = 25;
    static int baseEndTime = 200*6*5;

    public static int[][] getTransferTime(int resourceSize) {
        Random random = new Random();
        int[][] transferTime = new int[resourceSize][resourceSize];
        for (int i=0; i<resourceSize; i++) {
            for (int j=0; j<i; j++) {
                int time = random.nextInt(10)+5;
                transferTime[i][j] = time;
                transferTime[j][i] = time;
            }
            transferTime[i][i] = 0;
        }
        return transferTime;
    }

    public static int[][] getTransferCost(int resourceSize) {
        Random random = new Random();
        int[][] transferCost = new int[resourceSize][resourceSize];
        for (int i=0; i<resourceSize; i++) {
            for (int j=0; j<i; j++) {
                int cost = random.nextInt(150)+75;
                transferCost[i][j] = cost;
                transferCost[j][i] = cost;
            }
            transferCost[i][i] = 0;
        }
        return transferCost;
    }


    public static List<ResourceAgent> simulationResourceAgents(int finishTime) {
        List<ResourceAgent> resourceAgents = new ArrayList<>();
        Random random = new Random();
//        List<Integer> aveProcessTime = new ArrayList<>();
//        int lowProcessTime = 160;
//        for (int i=0; i<100; i++) {
//            aveProcessTime.add(lowProcessTime+i);
//        }


        for (int i=0; i<resourceSize; i++) {
            ResourceAgent resourceAgent = new ResourceAgent();
            resourceAgent.setResourceNo(i);
            Map<Integer, Integer> processCostMap = new HashMap<>();
            Map<Integer, Integer> processTimeMap = new HashMap<>();
            List<int[]> undressedTimeSectionList = new ArrayList<>();
            for (int j=0; j<totalProcessNum*0.8; j++) {

                int type = random.nextInt(totalProcessNum);
                if (processCostMap.containsKey(type)) {
                    j--;
                    continue;
                }
//                int value = random.nextInt(25)+6;
                int cost = random.nextInt(600)+300;
                processCostMap.put(type, cost);
                int time = random.nextInt(100)+150;
//                int time = aveProcessTime.get(type);
                processTimeMap.put(type, time);
            }
            undressedTimeSectionList.add(new int[] {0,finishTime});
            resourceAgent.setProcessCostMap(processCostMap);
            resourceAgent.setProcessTimeMap(processTimeMap);
            resourceAgent.setUndressedTimeSectionList(undressedTimeSectionList);
//            resourceAgent.setHaveAssignedTimeSet(new HashSet<>());
            resourceAgents.add(resourceAgent);
        }
        return resourceAgents;
    }

    public static List<TaskAgent> simulateTaskAgents(int finishTime) {
        List<TaskAgent> taskAgents = new ArrayList<>();
        Random random = new Random();
        for (int i=0; i<taskSize; i++) {
            TaskAgent taskAgent = new TaskAgent();
            List<Integer> subTaskList = new ArrayList<>();
            List<Integer> subTaskRewardList = new ArrayList<>();
            for (int j=0; j<6; j++) {
                subTaskList.add(random.nextInt(totalProcessNum));
                subTaskRewardList.add(random.nextInt(3000)+1500);
            }
            taskAgent.setSubTaskList(subTaskList);
            taskAgent.setSubTaskRewardList(subTaskRewardList);
            taskAgent.setProcessStartTime(0);
            taskAgent.setProcessEndTime(finishTime);
            taskAgents.add(taskAgent);
        }
        return taskAgents;
    }




    public void toTxt() {
        SituationData situationData = new SituationData();
        situationData.setTransferTime(getTransferTime(resourceSize));
        situationData.setTransferCost(getTransferCost(resourceSize));
        situationData.setResourceAgents(simulationResourceAgents(baseEndTime));
        situationData.setTaskAgents(simulateTaskAgents(baseEndTime));
        try {
            File writeName = new File("D:\\Coding\\JavaProject\\multi-agent-trans-v2\\data\\small-scale" + resourceSize + "-" + taskSize + "1.txt"); // 相对路径，如果没有则要建立一个新的output.txt文件
            if (!writeName.exists()) {
                writeName.createNewFile(); // 创建新文件,有同名的文件的话直接覆盖
            }
            BufferedWriter out = new BufferedWriter(new FileWriter(writeName));
            // 写入transferTime
            int[][] transferTime = situationData.getTransferTime();
            for (int i = 0; i < transferTime.length; i++) {
                StringBuffer sb = new StringBuffer();
                for (int j = 0; j < transferTime[0].length; j++) {
                    sb.append(transferTime[i][j]);
                    sb.append(',');
                }
                sb.deleteCharAt(sb.length() - 1);
                out.write(sb.toString());
                out.newLine();
            }

            // 写入transferCost
            int[][] transferCost = situationData.getTransferCost();
            for (int i = 0; i < transferCost.length; i++) {
                StringBuffer sb = new StringBuffer();
                for (int j = 0; j < transferCost[0].length; j++) {
                    sb.append(transferCost[i][j]);
                    sb.append(',');
                }
                sb.deleteCharAt(sb.length() - 1);
                out.write(sb.toString());
                out.newLine();
            }

            // 写入制造资源Agent
            List<ResourceAgent> resourceAgents = situationData.getResourceAgents();

            for (int i = 0; i < resourceAgents.size(); i++) {
                ResourceAgent resourceAgent = resourceAgents.get(i);
                StringBuffer sb = new StringBuffer();
                sb.append(resourceAgent.getResourceNo());
                out.write(sb.toString());
                out.newLine();


                StringBuffer sb2 = new StringBuffer();
                for (Map.Entry<Integer, Integer> entry : resourceAgent.getProcessTimeMap().entrySet()) {
                    sb2.append(entry.getKey());
                    sb2.append(',');
                    sb2.append(entry.getValue());
                    sb2.append(',');
                }
                sb2.deleteCharAt(sb2.length() - 1);
                out.write(sb2.toString());
                out.newLine();

                StringBuffer sb1 = new StringBuffer();
                for (Map.Entry<Integer, Integer> entry : resourceAgent.getProcessCostMap().entrySet()) {
                    sb1.append(entry.getKey());
                    sb1.append(',');
                    sb1.append(entry.getValue());
                    sb1.append(',');
                }
                sb1.deleteCharAt(sb1.length() - 1);
                out.write(sb1.toString());
                out.newLine();
            }
            // 写入制造任务Agent
            List<TaskAgent> taskAgents = situationData.getTaskAgents();
            for (int i=0; i<taskAgents.size(); i++) {
                TaskAgent taskAgent = taskAgents.get(i);
                StringBuffer sb = new StringBuffer();
                for (int j=0; j<taskAgent.getSubTaskList().size(); j++) {
                    sb.append(taskAgent.getSubTaskList().get(j));
                    sb.append(',');
                }
                sb.deleteCharAt(sb.length()-1);
                out.write(sb.toString());
                out.newLine();

                StringBuffer sb1 = new StringBuffer();
                for (int j=0; j<taskAgent.getSubTaskRewardList().size(); j++) {
                    sb1.append(taskAgent.getSubTaskRewardList().get(j));
                    sb1.append(',');
                }
                sb1.deleteCharAt(sb1.length()-1);
                out.write(sb1.toString());
                out.newLine();
            }
            out.flush(); // 把缓存区内容压入文件
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public SituationData readTxt() {
        int[][] transferTime = new int[resourceSize][resourceSize];
        int[][] transferCost = new int[resourceSize][resourceSize];
        List<ResourceAgent> resourceAgents = new ArrayList<>();
        List<TaskAgent> taskAgents = new ArrayList<>();

        int totalSubTaskReward = 0;

        try {
            // 防止文件建立或读取失败，用catch捕捉错误并打印，也可以throw
            /* 读入TXT文件 */
            String pathname = "D:\\Coding\\JavaProject\\multi-agent-trans-v2\\data\\small-scale" + resourceSize + "-" + taskSize + "1.txt"; // 绝对路径或相对路径都可以，这里是绝对路径，写入文件时演示相对路径
            File filename = new File(pathname); // 要读取以上路径的input。txt文件
            InputStreamReader reader = new InputStreamReader(
                    new FileInputStream(filename)); // 建立一个输入流对象reader
            BufferedReader br = new BufferedReader(reader); // 建立一个对象，它把文件内容转成计算机能读懂的语言
            String line = "";

            // 读取transferTime
            for(int i=0; i<resourceSize; i++) {
                line = br.readLine();
                String[] strs = line.split(",");
                for (int j=0; j<strs.length; j++) {
                    transferTime[i][j] = Integer.parseInt(strs[j]);
                }
            }
            // 读取transferCost
            for(int i=0; i<resourceSize; i++) {
                line = br.readLine();
                String[] strs = line.split(",");
                for (int j=0; j<strs.length; j++) {
                    transferCost[i][j] = Integer.parseInt(strs[j]);
                }
            }
            // 读取ResourceAgent
            for (int i=0; i<resourceSize; i++) {
                ResourceAgent resourceAgent = new ResourceAgent();
                line = br.readLine();
                Map<Integer, Integer> processTimeMap = new HashMap<>();
                line = br.readLine();
                String[] strs = line.split(",");
                for(int j=0; j<strs.length/2; j++) {
                    processTimeMap.put(Integer.parseInt(strs[2*j]), Integer.parseInt(strs[2*j+1]));
                }
                Map<Integer, Integer> processCostMap = new HashMap<>();
                line = br.readLine();
                strs = line.split(",");
                for(int j=0; j<strs.length/2; j++) {
                    processCostMap.put(Integer.parseInt(strs[2*j]), Integer.parseInt(strs[2*j+1]));
                }
                resourceAgent.setProcessCostMap(processCostMap);
                resourceAgent.setProcessTimeMap(processTimeMap);
                resourceAgent.setResourceNo(i);
                List<int[]> undressedTimeSectionList = new ArrayList<>();
                undressedTimeSectionList.add(new int[] {0,baseEndTime});
                resourceAgent.setUndressedTimeSectionList(undressedTimeSectionList);

                resourceAgents.add(resourceAgent);
            }
            for (int i=0; i<taskSize; i++) {
                TaskAgent taskAgent = new TaskAgent();
                line = br.readLine();
                String[] strs = line.split(",");
                List<Integer> subTaskList = new ArrayList<>();
                for (int j=0; j<strs.length; j++) {
                    subTaskList.add(Integer.parseInt(strs[j]));
                }
                taskAgent.setSubTaskList(subTaskList);

                line = br.readLine();
                strs = line.split(",");
                List<Integer> subTaskRewardList = new ArrayList<>();
                for (int j=0; j<strs.length; j++) {
                    subTaskRewardList.add(Integer.parseInt(strs[j]));
                    totalSubTaskReward += Integer.parseInt(strs[j]);
                }
                taskAgent.setSubTaskRewardList(subTaskRewardList);
                taskAgent.setProcessStartTime(0);
                taskAgent.setProcessEndTime(baseEndTime);

                taskAgents.add(taskAgent);
            }
            br.close();
            reader.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        SituationData situationData = new SituationData();
        situationData.setTransferCost(transferCost);
        situationData.setTransferTime(transferTime);
        situationData.setResourceAgents(resourceAgents);
        situationData.setTaskAgents(taskAgents);
        System.out.println("totalSubTaskReward " + totalSubTaskReward);
        return situationData;
    }

    public static void main(String[] args) {
        SmallDataSimulation smallDataSimulation = new SmallDataSimulation();
        smallDataSimulation.toTxt();
//        System.out.println(smallDataSimulation.readTxt());

//        int[][] transferTime =  getTransferTime(resourceSize);
//        for (int i=0; i<transferTime.length; i++) {
//            StringBuffer sb = new StringBuffer();
//            for (int j=0; j<transferTime[0].length; j++) {
//                sb.append(transferTime[i][j]);
//                sb.append(',');
//            }
//            sb.deleteCharAt(sb.length()-1);
//            System.out.println(sb.toString());
//        }
    }

}
