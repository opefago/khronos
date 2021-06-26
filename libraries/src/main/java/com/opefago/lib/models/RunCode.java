package com.opefago.lib.models;

import com.opefago.lib.common.types.Language;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.UUID;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RunCode {
    private UUID codeId;
    private Language lang;
    private String source;
}
