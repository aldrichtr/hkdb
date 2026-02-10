
function ConvertFrom-KeySequence {
    <#
    .SYNOPSIS
        Convert a "well formed" key sequence string into a numeric representation
    #>
    [CmdletBinding()]
    param(
        # The KeySequence to convert
        [Parameter(
        )]
        [string]$InputObject
    )
    begin {
        Write-Debug "`n$('-' * 80)`n-- Begin $($MyInvocation.MyCommand.Name)`n$('-' * 80)"
        $keyValue = [UInt16](0)
    }
    process {
        $sequence = $InputObject.Trim()
        if ($sequence -match '^<(.*)>') { $sequence = $Matches.1 }
        #TODO: This part needs more thought before continueing
        # ? What are the possible input formats
        # ? Should this function pass the input along to more specialized functions
        $parts = $sequence -split ' '
        Write-Debug "Found $($parts.Count) Chords"
        foreach ($part in $parts) {
            if ($part.Length -gt 1) {
                # Split the chord into its pieces. might be '+' or '-'
                if ($part.IndexOf('-') -ge 0) {
                    $splitChar = '-'
                } elseif ($part.IndexOf('+') -ge 0) {
                    $splitChar = '+'
                } elseif ($null -ne ($part | Convert-KeyName))
                $keyNames = $part -split $splitChar
            } else {
                #TODO: Its just one character, Lookup and output
            }
        }

    }
    end {
        Write-Debug "`n$('-' * 80)`n-- End $($MyInvocation.MyCommand.Name)`n$('-' * 80)"
    }
}
