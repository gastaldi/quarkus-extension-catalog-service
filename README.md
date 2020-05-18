# Quarkus Extension Catalog service

This project provides a model and a parser API to handle Quarkus extension repositories.

A repository is a local directory (which can be cloned from a Git repository) with the following structure: 

```bash
.
├── extensions
│   └── jsf.json
└── platforms.json
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

Platforms are a set of extensions of a specific version and MUST exist as a BOM. Since the order is important, it is declared as a single JSON (ordered by priority - the preferred BOMs in the top)

```json
[
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
  },
  {
    "group-id": "io.quarkus",
    "artifact-id": "quarkus-bom",
    "artifact-id-json": "quarkus-bom-descriptor-json",
    "releases": [
      {
        "version": "1.3.1.Final"
      },
      {
        "version": "1.3.2.Final"
      },
      {
        "version": "1.4.0.CR1"
      }
    ]
  }
]
```

The idea is to provide an utility that would allow to perform all sorts of queries across the repository, such as:

* which Quarkus Core versions are available;
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
