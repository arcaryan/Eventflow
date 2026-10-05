[CmdletBinding()]
param(
    [switch]$NoBrowser,
    [switch]$KeepOpen
)

# EventFlow one-command Windows setup.
# This script is intentionally self-contained and does not require administrator rights.

$ErrorActionPreference = 'Stop'
Set-StrictMode -Version Latest
[Net.ServicePointManager]::SecurityProtocol = [Net.SecurityProtocolType]::Tls12

$ProjectRoot = (Resolve-Path -LiteralPath $PSScriptRoot).Path
$LocalRoot = Join-Path $env:LOCALAPPDATA 'EventFlow'
$ToolRoot = Join-Path $LocalRoot 'tools'
$RuntimeRoot = Join-Path $LocalRoot 'runtime'
$DownloadRoot = Join-Path $ToolRoot 'downloads'
$StatePath = Join-Path $RuntimeRoot 'ports.json'
$DbPropertiesPath = Join-Path $ProjectRoot 'src\main\resources\db.properties'

function Write-Step([string]$Message) {
    Write-Host "`n==> $Message" -ForegroundColor Cyan
}

function Write-Info([string]$Message) {
    Write-Host "    $Message" -ForegroundColor Gray
}

function Test-JdkHome([string]$Candidate) {
    if ([string]::IsNullOrWhiteSpace($Candidate)) { return $false }
    $java = Join-Path $Candidate 'bin\java.exe'
    $javac = Join-Path $Candidate 'bin\javac.exe'
    if (-not (Test-Path -LiteralPath $java) -or -not (Test-Path -LiteralPath $javac)) { return $false }
    # java.exe writes -version to stderr. With ErrorActionPreference=Stop that
    # looks like a failure in Windows PowerShell, so inspect the file metadata
    # instead of executing it just to discover the version.
    $versionText = [Diagnostics.FileVersionInfo]::GetVersionInfo($java).ProductVersion
    $match = [regex]::Match([string]$versionText, '^(\d+)')
    return $match.Success -and ([int]$match.Groups[1].Value -ge 17)
}

function Find-JdkHome {
    $candidates = New-Object System.Collections.Generic.List[string]
    if ($env:JAVA_HOME) { $candidates.Add($env:JAVA_HOME) }

    $javaCommand = Get-Command java.exe -ErrorAction SilentlyContinue
    if ($javaCommand) {
        $javaBin = Split-Path -Parent $javaCommand.Source
        $candidates.Add((Split-Path -Parent $javaBin))
    }

    $programFiles = @($env:ProgramFiles, ${env:ProgramFiles(x86)}) | Where-Object { $_ }
    foreach ($root in $programFiles) {
        $candidates.Add((Join-Path $root 'Eclipse Adoptium\jdk-17.0.20.101-hotspot'))
        $candidates.Add((Join-Path $root 'Java\jdk-17'))
    }

    Get-ChildItem -LiteralPath $ToolRoot -Directory -Filter 'jdk-*' -ErrorAction SilentlyContinue |
        ForEach-Object { $candidates.Add($_.FullName) }

    foreach ($candidate in ($candidates | Select-Object -Unique)) {
        if (Test-JdkHome $candidate) { return (Resolve-Path -LiteralPath $candidate).Path }
    }
    return $null
}

function Download-File([string]$Uri, [string]$Destination) {
    if (Test-Path -LiteralPath $Destination) {
        $existing = Get-Item -LiteralPath $Destination
        if ($existing.Length -gt 1MB) { return $Destination }
        Remove-Item -LiteralPath $Destination -Force
    }

    Write-Info "Downloading $Uri"
    $webClient = New-Object System.Net.WebClient
    try {
        # WebClient avoids the very noisy PowerShell 5.1 progress renderer when
        # this launcher is started by double-clicking the CMD file.
        $webClient.DownloadFile($Uri, $Destination)
    } catch {
        if (Test-Path -LiteralPath $Destination) { Remove-Item -LiteralPath $Destination -Force }
        throw "Download failed: $Uri`n$($_.Exception.Message)"
    } finally {
        $webClient.Dispose()
    }
    return $Destination
}

function Download-FirstAvailable([string[]]$Uris, [string]$Destination) {
    $lastError = $null
    foreach ($uri in $Uris) {
        try { return (Download-File $uri $Destination) }
        catch { $lastError = $_.Exception.Message; Write-Info "That download was unavailable; trying the next official mirror/version." }
    }
    throw "Could not download a required dependency. $lastError"
}

function Expand-ZipHome([string]$ZipPath, [string]$NamePattern) {
    $extractPath = Join-Path $ToolRoot ("extract-" + [guid]::NewGuid().ToString('N'))
    New-Item -ItemType Directory -Path $extractPath -Force | Out-Null
    try {
        # Use the .NET ZIP API instead of Expand-Archive so PowerShell 5.1 does
        # not fill a double-clicked console with extraction progress bars.
        Add-Type -AssemblyName System.IO.Compression.FileSystem
        [System.IO.Compression.ZipFile]::ExtractToDirectory($ZipPath, $extractPath)
        $archiveHome = Get-ChildItem -LiteralPath $extractPath -Directory | Where-Object { $_.Name -like $NamePattern } | Select-Object -First 1
        if (-not $archiveHome) { throw "The downloaded archive did not contain an expected folder ($NamePattern)." }
        $destination = Join-Path $ToolRoot $archiveHome.Name
        if (-not (Test-Path -LiteralPath $destination)) {
            Move-Item -LiteralPath $archiveHome.FullName -Destination $destination
        }
        return (Resolve-Path -LiteralPath $destination).Path
    } finally {
        if (Test-Path -LiteralPath $extractPath) { Remove-Item -LiteralPath $extractPath -Recurse -Force }
    }
}

function Ensure-Jdk {
    $existing = Find-JdkHome
    if ($existing) {
        Write-Info "Using Java at $existing"
        return $existing
    }

    $zip = Join-Path $DownloadRoot 'temurin-17-windows-x64.zip'
    $uri = 'https://api.adoptium.net/v3/binary/latest/17/ga/windows/x64/jdk/hotspot/normal/eclipse?project=jdk'
    Download-File $uri $zip | Out-Null
    $jdkHome = Expand-ZipHome $zip 'jdk-*'
    if (-not (Test-JdkHome $jdkHome)) { throw 'The downloaded Java runtime is not a valid JDK 17 installation.' }
    return $jdkHome
}

function Ensure-Maven {
    $command = Get-Command mvn.cmd -ErrorAction SilentlyContinue
    if ($command) {
        Write-Info "Using Maven at $($command.Source)"
        return $command.Source
    }

    $existing = Get-ChildItem -LiteralPath $ToolRoot -Directory -Filter 'apache-maven-*' -ErrorAction SilentlyContinue | Select-Object -First 1
    if ($existing -and (Test-Path -LiteralPath (Join-Path $existing.FullName 'bin\mvn.cmd'))) {
        return (Join-Path $existing.FullName 'bin\mvn.cmd')
    }

    $zip = Join-Path $DownloadRoot 'apache-maven-3.9.16-bin.zip'
    Download-File 'https://dlcdn.apache.org/maven/maven-3/3.9.16/binaries/apache-maven-3.9.16-bin.zip' $zip | Out-Null
    $mavenHome = Expand-ZipHome $zip 'apache-maven-*'
    return (Join-Path $mavenHome 'bin\mvn.cmd')
}

function Ensure-Tomcat {
    if ($env:CATALINA_HOME -and (Test-Path -LiteralPath (Join-Path $env:CATALINA_HOME 'bin\startup.bat'))) {
        Write-Info "Using Tomcat at $env:CATALINA_HOME"
        return (Resolve-Path -LiteralPath $env:CATALINA_HOME).Path
    }

    $existing = Get-ChildItem -LiteralPath $ToolRoot -Directory -Filter 'apache-tomcat-*' -ErrorAction SilentlyContinue | Select-Object -First 1
    if ($existing -and (Test-Path -LiteralPath (Join-Path $existing.FullName 'bin\startup.bat'))) {
        return (Resolve-Path -LiteralPath $existing.FullName).Path
    }

    $zip = Join-Path $DownloadRoot 'apache-tomcat-10.1.44.zip'
    Download-File 'https://archive.apache.org/dist/tomcat/tomcat-10/v10.1.44/bin/apache-tomcat-10.1.44.zip' $zip | Out-Null
    return (Expand-ZipHome $zip 'apache-tomcat-*')
}

function Find-MySqlHome {
    $candidates = New-Object System.Collections.Generic.List[string]
    if ($env:EVENTFLOW_MYSQL_HOME) { $candidates.Add($env:EVENTFLOW_MYSQL_HOME) }

    $mysqldCommand = Get-Command mysqld.exe -ErrorAction SilentlyContinue
    if ($mysqldCommand) { $candidates.Add((Split-Path -Parent (Split-Path -Parent $mysqldCommand.Source))) }

    foreach ($root in @($env:ProgramFiles, ${env:ProgramFiles(x86)}) | Where-Object { $_ }) {
        $mysqlParent = Join-Path $root 'MySQL'
        if (Test-Path -LiteralPath $mysqlParent) {
            Get-ChildItem -LiteralPath $mysqlParent -Directory -Filter 'MySQL Server *' -ErrorAction SilentlyContinue | ForEach-Object { $candidates.Add($_.FullName) }
        }
    }

    Get-ChildItem -LiteralPath $ToolRoot -Directory -Filter 'mysql-*' -ErrorAction SilentlyContinue | ForEach-Object { $candidates.Add($_.FullName) }
    foreach ($candidate in ($candidates | Select-Object -Unique)) {
        if ((Test-Path -LiteralPath (Join-Path $candidate 'bin\mysqld.exe')) -and (Test-Path -LiteralPath (Join-Path $candidate 'bin\mysql.exe'))) {
            return (Resolve-Path -LiteralPath $candidate).Path
        }
    }
    return $null
}

function Ensure-MySql {
    $existing = Find-MySqlHome
    if ($existing) {
        Write-Info "Using MySQL binaries at $existing"
        return $existing
    }

    $zip = Join-Path $DownloadRoot 'mysql-winx64.zip'
    $uris = @('https://cdn.mysql.com/Downloads/MySQL-8.4/mysql-8.4.9-winx64.zip')
    try {
        Download-FirstAvailable $uris $zip | Out-Null
        return (Expand-ZipHome $zip 'mysql-*')
    } catch {
        $winget = Get-Command winget.exe -ErrorAction SilentlyContinue
        if (-not $winget) { throw }
        Write-Info 'The portable MySQL archive was unavailable; trying the official WinGet package.'
        & $winget.Source install --id Oracle.MySQL --exact --silent --accept-source-agreements --accept-package-agreements | Out-Host
        $installed = Find-MySqlHome
        if (-not $installed) { throw 'MySQL could not be installed automatically.' }
        return $installed
    }
}

function Test-TcpPort([int]$Port) {
    $client = New-Object System.Net.Sockets.TcpClient
    try {
        $async = $client.BeginConnect('127.0.0.1', $Port, $null, $null)
        if (-not $async.AsyncWaitHandle.WaitOne(500)) { return $false }
        $client.EndConnect($async)
        return $true
    } catch { return $false }
    finally { $client.Close() }
}

function Get-FreePort([int]$PreferredPort) {
    $port = $PreferredPort
    while (Test-TcpPort $port) { $port++ }
    return $port
}

function Wait-Until([scriptblock]$Condition, [int]$TimeoutSeconds, [string]$Description) {
    $deadline = (Get-Date).AddSeconds($TimeoutSeconds)
    while ((Get-Date) -lt $deadline) {
        if (& $Condition) { return }
        Start-Sleep -Seconds 1
    }
    throw "Timed out waiting for $Description."
}

function Invoke-MySqlQuery([string]$MysqlExe, [int]$Port, [string]$Query) {
    $startInfo = New-Object System.Diagnostics.ProcessStartInfo
    $startInfo.FileName = $MysqlExe
    $startInfo.Arguments = "--no-defaults --protocol=TCP --host=127.0.0.1 --port=$Port --user=root --password= --batch --skip-column-names -e `"$Query`""
    $startInfo.UseShellExecute = $false
    $startInfo.CreateNoWindow = $true
    $startInfo.RedirectStandardOutput = $true
    $startInfo.RedirectStandardError = $true
    $process = New-Object System.Diagnostics.Process
    $process.StartInfo = $startInfo
    try {
        [void]$process.Start()
        $stdout = $process.StandardOutput.ReadToEnd()
        [void]$process.StandardError.ReadToEnd()
        $process.WaitForExit()
        return [pscustomobject]@{ ExitCode = $process.ExitCode; Output = $stdout.Trim() }
    } catch { return [pscustomobject]@{ ExitCode = -1; Output = '' } }
    finally { $process.Dispose() }
}

function Test-MySql([string]$MysqlExe, [int]$Port) {
    $result = Invoke-MySqlQuery $MysqlExe $Port 'SELECT 1;'
    return ($result.ExitCode -eq 0 -and $result.Output -eq '1')
}

function Test-MySqlSchema([string]$MysqlExe, [int]$Port) {
    $query = "SELECT COUNT(*) FROM information_schema.tables WHERE table_schema='event_management' AND table_name='activity_logs';"
    $result = Invoke-MySqlQuery $MysqlExe $Port $query
    return ($result.ExitCode -eq 0 -and $result.Output -eq '1')
}

function Test-MySqlSeed([string]$MysqlExe, [int]$Port) {
    $query = "SELECT COUNT(*) FROM event_management.users WHERE email='admin@eventflow.local';"
    $result = Invoke-MySqlQuery $MysqlExe $Port $query
    return ($result.ExitCode -eq 0 -and $result.Output -eq '1')
}

function Invoke-MySqlFile([string]$MysqlExe, [int]$Port, [string]$SqlFile, [string]$Database) {
    $startInfo = New-Object System.Diagnostics.ProcessStartInfo
    $startInfo.FileName = $MysqlExe
    $startInfo.Arguments = "--protocol=TCP --host=127.0.0.1 --port=$Port --user=root --password=" + $(if ($Database) { " --database=$Database" } else { '' })
    $startInfo.UseShellExecute = $false
    $startInfo.CreateNoWindow = $true
    $startInfo.RedirectStandardInput = $true
    $startInfo.RedirectStandardOutput = $true
    $startInfo.RedirectStandardError = $true
    $process = New-Object System.Diagnostics.Process
    $process.StartInfo = $startInfo
    [void]$process.Start()
    $process.StandardInput.Write([IO.File]::ReadAllText($SqlFile))
    $process.StandardInput.Close()
    $stdout = $process.StandardOutput.ReadToEnd()
    $stderr = $process.StandardError.ReadToEnd()
    $process.WaitForExit()
    if ($process.ExitCode -ne 0) { throw "MySQL could not load $SqlFile`n$stderr`n$stdout" }
}

function Test-App([int]$Port) {
    try {
        $response = Invoke-WebRequest -Uri "http://localhost:$Port/event-management/" -UseBasicParsing -TimeoutSec 4
        return $response.StatusCode -eq 200
    } catch { return $false }
}

function Set-DatabaseProperties([int]$Port) {
    $content = @(
        "db.url=jdbc:mysql://127.0.0.1:$Port/event_management?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true",
        'db.username=root',
        'db.password='
    ) -join "`n"
    $utf8NoBom = New-Object System.Text.UTF8Encoding($false)
    [IO.File]::WriteAllText($DbPropertiesPath, $content + "`n", $utf8NoBom)
}

function Invoke-MavenBuild([string]$MavenCommand, [string]$JavaHome) {
    # Keep JAVA_HOME and PATH local to the Maven child process. Do not mutate
    # PowerShell automatic variables or the parent process environment.
    $childPath = "$JavaHome\bin;$($env:PATH)"
    $childCommand = 'set "JAVA_HOME=' + $JavaHome + '" && set "PATH=' + $childPath + '" && call "' + $MavenCommand + '" clean test package'
    $process = Start-Process -FilePath $env:ComSpec -ArgumentList @('/d', '/s', '/c', $childCommand) -WorkingDirectory $ProjectRoot -Wait -PassThru -NoNewWindow
    if ($process.ExitCode -ne 0) { throw "Maven build failed with exit code $($process.ExitCode)." }
}

function Start-Tomcat([string]$StartupPath, [string]$TomcatHome, [string]$JavaHome) {
    # Tomcat receives its required environment only in its child cmd.exe.
    # The launcher PowerShell process remains untouched.
    $childCommand = 'set "JAVA_HOME=' + $JavaHome + '" && set "CATALINA_HOME=' + $TomcatHome + '" && call "' + $StartupPath + '"'
    Start-Process -FilePath $env:ComSpec -ArgumentList @('/d', '/s', '/c', $childCommand) -WindowStyle Hidden | Out-Null
}

try {
    Write-Host 'EventFlow automatic setup' -ForegroundColor Green
    Write-Info "Project: $ProjectRoot"

    New-Item -ItemType Directory -Path $ToolRoot -Force | Out-Null
    New-Item -ItemType Directory -Path $DownloadRoot -Force | Out-Null
    New-Item -ItemType Directory -Path $RuntimeRoot -Force | Out-Null

    Write-Step 'Checking Java, Maven, MySQL, and Tomcat'
    $javaHome = Ensure-Jdk
    $maven = Ensure-Maven
    $tomcatHome = Ensure-Tomcat
    $mysqlHome = Ensure-MySql
    $mysqlExe = Join-Path $mysqlHome 'bin\mysql.exe'
    $mysqldExe = Join-Path $mysqlHome 'bin\mysqld.exe'
    Write-Info 'All required software is available.'

    $state = $null
    if (Test-Path -LiteralPath $StatePath) {
        try { $state = Get-Content -LiteralPath $StatePath -Raw | ConvertFrom-Json } catch { $state = $null }
    }
    $mysqlPort = if ($state -and ($state.PSObject.Properties.Name -contains 'MysqlPort')) { [int]$state.MysqlPort } else { 3306 }
    $httpPort = if ($state -and ($state.PSObject.Properties.Name -contains 'HttpPort')) { [int]$state.HttpPort } else { 8080 }

    Write-Step 'Starting the local MySQL database'
    if (-not (Test-MySql $mysqlExe $mysqlPort)) {
        if (Test-TcpPort $mysqlPort) { $mysqlPort = Get-FreePort ($mysqlPort + 1) }
        $mysqlData = Join-Path $RuntimeRoot 'mysql-data'
        New-Item -ItemType Directory -Path $mysqlData -Force | Out-Null
        if (-not (Test-Path -LiteralPath (Join-Path $mysqlData 'mysql'))) {
            Write-Info 'Initializing the private local database directory.'
            $initArgs = @('--no-defaults', '--initialize-insecure', "--basedir=`"$mysqlHome`"", "--datadir=`"$mysqlData`"", '--console')
            $initProcess = Start-Process -FilePath $mysqldExe -ArgumentList $initArgs -Wait -PassThru -NoNewWindow
            if ($initProcess.ExitCode -ne 0) { throw 'MySQL database initialization failed.' }
        }
        $mysqlLog = Join-Path $RuntimeRoot 'mysql.log'
        $mysqlErrorLog = Join-Path $RuntimeRoot 'mysql-error.log'
        $mysqlArgs = @('--no-defaults', "--basedir=`"$mysqlHome`"", "--datadir=`"$mysqlData`"", "--port=$mysqlPort", '--bind-address=127.0.0.1', '--console')
        Start-Process -FilePath $mysqldExe -ArgumentList $mysqlArgs -WindowStyle Hidden -RedirectStandardOutput $mysqlLog -RedirectStandardError $mysqlErrorLog | Out-Null
        Wait-Until { Test-MySql $mysqlExe $mysqlPort } 45 'MySQL'
    }
    Write-Info "MySQL is ready on port $mysqlPort."

    Write-Step 'Creating the schema and demo data'
    if (-not (Test-MySqlSchema $mysqlExe $mysqlPort)) {
        Invoke-MySqlFile $mysqlExe $mysqlPort (Join-Path $ProjectRoot 'docs\database-schema.sql') ''
        Write-Info 'Created the EventFlow database schema.'
    } else {
        Write-Info 'Existing EventFlow database schema detected; keeping existing data.'
    }
    if (-not (Test-MySqlSeed $mysqlExe $mysqlPort)) {
        Invoke-MySqlFile $mysqlExe $mysqlPort (Join-Path $ProjectRoot 'docs\sample-data.sql') 'event_management'
        Write-Info 'Loaded the EventFlow demo data.'
    } else {
        Write-Info 'Existing EventFlow demo data detected; keeping existing data.'
    }
    Set-DatabaseProperties $mysqlPort
    Write-Info 'Database schema, seed data, and local connection settings are ready.'

    Write-Step 'Building the application with Maven'
    Invoke-MavenBuild $maven $javaHome

    Write-Step 'Deploying and starting Tomcat'
    if (Test-TcpPort $httpPort -and -not (Test-App $httpPort)) {
        try {
            # Replacing a WAR can briefly make the existing application return a
            # non-200 response. Give that same Tomcat time to finish reloading
            # before deciding that the port belongs to another application.
            Wait-Until { Test-App $httpPort } 30 'existing EventFlow/Tomcat reload'
        } catch {
            $httpPort = Get-FreePort ($httpPort + 1)
        }
    }
    $serverXml = Join-Path $tomcatHome 'conf\server.xml'
    $serverText = [IO.File]::ReadAllText($serverXml)
    $serverText = [regex]::Replace($serverText, '(<Connector\s+port=")\d+("\s+protocol="HTTP/1.1")', { param($m) $m.Groups[1].Value + $httpPort + $m.Groups[2].Value }, 1)
    $utf8NoBom = New-Object System.Text.UTF8Encoding($false)
    [IO.File]::WriteAllText($serverXml, $serverText, $utf8NoBom)
    Copy-Item -LiteralPath (Join-Path $ProjectRoot 'target\event-management.war') -Destination (Join-Path $tomcatHome 'webapps\event-management.war') -Force
    if (-not (Test-App $httpPort)) {
        $startup = Join-Path $tomcatHome 'bin\startup.bat'
        Start-Tomcat $startup $tomcatHome $javaHome
    }
    Wait-Until { Test-App $httpPort } 60 'EventFlow/Tomcat'

    $stateContent = @{ MysqlPort = $mysqlPort; HttpPort = $httpPort } | ConvertTo-Json
    [IO.File]::WriteAllText($StatePath, $stateContent, $utf8NoBom)
    $url = "http://localhost:$httpPort/event-management/"
    Write-Host "`nEventFlow is running: $url" -ForegroundColor Green
    Write-Host 'Demo accounts use password: password' -ForegroundColor Gray
    Write-Host '  admin@eventflow.local' -ForegroundColor Gray
    Write-Host '  organizer@eventflow.local' -ForegroundColor Gray
    Write-Host '  jordan@eventflow.local' -ForegroundColor Gray
    if (-not $NoBrowser) { Start-Process $url | Out-Null }
    if ($KeepOpen) { Read-Host 'Press Enter to close this PowerShell window' }
} catch {
    Write-Host "`nSETUP FAILED" -ForegroundColor Red
    Write-Host $_.Exception.Message -ForegroundColor Red
    Write-Host "`nIf this is a network/download problem, run the script again after reconnecting to the internet." -ForegroundColor Yellow
    if ($KeepOpen) { Read-Host 'Press Enter to close this PowerShell window' }
    exit 1
}