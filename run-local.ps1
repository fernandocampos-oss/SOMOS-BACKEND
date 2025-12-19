# Script para ejecutar el backend en modo local
# Este script configura el perfil local y ejecuta Maven

Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  Iniciando Backend en Modo LOCAL" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# Configurar perfil
$env:SPRING_PROFILES_ACTIVE = "local"

# Verificar que estamos en el directorio correcto
if (-not (Test-Path ".\mvnw.cmd")) {
    Write-Host "ERROR: No se encuentra mvnw.cmd en el directorio actual" -ForegroundColor Red
    Write-Host "Asegúrate de ejecutar este script desde:" -ForegroundColor Yellow
    Write-Host "  C:\Users\fernando.campos\Desktop\GDR\servicio-marcaciones-backend" -ForegroundColor Yellow
    exit 1
}

Write-Host "Perfil activo: LOCAL" -ForegroundColor Green
Write-Host "Base de datos: localhost:5432/gdr" -ForegroundColor Green
Write-Host ""
Write-Host "Iniciando Maven..." -ForegroundColor Yellow
Write-Host ""

# Ejecutar Maven
& .\mvnw.cmd spring-boot:run

# Capturar código de salida
$exitCode = $LASTEXITCODE

if ($exitCode -eq 0) {
    Write-Host ""
    Write-Host "========================================" -ForegroundColor Green
    Write-Host "  Backend finalizado correctamente" -ForegroundColor Green
    Write-Host "========================================" -ForegroundColor Green
} else {
    Write-Host ""
    Write-Host "========================================" -ForegroundColor Red
    Write-Host "  Backend finalizó con errores" -ForegroundColor Red
    Write-Host "========================================" -ForegroundColor Red
}

exit $exitCode
