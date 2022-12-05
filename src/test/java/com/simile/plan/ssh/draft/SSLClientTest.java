package com.simile.plan.ssh.draft;

import org.apache.sshd.client.SshClient;
import org.junit.Test;

/**
 * sshd
 * @Author yitao
 * @Created 2022/11/24
 */
public class SSLClientTest {

    @Test
    public void name() {
        SshClient client = SshClient.setUpDefaultClient();
        client.start();
//        // using the client for multiple sessions...
//        try (ClientSession session = client.connect("haizhi", "localhost")
//                .verify(...timeout...)
//                .getSession()) {
//            session.addPasswordIdentity(...password..); // for password-based authentication
//            // or
//            session.addPublicKeyIdentity(...key-pair...); // for password-less authentication
//            // Note: can add BOTH password AND public key identities - depends on the client/server security setup
//
//            session.auth().verify(...timeout...);
//            // start using the session to run commands, do SCP/SFTP, create local/remote port forwarding, etc...
//        }
    }
}
