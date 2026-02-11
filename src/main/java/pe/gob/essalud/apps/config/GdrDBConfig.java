package pe.gob.essalud.apps.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

@Configuration
@EnableJpaRepositories(
        entityManagerFactoryRef = "gdrEntityManagerFactory",
        transactionManagerRef = "gdrTransactionManager",
        basePackages = "pe.gob.essalud.apps.repository.gdr"
)
public class GdrDBConfig {

    /**
     * Para perfil LOCAL: usa BD GDR separada (localhost)
     */
    @Bean(name = "gdrDataSource")
    @Profile("local")
    @ConfigurationProperties(prefix = "gdr.datasource")
    public DataSource gdrDataSourceLocal() {
        return DataSourceBuilder.create().build();
    }

    /**
     * Para perfiles DEV/PROD: usa la misma BD que spring.datasource
     */
    @Bean(name = "gdrDataSource")
    @Profile({"dev", "prod"})
    public DataSource gdrDataSourceRemote(@Qualifier("dataSource1") DataSource mainDataSource) {
        return mainDataSource;
    }

    @Bean(name = "gdrEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean gdrEntityManagerFactory(
            EntityManagerFactoryBuilder builder,
            @Qualifier("gdrDataSource") DataSource dataSource) {
        return builder
                .dataSource(dataSource)
                .packages("pe.gob.essalud.apps.model.gdr")
                .persistenceUnit("gdrPersistenceUnit")
                .build();
    }

    @Bean(name = "gdrTransactionManager")
    public PlatformTransactionManager gdrTransactionManager(
            @Qualifier("gdrEntityManagerFactory") EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }
}
