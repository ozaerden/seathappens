package com.seathappens.common.context;

import org.slf4j.MDC;

import java.util.Optional;

public final class CorrelationIdContext {

    public static final String MDC_KEY = "correlationId";

    private CorrelationIdContext() {
    }

    public static Optional<String> getCurrentCorrelationId() {
        return Optional.ofNullable(MDC.get(MDC_KEY))
                .filter(correlationId -> !correlationId.isBlank());
    }
}
