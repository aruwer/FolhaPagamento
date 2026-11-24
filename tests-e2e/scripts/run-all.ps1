<#
.SYNOPSIS
  Executa os testes de backend (Bruno CLI) e de frontend (Cypress) contra uma database limpa
  e salva as evidências em 09_Homologacao/evidencias-auto/.

.DESCRIPTION
  1. Recria a database "folha_reteste" no PostgreSQL do Docker Compose (não toca em folha_pagamento).
  2. Sobe o JAR da aplicação na porta indicada, apontando para essa database.
  3. Roda a coleção Bruno e os specs Cypress.
  4. Encerra a aplicação. O código de saída é diferente de zero se algum teste falhar.

  Pré-requisitos: Docker em execução, `docker compose up -d`, JAR gerado (`.\mvnw.cmd clean package`),
  Node.js e `npm install` dentro de tests-e2e.
#>
param(
    [string]$Senha = "folha_app",
    [int]$Porta = 8090,
    [switch]$SoBruno,
    [switch]$SoCypress
)

$ErrorActionPreference = "Stop"
$e2e = Resolve-Path "$PSScriptRoot\.."
$raiz = Resolve-Path "$e2e\.."
$jar = Join-Path $raiz "target\folha-pagamento-2.0.0.jar"
$evid = Join-Path $raiz "09_Homologacao\evidencias-auto"
$bru = Join-Path $e2e "node_modules\.bin\bru.cmd"
$cypress = Join-Path $e2e "node_modules\.bin\cypress.cmd"
$env:Path += ";C:\Program Files\nodejs"
# VS Code e outras ferramentas Electron definem esta variavel; com ela o Cypress falha com "bad option: --smoke-test".
Remove-Item Env:ELECTRON_RUN_AS_NODE -ErrorAction SilentlyContinue

if (-not (Test-Path $jar)) { throw "JAR não encontrado em $jar. Rode .\mvnw.cmd clean package." }
if (-not (Test-Path $bru)) { throw "Dependências não instaladas. Rode 'npm install' em $e2e." }

# 1) database limpa
Push-Location $raiz
docker compose exec -T postgres psql -U folha_app -d folha_pagamento -c "DROP DATABASE IF EXISTS folha_reteste" -c "CREATE DATABASE folha_reteste" | Out-Null
Pop-Location

# 2) evidências anteriores
# apaga so o conteudo (a pasta pode estar aberta no editor ou no terminal)
if (Test-Path $evid) { Get-ChildItem $evid -Force | Remove-Item -Recurse -Force }
New-Item -ItemType Directory -Force "$evid\bruno", "$evid\cypress" | Out-Null

# 3) aplicação
$env:DATABASE_URL = "jdbc:postgresql://localhost:5433/folha_reteste"
$env:DATABASE_PASSWORD = $Senha
$log = Join-Path $evid "aplicacao.log"
$app = Start-Process java -ArgumentList "-jar", "`"$jar`"", "--server.port=$Porta" `
    -RedirectStandardOutput $log -RedirectStandardError "$log.err" -PassThru -WindowStyle Hidden
$ok = $false
for ($i = 0; $i -lt 60 -and -not $ok; $i++) {
    Start-Sleep -Seconds 2
    try { $ok = (Invoke-WebRequest "http://localhost:$Porta/" -UseBasicParsing -TimeoutSec 3).StatusCode -eq 200 } catch { }
}
if (-not $ok) { Stop-Process -Id $app.Id -Force -ErrorAction SilentlyContinue; throw "A aplicação não respondeu em http://localhost:$Porta. Veja $log" }

$resultado = @{}
try {
    $agora = Get-Date
    $info = @(
        "Execução: $($agora.ToString('dd/MM/yyyy HH:mm:ss'))",
        "Commit: $(git -C $raiz rev-parse --short HEAD)",
        "Aplicação: $jar (porta $Porta), database folha_reteste (PostgreSQL 17)",
        "Node: $(node -v) · Bruno CLI: $(& $bru --version) · Cypress: $(& $cypress --version | Select-Object -First 1)"
    )
    $info | Set-Content "$evid\execucao.txt" -Encoding utf8

    if (-not $SoCypress) {
        Push-Location "$e2e\bruno"
        & $bru run --env local `
            --env-var "baseUrl=http://localhost:$Porta" `
            --env-var "mes=$($agora.Month)" --env-var "ano=$($agora.Year)" --env-var "anoOutro=$($agora.Year - 1)" `
            --reporter-json "$evid\bruno\resultado.json" --reporter-html "$evid\bruno\relatorio.html" `
            --reporter-junit "$evid\bruno\resultado.xml" 2>&1 | Out-File "$evid\bruno\saida.txt" -Encoding utf8
        $resultado.Bruno = $LASTEXITCODE
        Get-Content "$evid\bruno\saida.txt" | Select-Object -Last 14
        Pop-Location
    }
    if (-not $SoBruno) {
        Push-Location $e2e
        $env:BASE_URL = "http://localhost:$Porta"
        & $cypress run --browser electron 2>&1 | Out-File "$evid\cypress\saida.txt" -Encoding utf8
        $resultado.Cypress = $LASTEXITCODE
        Get-Content "$evid\cypress\saida.txt" | Select-Object -Last 30
        Pop-Location
    }

    # Impressão real (CT-040 / BUG-004): PDF gerado pelo Edge a partir do demonstrativo individual de BR002 (Bruno)
    $edge = @("C:\Program Files (x86)\Microsoft\Edge\Application\msedge.exe", "C:\Program Files\Microsoft\Edge\Application\msedge.exe") |
        Where-Object { Test-Path $_ } | Select-Object -First 1
    if ($edge -and -not $SoCypress) {
        New-Item -ItemType Directory -Force "$evid\impressao" | Out-Null
        $pdf = "$evid\impressao\demonstrativo-BR002.pdf"
        # o Edge exige barras normais nos caminhos
        $pdfArg = $pdf -replace '\\', '/'
        $perfil = (Join-Path $env:TEMP "edge-e2e-perfil") -replace '\\', '/'
        # o Edge escreve "bytes written" no stderr; no PowerShell 5.1 isso viraria erro terminante com "Stop"
        $ErrorActionPreference = "Continue"
        & $edge --headless=new --disable-gpu "--user-data-dir=$perfil" --virtual-time-budget=4000 --no-pdf-header-footer `
            "--print-to-pdf=$pdfArg" "http://localhost:$Porta/folha/BR002?mes=$($agora.Month)&ano=$($agora.Year)" 2>&1 | Out-Null
        $ErrorActionPreference = "Stop"
        Start-Sleep -Seconds 2
        if (Test-Path $pdf) { "PDF de impressao gerado: $pdf" } else { "AVISO: o PDF de impressao nao foi gerado" }
    }
}
finally {
    # o "java" do PATH pode ser um atalho que abre o processo real como filho: encerra quem escuta na porta
    Get-NetTCPConnection -LocalPort $Porta -State Listen -ErrorAction SilentlyContinue |
        ForEach-Object { Stop-Process -Id $_.OwningProcess -Force -ErrorAction SilentlyContinue }
    Stop-Process -Id $app.Id -Force -ErrorAction SilentlyContinue
}

"`nResumo: " + (($resultado.GetEnumerator() | ForEach-Object { "$($_.Key)=" + $(if ($_.Value -eq 0) { "OK" } else { "FALHOU ($($_.Value))" }) }) -join " · ")
if (($resultado.Values | Where-Object { $_ -ne 0 }).Count -gt 0) { exit 1 }
