package cloud.util;

import cloud.global.GlobalKafkaConfig;
import cloud.global.GlobalResourceValue;

/**
 * @author ：LLH
 * @date ：Created in 2022/4/6 20:07
 * @description：计算效能值
 */
public class CalEfficiencyUtil {
    public static double getEfficiencyValue(double taskValue, double processCost, double debit) {
        double reward = taskValue * GlobalResourceValue.yield;
        double riskCost = debit * GlobalResourceValue.fault;
        return reward-processCost-riskCost;
    }
}
