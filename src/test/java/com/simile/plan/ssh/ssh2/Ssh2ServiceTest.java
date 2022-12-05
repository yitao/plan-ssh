package com.simile.plan.ssh.ssh2;

import com.simile.plan.ssh.SshResult;
import com.simile.plan.ssh.SshSource;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @Author yitao
 * @Created 2022/12/05
 */
public class Ssh2ServiceTest {
    Ssh2Service ssh2Service;

    @Before
    public void setUp() throws Exception {
        String host = "192.168.1.101";
        int port = 22;
        String user = "test";
        String password = "haizhi@123";
        SshSource source = new SshSource(host, port, user, password);
        ssh2Service = new Ssh2Service(source);
        ssh2Service.login();
    }

    @After
    public void tearDown() throws Exception {
        ssh2Service.logout();
    }

    @Test
    public void runCmd() {
        SshResult result = ssh2Service.runCmd("pwd");
        System.out.println(result.getCode());
        System.out.println(result.getOut());

        result = ssh2Service.runCmd("ps -ef | grep java");
        System.out.println(result.getCode());
        System.out.println(result.getOut());
    }
}