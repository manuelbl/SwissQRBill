#!/usr/bin/env python3
"""Updates the examples and the README to use the current version of the QR bill generator.

Usage: scripts/sync_example_versions.py

The version is read from the generator's POM. The examples and the README usually refer
to the released version, i.e. they are updated as part of a release, after the version
has been set to the version being released. The CI pipeline runs this script to build
the examples with the version under development.
"""

import re
from pathlib import Path

ROOT = Path(__file__).resolve().parent.parent

VERSION_SOURCE = "generator/pom.xml"
VERSION_SOURCE_PATTERN = r"<artifactId>qrbill-generator</artifactId>\s*\n\s*<version>([^<]+)</version>"

# Maven dependency with an exact version. Group 1 is the version.
MAVEN_EXACT = r"<artifactId>qrbill-generator</artifactId>\s*\n\s*<version>([^<,\[\]()]+)</version>"
# Maven dependency with a version range. Group 1 is the lower bound.
MAVEN_RANGE = r"<artifactId>qrbill-generator</artifactId>\s*\n\s*<version>\[([^,]+),"
# Gradle dependency, in Groovy or Kotlin syntax, with an exact version. Group 1 is the version.
GRADLE_EXACT = r"""net\.codecrete\.qrbill:qrbill-generator:([^"'+]+)["']"""
# Gradle dependency with a minimum version (X.Y.Z+). Group 1 is the version.
GRADLE_MINIMUM = r"""net\.codecrete\.qrbill:qrbill-generator:([^"'+]+)\+["']"""

# File, regular expression matching the version, and the expected number of matches.
REPLACEMENTS = [
    ("examples/maven_example/pom.xml", MAVEN_EXACT, 1),
    ("examples/jasper_reports_rendering/pom.xml", MAVEN_EXACT, 1),
    ("examples/gradle_example/build.gradle", GRADLE_EXACT, 1),
    ("examples/append_to_pdf/build.gradle", GRADLE_EXACT, 1),
    ("examples/kotlin_example/build.gradle.kts", GRADLE_EXACT, 1),
    ("README.md", MAVEN_RANGE, 1),
    ("README.md", GRADLE_MINIMUM, 1),
]


def replace_version(path, pattern, expected_matches, version):
    """Replaces the version matched by group 1 of the pattern, in the given file."""
    file = ROOT / path
    text = file.read_text(encoding="utf-8")
    matches = list(re.finditer(pattern, text, flags=re.M))
    if len(matches) != expected_matches:
        raise SystemExit(
            f"{path}: expected {expected_matches} match(es) of /{pattern}/, found {len(matches)}")

    for match in reversed(matches):
        text = text[:match.start(1)] + version + text[match.end(1):]
    file.write_text(text, encoding="utf-8")


def current_version():
    """Returns the version of the generator."""
    text = (ROOT / VERSION_SOURCE).read_text(encoding="utf-8")
    match = re.search(VERSION_SOURCE_PATTERN, text)
    if match is None:
        raise SystemExit(f"{VERSION_SOURCE}: version not found")
    return match.group(1)


def main():
    version = current_version()
    for path, pattern, expected_matches in REPLACEMENTS:
        replace_version(path, pattern, expected_matches, version)

    print(f"examples and README set to {version}")


if __name__ == "__main__":
    main()
