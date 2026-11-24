/*
 * PS Diagram.exe - native Windows launcher for psdiagram.jar.
 *
 * Same behaviour as the original C# (.NET 2.0) launcher, but with no .NET dependency:
 * 1. psdiagram.jar is looked up in the current directory, then in the directory of this exe;
 * 2. it is started with the bundled jre\bin\javaw.exe, else with %JAVA_HOME%\bin\javaw.exe,
 *    else (after a warning) through the shell's .jar association;
 * 3. the command-line arguments are passed through unchanged (file associations use "%1").
 * Exit codes: 0 = started, 1 = psdiagram.jar not found, 2 = start failed.
 *
 * Build: build.ps1 (pinned WinLibs MinGW-w64 i686), output dist/PS Diagram.exe.
 */
#define WIN32_LEAN_AND_MEAN
#include <windows.h>
#include <shellapi.h>
#include <wchar.h>

#define PSDIAGRAM_FILE L"psdiagram.jar"
#define MAX_LONG_PATH 32768

static wchar_t currentDir[MAX_LONG_PATH];
static wchar_t selfDir[MAX_LONG_PATH];
static wchar_t buf[MAX_LONG_PATH];
static wchar_t javaHome[MAX_LONG_PATH];
static wchar_t jarPath[MAX_LONG_PATH];
static wchar_t javaw[MAX_LONG_PATH];
static wchar_t cmdLine[MAX_LONG_PATH];
static wchar_t message[MAX_LONG_PATH];

/* dst = dir + '\' + name (the separator is added only when dir doesn't end with one) */
static int joinPath(wchar_t *dst, size_t dstLen, const wchar_t *dir, const wchar_t *name)
{
    size_t len = wcslen(dir);
    const wchar_t *sep = (len > 0 && (dir[len - 1] == L'\\' || dir[len - 1] == L'/')) ? L"" : L"\\";
    int n = _snwprintf(dst, dstLen, L"%ls%ls%ls", dir, sep, name);
    return n >= 0 && (size_t) n < dstLen;
}

static int fileExists(const wchar_t *path)
{
    DWORD attr = GetFileAttributesW(path);
    return attr != INVALID_FILE_ATTRIBUTES && !(attr & FILE_ATTRIBUTE_DIRECTORY);
}

/* the arguments part of the raw command line: argv[0] is skipped with the same rules as the CRT */
static const wchar_t *argumentsTail(void)
{
    const wchar_t *p = GetCommandLineW();
    if (*p == L'"') {
        p++;
        while (*p && *p != L'"') {
            p++;
        }
        if (*p == L'"') {
            p++;
        }
    } else {
        while (*p && *p != L' ' && *p != L'\t') {
            p++;
        }
    }
    while (*p == L' ' || *p == L'\t') {
        p++;
    }
    return p;
}

/* "Při spuštění launcheru nastala chyba" with the Windows error text, then exit code 2 */
static void failWithError(DWORD error, const wchar_t *what)
{
    wchar_t *sysMsg = NULL;
    FormatMessageW(FORMAT_MESSAGE_ALLOCATE_BUFFER | FORMAT_MESSAGE_FROM_SYSTEM | FORMAT_MESSAGE_IGNORE_INSERTS,
            NULL, error, 0, (LPWSTR) &sysMsg, 0, NULL);
    _snwprintf(message, MAX_LONG_PATH, L"Při spuštění launcheru nastala chyba:\n%ls\n%ls (%lu)",
            what, sysMsg ? sysMsg : L"", (unsigned long) error);
    message[MAX_LONG_PATH - 1] = L'\0';
    MessageBoxW(NULL, message, L"Chyba", MB_OK | MB_ICONERROR);
    ExitProcess(2);
}

static void startJavaw(const wchar_t *javawPath, const wchar_t *args)
{
    STARTUPINFOW si;
    PROCESS_INFORMATION pi;
    int n = _snwprintf(cmdLine, MAX_LONG_PATH, L"\"%ls\" -jar \"%ls\"%ls%ls", javawPath, jarPath, *args ? L" " : L"", args);
    if (n < 0 || n >= MAX_LONG_PATH) {
        failWithError(ERROR_FILENAME_EXCED_RANGE, javawPath);
    }
    ZeroMemory(&si, sizeof(si));
    si.cb = sizeof(si);
    ZeroMemory(&pi, sizeof(pi));
    if (!CreateProcessW(javawPath, cmdLine, NULL, NULL, FALSE, 0, NULL, NULL, &si, &pi)) {
        failWithError(GetLastError(), javawPath);
    }
    CloseHandle(pi.hThread);
    CloseHandle(pi.hProcess);
}

int WINAPI wWinMain(HINSTANCE hInstance, HINSTANCE hPrevInstance, PWSTR pCmdLine, int nCmdShow)
{
    const wchar_t *pathToUse = NULL;
    const wchar_t *args = argumentsTail();
    DWORD len;
    wchar_t *slash;
    (void) hInstance;
    (void) hPrevInstance;
    (void) pCmdLine;
    (void) nCmdShow;

    len = GetCurrentDirectoryW(MAX_LONG_PATH, currentDir);
    if (len == 0 || len >= MAX_LONG_PATH) {
        failWithError(GetLastError(), L"GetCurrentDirectory");
    }
    len = GetModuleFileNameW(NULL, selfDir, MAX_LONG_PATH);
    if (len == 0 || len >= MAX_LONG_PATH) {
        failWithError(GetLastError(), L"GetModuleFileName");
    }
    slash = wcsrchr(selfDir, L'\\');
    if (slash != NULL) {
        *slash = L'\0';
    }

    if (joinPath(buf, MAX_LONG_PATH, currentDir, PSDIAGRAM_FILE) && fileExists(buf)) {
        pathToUse = currentDir;
    } else if (joinPath(buf, MAX_LONG_PATH, selfDir, PSDIAGRAM_FILE) && fileExists(buf)) {
        pathToUse = selfDir;
    }

    if (pathToUse != NULL) {
        joinPath(jarPath, MAX_LONG_PATH, pathToUse, PSDIAGRAM_FILE);

        if (joinPath(javaw, MAX_LONG_PATH, pathToUse, L"jre\\bin\\javaw.exe") && fileExists(javaw)) {
            /* launch through own JRE */
            startJavaw(javaw, args);
        } else if ((len = GetEnvironmentVariableW(L"JAVA_HOME", javaHome, MAX_LONG_PATH)) > 0
                && len < MAX_LONG_PATH
                && joinPath(javaw, MAX_LONG_PATH, javaHome, L"bin\\javaw.exe") && fileExists(javaw)) {
            /* launch through local JRE */
            startJavaw(javaw, args);
        } else {
            /* just try to launch it anyway */
            HINSTANCE result;
            /* break between the sentences; "PS Diagram" = non-breaking space, so Windows' auto-wrap never splits it */
            MessageBoxW(NULL, L"Nepodařilo se lokalizovat Java Runtime Environment.\nPS Diagram pravděpodobně nebude možné spustit.",
                    L"Chyba: JRE nebylo nalezeno", MB_OK | MB_ICONWARNING);
            result = ShellExecuteW(NULL, NULL, jarPath, *args ? args : NULL, NULL, SW_SHOWNORMAL);
            if ((INT_PTR) result <= 32) {
                failWithError(GetLastError(), jarPath);
            }
        }
    } else {
        if (lstrcmpiW(currentDir, selfDir) != 0) {
            _snwprintf(buf, MAX_LONG_PATH, L"%ls\n%ls", currentDir, selfDir);
        } else {
            _snwprintf(buf, MAX_LONG_PATH, L"%ls", currentDir);
        }
        buf[MAX_LONG_PATH - 1] = L'\0';
        _snwprintf(message, MAX_LONG_PATH, L"Potřebný soubor \"%ls\" nebyl nalezen.\n\nOčekávaná umístění:\n%ls",
                PSDIAGRAM_FILE, buf);
        message[MAX_LONG_PATH - 1] = L'\0';
        MessageBoxW(NULL, message, L"Chyba: soubor nenalezen", MB_OK | MB_ICONERROR);
        return 1;
    }

    return 0;
}
