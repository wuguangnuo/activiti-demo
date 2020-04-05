package cn.wgn.workflow.entity;

import java.util.Date;

public class VacTask {
    /**
     * 任务ID
     */
    private String id;
    /**
     * 操作人
     */
    private String name;
    /**
     * 请假单
     */
    private Vacation vac;
    /**
     * 创建时间
     */
    private Date createTime;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Vacation getVac() {
        return vac;
    }

    public void setVac(Vacation vac) {
        this.vac = vac;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}
