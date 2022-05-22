package cloud.util.small;

import cloud.util.ResourceAgent;
import cloud.util.TaskAgent;
import lombok.Data;

import java.util.List;

/**
 * @author ：LLH
 * @date ：Created in 2022/4/25 16:11
 * @description：环境数据
 */
@Data
public class SituationData {
    private int[][] transferTime;
    private int[][] transferCost;
    private List<ResourceAgent> resourceAgents;
    private List<TaskAgent> taskAgents;
}
