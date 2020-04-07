package cn.wgn.workflow.entity;

import java.util.Date;

public class Vacation {

    /**
     * 请假时长
     */
    private int days;
    /**
     * 请假原因
     */
    private String reason;
    /**
     * 申请人
     */
    private String applyUser;
    /**
     * 申请时间
     */
    private Date applyTime;
    /**
     * 申请状态
     */
    private String applyStatus;
    /**
     * 审核人
     */
    private String auditor;
    /**
     * 审核说明
     */
    private String result;
    /**
     * 审核时间
     */
    private Date auditTime;

    public int getDays() {
        return days;
    }

    public void setDays(int days) {
        this.days = days;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getApplyUser() {
        return applyUser;
    }

    public void setApplyUser(String applyUser) {
        this.applyUser = applyUser;
    }

    public Date getApplyTime() {
        return applyTime;
    }

    public void setApplyTime(Date applyTime) {
        this.applyTime = applyTime;
    }

    public String getApplyStatus() {
        return applyStatus;
    }

    public void setApplyStatus(String applyStatus) {
        this.applyStatus = applyStatus;
    }

    public String getAuditor() {
        return auditor;
    }

    public void setAuditor(String auditor) {
        this.auditor = auditor;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public Date getAuditTime() {
        return auditTime;
    }

    public void setAuditTime(Date auditTime) {
        this.auditTime = auditTime;
    }
}
