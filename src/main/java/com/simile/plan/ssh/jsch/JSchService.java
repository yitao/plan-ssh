package com.simile.plan.ssh.jsch;

import java.io.IOException;
import java.io.InputStream;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.simile.plan.ssh.AbstractSshService;
import com.simile.plan.ssh.SSHException;
import com.simile.plan.ssh.SshResult;
import com.simile.plan.ssh.SshSource;
import org.apache.commons.io.IOUtils;

/**
 * @Author yitao
 * @Created 2022/12/05
 */
public class JSchService extends AbstractSshService {
    private JSch jsch;
    private Session session = null;

    public JSchService(SshSource source) {
        super(source);
        this.jsch = new JSch();
    }

    @Override
    public boolean login() {
        try {
            session = jsch.getSession(source.getUser(), source.getHost(), source.getPort());
            session.setConfig("StrictHostKeyChecking", "no");
            session.setPassword(source.getPassword());
            session.connect();
            return true;
        } catch (JSchException e) {
            throw new SSHException(String.format("Unable to connect to remote server[%s], %s", source.getHost(), e.getMessage()), e);
        }
    }

    @Override
    public void logout() {
        if (session != null && session.isConnected()) {
            session.disconnect();
        }

    }

    @Override
    public SshResult runCmd(String cmd) {
        ChannelExec channelExec = null;
        try {
            channelExec = (ChannelExec) session.openChannel("exec");
            InputStream in = channelExec.getInputStream();
            InputStream err = channelExec.getErrStream();
            channelExec.setCommand(cmd);
            channelExec.connect();
            String out = IOUtils.toString(in, "UTF-8");
            String error = IOUtils.toString(err, "UTF-8");
            return SshResult.builder().code(channelExec.getExitStatus())
                    .out(out).error(error).build();
        } catch (JSchException | IOException e) {
            throw new SSHException("Run Cmd error ", e);
        } finally {
            if (channelExec != null && !channelExec.isClosed()) {
                channelExec.disconnect();
            }
        }
    }

    @Override
    public boolean uploadFile(String localFilePath, String remoteFilePath) {
        //TODO http://www.jcraft.com/jsch/examples/ScpTo.java.html
        return false;
    }

    @Override
    public boolean downloadFile(String localFilePath, String remoteFilePath) {
        //TODO http://www.jcraft.com/jsch/examples/ScpTo.java.html
        return false;
    }
}
