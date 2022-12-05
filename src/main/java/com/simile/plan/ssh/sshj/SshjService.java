package com.simile.plan.ssh.sshj;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import com.simile.plan.ssh.AbstractSshService;
import com.simile.plan.ssh.SSHException;
import com.simile.plan.ssh.SshResult;
import com.simile.plan.ssh.SshSource;
import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.connection.channel.direct.Session;
import net.schmizz.sshj.sftp.SFTPClient;
import net.schmizz.sshj.xfer.FileSystemFile;
import org.apache.commons.io.IOUtils;

/**
 * @Author yitao
 * @Created 2022/12/05
 */
public class SshjService extends AbstractSshService {
    private SSHClient ssh;

    public SshjService(SshSource source) {
        super(source);
    }

    @Override
    public boolean login() {
        try {
            ssh = new SSHClient();
            ssh.loadKnownHosts();
            ssh.connect(source.getHost());
            ssh.authPassword(source.getUser(), source.getPassword());
            return true;
        } catch (Exception e) {
            throw new SSHException(String.format("Unable to connect to remote server[%s], %s", source.getHost(), e.getMessage()), e);
        }
    }

    @Override
    public void logout() {
        if (ssh != null && ssh.isConnected()) {
            try {
                ssh.close();
            } catch (IOException e) {
                throw new SSHException("close error", e);
            }
        }

    }

    @Override
    public SshResult runCmd(String cmd) {
        try (Session session = ssh.startSession()) {
            Session.Command command = session.exec(cmd);
            command.join(5, TimeUnit.SECONDS);

            String out = IOUtils.toString(command.getInputStream(), "UTF-8");
            String error = IOUtils.toString(command.getErrorStream(), "UTF-8");
            return SshResult.builder().code(command.getExitStatus())
                    .out(out).error(error).build();
        } catch (IOException e) {
            throw new SSHException("Run Cmd error ", e);
        }
    }

    @Override
    public boolean uploadFile(String localFilePath, String remoteFilePath) {
        //https://github.com/hierynomus/sshj/blob/master/examples/src/main/java/net/schmizz/sshj/examples/SCPUpload.java
        try {
            ssh.useCompression();
            ssh.newSCPFileTransfer().upload(new FileSystemFile(localFilePath), remoteFilePath);
            return true;
        } catch (IOException e) {
            throw new SSHException(e);
        }
    }

    @Override
    public boolean downloadFile(String localFilePath, String remoteFilePath) {
        //https://github.com/hierynomus/sshj/blob/master/examples/src/main/java/net/schmizz/sshj/examples/SCPDownload.java
        try (SFTPClient sftp = ssh.newSFTPClient()) {
            sftp.get(remoteFilePath, new FileSystemFile(localFilePath));
            return true;
        } catch (IOException e) {
            throw new SSHException(e);
        }
    }

}
