package com.simile.plan.ssh;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SshResult {

    private int code;

    private Object out;

    private String error;
}
