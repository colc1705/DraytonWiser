# DraytonWiser


To get your system secret

1. Press the setup button on your HeatHub, the light will start flashing
2. Look for the SSID 'WiserHeatXXX' where XXX is random 
3. Connect to the SSID from a PC
4. Once connected run the following command in PowerShell

  Invoke-RestMethod -Method Get -UseBasicParsing -Uri http://192.168.8.1/secret/
  
5. This will return a string which is your system secret
6. Press the setup button on the HeatHub again and it will go back to normal operations


You will need to enter the IP address of your hub into the smartapp so I recommend you configure your router to give it a fixed address

