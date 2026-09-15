$ErrorActionPreference = "Stop"

Get-PnpDevice -PresentOnly |
    Where-Object {
        $_.FriendlyName -match "Android|ADB|MTP|Pixel|Samsung|Motorola|OnePlus|Phone|USB Composite"
    } |
    Select-Object Status, Class, FriendlyName, InstanceId |
    Format-Table -AutoSize
