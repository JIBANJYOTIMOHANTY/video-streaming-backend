# PowerShell script to run all backend microservices at once
# Usage: .\run-all.ps1

function Start-ServiceWithEnv {
    param(
        [string]$serviceName,
        [string]$dirPath
    )
    
    # Load .env file if it exists
    $envFile = Join-Path $dirPath ".env"
    $envVars = @{}
    if (Test-Path $envFile) {
        Get-Content $envFile | ForEach-Object {
            $line = $_.Trim()
            if ($line -and -not $line.StartsWith("#") -and $line.Contains("=")) {
                $parts = $line.Split("=", 2)
                $key = $parts[0].Trim()
                $value = $parts[1].Trim()
                $envVars[$key] = $value
            }
        }
    }

    # Prepare command to set env variables and run mvnw
    $cmd = ""
    foreach ($key in $envVars.Keys) {
        # Escape any special characters for cmd environment variables
        $val = $envVars[$key]
        $cmd += "set $key=$val&&"
    }
    $cmd += "mvnw.cmd spring-boot:run"

    Write-Host "Launching $serviceName..." -ForegroundColor Green
    Start-Process cmd.exe "/c title $serviceName && $cmd" -WorkingDirectory $dirPath
}

# 1. Start Eureka Server
$eurekaPath = Join-Path $PSScriptRoot "eureka-server"
Start-ServiceWithEnv "EurekaServer" $eurekaPath

# Wait for Eureka server to spin up
Write-Host "Waiting 8 seconds for Eureka Server to initialize..." -ForegroundColor Yellow
Start-Sleep -Seconds 8

# 2. Start API Gateway
$gatewayPath = Join-Path $PSScriptRoot "api-gateway"
Start-ServiceWithEnv "ApiGateway" $gatewayPath

# 3. Start remaining services
$services = @(
    "auth-service",
    "user-service",
    "video-service",
    "upload-service",
    "streaming-service",
    "analytics-service"
)

foreach ($service in $services) {
    $servicePath = Join-Path $PSScriptRoot $service
    Start-ServiceWithEnv $service $servicePath
}

Write-Host "All backend services launched in separate windows!" -ForegroundColor Cyan
