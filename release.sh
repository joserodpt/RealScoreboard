#!/usr/bin/env bash
# Cut a release: tag the version in pom.xml, push the tag, and have JitPack build it.
#
# For anyone depending on the API module, the tag *is* the release: JitPack builds
# any tag on demand, so consumers never need `git clone && mvn install`. That is
# also why this refuses to tag a commit the remote has not seen - JitPack clones
# from GitHub, so an unpushed commit is invisible to it.
#
# Nothing here is repo-specific: the version comes from the root pom, the
# owner/repo from `origin`. The same script sits in each Real* repo.
set -euo pipefail

PROJECT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

AT_COMMIT=""       # --at: tag something other than HEAD (retro-tagging an old release)
VERSION=""         # --version: override the version read from pom.xml
DRY_RUN=0
SKIP_BUILD=0
SKIP_JITPACK=0

say()  { printf '\033[36m::\033[0m %s\n' "$*"; }
warn() { printf '\033[33mwarning:\033[0m %s\n' "$*" >&2; }
die()  { printf '\033[31merror:\033[0m %s\n' "$*" >&2; exit 1; }

usage() {
  cat <<'USAGE'
usage: ./release.sh [options]

Tags the version declared in pom.xml as v<version>, pushes the tag to origin,
and asks JitPack to build it so the artifacts are ready for consumers.

options:
  --at <commit>     tag this commit instead of HEAD (must already be on origin)
  --version <ver>   use this version instead of the one in pom.xml
  --skip-build      do not run `mvn clean package` before tagging
  --no-jitpack      do not trigger the JitPack build
  --dry-run         show what would happen, change nothing
  -h, --help        this text
USAGE
}

while [[ $# -gt 0 ]]; do
  case "$1" in
    --at)       AT_COMMIT="${2:-}"; [[ -n "$AT_COMMIT" ]] || die "--at needs a commit"; shift 2 ;;
    --version)  VERSION="${2:-}";   [[ -n "$VERSION" ]]   || die "--version needs a value"; shift 2 ;;
    --skip-build)  SKIP_BUILD=1; shift ;;
    --no-jitpack)  SKIP_JITPACK=1; shift ;;
    --dry-run)     DRY_RUN=1; shift ;;
    -h|--help)     usage; exit 0 ;;
    *) die "unknown option '$1' (try --help)" ;;
  esac
done

cd "$PROJECT_DIR"

command -v mvn  >/dev/null 2>&1 || die "Maven is not installed. Install it with: brew install maven"
command -v git  >/dev/null 2>&1 || die "git is not installed"
command -v curl >/dev/null 2>&1 || die "curl is not installed"

# ---------------------------------------------------------------- coordinates

ORIGIN="$(git remote get-url origin 2>/dev/null)" || die "no 'origin' remote"
# Accept both git@github.com:owner/repo.git and https://github.com/owner/repo.git
SLUG="$(printf '%s' "$ORIGIN" | sed -E 's#^.*github\.com[:/]##; s#\.git$##')"
OWNER="${SLUG%%/*}"
REPO="${SLUG##*/}"
[[ -n "$OWNER" && -n "$REPO" && "$OWNER" != "$SLUG" ]] || die "origin is not a GitHub remote: $ORIGIN"

if [[ -z "$VERSION" ]]; then
  VERSION="$(mvn help:evaluate -Dexpression=project.version -q -DforceStdout | tail -n 1)"
  [[ -n "$VERSION" ]] || die "could not read project.version from pom.xml"
fi
case "$VERSION" in
  *SNAPSHOT*) die "refusing to release a snapshot version ($VERSION)" ;;
esac

TAG="v$VERSION"
say "$OWNER/$REPO -> $TAG"

# ---------------------------------------------------------------- preflight

say "fetching origin"
git fetch --quiet --tags origin

TARGET_SHA="$(git rev-parse --verify "${AT_COMMIT:-HEAD}^{commit}" 2>/dev/null)" \
  || die "not a commit: ${AT_COMMIT:-HEAD}"

if git rev-parse -q --verify "refs/tags/$TAG" >/dev/null; then
  die "tag $TAG already exists locally (delete it with: git tag -d $TAG)"
fi
if [[ -n "$(git ls-remote --tags origin "refs/tags/$TAG")" ]]; then
  die "tag $TAG already exists on origin - releases are immutable, bump the version instead"
fi

# JitPack clones from GitHub, so it can only see commits that are on the remote.
if [[ -z "$(git branch -r --contains "$TARGET_SHA" 2>/dev/null)" ]]; then
  die "${TARGET_SHA:0:10} is not on origin yet - push it first, or JitPack cannot build the tag"
fi

if [[ -z "$AT_COMMIT" ]]; then
  # Only meaningful when releasing HEAD; --at deliberately points into history.
  [[ -z "$(git status --porcelain)" ]] || die "working tree is dirty - commit or stash first"

  DEFAULT_BRANCH="$(git rev-parse --abbrev-ref origin/HEAD 2>/dev/null | sed 's#^origin/##' || true)"
  CURRENT_BRANCH="$(git rev-parse --abbrev-ref HEAD)"
  if [[ -n "$DEFAULT_BRANCH" && "$CURRENT_BRANCH" != "$DEFAULT_BRANCH" ]]; then
    warn "on '$CURRENT_BRANCH', not the default branch '$DEFAULT_BRANCH'"
  fi
fi

# ---------------------------------------------------------------- build

if [[ $SKIP_BUILD -eq 0 ]]; then
  say "building (mvn clean package)"
  if [[ $DRY_RUN -eq 1 ]]; then
    say "dry run: skipping the build"
  else
    mvn -B clean package || die "build failed - not tagging a broken tree"
  fi
else
  warn "skipping the build - tagging without verifying it compiles"
fi

# ---------------------------------------------------------------- tag

if [[ $DRY_RUN -eq 1 ]]; then
  say "dry run: would tag ${TARGET_SHA:0:10} as $TAG and push it to origin"
else
  say "tagging ${TARGET_SHA:0:10} as $TAG"
  git tag -a "$TAG" "$TARGET_SHA" -m "$REPO $VERSION"
  git push --quiet origin "refs/tags/$TAG"
  say "pushed $TAG"
fi

# ---------------------------------------------------------------- jitpack

# The dependency coordinate differs between layouts: a multi-module repo publishes
# com.github.<owner>.<repo>:<module artifactId>, a single-module one
# com.github.<owner>:<repo>. The build itself is per-repo either way.
print_coordinates() {
  local group artifact pom
  if grep -q '<module>' pom.xml; then
    group="com.github.$OWNER.$REPO"
    for pom in */pom.xml; do
      [[ -f "$pom" ]] || continue
      # Module poms in these repos declare their own artifactId above <parent>,
      # so the first one in the file is the module's.
      artifact="$(sed -n 's#.*<artifactId>\(.*\)</artifactId>.*#\1#p' "$pom" | head -n 1)"
      [[ -n "$artifact" ]] && printf '    %s:%s:%s\n' "$group" "$artifact" "$TAG"
    done
  else
    printf '    com.github.%s:%s:%s\n' "$OWNER" "$REPO" "$TAG"
  fi
}

if [[ $SKIP_JITPACK -eq 1 || $DRY_RUN -eq 1 ]]; then
  say "skipping the JitPack build"
else
  say "asking JitPack to build $TAG (first build takes a few minutes)"
  LOG="$(mktemp -t jitpack-build)"
  # Requesting build.log is what triggers the build; it blocks until it finishes.
  CODE="$(curl -sS -o "$LOG" -w '%{http_code}' \
    "https://jitpack.io/com/github/$OWNER/$REPO/$TAG/build.log" || true)"
  if [[ "$CODE" == "200" ]] && ! grep -qiE '^(BUILD FAILURE|ERROR:)' "$LOG"; then
    say "JitPack build ok"
  else
    warn "JitPack build did not report success (HTTP $CODE) - last lines:"
    tail -n 15 "$LOG" >&2 || true
    warn "full log: https://jitpack.io/com/github/$OWNER/$REPO/$TAG/build.log"
  fi
  rm -f "$LOG"
fi

echo
say "released $REPO $TAG"
echo "  consumers can depend on it with:"
print_coordinates
echo "  jitpack: https://jitpack.io/#$OWNER/$REPO/$TAG"
