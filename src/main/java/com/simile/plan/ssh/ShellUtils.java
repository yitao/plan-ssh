package com.simile.plan.ssh;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;


/**
 * 脚本工具类 created by yitao on 2020/03/16
 */
public class ShellUtils {

    public static List<String>[] runCmd(String... cmd) throws IOException, InterruptedException, ExecutionException {
        List<String>[] result = new ArrayList[2];
        Runtime runtime = Runtime.getRuntime();
        Process process = runtime.exec(cmd);

        ExecutorService executor = Executors.newFixedThreadPool(2);
        StreamConsume std = new StreamConsume(process.getInputStream(), "stdout");
        StreamConsume error = new StreamConsume(process.getErrorStream(), "error");
        Future<List<String>> stdFuture = executor.submit(std);
        Future<List<String>> errorFuture = executor.submit(error);
        process.waitFor(30, TimeUnit.MINUTES);
        result[0] = stdFuture.get();
        result[1] = errorFuture.get();
        process.destroyForcibly();
		executor.shutdown();
        return result;
    }

    public static class StreamConsume implements Callable<List<String>> {
        private InputStream inputStream;
        private String streamType = null;

        StreamConsume(InputStream inputStream, String streamType) {
            this.inputStream = inputStream;
            this.streamType = streamType;
        }

        @Override
        public List<String> call() throws Exception {
            List<String> result = new ArrayList<>();
            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
            String line = null;
            while ((line = br.readLine()) != null) {
                result.add(line);
            }
            br.close();
            inputStream.close();
            return result;
        }
    }
}
