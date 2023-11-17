package com.mes.techdebt.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableJpaRepositories({ "com.cargill.techdebt.repository" })
@EnableTransactionManagement
public class DatabaseConfiguration {}
