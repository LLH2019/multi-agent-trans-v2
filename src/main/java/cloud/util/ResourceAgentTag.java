package cloud.util;

import lombok.Data;

/**
 * @author ：LLH
 * @date ：Created in 2022/4/20 18:45
 * @description：时间效能值
 */
@Data
public class ResourceAgentTag {
    private ResourceAgent resourceAgent;
    private Integer startTime;
    private Integer endTime;
    private Integer preResourceTag;

}
