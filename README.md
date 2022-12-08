# simsend


simsend.json

{
  "global": {
    "email": "test@test.com",
    "phoneForSendSmsNoBlock": "+79991111111",
    "simCards": [
      {
        "number": "+79991111111",
        "operator": "MTS",
        "ccid": "89700010050360032119",
        "sendCusdBalance": true,
        "sendSmsForNoBlock": true
      },
      {
        "number": "+79992222222",
        "operator": "MTS",
        "ccid": "89700010050360033119",
        "sendCusdBalance": true,
        "sendSmsForNoBlock": true
      }
    ]
  },
  "log": {
    "home": "/home/evg/project/temp/simsend-app/logs/",
    "rootLevel": "DEBUG",
    "threshold": "DEBUG",
    "consoleThresholdLevel": "DEBUG"
  },
  "gmail": {
    "tokensDirectoryPath": "/home/evg/project/temp/simsend-app",
    "credentialsFilePath": "/home/evg/project/temp/simsend-app/client_secret.json"
  },
  "other": {
    "test": 0
  }
}

