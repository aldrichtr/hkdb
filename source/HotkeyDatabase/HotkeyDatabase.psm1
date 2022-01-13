$import_options = @{
    Path        = $PSScriptRoot
    Filter      = "*.ps1"
    Recurse     = $true
    ErrorAction = 'Stop'
}

Function Resolve-UnLoaded {
    [CmdletBinding()]
    param()
    $custom = Get-Content "$PSScriptRoot\LoadOrder.txt"

    Get-ChildItem @import_options -Recurse | ForEach-Object {
       $rel = $_.FullName -replace [regex]::Escape("$PSScriptRoot\") , ''
       if ($rel -notin $custom) {
            Write-Warning "$rel is not listed in custom"
       }
    }

}

if (Test-Path "$PSScriptRoot\LoadOrder.txt") {
    Write-Host "Using custom load order"
    Resolve-UnLoaded
    try {
        Get-Content "$PSScriptRoot\LoadOrder.txt" | ForEach-Object {
            switch -Regex ($_) {
                '^\s*$' {
                    # blank line, skip
                    continue
                }
                '^\s*#.*$' {
                    # Comment line, skip
                    continue
                }
                '^.*\.ps1' {
                    # load these
                    . "$PSScriptRoot\$_"
                    continue
                }
                default {
                    #unrecognized, skip
                    continue
                }
            }
        }
    } catch {
        Write-Error "Custom load order $_"
    }
} else {
    try {
        foreach ($type in 'enum', 'classes', 'private', 'public') {
            $import_options.Path = "$PSScriptRoot\$type"
            Get-ChildItem @import_options | ForEach-Object {
                . $_.FullName
            }
        }
    } catch {
        throw "An error occured during the dot-sourcing of module .ps1 files:`n$_"
    }
}
