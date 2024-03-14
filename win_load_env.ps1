[string[]]$variables = Get-Content -Path .env -Encoding utf8

foreach($var in $variables) {
    $keyVal = $var -split '=', 2
    $key = $keyVal[0].Trim("'")
    $val = $keyVal[1].Trim("'")
    "$key=$val"
    [Environment]::SetEnvironmentVariable($key, $val)
}
