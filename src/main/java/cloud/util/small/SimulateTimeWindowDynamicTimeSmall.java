package cloud.util.small;

import cloud.util.ResourceAgent;
import cloud.util.ResourceAgentTag;
import cloud.util.TaskAgent;
import cloud.util.TimeAndEValue;

import java.util.*;


/**
 * @author ：LLH
 * @date ：Created in 2022/4/6 21:16
 * @description：仿真时间窗口
 */
public class SimulateTimeWindowDynamicTimeSmall {

    static int resourceSize = 5;
    static int taskSize = 10;
    static int processSum = 6;

    public static int[] simulateValue(int finishTime) {
        List<Integer> allocatedList = new ArrayList<>();
        List<Integer> unAllocatedList = new ArrayList<>();
        List<Integer> totalCostList = new ArrayList<>();
        List<Integer> totalProcessList = new ArrayList<>();
        List<Integer> totalAllProcessCostList = new ArrayList<>();
        List<Integer> averageCostList = new ArrayList<>();

        int totalSubTaskReward = 0;
        int totalProcessCost = 0;
        int totalTransferCost = 0;

        for (int round = 0; round < 1; round++) {
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
            int totalConnectTime = 0;
            int totalProcessTime = 0;

            int finishAllocated = 0;

            for (int i = 0; i < taskAgents.size(); i++) {
                TaskAgent taskAgent = taskAgents.get(i);
                List<Integer> subTaskList = taskAgent.getSubTaskList();
                List<Integer> subTaskRewardList = taskAgent.getSubTaskRewardList();

                Map<Integer, int[]> processInfoMap = new HashMap<>();

                int curTaskCost = 0;
                int tagNum = 0;
                boolean tag = true;
                Map<Integer, ResourceAgentTag> resourceAgentTagMap = new HashMap<>();


                TimeAndEValue[][] timeAndEValues = new TimeAndEValue[8][20];
                for (int m = 0; m < processSum; m++) {
                    for (int n = 0; n < resourceSize; n++) {
                        timeAndEValues[m][n] = new TimeAndEValue();
                    }
                }

                int firstProcessNo = subTaskList.get(0);
                for (int k = 0; k < resourceSize; k++) {
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
                totalSubTaskReward += taskAgent.getSubTaskRewardList().get(0);
                for (int j = 1; j < processSum; j++) {
                    int processNo = subTaskList.get(j);
                    totalSubTaskReward += taskAgent.getSubTaskRewardList().get(j);

//                    int reward = random.nextInt();
//                    subTaskMap.put(processNo, reward);

//                    System.out.println(j + " 444");
                    for (int k = 0; k < resourceSize; k++) {
//                        System.out.print(k+ " ");
                        ResourceAgent resourceAgent = resourceAgents.get(k);
                        if (!resourceAgent.getProcessTimeMap().containsKey(processNo)) {
                            timeAndEValues[j][k].tag = false;
                        } else {
                            List<int[]> relationValueList = new ArrayList<>();

                            for (int p = 0; p < resourceSize; p++) {
                                TimeAndEValue preTimeAndValue = timeAndEValues[j - 1][p];
                                if (preTimeAndValue.tag == false) {
                                    continue;
                                } else {
                                    int[][] preRelationValues = preTimeAndValue.getRelationValues();
                                    ResourceAgent preResourceAgent = resourceAgents.get(p);
                                    for (int[] preRelationValue : preRelationValues) {
                                        // 取得当前的relativeValue值
                                        int[] curRelationValueMore = getCurRelationValueMore(preRelationValue, preResourceAgent,resourceAgent, transferTime, processNo, preRelationValue[0], finishTime);
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
                for (int k = 0; k < resourceSize; k++) {
                    if (timeAndEValues[processSum-1][k].tag == true) {
                        int[][] relationValues = timeAndEValues[processSum-1][k].relationValues;
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
                totalProcessCost += minCost;
                int selectedTagNum = selectedValue[2];
                ResourceAgentTag resourceAgentTag = resourceAgentTagMap.get(selectedTagNum);
//                System.out.println("resourceAgentTag " + resourceAgentTag);
                if (addHandleTime(resourceAgentTag.getResourceAgent(), resourceAgentTag.getStartTime(), resourceAgentTag.getEndTime())) {
                    finishTagNum++;
                    int [] showData = new int[] {resourceAgentTag.getResourceAgent().getResourceNo(), resourceAgentTag.getStartTime(),
                            resourceAgentTag.getEndTime(), i};
                    showDataList.add(showData);
                }

                totalProcessTime += (resourceAgentTag.getEndTime() - resourceAgentTag.getStartTime());
//                int preHandleAgent = -1;
                for (int j = processSum-1; j >= 1; j--) {
                    selectedTagNum = resourceAgentTag.getPreResourceTag();
                    ResourceAgentTag preResourceTag  = resourceAgentTag;

                    resourceAgentTag = resourceAgentTagMap.get(selectedTagNum);
                    totalProcessTime += (resourceAgentTag.getEndTime() - resourceAgentTag.getStartTime());
                    if (addHandleTime(resourceAgentTag.getResourceAgent(), resourceAgentTag.getStartTime(), resourceAgentTag.getEndTime())) {
                        finishTagNum++;
                        int [] showData = new int[] {resourceAgentTag.getResourceAgent().getResourceNo(), resourceAgentTag.getStartTime(),
                                resourceAgentTag.getEndTime(), i};
                        showDataList.add(showData);
                    }
                    System.out.println("resourceAgentTag.getResourceAgent().getResourceNo() " + preResourceTag.getResourceAgent().getResourceNo() + " " + resourceAgentTagMap.get(selectedTagNum).getResourceAgent().getResourceNo());
                    totalTransferCost += transferCost[preResourceTag.getResourceAgent().getResourceNo()][resourceAgentTagMap.get(selectedTagNum).getResourceAgent().getResourceNo()];
                }
                if (finishTagNum >= processSum) {
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
            averageCostList.add(totalCost / allocated);

//            System.out.println(allocated + " " + unallcated + " " + totalCost + " " + totalCost/(allocated) + " " + totalProcess + " " +totalAllProcessCost/totalProcess + " "
//                    + totalConnectTime + " " + totalConnectTime/totalProcess + " ");
        }

        System.out.println("real totalSubTaskReward " + totalSubTaskReward);
        System.out.println("real totalProcessCost " + totalProcessCost);
        System.out.println("real totalTransferCost" + totalTransferCost);

        int[] res = new int[5];
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

    private static int[] getCurRelationValueMore(int[] preRelationValue, ResourceAgent preResourceAgent, ResourceAgent resourceAgent, int[][] transferTime, int processNo, int startTime, int endTime) {
        Map<Integer, Integer> processTimeMap = resourceAgent.getProcessTimeMap();
        if (!processTimeMap.containsKey(processNo)) {
            return new int[]{-1,-1};
        }
        int distanceTime = 0;
        if (preResourceAgent != null) {
            distanceTime = transferTime[preResourceAgent.getResourceNo()][resourceAgent.getResourceNo()];
        }

        List<int[]> undressedTimeSectionList = resourceAgent.getUndressedTimeSectionList();
        for (int[] section : undressedTimeSectionList) {
            int maxStart = Math.max(section[0], startTime)+distanceTime;
            int minEnd = Math.min(section[1],endTime);
            if (minEnd-maxStart >= processTimeMap.get(processNo)) {
                return new  int[]{maxStart,maxStart+processTimeMap.get(processNo), preRelationValue[1]+resourceAgent.getProcessCostMap().get(processNo), preRelationValue[2]};
            }
        }
        return new int[]{-1,-1};

    }

    public static boolean addHandleTime(ResourceAgent resourceAgent, int startTime, int endTime) {
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
