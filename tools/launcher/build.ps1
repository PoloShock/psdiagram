# Builds dist\PS Diagram.exe (32-bit, runs on any Windows from 7 up, no .NET or other runtime needed).
# Reference toolchain (pinned, the exe bytes depend on it): WinLibs i686 MinGW-w64,
#   winlibs-i686-posix-dwarf-gcc-16.2.0-mingw-w64msvcrt-14.0.0-r2.zip
#   https://github.com/brechtsanders/winlibs_mingw/releases/tag/16.2.0posix-14.0.0-msvcrt-r2
#   sha256 f1fab67fbe6ed1b0a390d06db6af8b4f0ca25fceab4f51bac2a88af8b81a93ec
# The maintainer's dev setup installs it into ..\tools\mingw32 next to the repo (the default -MingwBin below).
# The build is deterministic for one toolchain (no timestamps, no paths, no compiler ident), so a rebuild of
# unchanged sources gives the same bytes, in any folder. Rebuild and commit the exe only when the source changes,
# so the hash, and the reputation it builds with antivirus/SmartScreen, stays the same across releases.
# Usage: .\build.ps1 [-Check] [-MingwBin <dir with gcc.exe>]
#   -Check: build into build\ and compare with dist\, don't overwrite
param(
    [switch]$Check,
    [string]$MingwBin = $(if ($env:PSD_MINGW_BIN) { $env:PSD_MINGW_BIN } else { "$PSScriptRoot\..\..\..\tools\mingw32\bin" })
)
$ErrorActionPreference = 'Stop'
Set-Location $PSScriptRoot
if (-not (Test-Path "$MingwBin\gcc.exe")) { throw "gcc.exe not found in $MingwBin (pass -MingwBin or set PSD_MINGW_BIN)" }
$env:PATH = "$MingwBin;$env:PATH"   # gcc runs as, ld etc. from its own bin
$out = 'dist\PS Diagram.exe'
New-Item -ItemType Directory -Force build, dist, build\nomanifest | Out-Null

# WinLibs links a default application manifest (default-manifest.o) into every exe, which clashes with ours
# ("ld: .rsrc merge failure: multiple non-default manifests"). An empty object found first via -B replaces it.
Set-Content -Path build\empty.c -Value '' -Encoding ascii
& gcc.exe -c -o build\nomanifest\default-manifest.o build\empty.c
if ($LASTEXITCODE) { throw 'compiling the empty default-manifest.o failed' }

& windres.exe -O coff launcher.rc build\launcher.res.o
if ($LASTEXITCODE) { throw 'windres failed' }
& gcc.exe -std=c99 -O2 -Wall -Wextra -Werror -municode -mwindows -static -s -fno-ident `
    '-Wl,--no-insert-timestamp' '-Wl,--dynamicbase' '-Wl,--nxcompat' -Bbuild/nomanifest/ `
    -o build\launcher.exe launcher.c build\launcher.res.o -lshell32
if ($LASTEXITCODE) { throw 'gcc failed' }

$new = (Get-FileHash -Algorithm SHA256 build\launcher.exe).Hash.ToLower()
if ($Check) {
    $old = (Get-FileHash -Algorithm SHA256 $out).Hash.ToLower()
    if ($new -eq $old) { "OK: rebuilt launcher matches $out ($new)" }
    else { Write-Error "rebuilt launcher differs from the committed $out`n  build: $new`n  dist:  $old"; exit 1 }
} else {
    Copy-Item -Force build\launcher.exe $out
    "$new  $out"
}
