package cloud.util;

import lombok.Data;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author ：LLH
 * @date ：Created in 2022/4/6 21:23
 * @description：制造资源Agent模拟
 */
@Data
public class ResourceAgent {
//    private List<int[]> sectionList;
//    private Set<Integer> processSet;
    // resource 编号
    private Integer resourceNo;
    // 对应工序处理成本
    private Map<Integer, Integer> processCostMap;
    // 对应工序处理时长
    private Map<Integer, Integer> processTimeMap;


    // 已安排加工的时间点
    private Set<Integer> haveAssignedTimeSet;

    private List<int[]> undressedTimeSectionList;
}
