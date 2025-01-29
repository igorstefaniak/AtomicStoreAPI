package pl.projekt.store;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.devtools.restart.RestartScope;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

import jakarta.annotation.PreDestroy;


@SpringBootApplication
public class AtomicStoreApplication {

    private PostgreSQLContainer<?> postgresContainer;

    @SuppressWarnings("resource")
    @Bean
	@ServiceConnection
	@RestartScope
    public PostgreSQLContainer<?> postgresContainer() {
        DockerImageName customImage = DockerImageName.parse("zajacp/postgresql-pl:15.3")
                .asCompatibleSubstituteFor("postgres");
    
        PostgreSQLContainer<?> container = new PostgreSQLContainer<>(customImage)
                .withDatabaseName("atomic_store")
                .withEnv("POSTGRES_INITDB_ARGS", "--encoding=UTF-8 --lc-collate=pl_PL.UTF-8 --lc-ctype=pl_PL.UTF-8");
    
        container.start();
        return container;
    }
    

    @PreDestroy
    public void stopContainer() {
        if (postgresContainer != null) {
            postgresContainer.close();
        }
    }

    public static void main(String[] args) {
        SpringApplication.run(AtomicStoreApplication.class, args);

        /** 
         * TODO ponizsza linijka sluzy do szyfrowania Stringa do Stringa ASCII zawierającego reprezentacje w Base64

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        System.out.println(encoder.encode("a")); 
        
        **/
    }
}