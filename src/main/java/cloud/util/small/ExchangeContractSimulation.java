package cloud.util.small;

import lombok.Data;

import java.util.List;

/**
 * @author ：LLH
 * @date ：Created in 2022/5/22 15:50
 * @description：链式转让仿真
 */
public class ExchangeContractSimulation {
    public static void main(String[] args) {
//        for (int i=0;)





    }
    @Data
    class MdResource {
        Integer resourceNo;
        List<int[]> subTaskList;
    }
    @Data
    class MdTask {
        Integer taskNo;
        List<Integer> processResourceList;
    }

//
//    List<MdResource> getMdResourceList() {
//        List<MdResource> mdResources = new ArrayList<>();
//        for (int i=1; i<=5; i++) {
//            MdResource mdResource = new MdResource();
//            mdResource.setResourceNo(i);
//        }
//
//    }
//
//    List<MdTask> getMdTaskList() {
//        List<MdTask> mdTasks = new ArrayList<>();
//        for (int i=1; i<=10; i++) {
//            MdTask mdTask = new MdTask();
//            mdTask.setTaskNo(i);
//
//        }
//    }

}
