#!/usr/bin/env python3
"""Sets the version of the QR bill generator.

Usage: scripts/set_version.py [--check] <version>

The version must be of the form X.Y.Z or X.Y.Z-SNAPSHOT. It is applied to the generator's
POM. The examples and the README are not touched; they refer to the released version and
are updated with sync_example_versions.py.

With --check, no file is modified. Instead, the script fails unless the POM already
carries the given version. The release workflow uses it to verify the tag.
"""

import re
import sys
from pathlib import Path

VERSION_PATTERN = re.compile(r"^\d+\.\d+\.\d+(-SNAPSHOT)?$")

ROOT = Path(__file__).resolve().parent.parent

POM = "generator/pom.xml"
# Group 1 is the version.
POM_VERSION = r"<artifactId>qrbill-generator</artifactId>\s*\n\s*<version>([^<]+)</version>"


def find_version(text):
    """Returns the match of the POM's version (group 1 is the version)."""
    matches = list(re.finditer(POM_VERSION, text))
    if len(matches) != 1:
        raise SystemExit(f"{POM}: expected 1 match of /{POM_VERSION}/, found {len(matches)}")
    return matches[0]


def main():
    args = sys.argv[1:]
    check = args[:1] == ["--check"]
    if check:
        args = args[1:]
    if len(args) != 1:
        raise SystemExit(f"usage: {sys.argv[0]} [--check] <version>")

    version = args[0]
    if not VERSION_PATTERN.match(version):
        raise SystemExit(f"invalid version: {version} (expected X.Y.Z or X.Y.Z-SNAPSHOT)")

    file = ROOT / POM
    text = file.read_text(encoding="utf-8")
    match = find_version(text)

    if check:
        if match.group(1) != version:
            raise SystemExit(f"{POM}: found version {match.group(1)}, expected {version}")
        print(f"version is {version}")
        return

    file.write_text(text[:match.start(1)] + version + text[match.end(1):], encoding="utf-8")
    print(f"version set to {version}")


if __name__ == "__main__":
    main()
