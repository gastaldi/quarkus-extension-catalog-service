# Quarkus Extension Catalog service

This project provides a model and a parser API to handle Quarkus extension repositories.

A repository is a local directory (which can be cloned from a Git repository) with the following structure: 

```bash
.
├── extensions
│   └── jsf.yaml
└── platforms
    └── quarkus-bom.yaml
```

## Extensions
For maintenance purposes, each extension is declared in its own file and has the following structure:

```json
{
  "group-id": "org.apache.myfaces.core.extensions.quarkus",
  "artifact-id": "quarkus-myfaces",
  "releases": [
    {
      "version": "2.3-next",
      "quarkus-core":  "1.3.1.Final"
    },
    {
      "version": "2.4-next",
      "quarkus-core": "1.3.2.Final"
    }
  ]
}
``` 

The Quarkus extension MUST be released to a Maven repository. The descriptor states the GAV and the Quarkus version for each release (which is listed here for performance purposes - if not specified, the parser will attempt to resolve using the Maven Resolver API)


## Platforms 

Platforms are a set of extensions of a specific version and MUST exist as a BOM. 

```json
{
  "group-id": "io.quarkus",
  "artifact-id": "quarkus-universe-bom",
  "releases": [
    {
      "version": "1.3.1.Final"
    },
    {
      "version": "1.3.2.Final"
    }
  ]
}
```

At this point, there is a simple extension repository specification file in YAML format (an example can be found in `playground\quarkus-extensions-repo.yaml`)
that lists Maven coordinates of the existing platform BOMs as well as Maven coordinates of extensions that aren't appearing in any platform.
Theoretically, this kind of file could be exposed to the users and be defining the content of an extension repository (e.g. Quarkus community extension repository).

There is a repository builder (implemented in the `builder` module) that can parse the spec yaml file and build the corresponding object model that
allows to perform all sorts of queries accross the repository, such as:

* which Quarkus Core versions are supported in the repository;
* which platforms are available;
* which extensions are available.

For a given Quarkus Core version:

* list the available platforms;
* list all the extensions including those that aren't a part of any platform.

For a given platform:

* list the platform versions available (i.e. releases of this platform);
* list Quarkus Core versions it supports.

For a given platform release:

* Quarkus Core version;
* Extension releases that are included.

For a given extension:

* list available versions of the extension (i.e. releases of the extension);
* (The list could be easily extended)

For a given extension release:

* Quarkus Core versions it was found to be compatible with;
* Platforms it is appearing in.

The builder may take time to process the spec file and initialize the in-memory representation of the repo. However, once built, the repo
could be persisted in another form that is more optimal to initialize from, if necessary. The idea to have the simplest possible format
exposed to the users and admins to define the extension repository.

Also, just in case, the repo could also be initialized in a static-init method.

## To give it a try

1. `mvn install` This will install the `api` and the `neo4j` modules into the local Maven repo

## Running the application in dev mode

You can run your application in dev mode that enables live coding using:
```
./mvnw quarkus:dev
```

## Packaging and running the application

The application can be packaged using `./mvnw package`.
It produces the `catalog-runner.jar` file in the `/target` directory.
Be aware that it’s not an _über-jar_ as the dependencies are copied into the `target/lib` directory.

The application is now runnable using `java -jar target/catalog-runner.jar`.

## Creating a native executable

You can create a native executable using: `./mvnw package -Pnative`.

Or, if you don't have GraalVM installed, you can run the native executable build in a container using: `./mvnw package -Pnative -Dquarkus.native.container-build=true`.

You can then execute your native executable with: `./target/catalog-runner`

If you want to learn more about building native executables, please consult the https://quarkus.io/guides/building-native-image-guide.


