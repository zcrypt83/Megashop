param(
    [Parameter(ValueFromRemainingArguments = $true)]
    [string[]]$MavenArguments
)

$nativeAccessOption = "--enable-native-access=ALL-UNNAMED"
if ($env:MAVEN_OPTS -notlike "*$nativeAccessOption*") {
    $env:MAVEN_OPTS = "$env:MAVEN_OPTS $nativeAccessOption".Trim()
}

$globalMaven = Get-Command mvn.cmd -ErrorAction SilentlyContinue
$localMaven = Join-Path $PSScriptRoot "..\tools\apache-maven-3.9.9\bin\mvn.cmd"

if ($globalMaven) {
    $maven = $globalMaven.Source
} elseif (Test-Path $localMaven) {
    $maven = (Resolve-Path $localMaven).Path
} else {
    Write-Error "Maven 3.9+ no esta instalado ni disponible en tools\apache-maven-3.9.9."
    exit 1
}

& $maven @MavenArguments
exit $LASTEXITCODE
