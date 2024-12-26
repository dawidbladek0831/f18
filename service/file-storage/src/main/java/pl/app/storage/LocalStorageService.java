package pl.app.storage;

import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

@Component
class LocalStorageService implements StorageService {
    private static final Logger logger = LoggerFactory.getLogger(LocalStorageService.class);
    private final Environment environment;
    private Path fileStorageRootPath;

    public LocalStorageService(Environment environment) throws IOException {
        this.environment = environment;
        init();
    }

    public void init() throws IOException {
        try {
            Path path = Path.of(
                    System.getProperty("user.home"),
                    environment.getRequiredProperty("application.storage.local.path"));
            fileStorageRootPath = Files.createDirectories(path);
            logger.info("Temporary files location '{}'", fileStorageRootPath.toString());
        } catch (IOException e) {
            logger.error("Failed to create local directory to store files");
            throw e;
        }
    }

    @Override
    public Mono<Void> init(ObjectId containerId) {
        final Path pathOfDirectory = pathOfDirectory(containerId);
        if (!Files.isDirectory(pathOfDirectory)) {
            try {
                Files.createDirectories(pathOfDirectory);
            } catch (IOException e) {
                return Mono.error(() -> new RuntimeException("Failed to create local directory to store files"));
            }
        }
        return Mono.empty();
    }

    @Override
    public Mono<byte[]> get(ObjectId containerId, String storageId) {
        final Path path = pathOfFile(containerId, storageId);
        try {
            byte[] bytes = Files.readAllBytes(path);
            return Mono.just(bytes);
        } catch (IOException e) {
            return Mono.empty();
        }
    }

    @Override
    public Mono<Void> create(ObjectId containerId, String storageId, byte[] content) {
        final Path path = pathOfFile(containerId, storageId);
        try {
            Files.write(path, content, StandardOpenOption.CREATE_NEW);
        } catch (IOException e) {
            return Mono.error(() -> new RuntimeException("Failed to create file"));
        }
        return Mono.empty();
    }

    @Override
    public Mono<Void> copy(ObjectId containerId, String storageId, String newStorageId) {
        return get(containerId, storageId)
                .flatMap(bytes -> create(containerId, newStorageId, bytes));
    }

    @Override
    public Mono<Void> delete(ObjectId containerId, String storageId) {
        final Path path = pathOfFile(containerId, storageId);
        try {
            Files.delete(path);
        } catch (IOException e) {
            return Mono.error(() -> new RuntimeException("Failed to delete file"));
        }
        return Mono.empty();
    }

    private Path pathOfFile(ObjectId containerId, String storageId) {
        Path relativeFilePath = containerId != null ? Path.of(containerId.toHexString(), storageId) : Path.of(storageId);
        return fileStorageRootPath.resolve(relativeFilePath);
    }

    private Path pathOfDirectory(ObjectId containerId) {
        Path relativeFilePath = Path.of(containerId.toHexString());
        return fileStorageRootPath.resolve(relativeFilePath);
    }
}
