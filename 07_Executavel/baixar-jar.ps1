<#
.SYNOPSIS
  Baixa o JAR da release QDS_2.0.0 do GitHub e confere o SHA-256.
#>
param([string]$Tag = "QDS_2.0.0")

$ErrorActionPreference = "Stop"
$nome = "folha-pagamento-2.0.0.jar"
$url = "https://github.com/aruwer/FolhaPagamento/releases/download/$Tag/$nome"
$destino = Join-Path $PSScriptRoot $nome
$esperado = ((Get-Content (Join-Path $PSScriptRoot "SHA256.txt") | Where-Object { $_ -match $nome } | Select-Object -First 1) -split '\s+')[0]

Write-Host "Baixando $url"
try {
    Invoke-WebRequest -Uri $url -OutFile $destino -UseBasicParsing
} catch {
    throw "Não foi possível baixar. A release $Tag já foi publicada? ($_)"
}

$real = (Get-FileHash $destino -Algorithm SHA256).Hash.ToLower()
if ($esperado -and $real -ne $esperado.ToLower()) {
    Remove-Item $destino -Force
    throw "SHA-256 diferente do esperado (esperado $esperado, obtido $real). Arquivo removido."
}
Write-Host "OK: $destino (SHA-256 $real)"
