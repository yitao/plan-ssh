package com.simile.plan.ssh.jsch;

import com.simile.plan.ssh.SshResult;
import com.simile.plan.ssh.SshSource;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @Author yitao
 * @Created 2022/12/05
 */
public class JSchServiceTest {
    JSchService jSchService;

    @Before
    public void setUp() throws Exception {
        String host = "192.168.1.101";
        int port = 22;
        String user = "test";
        String password = "haizhi@123";
        SshSource source = new SshSource(host, port, user, password);
        jSchService = new JSchService(source);
        jSchService.login();
    }

    @After
    public void tearDown() throws Exception {
        jSchService.logout();
    }

    @Test
    public void runCmd() {
        SshResult result = jSchService.runCmd("pwd");
        System.out.println(result.getCode());
        System.out.println(result.getOut());

        result = jSchService.runCmd("ps -ef | grep java");
        System.out.println(result.getCode());
        System.out.println(result.getOut());
    }
}