# Скрипт для публикации Docker образа на Docker Hub
param(
    [Parameter(Mandatory=$true)]
    [string]$DockerUsername
)

Write-Host "Тегирование образа..." -ForegroundColor Green
docker tag akira:latest "${DockerUsername}/akira:latest"

Write-Host "Публикация образа на Docker Hub..." -ForegroundColor Green
docker push "${DockerUsername}/akira:latest"

Write-Host "Образ успешно опубликован!" -ForegroundColor Green
Write-Host "Используйте следующую команду для установки переменной окружения:" -ForegroundColor Yellow
Write-Host "`$env:DOCKER_USERNAME = '$DockerUsername'" -ForegroundColor Cyan

