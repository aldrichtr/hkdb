
function ConvertFrom-KeyCode {
    <#
    .SYNOPSIS
        Convert a numeric value to its string representation
    #>
    [CmdletBinding()]
    param(
        # The keycode value
        [Parameter(
        )]
        [UInt16]$InputObject
    )
    begin {
        Write-Debug "`n$('-' * 80)`n-- Begin $($MyInvocation.MyCommand.Name)`n$('-' * 80)"
        $config = Import-Configuration
        $sb = [System.Text.StringBuilder]::new()
    }
    process {
        if ($InputObject -band [Modifier]::Alt) { [void]$sb.Append('Alt+')}
        if ($InputObject -band [Modifier]::Shift) { [void]$sb.Append('Shift+')}
        if ($InputObject -band [Modifier]::Ctrl) { [void]$sb.Append('Ctrl+')}
        if ($InputObject -band [Modifier]::Win) { [void]$sb.Append('Win+')}

        $keyValue = ($InputObject -band 0xFF)
        $keyCode = [KeyCode]$keyValue
        [void]$sb.Append($keyCode)

        $sb.ToString()

    }
    end {
        Write-Debug "`n$('-' * 80)`n-- End $($MyInvocation.MyCommand.Name)`n$('-' * 80)"
    }
}
