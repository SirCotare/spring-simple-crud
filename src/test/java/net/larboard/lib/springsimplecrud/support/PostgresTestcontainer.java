package net.larboard.lib.springsimplecrud.support;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

@Log4j2
public class PostgresTestcontainer implements BeforeAllCallback {
    public static final PostgreSQLContainer<?> POSTGRES_CONTAINER;

    static {
        POSTGRES_CONTAINER = new PostgreSQLContainer<>(
                DockerImageName.parse("postgres:15")
                        .asCompatibleSubstituteFor("postgres"))
                .withDatabaseName("app")
                // See https://www.postgresql.org/docs/current/non-durability.html for details
                .withCommand("postgres -c max_connections=500 -c fsync=off -c synchronous_commit=off -c full_page_writes=off -c max_wal_size=2GB -c checkpoint_timeout=20min")
                .withUsername("admin")
                .withPassword("password")
                .withReuse(true);

        POSTGRES_CONTAINER.start();
    }

    @Override
    public void beforeAll(ExtensionContext extensionContext) {
        System.setProperty("spring.datasource.url", POSTGRES_CONTAINER.getJdbcUrl());
        System.setProperty("spring.datasource.username", POSTGRES_CONTAINER.getUsername());
        System.setProperty("spring.datasource.password", POSTGRES_CONTAINER.getPassword());
    }
}