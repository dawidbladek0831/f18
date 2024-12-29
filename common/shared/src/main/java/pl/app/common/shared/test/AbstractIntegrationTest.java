package pl.app.common.shared.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.ComposeContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.lifecycle.Startables;

import java.io.File;


@Testcontainers
public abstract class AbstractIntegrationTest {
    private static final Logger logger = LoggerFactory.getLogger(AbstractIntegrationTest.class);
    public static ComposeContainer environment = new ComposeContainer(new File("src/test/resources/docker-compose.yaml"))
            // {"t":{"$date":"2024-08-14T14:06:03.920+00:00"},"s":"I",  "c":"CONTROL",  "id":8423403, "ctx":"initandlisten","msg":"mongod startup complete",...
            .waitingFor("mongo", Wait.forLogMessage(".*\"msg\":\"mongod startup complete\".*", 1))
            //[2024-08-14 13:54:32,527] INFO [KafkaRaftServer nodeId=0] Kafka Server started (kafka.server.KafkaRaftServer)
            .waitingFor("kafka", Wait.forLogMessage(".*Kafka Server started.*", 1));

    static {
        logger.info("Starting docker environment");
        Startables.deepStart(environment).join();
        logger.info("Started docker environment");
    }

    @DynamicPropertySource
    static void redisProperties(DynamicPropertyRegistry registry) {
        logger.info("Setting dynamic properties to registry");
        registry.add("spring.data.mongodb.port", () -> "27018");
        registry.add("app.kafka.bootstrap.servers", () -> "127.0.0.1:19093");
    }
}