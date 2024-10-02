package com.digibank.agentmanagement;

import com.digibank.agentmanagement.filter.RequestAndResponseLoggingFilter;
import com.digibank.agentmanagement.filter.TransactionFilter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@EnableEurekaClient
public class AgentManagementApplication extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(AgentManagementApplication.class, args);
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

//    @Bean
//    public FilterRegistrationBean<RequestAndResponseLoggingFilter> loggingFilter() {
//        FilterRegistrationBean<RequestAndResponseLoggingFilter> registrationBean = new FilterRegistrationBean<>();
//
//        registrationBean.setFilter(new RequestAndResponseLoggingFilter());
//        registrationBean.addUrlPatterns("/api-public/*");
//
//        return registrationBean;
//    }

	@Bean
	public FilterRegistrationBean<TransactionFilter> transactionFilter() {
		FilterRegistrationBean<TransactionFilter> registrationBean = new FilterRegistrationBean<>();

		registrationBean.setFilter(new TransactionFilter());
		registrationBean.addUrlPatterns("/api-public/*");

		return registrationBean;
	}
}
