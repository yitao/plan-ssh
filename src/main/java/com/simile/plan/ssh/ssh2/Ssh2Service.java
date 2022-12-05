package com.simile.plan.ssh.ssh2;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;

import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.Session;
import com.simile.plan.ssh.AbstractSshService;
import com.simile.plan.ssh.SSHException;
import com.simile.plan.ssh.SshResult;
import com.simile.plan.ssh.SshSource;
import org.apache.commons.io.IOUtils;

/**
 * @Author yitao
 * @Created 2022/12/05
 */
public class Ssh2Service extends AbstractSshService {
    Connection connection = null;

    public Ssh2Service(SshSource source) {
        super(source);
    }

    @Override
    public boolean login() {
        try {
            connection = new Connection(source.getHost(), source.getPort());
            connection.connect();// 连接
            return connection.authenticateWithPassword(source.getUser(), source.getPassword());// 认证
        } catch (IOException e) {
            throw new SSHException(String.format("Unable to connect to remote server[%s], %s", source.getHost(), e.getMessage()), e);
        }
    }

    @Override
    public void logout() {
        if (connection != null) {
            connection.close();
        }
    }


    @Override
    public SshResult runCmd(String cmd) {
        //打开连接
        Session session = null;
        try {
            session = connection.openSession();
            //打开bash
            session.requestPTY("bash");
            session.execCommand(cmd);

            String out = IOUtils.toString(session.getStdout(), "UTF-8");
            String error = IOUtils.toString(session.getStderr(), "UTF-8");
            return SshResult.builder().code(session.getExitStatus())
                    .out(out).error(error).build();
        } catch (IOException e) {
            throw new SSHException("Run Cmd error ", e);
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

    @Override
    public boolean uploadFile(String localFilePath, String remoteFilePath) {
        return false;
    }

    @Override
    public boolean downloadFile(String localFilePath, String remoteFilePath) {
        return false;
    }

}
