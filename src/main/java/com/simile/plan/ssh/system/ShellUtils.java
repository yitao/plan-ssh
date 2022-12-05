package com.simile.plan.ssh.system;

import java.io.BufferedReader;
import java.io.File;
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

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;


/**
 * 脚本工具类 created by yitao on 2020/03/16
 */
@Slf4j
public class ShellUtils {

    public static int runCmd(ShellRunContext shellRunContext, String[] cmd) throws IOException, InterruptedException {
        File workDir = new File(shellRunContext.getWorkDir());
//        Runtime runtime = Runtime.getRuntime();
//        Process process = runtime.exec(cmd, new String[]{}, workDir);
        ProcessBuilder pb = new ProcessBuilder(cmd);
        pb.directory(workDir);
        pb.redirectErrorStream(true);
        Process process = pb.start();

        if (shellRunContext.isNeedLog()) {
            ExecutorService executor = Executors.newFixedThreadPool(2);
            StreamConsume std = new StreamConsume(process.getInputStream(), "stdout");
            StreamConsume error = new StreamConsume(process.getErrorStream(), "error");
            Future<List<String>> stdFuture = executor.submit(std);
            Future<List<String>> errorFuture = executor.submit(error);

            shellRunContext.setStdFuture(stdFuture);
            shellRunContext.setErrorFuture(errorFuture);
            executor.shutdown();
        }
        int code = process.waitFor();
        log.debug("code:{}", code);
        process.destroyForcibly();
        process.destroy();
        return code;
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
//                log.debug("cmd {}:{}", streamType, line);
                result.add(line.trim());
            }
            br.close();
            inputStream.close();
            return result;
        }
    }


    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ShellRunContext {
        private String workDir;
        private boolean needLog;
        private Future<List<String>> stdFuture;
        private Future<List<String>> errorFuture;

        private Long timeout;
        private TimeUnit timeUnit;

        public static ShellRunContext _default() {
            ShellRunContext context = new ShellRunContext();
            context.setNeedLog(true);
            return context;
        }
    }

}
