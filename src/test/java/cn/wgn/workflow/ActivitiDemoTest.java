package cn.wgn.workflow;

import org.activiti.engine.IdentityService;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.identity.Group;
import org.activiti.engine.identity.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ActivitiDemoTest {

    @Resource
    private ProcessEngine processEngine;

    @Test
    public void test() {
        IdentityService is = processEngine.getIdentityService();
        // 添加用户组
        Group empGroup = saveGroup(is, "empGroup", "员工组");
        Group manageGroup = saveGroup(is, "manageGroup", "经理组");
        Group dirGroup = saveGroup(is, "dirGroup", "总监组");
        // 添加用户
        User empA = saveUser(is, "empa", "123456"); // 员工a
        User empB = saveUser(is, "empb", "123456"); // 员工b
        User managea = saveUser(is, "managea", "123456"); // 经理a
        User manageb = saveUser(is, "manageb", "123456"); // 经理b
        User dira = saveUser(is, "dira", "123456"); // 总监a
        // 绑定关系
        saveRel(is, empA, empGroup);
        saveRel(is, empB, empGroup);
        saveRel(is, managea, manageGroup);
        saveRel(is, manageb, manageGroup);
        saveRel(is, dira, dirGroup);
    }

    User saveUser(IdentityService is, String id, String pw) {
        User u = is.newUser(id);
        u.setPassword(pw);
        is.saveUser(u);
        return u;
    }

    Group saveGroup(IdentityService is, String id, String name) {
        Group g = is.newGroup(id);
        g.setName(name);
        is.saveGroup(g);
        return g;
    }

    void saveRel(IdentityService is, User u, Group g) {
        is.createMembership(u.getId(), g.getId());
    }
}
