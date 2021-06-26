package com.opefago.lib.models;

import com.opefago.lib.common.types.Status;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CodeStatus {
    private Status status;
    private String message;
}
