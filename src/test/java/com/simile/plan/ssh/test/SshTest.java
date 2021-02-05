package com.simile.plan.ssh.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Scanner;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import org.apache.commons.io.IOUtils;

/**
 * @author yitao
 * @since 2020-12-10
 */
public class SshTest {

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


        while (true) {
            ChannelExec channelExec = (ChannelExec) session.openChannel("exec");
            InputStream in = channelExec.getInputStream();
//            BufferedReader br= new BufferedReader(new InputStreamReader(in));
            System.out.print(">");
            String n = s.next();
            channelExec.setCommand(n);
            channelExec.setErrStream(System.err);
            channelExec.connect();

            String out = IOUtils.toString(in, "UTF-8");
            System.out.println(out);
            if ("exit".equals(n)) {
                break;
            }
            channelExec.disconnect();
        }
        session.disconnect();
    }

//    http://www.jcraft.com/jsch/examples/ScpTo.java.html
}
