package cloud.util;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @author ：LLH
 * @date ：Created in 2022/4/6 22:35
 * @description：任务Agent
 */
@Data
public class TaskAgent {
    // 子任务序列
    private List<Integer> subTaskList;
    // 子任务的价值
    private List<Integer> subTaskRewardList;
    // 加工开始时间
    private Integer processStartTime;
    // 加工截止时间
    private Integer processEndTime;
    private Map<Integer, Integer> subTaskMap;
    // 关于加工工序相关的信息
    private Map<Integer, int[]> processInfoMap;
}
