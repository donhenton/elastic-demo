package com.dhenton9000.elastic.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.ErrorPage;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import springfox.documentation.builders.ApiInfoBuilder;
import static springfox.documentation.builders.PathSelectors.regex;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@SpringBootApplication
@EnableSwagger2
public class ElasticDemoApplication {

    @Bean
    public WebServerFactoryCustomizer<TomcatServletWebServerFactory> containerCustomizer() {
        return (server -> {

            ErrorPage custom404Page = new ErrorPage(HttpStatus.NOT_FOUND, "/404");
            server.addErrorPages(custom404Page);

        });
    }

    // boot 2.0 migration guide 
    // https://github.com/spring-projects/spring-boot/wiki/Spring-Boot-2.0-Migration-Guide
    /*
    
    use to support CORS
    https://spring.io/guides/gs/rest-service-cors/
     */
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebConfigurator();
    }

    @Bean
    public Docket bookSearchApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("books")
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.any())
                //.paths(paths())
                .paths(regex("/books/search/.*"))
                .build();
    }

    @Bean
    public Docket githubApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("github")
                .apiInfo(githubInfo())
                .select()
                .paths(regex("/github/search/.*"))
                .build();
    }

    private ApiInfo githubInfo() {
        Contact c = new Contact("Don Henton", "http://donhenton.com", null);
        return new ApiInfoBuilder()
                .title("GitHub Search")
                .description("Github Search")
                .contact(c)
                .license("Apache License Version 2.0")
                .version("1.0")
                .build();
    }

    private ApiInfo apiInfo() {
        Contact c = new Contact("Don Henton", "http://donhenton.com", null);
        return new ApiInfoBuilder()
                .title("Search")
                .description("Search")
                .contact(c)
                .license("Apache License Version 2.0")
                .version("1.0")
                .build();
    }

    public static void main(String[] args) {
        SpringApplication.run(ElasticDemoApplication.class, args);
    }

    @Bean
    public RestTemplate getTemplate() {
        RestTemplate restTemplate =  new RestTemplate(getClientHttpRequestFactory());
        
         restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
        
        return restTemplate;
    }

    private ClientHttpRequestFactory getClientHttpRequestFactory() {
        int timeout = 5000;
        HttpComponentsClientHttpRequestFactory clientHttpRequestFactory
                = new HttpComponentsClientHttpRequestFactory();
        clientHttpRequestFactory.setConnectTimeout(timeout);
        return clientHttpRequestFactory;
    }

}
