$ErrorActionPreference = "Stop"

function Exec-Command($cmd, $args) {
  $p = Start-Process -FilePath $cmd -ArgumentList $args -NoNewWindow -PassThru -Wait
  if ($p.ExitCode -ne 0) { throw "Command failed: $cmd $args" }
}

# Ensure adb is running and a device is connected
try {
  Exec-Command adb "start-server"
  $devices = (& adb devices) -split "`n" | Select-String "\tdevice$"
  if (-not $devices) { Write-Host "Nenhum dispositivo conectado. Conecte via USB ou adb connect."; exit 1 }
} catch {
  Write-Error $_; exit 1
}

# Build and install debug
Exec-Command .\gradlew.bat "installDebug -x lint -x test --console=plain"

# Launch app main activity
Exec-Command adb "shell am start -n com.ufpr.equilibrium/.MainActivity"

Write-Host "Aplicativo instalado e iniciado no dispositivo."



