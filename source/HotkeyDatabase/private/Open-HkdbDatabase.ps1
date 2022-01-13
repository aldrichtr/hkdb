Function Open-HkdbDatabase {
    <#
    .SYNOPSIS
        Create an SQLite Database connection to the hkdb
    #>
    [CmdletBinding()]
    param(
        # Optionally, specify a different path than the one specified in the configuration
        [Parameter(
            ValueFromPipeline
        )]
        [string]$Path,

        # Optionally, specify a connection name for the database, default is 'hkdb'
        [Parameter(
        )]
        [string]$Name
    )
    begin {
        $config = Import-Configuration
        if (-not($PSBoundParameters['Path'])) {
            $Path = $config.Database.Path
        }
        if (-not($PSBoundParameters['Name'])) {
            $Name = $config.Database.Name
        }
    }
    process {
        try {
            Write-Verbose "Opening connection to SQLite database: $Path using: $Name"
            Open-SqliteConnection -DataSource $Path -ConnectionName $Name
        }
        catch {
            Write-Error "Error opening HotKey Database`n$_"
        }
    }
    end {
        if (Test-SqlConnection -ConnectionName $Name) {
            Write-Verbose "Successfully opened connection"
        }
    }
}
