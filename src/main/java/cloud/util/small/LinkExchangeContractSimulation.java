package cloud.util.small;

import cloud.util.ResourceAgent;
import cloud.util.TaskAgent;
import com.sun.scenario.effect.impl.sw.sse.SSEBlend_SRC_OUTPeer;
import lombok.Data;

import java.io.*;
import java.util.*;

/**
 * @author ：LLH
 * @date ：Created in 2022/5/22 15:50
 * @description：链式转让仿真
 */
public class LinkExchangeContractSimulation {
    public static int resourceSize  = 5;
    public static int taskSize = 10;
    public static int totalProcessNum = 25;
    public static int baseEndTime = 200*6*5;

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
//        for (int i=0;)
        // 读取相关数据，此处为初次分配的情况


        System.out.println("发现新的可接收合同");
        System.out.println("10 2 21");
        System.out.println("制造资源 4");
        System.out.println("(7 1 17) (1 2 4) (4 4 18) (9 2 23) (1 6 18) (7 2 18) (3 4 18) (5 2 5) (6 3 5)");
        System.out.println("(7 1 17) 3 2714");
        System.out.println("(9 2 23) 3 2772");
        System.out.println("制造资源 5");
        System.out.println("(4 2 2) (5 1 11) (1 4 24) (2 2 21) (2 3 10) (3 3 21) (2 5 10) (3 5 15)");
        System.out.println("(5 1 11) 2 2691");
        System.out.println("(1 4 24) 3 2872");
        System.out.println("(2 3 10) 2 2711");
        System.out.println();

        System.out.println("发现可交换的合同");
        System.out.println("(8 1 7 1) (9 2 23 4) 123");

    }
    @Data
    class MdResource {
        Integer resourceNo;
        List<int[]> subTaskList;
    }
    @Data
    class MdTask {
        Integer taskNo;
        List<Integer> processResourceList;
    }

//
//    List<MdResource> getMdResourceList() {
//        List<MdResource> mdResources = new ArrayList<>();
//        for (int i=1; i<=5; i++) {
//            MdResource mdResource = new MdResource();
//            mdResource.setResourceNo(i);
//        }
//
//    }
//
//    List<MdTask> getMdTaskList() {
//        List<MdTask> mdTasks = new ArrayList<>();
//        for (int i=1; i<=10; i++) {
//            MdTask mdTask = new MdTask();
//            mdTask.setTaskNo(i);
//
//        }
//    }

}
