package com.simile.plan.ssh;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SshSource {
    private static final int DEFAULT_PORT = 22;

    private String host;

    private int port = DEFAULT_PORT;

    private String user;

    private String password;
}
