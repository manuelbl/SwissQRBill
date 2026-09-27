# Development Cycle and Releasing

The version of the QR bill generator is increased at the start of each development cycle and
carries the suffix `-SNAPSHOT` until it is released:

```shell
scripts/set_version.py 3.4.1-SNAPSHOT
```

The script updates `generator/pom.xml`. It does not touch the examples and the README: they
refer to the released version so that they work for anybody checking out the repository.
The CI pipeline runs `scripts/sync_example_versions.py` to build the examples with the version
under development, ensuring that it does not break them.

## Releasing

Set the version to release and update the examples and the README to use it:

```shell
scripts/set_version.py 3.4.1
scripts/sync_example_versions.py
git commit -a -m "Prepare for release 3.4.1"
git push
```

Tag the release and push the tag:

```shell
git tag v3.4.1
git push origin v3.4.1
```

The tag starts the release workflow (`.github/workflows/release.yml`):

1. `verify` runs the continuous integration build on the tagged commit.
2. `publish` waits for approval in the `release` environment. After it has been approved, it
   checks that the tag matches the version in `generator/pom.xml` and publishes the library to
   Maven Central.
3. `draft-release` creates a draft GitHub release with generated release notes and the
   library, sources and Javadoc jars attached.

Approve the deployment in the workflow run on GitHub. Once the workflow has completed, edit
the draft release: give it a title of the form `v3.4.1: <summary>`, edit the release notes
and publish it.

Maven Central publishes the artifacts immediately (`autoPublish`), so they cannot be withdrawn.
If publishing fails, fix the cause and either delete and push the tag again or publish from a
local checkout of the tag, with the GPG key in the local keyring and the Maven Central user
token configured for the server `central` in `~/.m2/settings.xml`:

```shell
cd generator
./mvnw -Prelease deploy
```

Finally, start the next development cycle:

```shell
scripts/set_version.py 3.4.2-SNAPSHOT
git commit -a -m "Start development of 3.4.2"
git push
```

## One-time setup

The credentials are stored as secrets of the GitHub environment `release`
(*Settings > Environments*):

| Secret             | Content                                                 |
|--------------------|---------------------------------------------------------|
| `CENTRAL_USERNAME` | Username of the Maven Central user token                |
| `CENTRAL_PASSWORD` | Password of the Maven Central user token                |
| `GPG_PRIVATE_KEY`  | ASCII-armored private GPG key for signing the artifacts |
| `GPG_PASSPHRASE`   | Passphrase of the GPG key                               |

The private GPG key is exported with:

```shell
gpg --armor --export-secret-keys <key-id>
```

The environment is protected by:

- *Required reviewers*: the maintainer (with *Prevent self-review* disabled).
- *Deployment branches and tags*: *Selected branches and tags* with the single tag rule `v*`.

Additionally, a tag ruleset (*Settings > Rules > Rulesets*) targeting `v*` restricts the
creation of release tags to the maintainer.
