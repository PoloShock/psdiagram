#!/bin/sh
# Builds dist/PS Diagram.exe (32-bit, runs on any Windows from 7 up, no .NET or other runtime needed).
# Needs MinGW-w64 i686 from Debian bookworm (gcc-mingw-w64-i686 binutils-mingw-w64-i686); on Windows run it in a
# debian:bookworm Docker container (see the repo CLAUDE.md).
# The build is deterministic for one toolchain (no timestamps, no paths, no compiler ident), so a rebuild of
# unchanged sources gives the same bytes. CI rebuilds it on debian:bookworm and
# fails if the result differs from the committed exe. Rebuild and commit the exe only when the source changes,
# so the hash, and the reputation it builds with antivirus/SmartScreen, stays the same across releases.
# Usage: ./build.sh [--check]   (--check: build into build/ and compare with dist/, don't overwrite)
set -eu
cd "$(dirname "$0")"
CC=${CC:-i686-w64-mingw32-gcc}
WINDRES=${WINDRES:-i686-w64-mingw32-windres}
OUT="dist/PS Diagram.exe"

mkdir -p build dist
"$WINDRES" -O coff launcher.rc build/launcher.res.o
"$CC" -std=c99 -O2 -Wall -Wextra -Werror -municode -mwindows -static -s -fno-ident \
    -Wl,--no-insert-timestamp -Wl,--dynamicbase -Wl,--nxcompat \
    -o build/launcher.exe launcher.c build/launcher.res.o -lshell32

if [ "${1:-}" = "--check" ]; then
    if cmp -s build/launcher.exe "$OUT"; then
        echo "OK: rebuilt launcher matches $OUT ($(sha256sum "$OUT" | cut -d' ' -f1))"
    else
        echo "ERROR: rebuilt launcher differs from the committed $OUT" >&2
        sha256sum build/launcher.exe "$OUT" >&2
        exit 1
    fi
else
    cp build/launcher.exe "$OUT"
    sha256sum "$OUT"
fi
