#!/data/data/com.termux/files/usr/bin/bash
echo "▶ Compilando SensisFF Sensor..."
cd ~/sensor-ff
gradle assembleDebug

APK="app/build/outputs/apk/debug/app-debug.apk"
if [ -f "$APK" ]; then
    cp $APK ~/storage/downloads/SensisFF-Sensor.apk
    echo "✅ APK guardado en Descargas"
    adb install ~/storage/downloads/SensisFF-Sensor.apk
    echo "✅ APK instalado"
else
    echo "❌ Error en compilación"
fi
