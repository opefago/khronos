package com.opefago.lib.models.command;

import com.opefago.lib.common.types.Language;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class RunCodeCommand {
    @NotNull
    private Language lang;
    @NotNull
    private String source;
}
