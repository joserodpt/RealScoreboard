#!/usr/bin/env bash
set -euo pipefail

PROJECT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
MODULE_DIR="$PROJECT_DIR/realscoreboard-plugin"
TARGET_DIR="/Users/jose/Desktop/Files/dev/plugins"

if ! command -v mvn >/dev/null 2>&1; then
  echo "Error: Maven is not installed. Install it with: brew install maven"
  exit 1
fi

cd "$PROJECT_DIR"

VERSION="$(mvn -f "$MODULE_DIR/pom.xml" help:evaluate -Dexpression=project.version -q -DforceStdout | tail -n 1)"
if [[ -z "$VERSION" ]]; then
  echo "Error: Could not read plugin version from realscoreboard-plugin/pom.xml"
  exit 1
fi

JAR_NAME="realscoreboard-plugin-${VERSION}.jar"
OUTPUT_JAR_NAME="RealScoreboard-${VERSION}.jar"

echo "Building RealScoreboard..."
mvn -pl realscoreboard-plugin -am clean install

SOURCE_JAR="$MODULE_DIR/target/$JAR_NAME"
if [[ ! -f "$SOURCE_JAR" ]]; then
  SOURCE_JAR="$(ls -t "$MODULE_DIR"/target/realscoreboard-plugin-*.jar 2>/dev/null | grep -v '/original-' | head -n 1 || true)"
  if [[ -z "$SOURCE_JAR" || ! -f "$SOURCE_JAR" ]]; then
    echo "Error: Built jar not found in $MODULE_DIR/target"
    exit 1
  fi
  JAR_NAME="$(basename "$SOURCE_JAR")"
fi

mkdir -p "$TARGET_DIR"
cp "$SOURCE_JAR" "$TARGET_DIR/$OUTPUT_JAR_NAME"

echo "Done: Copied $JAR_NAME to $TARGET_DIR/$OUTPUT_JAR_NAME"
