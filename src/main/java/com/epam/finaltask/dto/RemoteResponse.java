package com.epam.finaltask.dto;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Data
@Slf4j
public class RemoteResponse {
    private boolean succeeded;
    private String statusCode;
    private String statusMessage;
    private List<?> results;

    public static RemoteResponse create(boolean succeeded, String statusCode, String statusMessage, List<?> additionalElements) {
        RemoteResponse response = new RemoteResponse();
        response.setSucceeded(succeeded);
        response.setStatusCode(statusCode);
        response.setStatusMessage(statusMessage);
        response.setResults(additionalElements);


        String logMessage = String.format(
                "Response details: succeeded=%s, statusCode=%s, statusMessage=%s, results=%s",
                succeeded, statusCode, statusMessage, additionalElements
        );

        if (succeeded) {
            log.info(logMessage);
        } else {
            log.error(logMessage);
        }
        return response;
    }
}
