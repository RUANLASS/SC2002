#!/usr/bin/env bash
# Replays every scripted run in test-runs/ against the real game and compares the
# complete console output with the saved .expected file.
#
#   bash test-runs/run_tests.sh            (run from the repository root)
#
# Needs only a JDK (17 or newer). Exit code 0 = every run reproduced exactly.
set -u
ROOT="$(cd "$(dirname "$0")/.." && pwd)"
TMP="$(mktemp -d)"; trap 'rm -rf "$TMP"' EXIT
fail=0

build() {            # build <source dir> <class dir>
  (cd "$1" && javac -d "$2" Main.java engine/*.java model/*.java staff/*.java ui/*.java) 2>&1 | grep -v JAVA_TOOL_OPTIONS
  return "${PIPESTATUS[0]}"
}

declare -A SRC=( [stage-1]="$ROOT" [stage-2]="$ROOT/Stage-2" [stage-3]="$ROOT/Stage-3" )

for stage in stage-1 stage-2 stage-3; do
  mkdir -p "$TMP/$stage"
  if ! build "${SRC[$stage]}" "$TMP/$stage"; then echo "BUILD FAILED: $stage"; fail=1; continue; fi
  for input in "$ROOT/test-runs/$stage"/*.in; do
    name="$(basename "$input" .in)"
    java -cp "$TMP/$stage" Main < "$input" > "$TMP/$stage.$name.actual" 2>/dev/null
    if diff -q "$ROOT/test-runs/$stage/$name.expected" "$TMP/$stage.$name.actual" > /dev/null; then
      echo "PASS  $stage/$name"
    else
      echo "FAIL  $stage/$name"; fail=1
      diff "$ROOT/test-runs/$stage/$name.expected" "$TMP/$stage.$name.actual" | head -8
    fi
  done
done
exit $fail
