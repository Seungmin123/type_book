package com.muzlive.kitpage.kitpage.config.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.sql.SQLException;
import javax.sql.DataSource;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "spring.datasource.hikari")
public class HikariPoolConfig extends HikariConfig {

	public HikariPoolConfig() {}

	@Bean
	public DataSource dataSource() throws SQLException {
		return new HikariDataSource(this);
	}

}
