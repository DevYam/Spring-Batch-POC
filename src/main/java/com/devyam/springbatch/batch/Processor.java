package com.devyam.springbatch.batch;

import com.devyam.springbatch.model.User;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;


@Component
public class Processor implements ItemProcessor<User, User> {

    private static final Map<String, String> DEPT_NAMES =
            new HashMap<>();

    public Processor() {
        DEPT_NAMES.put("001", "Technology");
        DEPT_NAMES.put("002", "Operations");
        DEPT_NAMES.put("003", "Accounts");
    }

    @Override
    public User process(User user) throws Exception {

        String departmentCode = user.getDept();
        String dept = DEPT_NAMES.get(departmentCode);
        user.setDept(dept);
        System.out.printf("Converted from %s to %s%n", departmentCode, dept);
        return user;
    }
}
