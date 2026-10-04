package fr.univjardinage.jardinage.config;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

@Slf4j
public class LoggingInterceptor implements ClientHttpRequestInterceptor {

    @Override
    public ClientHttpResponse intercept(
            HttpRequest request,
            byte[] body,
            ClientHttpRequestExecution execution
    ) throws IOException {

        // Log de la requête
        logRequest(request, body);

        long startTime = System.currentTimeMillis();

        // Exécution de la requête HTTP
        ClientHttpResponse response = execution.execute(request, body);

        // Calcul de la durée
        long duration = System.currentTimeMillis() - startTime;

        // Log de la réponse
        logResponse(response, duration);

        return response;
    }

    /**
     * Log les informations de la requête HTTP.
     */
    private void logRequest(HttpRequest request, byte[] body) {

        log.info("========== Requête HTTP ==========");
        log.info("URI : {} {}", request.getMethod(), request.getURI());
        log.info("Headers : {}", request.getHeaders());

        if (body.length > 0) {
            String bodyString = new String(
                    body,
                    StandardCharsets.UTF_8
            );

            log.info("Body : {}", bodyString);
        }
    }

    /**
     * Log les informations de la réponse HTTP.
     */
    private void logResponse(
            ClientHttpResponse response,
            long duration
    ) throws IOException {

        log.info("========== Réponse HTTP ==========");
        log.info(
                "Status : {} {}",
                response.getStatusCode(),
                response.getStatusText()
        );
        log.info("Headers : {}", response.getHeaders());
        log.info("Durée : {} ms", duration);

        String body = new BufferedReader(
                new InputStreamReader(
                        response.getBody(),
                        StandardCharsets.UTF_8
                )
        )
                .lines()
                .collect(Collectors.joining("\n"));

        log.info("Body : {}", body);
    }
}