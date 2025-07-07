<#
.SYNOPSIS
    Colorized Android logcat viewer for PowerShell
    
.DESCRIPTION
    This script provides a colorized view of Android logcat output, making it easier to 
    distinguish between different log levels. It can monitor logs for a specific package
    or process ID with colored output based on Android log priorities.
    
.PARAMETER Package
    The Android package name to monitor (e.g., "com.uth.synkr")
    
.PARAMETER ProcessId
    The specific process ID to monitor
    
.EXAMPLE
    .\colorized_logcat.ps1 -Package "com.uth.synkr"
    Monitor logs for the Synkr app package
    
.EXAMPLE
    .\colorized_logcat.ps1 -ProcessId 12924
    Monitor logs for process ID 12924
    
.EXAMPLE
    adb logcat --pid=12924 -v time | .\colorized_logcat.ps1
    Pipe logcat output through the colorizer
    
.NOTES
    Requires ADB (Android Debug Bridge) to be installed and in PATH
    Supports Android log levels: Verbose, Debug, Info, Warning, Error, Fatal
#>

param(
    [string]$Package = "",      # Android package name (optional)
    [int]$ProcessId = 0         # Process ID to monitor (optional)
)

# Define color mapping for Android log levels
# Each log level gets a distinct color for easy visual identification
$Colors = @{
    'V' = 'DarkGray'    # Verbose - least important, shown in dark gray
    'D' = 'Blue'        # Debug - development info, shown in blue
    'I' = 'Green'       # Info - general information, shown in green
    'W' = 'Yellow'      # Warning - potential issues, shown in yellow
    'E' = 'Red'         # Error - errors that don't crash app, shown in red
    'F' = 'Magenta'     # Fatal - critical errors, shown in magenta
}

# Build the base logcat command with time format
# -v time shows timestamp with each log entry
$LogcatCmd = "adb logcat -v time"

# Handle different input scenarios
if ($ProcessId -gt 0) {
    # If specific process ID provided, filter logs for that PID only
    $LogcatCmd += " --pid=$ProcessId"
}
elseif ($Package -ne "") {
    # If package name provided, get its current PID and filter logs
    $AppPid = adb shell pidof $Package
    if ($AppPid) {
        $LogcatCmd += " --pid=$AppPid"
        Write-Host "Monitoring logs for $Package (PID: $AppPid)" -ForegroundColor Cyan
    }
    else {
        # Exit if the specified package is not running
        Write-Host "App $Package is not running" -ForegroundColor Red
        exit
    }
}

# Display startup information to user
Write-Host "Starting logcat monitoring... Press Ctrl+C to stop" -ForegroundColor Cyan
Write-Host "Command: $LogcatCmd" -ForegroundColor Gray

# Main processing loop - execute logcat and process each line
Invoke-Expression $LogcatCmd | ForEach-Object {
    $line = $_
    
    # Parse Android logcat format to extract log level
    # Android logcat format: "MM-DD HH:MM:SS.mmm LEVEL/TAG(PID): message"
    # We look for pattern like " E/" or " W/" to identify log level
    if ($line -match '\s([VDIWEF])/') {
        # Extract the log level character from the regex match
        $level = $Matches[1]
        $color = $Colors[$level]
        
        # Apply color if we have a mapping for this log level
        if ($color) {
            Write-Host $line -ForegroundColor $color
        }
        else {
            # Fallback to default color if log level not recognized
            Write-Host $line
        }
    }
    else {
        # Lines that don't match standard logcat format (continuation lines, etc.)
        Write-Host $line
    }
}