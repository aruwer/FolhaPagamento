<#
.SYNOPSIS
  Inicia o PostgreSQL (Docker Compose) e executa o JAR do Sistema de Folha de Pagamento.

.DESCRIPTION
  Procura o JAR primeiro nesta pasta e depois em target\. Lê POSTGRES_PASSWORD do arquivo .env da raiz
  (criando-o a partir de .env.example, se não existir). Se a variável DATABASE_URL já estiver definida,
  ela é respeitada e o Docker Compose não é iniciado.

.EXAMPLE
  .\executar.ps1
  .\executar.ps1 -Porta 8082
#>
param([int]$Porta = 8081)

$ErrorActionPreference = "Stop"
$raiz = Resolve-Path "$PSScriptRoot\.."
$nome = "folha-pagamento-2.0.0.jar"

# 1) Java 17+
try {
    # o java escreve a versão no stderr; com "Stop" isso viraria erro
    $ErrorActionPreference = "Continue"
    $v = (& java -version 2>&1 | Select-Object -First 1).ToString()
    $ErrorActionPreference = "Stop"
    if ($v -notmatch '"(\d+)') { throw "versão não reconhecida" }
    if ([int]$Matches[1] -lt 17) { throw "Java $($Matches[1]) encontrado; é necessário Java 17 ou superior." }
} catch { throw "Java 17 ou superior não encontrado: $_" }

# 2) JAR
$jar = @("$PSScriptRoot\$nome", "$raiz\target\$nome") | Where-Object { Test-Path $_ } | Select-Object -First 1
if (-not $jar) {
    throw "JAR não encontrado. Rode .\baixar-jar.ps1 (release do GitHub) ou, na raiz, .\mvnw.cmd clean package."
}

# 3) senha do banco (.env)
$env_ = Join-Path $raiz ".env"
if (-not (Test-Path $env_)) { Copy-Item (Join-Path $raiz ".env.example") $env_; Write-Host "Criado .env a partir de .env.example." }
if (-not $env:DATABASE_PASSWORD) {
    $linha = Get-Content $env_ | Where-Object { $_ -match '^POSTGRES_PASSWORD=' } | Select-Object -First 1
    if (-not $linha) { throw "POSTGRES_PASSWORD não definido em $env_." }
    $env:DATABASE_PASSWORD = $linha.Substring("POSTGRES_PASSWORD=".Length)
}

# 4) banco de dados
if (-not $env:DATABASE_URL) {
    docker info *> $null
    if ($LASTEXITCODE -ne 0) { throw "Docker não está em execução. Abra o Docker Desktop e tente de novo." }
    Push-Location $raiz
    docker compose up -d
    Pop-Location
    Write-Host "Aguardando o PostgreSQL ficar saudável..."
    for ($i = 0; $i -lt 30; $i++) {
        $estado = docker inspect --format "{{.State.Health.Status}}" (docker compose -f "$raiz\docker-compose.yml" ps -q postgres) 2>$null
        if ($estado -eq "healthy") { break }
        Start-Sleep -Seconds 2
    }
}

# 5) aplicação
Write-Host "Iniciando $jar em http://localhost:$Porta (Ctrl+C para encerrar)"
& java -jar $jar "--server.port=$Porta"
