package com.simile.plan.ssh.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.Scanner;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

/**
 * @author yitao
 * @since 2020-12-10
 */
public class SshTest1 {

    public static void main(String[] args) throws IOException, JSchException {
        String host = "192.168.1.147";
        int port = 22;
        String user = "work";
        String password = "xingtu@2020";
        exeCommand(host, port, user, password);
    }

    public static void exeCommand(String host, int port, String user, String password)
            throws JSchException, IOException {
        Scanner s = new Scanner(System.in);
        JSch jsch = new JSch();
        Session session = jsch.getSession(user, host, port);
        session.setConfig("StrictHostKeyChecking", "no");
        session.setPassword(password);
        session.connect();


        ChannelExec channelExec = (ChannelExec) session.openChannel("exec");
//        OutputStream os = channelExec.getOutputStream();
//        channelExec.setErrStream(System.out);
//        channelExec.setOutputStream(System.out);


        channelExec.connect();
        System.out.println("连接成功");
//        channelExec.start();

        SshTest2.ReadThread is = new SshTest2.ReadThread(channelExec.getInputStream(), new PrintStream(System.out));
        new Thread(is).start();
        SshTest2.ReadThread error = new SshTest2.ReadThread(channelExec.getErrStream(), new PrintStream(System.out));
        new Thread(error).start();
//        channelExec.setOutputStream(System.out);
//        channelExec.setErrStream(System.out);
        channelExec.setInputStream(System.in,true);

    }

    static class ReadThread implements Runnable {
        private InputStream in;
        private PrintStream out;
        //编码
        private String charset = "UTF-8";

        //用于暂停的flag
        private boolean flag = true;

        // 停止线程
        public void stopThread() {
            flag = false;
        }

        /**
         * @param in  输入流，获取的输入
         * @param out 输出流
         */
        public ReadThread(InputStream in, PrintStream out) {
            super();
            this.in = in;
            this.out = out;
        }

        public void run() {
            BufferedReader br = null;
            try {
                br = new BufferedReader(new InputStreamReader(in, charset));
                String temp;
                //读取数据
                while ((temp = br.readLine()) != null && flag == true) {
                    if (out != null) {
                        out.println(temp);
                        out.flush();
                    }
                }
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

    }

//    http://www.jcraft.com/jsch/examples/ScpTo.java.html
}
