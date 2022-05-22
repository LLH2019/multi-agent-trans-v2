package cloud.util.small;

import cloud.util.ResourceAgent;
import cloud.util.TaskAgent;

import javax.swing.text.html.parser.Entity;
import java.util.*;

/**
 * @author ：LLH
 * @date ：Created in 2022/4/6 21:16
 * @description：仿真时间窗口
 */
public class SimulateTimeWindowFixedTimeSmall {

    public static int[] simulateValue(int finishTime) {
        List<Integer> allocatedList = new ArrayList<>();
        List<Integer> unAllocatedList = new ArrayList<>();
        List<Integer> totalCostList = new ArrayList<>();
        List<Integer> totalProcessList = new ArrayList<>();
        List<Integer> totalAllProcessCostList = new ArrayList<>();
        List<Integer> averageCostList = new ArrayList<>();

        for (int s=0; s<1; s++) {
            List<int[]> showDataList = new ArrayList<>();

            SmallDataSimulation smallDataSimulation = new SmallDataSimulation();
            SituationData situationData = smallDataSimulation.readTxt();
            int[][] transferTime = situationData.getTransferTime();
            int[][] transferCost = situationData.getTransferCost();
            List<ResourceAgent> resourceAgents = situationData.getResourceAgents();
            List<TaskAgent> taskAgents = situationData.getTaskAgents();

            int allocated = 0;
            int unallcated = 0;
            int totalCost = 0;
            int totalProcess = 0;
            int totalAllProcessCost = 0;

            int gap = finishTime /6;
            for (int i = 0; i < taskAgents.size(); i++) {
                TaskAgent taskAgent = taskAgents.get(i);
                List<Integer> subTaskList = taskAgent.getSubTaskList();
                List<Integer> subTaskRewardList = taskAgent.getSubTaskRewardList();

                Map<Integer, int[]> processInfoMap = new HashMap<>();

                int curCost = 0;
                boolean tag = true;
                int[] selectedTimeGap = new int[]{0, 0};
                ResourceAgent preResourceAgent = null;
                int preFinishTime = 0;
                for (int j = 0; j < 6; j++) {
                    int processNo = subTaskList.get(j);
                    int reward = subTaskRewardList.get(j);
                    int startTime = gap * j;
                    int endTime = gap * (j + 1);


                    int selectedAgentNo = -1;
                    int selectedTime = -1;
                    for (ResourceAgent resourceAgent : resourceAgents) {
                        int[] secTime = meetTimeGap(preResourceAgent, resourceAgent, transferTime, preFinishTime, processNo, startTime, endTime);
                        if (secTime[1] == 0) {
                            continue;
                        }
//                        System.out.println(selectedAgentNo + " " + secTime[0] + " " + secTime[1] + " ");
                        if (selectedAgentNo != -1) {
                            ResourceAgent haveSelectedAgent = resourceAgents.get(selectedAgentNo);
                            if (haveSelectedAgent.getProcessCostMap().get(processNo) <=
                                    resourceAgent.getProcessCostMap().get(processNo)) {
                                continue;
                            }
                        }
                        selectedAgentNo = resourceAgent.getResourceNo();
                        selectedTimeGap = secTime;

//                        System.out.println(selectedAgentNo);
                    }
//                    System.out.println(selectedAgentNo + " ");
                    if (selectedAgentNo == -1) {
//                        System.out.print(j+ " ");
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
//                    System.out.print(resourceAgents.get(selectedAgentNo).getProcessCostMap().get(processNo) + " ");
                    curCost += resourceAgents.get(selectedAgentNo).getProcessCostMap().get(processNo);
//                    Set<Integer> processTimeSet = resourceAgents.get(selectedAgentNo).getHaveAssignedTimeSet();
//                    processTimeSet.add(selectedTime);
//                    resourceAgents.get(selectedAgentNo).setHaveAssignedTimeSet(processTimeSet);
//                    startTime = selectedTime + 1;
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
//                    System.out.println("222222");
                    totalCost += curCost;
                    allocated++;
                }

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
            System.out.println(s + " " + allocated);
            averageCostList.add(totalCost / allocated);
        }
//            System.out.println(allocated + " " + unallcated + " " + totalCost + " " + totalCost/(allocated) + " " + totalProcess + " " +totalAllProcessCost/totalProcess + " "
//               + totalConnectTime + " " + totalConnectTime/totalProcess + " ");
        int [] res = new int[5];
        res[0] = (int) allocatedList.stream().mapToInt(Integer::intValue).average().getAsDouble();
        res[1] = (int) averageCostList.stream().mapToInt(Integer::intValue).average().getAsDouble();
        return res;
    }


    public static void main(String[] args) {

//        List<List<Integer>> costValueList = new ArrayList<>();
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
                break;
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
