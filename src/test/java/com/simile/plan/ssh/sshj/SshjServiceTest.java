package com.simile.plan.ssh.sshj;

import com.simile.plan.ssh.SshResult;
import com.simile.plan.ssh.SshSource;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @Author yitao
 * @Created 2022/12/05
 */
public class SshjServiceTest {
    SshjService sshjService;

    @Before
    public void setUp() throws Exception {
        String host = "192.168.1.101";
        int port = 22;
        String user = "test";
        String password = "haizhi@123";
        SshSource source = new SshSource(host, port, user, password);
        sshjService = new SshjService(source);
        sshjService.login();
    }

    @After
    public void tearDown() throws Exception {
        sshjService.logout();
    }

    @Test
    public void runCmd() {
        SshResult result = sshjService.runCmd("pwd");
        System.out.println(result.getCode());
        System.out.println(result.getOut());

        result = sshjService.runCmd("ps -ef | grep java");
        System.out.println(result.getCode());
        System.out.println(result.getOut());
    }
}