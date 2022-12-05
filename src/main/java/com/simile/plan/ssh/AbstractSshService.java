package com.simile.plan.ssh;

/**
 * @Author yitao
 * @Created 2022/12/05
 */
public abstract class AbstractSshService {
    protected SshSource source;

    public AbstractSshService(SshSource source) {
        this.source = source;
    }

    /**
     * 登录
     */
    public abstract boolean login();

    /**
     * 关闭连接
     */
    public abstract void logout();

    /**
     * 执行远程命令
     *
     * @param cmd 执行的命令
     * @return 0成功 1异常
     * @throws Exception
     */
    public abstract SshResult runCmd(String cmd);

    /**
     * 上传文件 基于scp
     *
     * @param localFilePath  本地文件路径，若为空或是*，表示当前目录下全部文件
     * @param remoteFilePath 远程路径，若为空，表示当前路径，若服务器上无此目录，则会自动创建
     * @throws Exception
     */
    public abstract boolean uploadFile(String localFilePath, String remoteFilePath);

    /**
     * 下载文件 基于scp
     *
     * @param localFilePath  本地文件名，若为空或是*，表示当前路径下全部文件
     * @param remoteFilePath 远程路径，若为空，表示当前路径，若服务器上无此目录，则会自动创建
     */
    public abstract boolean downloadFile(String localFilePath, String remoteFilePath);


}
