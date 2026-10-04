package fr.univjardinage.jardinage.config;

import fr.univjardinage.jardinage.exception.BadRequestException;
import fr.univjardinage.jardinage.exception.ClientException;
import fr.univjardinage.jardinage.exception.ResourceNotFoundException;
import fr.univjardinage.jardinage.exception.RestClientException;
import fr.univjardinage.jardinage.exception.ServerException;

import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

@Slf4j
public class CustomResponseErrorHandler
        implements ResponseErrorHandler {

    @Override
    public boolean hasError(ClientHttpResponse response)
            throws IOException {

        return response.getStatusCode().isError();
    }

    @Override
    public void handleError(
            URI url,
            HttpMethod method,
            ClientHttpResponse response
    ) throws IOException {

        HttpStatusCode statusCode = response.getStatusCode();

        String body = new BufferedReader(
                new InputStreamReader(
                        response.getBody(),
                        StandardCharsets.UTF_8
                )
        )
                .lines()
                .collect(Collectors.joining("\n"));

        log.error(
                "Erreur HTTP {} - {} : {}",
                statusCode.value(),
                method,
                body
        );

        if (statusCode.value() == 404) {
            throw new ResourceNotFoundException(
                    "Ressource non trouvée sur le serveur distant"
            );
        }

        if (statusCode.value() == 400) {
            throw new BadRequestException(
                    "Requête invalide : " + body
            );
        }

        if (statusCode.is4xxClientError()) {
            throw new ClientException(
                    "Erreur client : "
                            + statusCode.value()
                            + " - "
                            + body
            );
        }

        if (statusCode.is5xxServerError()) {
            throw new ServerException(
                    "Erreur serveur : "
                            + statusCode.value()
                            + " - "
                            + body
            );
        }

        throw new RestClientException(
                "Erreur HTTP inattendue : "
                        + statusCode.value()
        );
    }
}