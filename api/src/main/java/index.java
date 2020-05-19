//usr/bin/env jbang "$0" "$@" ; exit $?
//DEPS info.picocli:picocli:4.2.0
//DEPS io.quarkus.extensions:quarkus-extension-catalog-api:1.0.0.Alpha4

import java.nio.file.Path;
import java.util.concurrent.Callable;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.extensions.catalog.DefaultArtifactResolver;
import io.quarkus.extensions.catalog.RepositoryIndexer;
import io.quarkus.extensions.catalog.memory.MemoryExtensionRegistry;
import io.quarkus.extensions.catalog.model.Repository;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

@Command(name = "index", mixinStandardHelpOptions = true, version = "0.1",
        description = "Index Catalog")
class index implements Callable<Integer> {

    @Parameters(index = "0", description = "The repository path to index")
    private Path repositoryPath;

    @Parameters(index = "1", description = "The output file")
    private Path outputFile;

    public static void main(String... args) {
        int exitCode = new CommandLine(new index()).execute(args);
        System.exit(exitCode);
    }

    @Override
    public Integer call() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        MemoryExtensionRegistry registry = new MemoryExtensionRegistry();
        Repository repository = Repository.parse(repositoryPath, mapper);
        RepositoryIndexer indexer = new RepositoryIndexer(new DefaultArtifactResolver(mapper));
        indexer.index(repository, registry);
        registry.serialize(outputFile);
        return 0;
    }
}
