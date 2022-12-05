package com.simile.plan.ssh.draft;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.util.Scanner;

import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.Session;

/**
 * ssh2
 * @author yitao
 * @since 2020-12-10
 */
public class SshTest2 {

    public static void main(String[] args) throws IOException {
        String host = "192.168.1.101";
        int port = 22;
        String user = "test";
        String password = "haizhi@123";
        exeCommand(host, port, user, password);
    }

    public static void exeCommand(String host, int port, String user, String password)
            throws IOException {
        Scanner s = new Scanner(System.in);
        Connection connection = null;
        try {
            connection = new Connection(host, port);
            connection.connect();// 连接
            boolean flag = connection.authenticateWithPassword(user, password);// 认证
            if (flag) {
                System.out.println("================登录成功==================");
            }
        } catch (IOException e) {
            System.out.println("=========登录失败=========" + e);
            connection.close();
            return;
        }
        //打开连接
        Session session = connection.openSession();
        //打开bash
        session.requestPTY("bash");
//              session.requestPTY("xterm", 90, 30, 0, 0, null);

        session.startShell();

        //启动多线程，来获取我们运行的结果
        //第一个参数  输入流
        //第二个参数  输出流，这个直接输出的是控制台
        ReadThread is = new ReadThread(session.getStdout(), new PrintStream(System.out));
        new Thread(is).start();

        ReadThread error = new ReadThread(session.getStderr(), new PrintStream(System.out));
        new Thread(error).start();

        Scanner scanner = new Scanner(System.in);
        while (true) {
            String commondStr = scanner.nextLine();
            // 输出流
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(session.getStdin(), "utf-8"));
            out.write(commondStr + "\n");
            out.flush();

            if (commondStr.equals("exit")) {
                //停止线程
                is.stopThread();
                error.stopThread();
                session.close();
                connection.close();
                scanner.close();
            }
        }
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


//   http://www.ganymed.ethz.ch/ssh2/
}
