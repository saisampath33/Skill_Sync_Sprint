$services = @("eureka-server", "config-server", "api-gateway", "auth-service", "user-service", "skill-service")

foreach ($s in $services) {
    Write-Host "Starting $s..."
    Start-Process -FilePath "mvn.cmd" -ArgumentList "spring-boot:run" -WorkingDirectory (Join-Path $pwd $s) -WindowStyle Minimized
    if ($s -eq "eureka-server") { sleep 20 }
    if ($s -eq "config-server") { sleep 30 }
    else { sleep 5 }
}
Write-Host "Services are booting in the background..."
