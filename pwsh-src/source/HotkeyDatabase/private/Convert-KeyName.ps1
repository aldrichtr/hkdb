
using namespace System.Collections
function Convert-KeyName {
    <#
    .SYNOPSIS
        Attempt to find the given key name in the map
    #>
    [CmdletBinding()]
    param(
        # The name to find
        [Parameter(
            ValueFromPipeline
        )]
        [string]$InputObject
    )
    begin {
        Write-Debug "`n$('-' * 80)`n-- Begin $($MyInvocation.MyCommand.Name)`n$('-' * 80)"
        $options = @{
            Path = $PSCmdlet.MyInvocation.MyCommand.Module.ModuleBase
            ChildPath = 'data/keyData.txt'
        }

        $dataFile = (Join-Path @options)


        $nameMap = @{}
        $data = Get-Content $dataFile

        $lineNumber = -1
        :line foreach ($line in $data) {
            $lineNumber++

            # always skip the header info (first 5 lines)
            if ($lineNumber -lt 4) { continue line }
            # skip comment lines
            if ($line -match '^\s*#') { continue line }
            # skip blanks
            if ($line -match '^\s*$') {continue line }

            $names = [ArrayList]::new(($line -split ' '))
            $value = [int]($names[0])
            [void]$names.RemoveAt(0)
            foreach ($name in $names) { $nameMap[$name] = $value }
        }
    }
    process {
        if ($nameMap.ContainsKey($InputObject)) { $nameMap[$InputObject]}
    }
    end {
        Write-Debug "`n$('-' * 80)`n-- End $($MyInvocation.MyCommand.Name)`n$('-' * 80)"
    }
}
