# Nap lai database phan_cong_de_tai_db tu schema.sql moi nhat (cua dev).
# CANH BAO: script nay XOA va TAO LAI database -> reset ve du lieu demo.
# Cach chay: dong app dang chay, roi mo terminal va go:
#   powershell -ExecutionPolicy Bypass -File sql\rebuild-db.ps1

$ErrorActionPreference = "Stop"
$schema = Join-Path $PSScriptRoot "schema.sql"
$raw = Get-Content -Raw -Path $schema
# schema.sql tao DB ten "PhanCongDeTai"; doi sang dung DB ma app ket noi.
$raw = $raw -replace 'PhanCongDeTai', 'phan_cong_de_tai_db'
$batches = [System.Text.RegularExpressions.Regex]::Split($raw, '(?im)^\s*GO\s*$')

$cs = "Server=localhost\SQLEXPRESS;Database=master;User Id=sa;Password=123456;TrustServerCertificate=True;Encrypt=True"
$conn = New-Object System.Data.SqlClient.SqlConnection $cs
$conn.Open()
$n = 0
foreach ($b in $batches) {
    if ([string]::IsNullOrWhiteSpace($b)) { continue }
    $cmd = $conn.CreateCommand()
    $cmd.CommandText = $b
    $cmd.CommandTimeout = 180
    [void]$cmd.ExecuteNonQuery()
    $n++
}
$conn.Close()
Write-Host "Da nap lai database phan_cong_de_tai_db: $n batch. Khoi dong lai app de dung."
