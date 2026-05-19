$langCode = Read-Host "Enter language code (e.g., de_de)"
$title = Read-Host "Enter translation for Title"
$question = Read-Host "Enter translation for Question"
$yes = Read-Host "Enter translation for Yes"
$no = Read-Host "Enter translation for No"

$json = [PSCustomObject]@{
    "gui.helpful_creeper.title" = $title
    "gui.helpful_creeper.question" = $question
    "gui.helpful_creeper.yes" = $yes
    "gui.helpful_creeper.no" = $no
} | ConvertTo-Json -Depth 5

$dir = "src/main/resources/assets/helpful_creeper/lang"
if (!(Test-Path $dir)) {
    New-Item -ItemType Directory -Path $dir
}

$path = "$dir/$langCode.json"
$json | Out-File -FilePath $path -Encoding utf8
Write-Host "Success! Language file created at: $path"
