package com.simile.plan.ssh.test;

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
import org.apache.commons.io.IOUtils;

/**
 * @author yitao
 * @since 2020-12-10
 */
public class SshTest22 {

    public static void main(String[] args) throws IOException {
        String host = "192.168.1.147";
        int port = 22;
        String user = "work";
        String password = "xingtu@2020";
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


        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.print(">");
            String commondStr = scanner.nextLine();
            // 输出流
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(session.getStdin(), "utf-8"));
            out.write(commondStr + "\n");
            out.flush();
            //输出阻塞了！！！
            System.out.println(IOUtils.toString(session.getStdout(), "UTF-8"));
            System.out.println(IOUtils.toString(session.getStderr(), "UTF-8"));

            if (commondStr.equals("exit")) {
                //停止线程
                session.close();
                connection.close();
                scanner.close();
            }
        }
    }


//   http://www.ganymed.ethz.ch/ssh2/
}
