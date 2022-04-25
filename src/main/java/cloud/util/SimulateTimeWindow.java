package cloud.util;

import java.util.*;

/**
 * @author ：LLH
 * @date ：Created in 2022/4/6 21:16
 * @description：仿真时间窗口
 */
public class SimulateTimeWindow {
    static int resourceSize  = 20;

    public List<ResourceAgent> simulationResourceAgents() {
        List<ResourceAgent> resourceAgents = new ArrayList<>();
        Random random = new Random();

        for (int i=0; i<resourceSize; i++) {
            ResourceAgent resourceAgent = new ResourceAgent();
            resourceAgent.setResourceNo(i);
            Map<Integer, Integer> processCostMap = new HashMap<>();
            Map<Integer, Integer> processTimeMap = new HashMap<>();
            for (int j=0; j<160; j++) {

                int type = random.nextInt(200);
                if (processCostMap.containsKey(type)) {
                    j--;
                    continue;
                }
//                int value = random.nextInt(25)+6;
                int cost = random.nextInt(150)+75;
                processCostMap.put(type, cost);
                int time = random.nextInt(100)+160;
                processTimeMap.put(type, time);
            }

//            List<int[]> sections = new ArrayList<>();
//            Set<Integer> set = new HashSet<>();
//            for (int j=0; j<4; j++) {
//                int time = random.nextInt(40) + 1;
//                int value = random.nextInt(100) + 10;
//                int [] sec = new int[]{time,value};
//                sections.add(sec);
//                int type = random.nextInt(20);
//                set.add(type);
//            }
//            resourceAgent.setSectionList(sections);
//            resourceAgent.setProcessSet(set);
            resourceAgent.setProcessCostMap(processCostMap);
            resourceAgent.setProcessTimeMap(processTimeMap);
            resourceAgent.setHaveAssignedTimeSet(new HashSet<>());
            resourceAgents.add(resourceAgent);
        }
        return resourceAgents;
    }


    public static void main(String[] args) {

        for (int round=0; round<10; round++) {

            SimulateTimeWindow simulateTimeWindow = new SimulateTimeWindow();
            List<ResourceAgent> resourceAgents = simulateTimeWindow.simulationResourceAgents();
            Random random = new Random();
            int allocated = 0;
            int unallcated = 0;
            int totalCost = 0;
            int totalProcess = 0;
            int totalAllProcessCost = 0;
            int totalConnectTime = 0;
            for (int i = 0; i < 1; i++) {
                TaskAgent taskAgent = new TaskAgent();
                Map<Integer, Integer> subTaskMap = new HashMap<>();
//            List<Integer> subTaskList = new ArrayList<>();

                int startTime = 0;
                int curCost = 0;
                boolean tag = true;
                for (int j = 0; j < 8; j++) {
                    int processNo = random.nextInt(200);
                    int reward = random.nextInt();
                    int p=0;
                    subTaskMap.put(processNo, reward);
                    int endTime = 80 - (7 - j) * p;



                    int selectedAgentNo = -1;
                    int selectedTime = -1;
                    for (ResourceAgent resourceAgent : resourceAgents) {
                        for (int time = startTime; time < endTime; time++) {
                            if (resourceAgent.getHaveAssignedTimeSet().contains(time) && resourceAgent.getProcessCostMap().containsKey(processNo)) {
                                totalConnectTime++;
                                break;
                            }
                        }

                        if (!resourceAgent.getProcessCostMap().containsKey(processNo)) {
                            continue;
                        }
                        if (selectedAgentNo != -1) {
                            ResourceAgent haveSelectedAgent = resourceAgents.get(selectedAgentNo);
                            if (haveSelectedAgent.getProcessCostMap().get(processNo) <= resourceAgent.getProcessCostMap().get(processNo)) {
                                continue;
                            }
                        }

                        for (int time = startTime; time < endTime; time++) {
                            if (!resourceAgent.getHaveAssignedTimeSet().contains(time)) {
                                selectedAgentNo = resourceAgent.getResourceNo();
                                selectedTime = time;
                                continue;
                            }
                        }
                    }
//                    System.out.println(selectedAgentNo + " ");
                    if (selectedAgentNo == -1) {
                        unallcated++;
                        tag = false;
                        break;
                    }
                    totalProcess++;
                    totalAllProcessCost += resourceAgents.get(selectedAgentNo).getProcessCostMap().get(processNo);
                    curCost += resourceAgents.get(selectedAgentNo).getProcessCostMap().get(processNo);
                    Set<Integer> processTimeSet = resourceAgents.get(selectedAgentNo).getHaveAssignedTimeSet();
                    processTimeSet.add(selectedTime);
                    resourceAgents.get(selectedAgentNo).setHaveAssignedTimeSet(processTimeSet);
                    startTime = selectedTime + 1;

//                subTaskList.add();
                }
                if (tag) {
                    totalCost += curCost;
                    allocated++;
                }
                taskAgent.setSubTaskMap(subTaskMap);

            }

            System.out.print(allocated + " " + unallcated + " " + totalCost + " " + totalCost/(allocated) + " " + totalProcess + " " +totalAllProcessCost/totalProcess + " "
               + totalConnectTime + " " + totalConnectTime/totalProcess + " ");
        }
        System.out.println(" unallcated num ");
    }


}
