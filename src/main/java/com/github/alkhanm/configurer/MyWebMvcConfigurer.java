package com.github.alkhanm.configurer;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
public class MyWebMvcConfigurer implements WebMvcConfigurer{
    @Override //Capta as requisições paginadas
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        var pageableHandler = new PageableHandlerMethodArgumentResolver();
        //Define que por padrão as páginas começaram a ser exibidas na página 0 (primeira) e possuirão 5 elementos
        pageableHandler.setFallbackPageable(PageRequest.of(0, 5));
        resolvers.add(pageableHandler);
    }
}
