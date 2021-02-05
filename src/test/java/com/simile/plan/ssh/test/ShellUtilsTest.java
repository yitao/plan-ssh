package com.simile.plan.ssh.test;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Scanner;

import org.junit.Test;

/**
 * @author yitao
 * @since 2020-12-10
 */
public class ShellUtilsTest {

    @Test
    public void runCmd() {
    }


    public static void main(String[] args) throws IOException {
        Scanner s = new Scanner(System.in);

        ProcessBuilder pb = new ProcessBuilder("/bin/sh","-c","ssh work@192.168.1.147");
        pb.redirectErrorStream(true);
        Process p = pb.start();
        OutputStream os = p.getOutputStream();
        InputStream in = p.getInputStream();
//        InputStream err = p.getErrorStream();

        PrintWriter pw = new PrintWriter(os);
        BufferedReader br = new BufferedReader(new InputStreamReader(in));
        String out;
        while (true) {
            System.out.print(">");
            String n = s.next();
            pw.println(n);
            pw.flush();
            while((out = br.readLine())!=null){
                System.out.println(out);
            }
            if ("exit".equals(n)) {
                br.close();
                os.close();
                break;
            }
        }
    }
}