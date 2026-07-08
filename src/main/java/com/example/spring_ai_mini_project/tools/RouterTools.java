package com.example.spring_ai_mini_project.tools;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Service;

/**
 * Tools available to the /chat/support customer-support agent.
 */
@Slf4j
@Service
public class RouterTools {

    @Tool(description = "Reboots (restarts / power-cycles) the customer's router by its serial "
            + "number. Call this when the customer asks to restart, reboot, or power-cycle their "
            + "router, or says their internet is down and wants it restarted.")
    public String rebootRouter(
            @ToolParam(description = "The router serial number, e.g. RX500-00123") String serialNo) {
        log.info("Rebooting router with serial number: {}", serialNo);
        return "Router %s has been rebooted successfully. Please allow ~30 seconds for it to come back online."
                .formatted(serialNo);
    }
}
