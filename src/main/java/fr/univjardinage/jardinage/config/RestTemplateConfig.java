package fr.univjardinage.jardinage.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.converter.json.JacksonJsonHttpMessageConverter;

import java.util.List;

@Configuration
public class RestTemplateConfig {

    @Bean
    public RestTemplate restTemplate() {
        RestTemplate restTemplate =
                new RestTemplate(clientHttpRequestFactory());

        restTemplate.setErrorHandler(
                new CustomResponseErrorHandler()
        );

        restTemplate.setInterceptors(
                List.of(new LoggingInterceptor())
        );

        restTemplate.getMessageConverters().add(
                new JacksonJsonHttpMessageConverter()
        );

        return restTemplate;
    }

    private ClientHttpRequestFactory clientHttpRequestFactory() {
        SimpleClientHttpRequestFactory factory =
                new SimpleClientHttpRequestFactory();

        factory.setConnectTimeout(5000);
        factory.setReadTimeout(10000);

        return new BufferingClientHttpRequestFactory(factory);
    }
}