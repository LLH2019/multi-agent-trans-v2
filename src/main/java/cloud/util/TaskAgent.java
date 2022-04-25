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
    private List<Integer> subTaskList;
    private List<Integer> subTaskRewardList;

    private Map<Integer, Integer> subTaskMap;
}
