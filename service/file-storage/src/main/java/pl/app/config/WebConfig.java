package pl.app.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.http.codec.json.Jackson2JsonEncoder;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.web.reactive.config.CorsRegistry;
import org.springframework.web.reactive.config.WebFluxConfigurer;
import org.springframework.web.reactive.result.method.annotation.ArgumentResolverConfigurer;
import pl.app.common.shared.config.CustomArgumentResolver;
import pl.app.common.shared.config.ExceptionAdviceConfig;
import pl.app.common.shared.config.JacksonConfig;
import pl.app.common.shared.config.ModelMapperConfig;

@Configuration
@Import({
        JacksonConfig.class,
        ModelMapperConfig.class,
        ExceptionAdviceConfig.class
})
@RequiredArgsConstructor
public class WebConfig implements WebFluxConfigurer {


    private final Jackson2ObjectMapperBuilder jackson2ObjectMapperBuilder;

    @Override
    public void configureArgumentResolvers(ArgumentResolverConfigurer configurer) {
        WebFluxConfigurer.super.configureArgumentResolvers(configurer);
        configurer.addCustomResolver(new CustomArgumentResolver.PageableHandlerMethodArgumentResolver());
    }

    @Override
    public void configureHttpMessageCodecs(ServerCodecConfigurer configurer) {
        ObjectMapper objectMapper = jackson2ObjectMapperBuilder.build();
        objectMapper.configure(SerializationFeature.FLUSH_AFTER_WRITE_VALUE, true);
        configurer.defaultCodecs().jackson2JsonEncoder(new Jackson2JsonEncoder(objectMapper));
        configurer.defaultCodecs().jackson2JsonDecoder(new Jackson2JsonDecoder(objectMapper));
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("*")
                .allowedMethods("*")
                .allowedHeaders("*")
                .maxAge(3600);
    }
}

