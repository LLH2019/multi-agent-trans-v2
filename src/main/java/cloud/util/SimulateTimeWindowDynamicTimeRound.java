package cloud.util;

import java.util.*;

/**
 * @author ：LLH
 * @date ：Created in 2022/4/6 21:16
 * @description：仿真时间窗口
 */
public class SimulateTimeWindowDynamicTimeRound {
    static int resourceSize  = 20;
    static int baseEndTime = 210*8*5;

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

            resourceAgent.setProcessCostMap(processCostMap);
            resourceAgent.setProcessTimeMap(processTimeMap);
            resourceAgent.setUndressedTimeSectionList(undressedTimeSectionList);
            resourceAgent.setHaveAssignedTimeSet(new HashSet<>());
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

        for (int round = 0; round < 10; round++) {
            SimulateTimeWindowDynamicTimeRound simulateTimeWindow = new SimulateTimeWindowDynamicTimeRound();
            List<ResourceAgent> resourceAgents = simulateTimeWindow.simulationResourceAgents(finishTime);
            Random random = new Random();
            int allocated = 0;
            int unallcated = 0;
            int totalCost = 0;
            int totalProcess = 0;
            int totalAllProcessCost = 0;
            int totalConnectTime = 0;
            int totalProcessTime = 0;

            int finishAllocated = 0;

            for (int i = 0; i < 1000; i++) {
                TaskAgent taskAgent = new TaskAgent();
                Map<Integer, Integer> subTaskMap = new HashMap<>();
                List<Integer> subTaskList = new ArrayList<>();
                for (int j = 0; j < 8; j++) {
                    subTaskList.add(random.nextInt(100));
                }

                int curTaskCost = 0;
                int tagNum = 0;
                boolean tag = true;
                Map<Integer, ResourceAgentTag> resourceAgentTagMap = new HashMap<>();


                TimeAndEValue[][] timeAndEValues = new TimeAndEValue[8][20];
                for (int m = 0; m < 8; m++) {
                    for (int n = 0; n < 20; n++) {
                        timeAndEValues[m][n] = new TimeAndEValue();
                    }
                }

                int firstProcessNo = subTaskList.get(0);
                for (int k = 0; k < 20; k++) {
                    ResourceAgent resourceAgent = resourceAgents.get(k);
                    if (!resourceAgent.getProcessTimeMap().containsKey(firstProcessNo)) {
                        timeAndEValues[0][k].tag = false;
                    } else {
                        int[] relationValueMore = getFirstCurRelationValue(resourceAgent, firstProcessNo, 0, finishTime);
                        if (relationValueMore[0] == -1) {
                            timeAndEValues[0][k].tag = false;
                        } else {
                            int[][] relationValue = {{relationValueMore[1], relationValueMore[2], tagNum}};

                            timeAndEValues[0][k].relationValues = relationValue;
                            timeAndEValues[0][k].tag = true;

                            ResourceAgentTag resourceAgentTag = new ResourceAgentTag();
                            resourceAgentTag.setResourceAgent(resourceAgent);
                            resourceAgentTag.setStartTime(relationValueMore[0]);
                            resourceAgentTag.setEndTime(relationValueMore[1]);

                            resourceAgentTagMap.put(tagNum, resourceAgentTag);
                            tagNum++;
                        }
                    }
                }

//                System.out.println("222222");

                for (int j = 1; j < 8; j++) {
                    int processNo = subTaskList.get(j);
//                    int reward = random.nextInt();
//                    subTaskMap.put(processNo, reward);

//                    System.out.println(j + " 444");
                    for (int k = 0; k < 20; k++) {
//                        System.out.print(k+ " ");
                        ResourceAgent resourceAgent = resourceAgents.get(k);
                        if (!resourceAgent.getProcessTimeMap().containsKey(processNo)) {
                            timeAndEValues[j][k].tag = false;
                        } else {
                            List<int[]> relationValueList = new ArrayList<>();

                            for (int p = 0; p < 20; p++) {
                                TimeAndEValue preTimeAndValue = timeAndEValues[j - 1][p];
                                if (preTimeAndValue.tag == false) {
                                    continue;
                                } else {
                                    int[][] preRelationValues = preTimeAndValue.getRelationValues();
                                    for (int[] preRelationValue : preRelationValues) {
                                        // 取得当前的relativeValue值
                                        int[] curRelationValueMore = getCurRelationValue(preRelationValue, resourceAgent, processNo, preRelationValue[0], finishTime);
                                        if (curRelationValueMore[0] == -1) {
                                            continue;
                                        } else {
                                            ResourceAgentTag resourceAgentTag = new ResourceAgentTag();
                                            resourceAgentTag.setResourceAgent(resourceAgent);
                                            resourceAgentTag.setStartTime(curRelationValueMore[0]);
                                            resourceAgentTag.setEndTime(curRelationValueMore[1]);
                                            resourceAgentTag.setPreResourceTag(preRelationValue[2]);
                                            resourceAgentTagMap.put(tagNum, resourceAgentTag);
                                            relationValueList.add(new int[]{curRelationValueMore[1], curRelationValueMore[2], tagNum, preRelationValue[2]});
                                            tagNum++;
                                        }
                                    }
                                }
                            }
                            if (relationValueList.size() == 0) {
                                timeAndEValues[j][k].tag = false;
                                continue;
                            }

                            Collections.sort(relationValueList, (a, b) -> {
                                return a[2] - b[2];
                            });
                            int minEndTime = Integer.MAX_VALUE;

                            List<int[]> newRelationValueList = new ArrayList<>();
                            for (int w = 0; w < relationValueList.size(); w++) {
                                if (relationValueList.get(w)[1] < minEndTime) {
                                    minEndTime = relationValueList.get(w)[1];
                                    newRelationValueList.add(relationValueList.get(w));
                                }
                            }

                            int[][] relativeValues = new int[newRelationValueList.size()][4];
                            for (int w = 0; w < newRelationValueList.size(); w++) {
                                relativeValues[w] = newRelationValueList.get(w);
                            }
                            timeAndEValues[j][k].tag = true;
                            timeAndEValues[j][k].relationValues = relativeValues;
                        }
                    }
                }
//                System.out.println("333");
                int selectedFinalResource = -1;
                int minCost = Integer.MAX_VALUE;
                int[] selectedValue = new int[4];
                for (int k = 0; k < 20; k++) {
                    if (timeAndEValues[7][k].tag == true) {
                        int[][] relationValues = timeAndEValues[7][k].relationValues;
                        for (int[] value : relationValues) {
                            if (value[1] < minCost) {
                                selectedFinalResource = k;
                                minCost = value[1];
                                selectedValue = value;
                            }
                        }
                    }
                }

                if (selectedFinalResource == -1) {
                    unallcated++;
                    tag = false;
                    continue;
                }

                int finishTagNum = 0;
//                System.out.print(minCost + " ");
                totalCost += minCost;
                int selectedTagNum = selectedValue[2];
                ResourceAgentTag resourceAgentTag = resourceAgentTagMap.get(selectedTagNum);
//                System.out.println("resourceAgentTag " + resourceAgentTag);
                if (simulateTimeWindow.addHandleTime(resourceAgentTag.getResourceAgent(), resourceAgentTag.getStartTime(), resourceAgentTag.getEndTime()))
                    finishTagNum++;


                totalProcessTime += (resourceAgentTag.getEndTime() - resourceAgentTag.getStartTime());
                for (int j = 7; j >= 1; j--) {
                    selectedTagNum = resourceAgentTag.getPreResourceTag();
                    resourceAgentTag = resourceAgentTagMap.get(selectedTagNum);
                    totalProcessTime += (resourceAgentTag.getEndTime() - resourceAgentTag.getStartTime());
                    if (simulateTimeWindow.addHandleTime(resourceAgentTag.getResourceAgent(), resourceAgentTag.getStartTime(), resourceAgentTag.getEndTime()))
                        finishTagNum++;
                }
                if (finishTagNum >= 8) {
//                    System.out.println("11111");
                    finishAllocated++;
                }

//                    addHandleTime(resourceAgents.get(selectedAgentNo), selectedTimeGap);
//
//                    totalProcess++;
//                    totalAllProcessCost += resourceAgents.get(selectedAgentNo).getProcessCostMap().get(processNo);
//                    curCost += resourceAgents.get(selectedAgentNo).getProcessCostMap().get(processNo);
//                    Set<Integer> processTimeSet = resourceAgents.get(selectedAgentNo).getHaveAssignedTimeSet();
//                    processTimeSet.add(selectedTime);
//                    resourceAgents.get(selectedAgentNo).setHaveAssignedTimeSet(processTimeSet);
//                    startTime = selectedTime + 1;

//                subTaskList.add();
                if (tag) {
//                    totalCost += curCost;
                    allocated++;
                }
                taskAgent.setSubTaskMap(subTaskMap);

            }

            allocatedList.add(allocated);
            unAllocatedList.add(unallcated);
            totalCostList.add(totalCost);
            totalProcessList.add(totalProcess);
            totalAllProcessCostList.add(totalAllProcessCost);
            averageCostList.add(totalCost / allocated);

//            System.out.println(allocated + " " + unallcated + " " + totalCost + " " + totalCost/(allocated) + " " + totalProcess + " " +totalAllProcessCost/totalProcess + " "
//                    + totalConnectTime + " " + totalConnectTime/totalProcess + " ");
        }
        int[] res = new int[5];
        res[0] = (int) allocatedList.stream().mapToInt(Integer::intValue).average().getAsDouble();
        res[1] = (int) averageCostList.stream().mapToInt(Integer::intValue).average().getAsDouble();
        return res;
    }

    public static void main(String[] args) {
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

    }

    private static int[] getFirstCurRelationValue(ResourceAgent resourceAgent, int processNo, int startTime, int endTime) {
        Map<Integer, Integer> processTimeMap = resourceAgent.getProcessTimeMap();
        if (!processTimeMap.containsKey(processNo)) {
            return new int[]{-1,-1};
        }
        List<int[]> undressedTimeSectionList = resourceAgent.getUndressedTimeSectionList();
        for (int[] section : undressedTimeSectionList) {
            int maxStart = Math.max(section[0], startTime);
            int minEnd = Math.min(section[1],endTime);
            if (minEnd-maxStart >= processTimeMap.get(processNo)) {
                return new  int[]{maxStart,maxStart+processTimeMap.get(processNo), resourceAgent.getProcessCostMap().get(processNo)};
            }
        }
        return new int[]{-1,-1};
    }

    private static int[] getCurRelationValue(int[] preRelationValue, ResourceAgent resourceAgent, int processNo, int startTime, int endTime) {
        Map<Integer, Integer> processTimeMap = resourceAgent.getProcessTimeMap();
        if (!processTimeMap.containsKey(processNo)) {
            return new int[]{-1,-1};
        }
        List<int[]> undressedTimeSectionList = resourceAgent.getUndressedTimeSectionList();
        for (int[] section : undressedTimeSectionList) {
            int maxStart = Math.max(section[0], startTime);
            int minEnd = Math.min(section[1],endTime);
            if (minEnd-maxStart >= processTimeMap.get(processNo)) {
                return new  int[]{maxStart,maxStart+processTimeMap.get(processNo), preRelationValue[1]+resourceAgent.getProcessCostMap().get(processNo), preRelationValue[2]};
            }
        }
        return new int[]{-1,-1};

    }

    private boolean addHandleTime(ResourceAgent resourceAgent, int startTime, int endTime) {
        List<int[]> undressedTimeSectionList = resourceAgent.getUndressedTimeSectionList();
//        System.out.println("undressedTimeSectionList " + undressedTimeSectionList.size());
        boolean tag = false;
        for (int i=0;i<undressedTimeSectionList.size();i++) {
            int []section = undressedTimeSectionList.get(i);
            if (section[0] <= startTime && endTime <= section[1]) {
                if (section[0] == startTime && endTime == section[1]) {
//                    undressedTimeSectionList.remove(section);
                } else if (section[0] ==startTime) {
                    undressedTimeSectionList.add(new int[]{endTime, section[1]});
                } else if (section[1] == endTime) {
                    undressedTimeSectionList.add(new int[]{section[0],startTime});
                } else {
                    undressedTimeSectionList.add(new int[]{section[0], startTime});
                    undressedTimeSectionList.add(new int[]{endTime, section[1]});
                }
                undressedTimeSectionList.remove(section);
                tag = true;
                break;
            }
        }
        if (!tag) {
            System.out.println("ppppp");
        }
        return tag;
//        resourceAgent.setUndressedTimeSectionList(undressedTimeSectionList);
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
