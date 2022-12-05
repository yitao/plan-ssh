package com.simile.plan.ssh;

public class SSHException extends RuntimeException {

    public SSHException(String desc) {
        super(desc);
    }

    public SSHException(Throwable throwable) {
        super(throwable);
    }

    public SSHException(String desc, Throwable throwable) {
        super(desc, throwable);
    }
}
