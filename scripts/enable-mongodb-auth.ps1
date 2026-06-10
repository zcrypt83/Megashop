$ErrorActionPreference = "Stop"

$currentIdentity = [Security.Principal.WindowsIdentity]::GetCurrent()
$principal = New-Object Security.Principal.WindowsPrincipal($currentIdentity)
$isAdmin = $principal.IsInRole([Security.Principal.WindowsBuiltInRole]::Administrator)

if (-not $isAdmin) {
    $arguments = "-NoProfile -ExecutionPolicy Bypass -File `"$PSCommandPath`""
    Start-Process powershell.exe -Verb RunAs -Wait -ArgumentList $arguments
    exit
}

$config = "D:\mongodb\bin\mongod.cfg"
$backup = "D:\mongodb\bin\mongod.cfg.before-auth.bak"
$resultFile = Join-Path (Split-Path $PSScriptRoot -Parent) "mongodb-auth-result.txt"

if (-not (Test-Path -LiteralPath $config)) {
    throw "No se encontró $config"
}

Copy-Item -LiteralPath $config -Destination $backup -Force
$content = Get-Content -LiteralPath $config -Raw

if ($content -match "(?m)^security:\s*$") {
    if ($content -match "(?m)^\s+authorization:\s+enabled\s*$") {
        Write-Host "La autenticación ya estaba habilitada."
    } else {
        $content = $content -replace "(?m)^security:\s*$", "security:`r`n  authorization: enabled"
        Set-Content -LiteralPath $config -Value $content -Encoding utf8
    }
} elseif ($content -match "(?m)^#security:\s*$") {
    $content = $content -replace "(?m)^#security:\s*$", "security:`r`n  authorization: enabled"
    Set-Content -LiteralPath $config -Value $content -Encoding utf8
} else {
    $content = $content.TrimEnd() + "`r`n`r`nsecurity:`r`n  authorization: enabled`r`n"
    Set-Content -LiteralPath $config -Value $content -Encoding utf8
}

Restart-Service -Name MongoDB
Start-Sleep -Seconds 4

$service = Get-Service -Name MongoDB
Write-Host "MongoDB: $($service.Status)"
Write-Host "Autenticación habilitada. Backup: $backup"
Set-Content -LiteralPath $resultFile -Encoding utf8 -Value @(
    "success=true"
    "service=$($service.Status)"
    "config=$config"
    "backup=$backup"
    "completedAt=$(Get-Date -Format o)"
)
