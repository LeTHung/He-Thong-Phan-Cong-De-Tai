#!/usr/bin/env sh
set -eu

BASE_DIR="$(CDPATH= cd -- "$(dirname -- "$0")" && pwd)"
MAVEN_VERSION="3.9.16"
MAVEN_HOME="$BASE_DIR/.mvn/apache-maven-$MAVEN_VERSION"
MAVEN_BIN="$MAVEN_HOME/bin/mvn"
MAVEN_ZIP="$BASE_DIR/.mvn/wrapper/apache-maven-$MAVEN_VERSION-bin.zip"
MAVEN_URL="https://dlcdn.apache.org/maven/maven-3/$MAVEN_VERSION/binaries/apache-maven-$MAVEN_VERSION-bin.zip"

if [ ! -x "$MAVEN_BIN" ]; then
  echo "Downloading Apache Maven $MAVEN_VERSION..."
  mkdir -p "$(dirname "$MAVEN_ZIP")"
  if command -v curl >/dev/null 2>&1; then
    curl -fsSL "$MAVEN_URL" -o "$MAVEN_ZIP"
  else
    wget -q "$MAVEN_URL" -O "$MAVEN_ZIP"
  fi
  unzip -q -o "$MAVEN_ZIP" -d "$BASE_DIR/.mvn"
fi

exec "$MAVEN_BIN" "$@"
