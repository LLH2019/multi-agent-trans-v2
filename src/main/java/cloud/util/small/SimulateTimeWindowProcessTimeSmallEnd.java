package cloud.util.small;

import cloud.util.ResourceAgent;
import cloud.util.TaskAgent;

import java.util.*;

/**
 * @author ：LLH
 * @date ：Created in 2022/4/6 21:16
 * @description：仿真时间窗口
 */
public class SimulateTimeWindowProcessTimeSmallEnd {

    public static int[] simulateValue(int finishTime) {
        List<Integer> allocatedList = new ArrayList<>();
        List<Integer> unAllocatedList = new ArrayList<>();
        List<Integer> totalCostList = new ArrayList<>();
        List<Integer> totalProcessList = new ArrayList<>();
        List<Integer> totalAllProcessCostList = new ArrayList<>();
        List<Integer> averageCostList = new ArrayList<>();

        for (int round=0; round<1; round++) {
            List<int[]> showDataList = new ArrayList<>();

            SmallDataSimulation smallDataSimulation = new SmallDataSimulation();
            SituationData situationData = smallDataSimulation.readTxt();
            int[][] transferTime = situationData.getTransferTime();
            int[][] transferCost = situationData.getTransferCost();
            List<ResourceAgent> resourceAgents = situationData.getResourceAgents();
            List<TaskAgent> taskAgents = situationData.getTaskAgents();

            Map<Integer, List<Integer>> averageProcessTimeListMap = new HashMap<>();
            for (ResourceAgent resourceAgent : resourceAgents) {
                Map<Integer, Integer> processTimeMap = resourceAgent.getProcessTimeMap();
                for (Map.Entry<Integer, Integer> entry : processTimeMap.entrySet()) {
                    int processNo = entry.getKey();
                    int processTime = entry.getValue();
                    if (!averageProcessTimeListMap.containsKey(processNo)) {
                        List<Integer> list = new ArrayList<>();
                        list.add(processTime);
                        averageProcessTimeListMap.put(processNo, list);
                    } else {
                        List<Integer> list = averageProcessTimeListMap.get(processNo);
                        list.add(processTime);
                        averageProcessTimeListMap.put(processNo, list);
                    }
                }
            }

            Map<Integer, Integer> averageProcessTimeMap = new HashMap<>();
            for (Map.Entry<Integer, List<Integer>> entry : averageProcessTimeListMap.entrySet()) {
                int num = 0, total=0;
                for (int processTime : entry.getValue()) {
                    num++;
                    total+=processTime;
                }
                averageProcessTimeMap.put(entry.getKey(), total/num);
            }

            int allocated = 0;
            int unallcated = 0;
            int totalCost = 0;
            int totalProcess = 0;
            int totalAllProcessCost = 0;
            for (int i = 0; i < taskAgents.size(); i++) {
                TaskAgent taskAgent = taskAgents.get(i);
                List<Integer> subTaskList = taskAgent.getSubTaskList();

                Map<Integer, int[]> processInfoMap = new HashMap<>();

                int [] endTimes = algoEndTime(averageProcessTimeMap,subTaskList, finishTime);

                int startTime = 0;
                int preFinishTime = 0;
                int curCost = 0;
                boolean tag = true;
                int[] selectedTimeGap = new int[]{0,0};
                ResourceAgent preResourceAgent = null;
                for (int j = 0; j < subTaskList.size(); j++) {
                    int processNo = subTaskList.get(j);
//                    int reward = random.nextInt();
//                    subTaskMap.put(processNo, reward);
//                    int startTime = endTimes[j];
                    int endTime = endTimes[j+1];


                    int selectedAgentNo = -1;
                    int selectedTime = -1;
                    for (ResourceAgent resourceAgent : resourceAgents) {
                        int [] secTime = meetTimeGap(preResourceAgent, resourceAgent, transferTime, preFinishTime,processNo, startTime, endTime);
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
                        unallcated++;
                        tag = false;
                        break;
                    }
                    int [] processInfos = new int[] {selectedAgentNo, selectedTimeGap[0], selectedTimeGap[1], resourceAgents.get(selectedAgentNo).getProcessCostMap().get(processNo)};
                    int [] showData = new int[] {selectedAgentNo, selectedTimeGap[0], selectedTimeGap[1], i};
                    showDataList.add(showData);

                    processInfoMap.put(j, processInfos);

                    preResourceAgent = resourceAgents.get(selectedAgentNo);
                    addHandleTime(resourceAgents.get(selectedAgentNo), selectedTimeGap);
                    preFinishTime = selectedTimeGap[1];

                    totalProcess++;
                    totalAllProcessCost += resourceAgents.get(selectedAgentNo).getProcessCostMap().get(processNo);
                    curCost += resourceAgents.get(selectedAgentNo).getProcessCostMap().get(processNo);
                    Set<Integer> processTimeSet = resourceAgents.get(selectedAgentNo).getHaveAssignedTimeSet();
//                    processTimeSet.add(selectedTime);
                    resourceAgents.get(selectedAgentNo).setHaveAssignedTimeSet(processTimeSet);
                    startTime = selectedTime + 1;

                }

                taskAgent.setProcessInfoMap(processInfoMap);

                System.out.println(i);
                for (Map.Entry<Integer, int[]> entry : processInfoMap.entrySet()) {
                    System.out.print(entry.getKey() + " ");
                    int [] processInfo = entry.getValue();
                    for (int t=0; t<processInfo.length; t++) {
                        System.out.print(processInfo[t] + " ");
                    }
                    System.out.println();
                }

                if (tag) {
                    totalCost += curCost;
                    allocated++;
                }
//                taskAgent.setSubTaskMap(subTaskMap);

            }

            Collections.sort(showDataList, (a, b) -> {
                if (a[0] != b[0]) {
                    return a[0]-b[0];
                } else {
                    return a[1]-b[1];
                }
            });

            Map<Integer, List<int[]>> showDataMap = new HashMap<>();
            for (int i=0; i<5; i++) {
                showDataMap.put(i, new ArrayList<>());
            }

            for (int[] data : showDataList) {
                List<int[]> curDataList = showDataMap.get(data[0]);
                curDataList.add(data);
            }

            for (Map.Entry<Integer, List<int[]>> entry : showDataMap.entrySet()) {
                StringBuffer sbST = new StringBuffer();
                StringBuffer sbET = new StringBuffer();
                StringBuffer sbAS = new StringBuffer();

                List<int[]> ff = entry.getValue();
                for (int[] f : ff) {
                    sbST.append(f[1]);
                    sbST.append(',');
                    sbET.append(f[2]);
                    sbET.append(',');
                    sbAS.append(f[3]+1);
                    sbAS.append(',');
                }
                sbST.deleteCharAt(sbST.length()-1);
                sbET.deleteCharAt(sbET.length()-1);
                sbAS.deleteCharAt(sbAS.length()-1);

                System.out.println();
                System.out.println( "m" + entry.getKey() +".O_start = [" + sbST.toString() + "]");
                System.out.println("m" + entry.getKey() +".O_end = [" + sbET.toString() + "]");
                System.out.println("m" + entry.getKey() +".assigned_task = [" + sbAS.toString() + "]");
            }

            allocatedList.add(allocated);
            unAllocatedList.add(unallcated);
            totalCostList.add(totalCost);
            totalProcessList.add(totalProcess);
            totalAllProcessCostList.add(totalAllProcessCost);
            averageCostList.add(totalCost/allocated);
//            System.out.println(allocated + " " + unallcated + " " + totalCost + " " + totalCost/(allocated) + " " + totalProcess + " " +totalAllProcessCost/totalProcess + " "
//                    + totalConnectTime + " " + totalConnectTime/totalProcess + " ");
        }
        int [] res = new int[5];
        res[0] = (int) allocatedList.stream().mapToInt(Integer::intValue).average().getAsDouble();
        res[1] = (int) averageCostList.stream().mapToInt(Integer::intValue).average().getAsDouble();
        return res;

    }
    public static void main(String[] args) {

        List<int[]> res = new ArrayList<>();
        for (int i=1; i<=1; i++) {
            res.add(simulateValue(SmallDataSimulation.baseEndTime*i));
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
    }

    private static int[] algoEndTime(Map<Integer, Integer> averageProcessTimeMap, List<Integer> subTaskList, int finishTime) {
        int total = 0;
        int curTotal = 0;

        int [] result = new int[7];
        result[0]=0;
        for (int i=0; i<subTaskList.size(); i++) {
            total += averageProcessTimeMap.get(subTaskList.get(i));
        }
        for (int i=0; i<subTaskList.size(); i++) {
            curTotal += averageProcessTimeMap.get(subTaskList.get(i));
            result[i+1] = (int) ((double) curTotal/(double)total * finishTime);
        }
        return result;
    }

    private static void addHandleTime(ResourceAgent resourceAgent, int[] selectedTimeGap) {
        List<int[]> undressedTimeSectionList = resourceAgent.getUndressedTimeSectionList();
        for (int i=0; i<undressedTimeSectionList.size(); i++) {
            int[] section = undressedTimeSectionList.get(i);
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

    private static int[] meetTimeGap(ResourceAgent preResourceAgent, ResourceAgent resourceAgent, int[][] transferTime, int preFinishTime, int processNo, int startTime, int endTime) {
        Map<Integer, Integer> processTimeMap = resourceAgent.getProcessTimeMap();
        if (!processTimeMap.containsKey(processNo)) {
            return new int[]{0,0};
        }
        int distanceTime = 0;
        if (preResourceAgent != null) {
            distanceTime = transferTime[preResourceAgent.getResourceNo()][resourceAgent.getResourceNo()];
        }
        int compareTime = Math.max(preFinishTime+distanceTime, startTime);
        if (preResourceAgent == null) {
            compareTime = startTime;
        }
        List<int[]> undressedTimeSectionList = resourceAgent.getUndressedTimeSectionList();
        for (int[] section : undressedTimeSectionList) {
            int startSec = section[0];
            int endSec = section[1];
            int maxStart = Math.max(startSec, compareTime);
            int minEnd = Math.min(endSec, endTime);
            if (minEnd-maxStart >= processTimeMap.get(processNo)) {
                return new  int[]{maxStart, maxStart+processTimeMap.get(processNo)};
            }
        }
        return new int[]{0,0};
    }

}
