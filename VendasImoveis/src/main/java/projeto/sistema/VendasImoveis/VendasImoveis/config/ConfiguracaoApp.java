package projeto.sistema.VendasImoveis.VendasImoveis.config;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class ConfiguracaoApp implements WebMvcConfigurer {

	@Bean
	public DataSource dataSource() {

		DriverManagerDataSource dataSource = new DriverManagerDataSource();

		dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
		dataSource.setUrl("jdbc:mysql://localhost:3306/vendasImovel");
		dataSource.setUsername("root");
		dataSource.setPassword("root");

		return dataSource;
	}

	@Bean
	public JpaVendorAdapter jpaVendorAdapter() {

		HibernateJpaVendorAdapter adapter = new HibernateJpaVendorAdapter();

		adapter.setDatabase(Database.MYSQL);

		// esta relacionado com a versão do mysql usado...
		adapter.setDatabasePlatform("org.hibernate.dialect.MySQL8Dialect");

		// Faz com que Gere as funções sql
		// adapter.setShowSql(true);

		adapter.setPrepareConnection(true);

		// faz com que gere os comandos sql automaticamente (CRIA TABELAS)
		adapter.setGenerateDdl(true);

		return adapter;

	}

	@Override
	public void addCorsMappings(CorsRegistry registry) {
		registry.addMapping("/**") .allowedOrigins("http://localhost:3000")
        .allowedMethods("GET", "POST", "PUT", "DELETE");;
	} 

}
