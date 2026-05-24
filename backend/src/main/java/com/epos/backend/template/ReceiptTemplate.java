package com.epos.backend.template;

import java.util.Map;

public interface ReceiptTemplate {

    public String templateVersion();
    public String renderText(Map<String, Object> payload);
}
