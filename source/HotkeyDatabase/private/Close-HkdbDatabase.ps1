Function Close-HkdbDatabase {
    <#
    .SYNOPSIS
        Close a SQLite Database connection to the hkdb
    #>
    [CmdletBinding()]
    param(
        # Optionally, specify a connection name for the database, default is 'hkdb'
        [Parameter(
        )]
        [string]$Name
    )
    begin {
        $config = Import-Configuration
        if (-not($PSBoundParameters['Name'])) {
            $Name = $config.Database.Name
        }
    }
    process {
        try {
            Close-SqlConnection -ConnectionName $Name
        }
        catch {
            Write-Error "Error closing HotKey Database`n$_"
        }
    }
    end {}
}
