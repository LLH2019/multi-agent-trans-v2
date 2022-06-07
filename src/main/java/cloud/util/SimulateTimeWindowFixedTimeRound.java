package cloud.util;

import java.util.*;

/**
 * @author ：LLH
 * @date ：Created in 2022/4/6 21:16
 * @description：仿真时间窗口
 */
public class SimulateTimeWindowFixedTimeRound {
    static int resourceSize  = 20;
    static int baseEndTime = 210*8*5;
//    static int gapTime = 210*5;
    public List<ResourceAgent> simulationResourceAgents(int finishTime) {
        List<ResourceAgent> resourceAgents = new ArrayList<>();
        Random random = new Random();
        List<Integer> aveProcessTime = new ArrayList<>();
        int lowProcessTime = 160;
        for (int i=0; i<100; i++) {
            aveProcessTime.add(lowProcessTime+i);
        }


        for (int i=0; i<resourceSize; i++) {
            ResourceAgent resourceAgent = new ResourceAgent();
            resourceAgent.setResourceNo(i);
            Map<Integer, Integer> processCostMap = new HashMap<>();
            Map<Integer, Integer> processTimeMap = new HashMap<>();
            List<int[]> undressedTimeSectionList = new ArrayList<>();
            for (int j=0; j<80; j++) {

                int type = random.nextInt(100);
                if (processCostMap.containsKey(type)) {
                    j--;
                    continue;
                }
//                int value = random.nextInt(25)+6;
                int cost = random.nextInt(150)+75;
                processCostMap.put(type, cost);
//                int time = random.nextInt(100)+160;
                int time = aveProcessTime.get(type);
                processTimeMap.put(type, time);
            }
            undressedTimeSectionList.add(new int[] {0,finishTime});
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
            resourceAgent.setUndressedTimeSectionList(undressedTimeSectionList);
//            resourceAgent.setHaveAssignedTimeSet(new HashSet<>());
            resourceAgents.add(resourceAgent);
        }
        return resourceAgents;
    }

    public static int[] getRelativeValue(int finishTime) {
        List<Integer> allocatedList = new ArrayList<>();
        List<Integer> unAllocatedList = new ArrayList<>();
        List<Integer> totalCostList = new ArrayList<>();
        List<Integer> totalProcessList = new ArrayList<>();
        List<Integer> totalAllProcessCostList = new ArrayList<>();
        List<Integer> averageCostList = new ArrayList<>();

        for (int round=0; round<100; round++) {
            List<Integer> costValues = new ArrayList<>();
            SimulateTimeWindowFixedTimeRound simulateTimeWindow = new SimulateTimeWindowFixedTimeRound();
            List<ResourceAgent> resourceAgents = simulateTimeWindow.simulationResourceAgents(finishTime);
            Random random = new Random();
            int allocated = 0;
            int unallcated = 0;
            int totalCost = 0;
            int totalProcess = 0;
            int totalAllProcessCost = 0;
//            int
            int gap = finishTime/8;
            for (int i = 0; i < 1000; i++) {
                TaskAgent taskAgent = new TaskAgent();
                Map<Integer, Integer> subTaskMap = new HashMap<>();
                List<Integer> subTaskList = new ArrayList<>();
                for (int j = 0; j < 8; j++) {
                    subTaskList.add(random.nextInt(100));
                }

                int startTime = 0;
                int curCost = 0;
                boolean tag = true;
                int[] selectedTimeGap = new int[]{0,0};
                for (int j = 0; j < 8; j++) {
                    int processNo = subTaskList.get(j);
                    int reward = random.nextInt();
                    subTaskMap.put(processNo, reward);
                    int endTime = gap*(j+1);


                    int selectedAgentNo = -1;
                    int selectedTime = -1;
                    for (ResourceAgent resourceAgent : resourceAgents) {
                        int [] secTime = meetTimeGap(resourceAgent, processNo, startTime, endTime);
                        if (secTime[1] == 0) {
                            continue;
                        }

                        if (selectedAgentNo != -1) {
                            ResourceAgent haveSelectedAgent = resourceAgents.get(selectedAgentNo);
                            if (haveSelectedAgent.getProcessCostMap().get(processNo) <= resourceAgent.getProcessCostMap().get(processNo)) {
                                continue;
                            }
                        }
                        selectedAgentNo = resourceAgent.getResourceNo();
                        selectedTimeGap = secTime;
                    }
//                    System.out.println(selectedAgentNo + " ");
                    if (selectedAgentNo == -1) {
//                        System.out.print(j+ " ");
                        unallcated++;
                        tag = false;
                        break;
                    }

                    addHandleTime(resourceAgents.get(selectedAgentNo), selectedTimeGap);

                    totalProcess++;
                    totalAllProcessCost += resourceAgents.get(selectedAgentNo).getProcessCostMap().get(processNo);
//                    System.out.print(resourceAgents.get(selectedAgentNo).getProcessCostMap().get(processNo) + " ");
                    curCost += resourceAgents.get(selectedAgentNo).getProcessCostMap().get(processNo);
//                    Set<Integer> processTimeSet = resourceAgents.get(selectedAgentNo).getHaveAssignedTimeSet();
//                    processTimeSet.add(selectedTime);
//                    resourceAgents.get(selectedAgentNo).setHaveAssignedTimeSet(processTimeSet);
                    startTime = selectedTime + 1;

                }
                if (tag) {
                    totalCost += curCost;
                    costValues.add(curCost);
//                    System.out.print(curCost+",");
                    allocated++;
                }
                taskAgent.setSubTaskMap(subTaskMap);

            }
            allocatedList.add(allocated);
            unAllocatedList.add(unallcated);
            totalCostList.add(totalCost);
            totalProcessList.add(totalProcess);
            totalAllProcessCostList.add(totalAllProcessCost);
            averageCostList.add(totalCost/allocated);
//            System.out.println(allocated + " " + unallcated + " " + totalCost + " " + totalCost/(allocated) + " " + totalProcess + " " +totalAllProcessCost/totalProcess + " "
//               + totalConnectTime + " " + totalConnectTime/totalProcess + " ");
        }
        int [] res = new int[5];
        res[0] = (int) allocatedList.stream().mapToInt(Integer::intValue).average().getAsDouble();
        res[1] = (int) averageCostList.stream().mapToInt(Integer::intValue).average().getAsDouble();
        return res;
    }


    public static void main(String[] args) {

//        List<List<Integer>> costValueList = new ArrayList<>();
        List<int[]> res = new ArrayList<>();
        for (int i=1; i<=8; i++) {
            res.add(getRelativeValue(baseEndTime*i));
        }

        for (int i=0; i<res.size(); i++) {
            System.out.print(res.get(i)[0] + ",");
        }
        System.out.println();
        for (int i=0; i<res.size(); i++) {
            System.out.print(res.get(i)[1]+",");
        }

        System.out.println();
        for (int i=0; i<res.size(); i++) {
            System.out.print((3000*8-res.get(i)[1])+",");
        }

//        for (int j=0; j<23; j++) {
//            int curValues = 0; int num=0;
//            for (int i = 0; i < costValueList.size(); i++) {
//                if (costValueList.get(i).size()>j) {
//                    curValues += costValueList.get(i).get(j);
//                    num++;
//                }
//            }
//            System.out.print(curValues/num + ",");
//        }


//        System.out.println(" unallcated num ");
    }

    private static void addHandleTime(ResourceAgent resourceAgent, int[] selectedTimeGap) {
        List<int[]> undressedTimeSectionList = resourceAgent.getUndressedTimeSectionList();
        for (int[] section : undressedTimeSectionList) {
            if (section[0] <= selectedTimeGap[0] && selectedTimeGap[1] <= section[1]) {
                if (section[0] == selectedTimeGap[0] && selectedTimeGap[1] == section[1]) {
//                    undressedTimeSectionList.remove(section);
                } else if (section[0] == selectedTimeGap[0]) {
                    undressedTimeSectionList.add(new int[]{selectedTimeGap[1], section[1]});
                } else if (section[1] == selectedTimeGap[1]) {
                    undressedTimeSectionList.add(new int[]{section[0], selectedTimeGap[0]});
                } else {
                    undressedTimeSectionList.add(new int[]{section[0], selectedTimeGap[0]});
                    undressedTimeSectionList.add(new int[]{selectedTimeGap[1], section[1]});
                }
                undressedTimeSectionList.remove(section);
            }
        }
        resourceAgent.setUndressedTimeSectionList(undressedTimeSectionList);
    }

    private static int[] meetTimeGap(ResourceAgent resourceAgent, int processNo, int startTime, int endTime) {
        Map<Integer, Integer> processTimeMap = resourceAgent.getProcessTimeMap();
        if (!processTimeMap.containsKey(processNo)) {
            return new int[]{0,0};
        }
        List<int[]> undressedTimeSectionList = resourceAgent.getUndressedTimeSectionList();
        for (int[] section : undressedTimeSectionList) {
            int startSec = section[0];
            int endSec = section[1];
            int maxStart = Math.max(startSec, startTime);
            int minEnd = Math.min(endSec, endTime);
            if (minEnd-maxStart >= processTimeMap.get(processNo)) {
                return new  int[]{maxStart, maxStart+processTimeMap.get(processNo)};
            }
        }
        return new int[]{0,0};
    }


}
