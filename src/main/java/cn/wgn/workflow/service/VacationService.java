package cn.wgn.workflow.service;

import cn.wgn.workflow.entity.VacTask;
import cn.wgn.workflow.entity.Vacation;
import cn.wgn.workflow.util.ActivitiUtil;
import org.activiti.engine.*;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.history.HistoricTaskInstance;
import org.activiti.engine.history.HistoricVariableInstance;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Task;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

@Service
public class VacationService {

    @Resource
    private RuntimeService runtimeService;
    @Resource
    private IdentityService identityService;
    @Resource
    private TaskService taskService;
    @Resource
    private HistoryService historyService;
    // 流程名称
    private static String PROCESS_DEFINE_KEY = "workflow2";

    /**
     * 发起流程
     *
     * @param userName
     * @param vac
     * @return
     */
    public Object startVac(String userName, Vacation vac) {

        identityService.setAuthenticatedUserId(userName);
        // 开始流程
        ProcessInstance vacationInstance = runtimeService.startProcessInstanceByKey(PROCESS_DEFINE_KEY);
        // 查询当前任务
        Task currentTask = taskService.createTaskQuery()
                .processInstanceId(vacationInstance.getId())
                .singleResult();
        // 申明任务
        taskService.claim(currentTask.getId(), userName);

        Map<String, Object> vars = new HashMap<>(4);
        vars.put("applyUser", userName);
        vars.put("days", vac.getDays());
        vars.put("reason", vac.getReason());

        taskService.complete(currentTask.getId(), vars);

        return true;
    }

    /**
     * 我正在申请的流程
     *
     * @param userName
     * @return
     */
    public Object myVac(String userName) {
        List<ProcessInstance> instanceList = runtimeService.createProcessInstanceQuery()
                .startedBy(userName)
                .list();
        List<Vacation> vacList = new ArrayList<>();
        for (ProcessInstance instance : instanceList) {
            Vacation vac = getVac(instance);
            vacList.add(vac);
        }

        return vacList;
    }

    /**
     * 我申请过的流程
     *
     * @param userName
     * @return
     */
    public Object myVacRecord(String userName) {
        List<HistoricProcessInstance> hisProInstance = historyService.createHistoricProcessInstanceQuery()
                .processDefinitionKey(PROCESS_DEFINE_KEY)
                .startedBy(userName)
                .finished()
                .orderByProcessInstanceEndTime().desc().list();

        List<Vacation> vacList = new ArrayList<>();
        for (HistoricProcessInstance hisInstance : hisProInstance) {
            Vacation vacation = new Vacation();
            vacation.setApplyUser(hisInstance.getStartUserId());
            vacation.setApplyTime(hisInstance.getStartTime());
            vacation.setApplyStatus("申请结束");
            List<HistoricVariableInstance> varInstanceList = historyService.createHistoricVariableInstanceQuery()
                    .processInstanceId(hisInstance.getId()).list();
            ActivitiUtil.setVars(vacation, varInstanceList);
            vacList.add(vacation);
        }
        return vacList;
    }

    /**
     * 获取信息
     *
     * @param instance
     * @return
     */
    private Vacation getVac(ProcessInstance instance) {
        Integer days = runtimeService.getVariable(instance.getId(), "days", Integer.class);
        String reason = runtimeService.getVariable(instance.getId(), "reason", String.class);
        String applyUser = runtimeService.getVariable(instance.getId(), "applyUser", String.class);
        Vacation vac = new Vacation();
        vac.setApplyUser(instance.getStartUserId());
        vac.setDays(days);
        vac.setReason(reason);
        Date startTime = instance.getStartTime();
        vac.setApplyTime(startTime);
        vac.setApplyStatus(instance.isEnded() ? "申请结束" : "等待审批");
        return vac;
    }

    /**
     * 操作流程
     *
     * @param userName
     * @param vacTask
     * @return
     */
    public Object passAudit(String userName, VacTask vacTask) {
        String taskId = vacTask.getId();
        String result = vacTask.getVac().getResult();
        Map<String, Object> vars = new HashMap<>();
        vars.put("result", result);
        vars.put("auditor", userName);
        vars.put("auditTime", new Date());
        taskService.claim(taskId, userName);
        taskService.complete(taskId, vars);
        return true;
    }

    /**
     * 待我审核的流程
     *
     * @param userName
     * @return
     */
    public Object myAudit(String userName) {
        List<Task> taskList = taskService.createTaskQuery()
                .processDefinitionKey(PROCESS_DEFINE_KEY)
                .taskCandidateUser(userName)
                .orderByTaskCreateTime().desc().list();

        List<VacTask> vacTaskList = new ArrayList<>();
        for (Task task : taskList) {
            VacTask vacTask = new VacTask();
            vacTask.setId(task.getId());
            vacTask.setName(task.getName());
            vacTask.setCreateTime(task.getCreateTime());
            String instanceId = task.getProcessInstanceId();
            ProcessInstance instance = runtimeService.createProcessInstanceQuery()
                    .processInstanceId(instanceId)
                    .singleResult();
            Vacation vac = getVac(instance);
            vacTask.setVac(vac);
            vacTaskList.add(vacTask);
        }
        return vacTaskList;
    }

    /**
     * 我的审核记录
     *
     * @param userName
     * @return
     */
    public Object myAuditRecord(String userName) {
        List<HistoricProcessInstance> hisProInstance = historyService.createHistoricProcessInstanceQuery()
                .processDefinitionKey(PROCESS_DEFINE_KEY)
                .involvedUser(userName)
                .finished()
                .orderByProcessInstanceEndTime().desc().list();

        List<Vacation> vacList = new ArrayList<>();
        for (HistoricProcessInstance hisInstance : hisProInstance) {
            List<HistoricTaskInstance> hisTaskInstanceList = historyService.createHistoricTaskInstanceQuery()
                    .processInstanceId(hisInstance.getId())
                    .processFinished()
                    .taskAssignee(userName)
                    .orderByHistoricTaskInstanceEndTime().desc().list();
            boolean isMyAudit = false;
            for (HistoricTaskInstance taskInstance : hisTaskInstanceList) {
                if (taskInstance.getAssignee().equals(userName)) {
                    isMyAudit = true;
                }
            }
            if (!isMyAudit) {
                System.out.println("!!!!!!!!!!!!!!!!!!  错误，isMyAudit = false");
                continue;
            }
            Vacation vacation = new Vacation();
            vacation.setApplyUser(hisInstance.getStartUserId());
            vacation.setApplyStatus("申请结束");
            vacation.setApplyTime(hisInstance.getStartTime());
            List<HistoricVariableInstance> varInstanceList = historyService.createHistoricVariableInstanceQuery()
                    .processInstanceId(hisInstance.getId()).list();
            ActivitiUtil.setVars(vacation, varInstanceList);
            vacList.add(vacation);
        }
        return vacList;
    }
}
