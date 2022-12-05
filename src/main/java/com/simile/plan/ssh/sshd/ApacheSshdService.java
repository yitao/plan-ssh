package com.simile.plan.ssh.sshd;

import java.io.ByteArrayOutputStream;

import com.simile.plan.ssh.SSHException;
import com.simile.plan.ssh.SshResult;
import com.simile.plan.ssh.AbstractSshService;
import com.simile.plan.ssh.SshSource;
import lombok.extern.slf4j.Slf4j;
import org.apache.sshd.client.SshClient;
import org.apache.sshd.client.channel.ChannelExec;
import org.apache.sshd.client.session.ClientSession;
import org.apache.sshd.scp.client.DefaultScpClientCreator;
import org.apache.sshd.scp.client.ScpClient;
import org.apache.sshd.scp.client.ScpClientCreator;

/**
 * @Author yitao
 * @Created 2022/12/05
 */
@Slf4j
public class ApacheSshdService extends AbstractSshService {
    private ClientSession session;
    private SshClient client;

    public ApacheSshdService(SshSource source) {
        super(source);
    }

    /**
     * 登录
     */
    @Override
    public boolean login() {
        try {
            // 创建 SSH客户端
            client = SshClient.setUpDefaultClient();
            // 启动 SSH客户端
            client.start();
            // 通过主机IP、端口和用户名，连接主机，获取Session
            session = client.connect(source.getUser(), source.getHost(), source.getPort()).verify().getSession();
            // 给Session添加密码
            session.addPasswordIdentity(source.getPassword());
            // 校验用户名和密码的有效性
            return session.auth().verify().isSuccess();
        } catch (Exception e) {
            throw new SSHException(String.format("Unable to connect to remote server[%s], %s", source.getHost(), e.getMessage()), e);
        }
    }

    /**
     * 关闭连接
     */
    @Override
    public void logout() {
        try {
            //关闭session
            if (session != null && session.isOpen()) {
                session.close();
            }

            // 关闭 SSH客户端
            if (client != null && client.isOpen()) {
                client.stop();
                client.close();
            }
        } catch (Exception e) {
            throw new SSHException("Failed to close the connection, host: " + source.getHost(), e);
        }
    }

    /**
     * 上传文件 基于scp
     *
     * @param localFilePath  本地文件路径，若为空或是*，表示当前目录下全部文件
     * @param remoteFilePath 远程路径，若为空，表示当前路径，若服务器上无此目录，则会自动创建
     * @throws Exception
     */
    @Override
    public boolean uploadFile(String localFilePath, String remoteFilePath) {
        try {
            if (session == null) {
                throw new SSHException("Login session is empty, host: " + source.getHost());
            }
            ScpClientCreator creator = new DefaultScpClientCreator();
            ScpClient scpClient = creator.createScpClient(session);
            // ScpClient.Option.Recursive：递归copy，可以将子文件夹和子文件遍历copy
            scpClient.upload(localFilePath, remoteFilePath, ScpClient.Option.Recursive);
        } catch (Exception e) {
            throw new SSHException(e);
        }
        return true;
    }

    /**
     * 下载文件 基于scp
     *
     * @param localFilePath  本地文件名，若为空或是*，表示当前路径下全部文件
     * @param remoteFilePath 远程路径，若为空，表示当前路径，若服务器上无此目录，则会自动创建
     */
    @Override
    public boolean downloadFile(String localFilePath, String remoteFilePath) {
        try {
            if (session == null) {
                throw new SSHException("Login session is empty, host: " + source.getHost());
            }
            ScpClientCreator creator = new DefaultScpClientCreator();
            ScpClient scpClient = creator.createScpClient(session);
            scpClient.download(remoteFilePath, localFilePath);
        } catch (Exception e) {
            throw new SSHException(e);
        }
        return true;
    }

    /**
     * 执行远程命令
     *
     * @param cmd 执行的命令
     * @return 0成功 1异常
     * @throws Exception
     */
    @Override
    public SshResult runCmd(String cmd) {
        if (session == null) {
            throw new SSHException("Login session is empty, host: " + source.getHost());
        }
        try (ChannelExec channel = session.createExecChannel(cmd)) {
            int time = 0;
            channel.open();

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            channel.setOut(out);
            ByteArrayOutputStream err = new ByteArrayOutputStream();
            channel.setErr(err);

            while (true) {
                if (channel.isClosed()) {
                    break;
                }
                Thread.sleep(100);
                if (time > 9000) {  // 默认15分钟没执行完，直接获取结果
                    break;
                }
                time++;
            }
            SshResult sshVo = new SshResult();
            sshVo.setCode(channel.getExitStatus());
            sshVo.setOut(out);
            sshVo.setError(err.toString());
            return sshVo;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new SSHException(e.getMessage(), e);
        } catch (Exception e) {
            throw new SSHException(e.getMessage(), e);
        }
    }


}
