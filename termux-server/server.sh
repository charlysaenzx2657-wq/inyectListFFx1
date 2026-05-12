#!/data/data/com.termux/files/usr/bin/bash
# ================================================================
#  SENSIS GOOD — Servidor Local para APK
#  Escucha en localhost:7878
#  La APK manda comandos → este server los ejecuta con rish
# ================================================================

PORT=7878
LOG="$HOME/.sensis_server.log"

echo "[SENSIS] Iniciando servidor en puerto $PORT..." | tee -a "$LOG"

if ! command -v rish >/dev/null 2>&1; then
  echo "[ERROR] rish no encontrado. Instala: pkg install rish" | tee -a "$LOG"
  exit 1
fi

RISH_TEST=$(rish -c "echo RISH_OK" 2>&1)
if ! echo "$RISH_TEST" | grep -q "RISH_OK"; then
  echo "[ERROR] Shizuku no activo o sin permiso." | tee -a "$LOG"
  exit 1
fi

echo "[OK] Shizuku activo. Servidor listo en localhost:$PORT" | tee -a "$LOG"

# Usar ncat (netcat) incluido en Termux
if ! command -v ncat >/dev/null 2>&1 && ! command -v nc >/dev/null 2>&1; then
  pkg install -y nmap 2>/dev/null
fi

NC_CMD="ncat"
command -v ncat >/dev/null 2>&1 || NC_CMD="nc"

handle_request() {
  local raw="$1"
  # Parsear método y path del HTTP request
  local method=$(echo "$raw" | head -1 | cut -d' ' -f1)
  local path=$(echo "$raw" | head -1 | cut -d' ' -f2)
  local body=$(echo "$raw" | tail -1)

  # CORS headers
  local headers="HTTP/1.1 200 OK\r\nAccess-Control-Allow-Origin: *\r\nAccess-Control-Allow-Methods: POST, GET, OPTIONS\r\nAccess-Control-Allow-Headers: Content-Type\r\nContent-Type: application/json\r\n\r\n"

  if [ "$method" = "OPTIONS" ]; then
    printf "${headers}{\"status\":\"ok\"}"
    return
  fi

  case "$path" in
    /ping)
      printf "${headers}{\"status\":\"alive\",\"version\":\"2.0\"}"
      ;;
    /device)
      local brand=$(getprop ro.product.brand 2>/dev/null)
      local model=$(getprop ro.product.model 2>/dev/null)
      local android=$(getprop ro.build.version.release 2>/dev/null)
      local sdk=$(getprop ro.build.version.sdk 2>/dev/null)
      printf "${headers}{\"brand\":\"$brand\",\"model\":\"$model\",\"android\":\"$android\",\"sdk\":\"$sdk\"}"
      ;;
    /run)
      # El body trae el comando a ejecutar
      local cmd=$(echo "$body" | python3 -c "
import sys,json
try:
  d=json.load(sys.stdin)
  print(d.get('cmd',''))
except:
  print('')
" 2>/dev/null)
      if [ -z "$cmd" ]; then
        printf "${headers}{\"status\":\"error\",\"msg\":\"No cmd\"}"
        return
      fi
      local result=$(rish -c "$cmd" 2>&1)
      local escaped=$(echo "$result" | python3 -c "import sys,json; print(json.dumps(sys.stdin.read()))" 2>/dev/null || echo "\"done\"")
      printf "${headers}{\"status\":\"ok\",\"output\":$escaped}"
      ;;
    /inject/touch)
      # Ejecuta bloques de touch inject del script original
      rish -c "
settings put system pointer_speed 7
settings put system touch_sensitivity_mode 2
settings put system high_touch_sensitivity_enable 1
settings put system glove_mode_enabled 1
settings put system touch_noise_filter_level 0
settings put system touch_path_smoothing 0
settings put system multitouch_enabled 1
settings put system max_touch_points 10
settings put system ghost_touch_filter 1
settings put system input_event_rate 240
settings put system motion_prediction_enabled 0
settings put system touch_response_time 1
settings put system touch_idle_polling_reduction 0
settings put system fling_velocity_multiplier 1.5
settings put system fling_max_velocity 24000
settings put system fling_min_velocity 50
settings put system overscroll_distance 0
settings put system overfling_distance 4
settings put system scroll_friction 0.008
settings put system palm_rejection_level 0
settings put system palm_area_threshold 1800
settings put system edge_touch_exclusion_zone 0
settings put system top_edge_exclusion 0
settings put system bottom_edge_exclusion 0
settings put system left_edge_exclusion 0
settings put system right_edge_exclusion 0
settings put system touch_size_max 25
settings put system touch_size_min 1
settings put system touch_pressure_scale 1.0
settings put system touch_pressure_min 1
settings put system touch_pressure_max 255
settings put system touch_pressure_gain 1.5
settings put system touch_pressure_normalize 1
settings put system touch_interpolation_enabled 1
settings put system touch_interpolation_factor 0.1
settings put system touch_boost_level 7
settings put system input_boost_duration 200
settings put system display_adaptive_rate 1
settings put system display_hfr_enable 1
settings put system perf_boost_enable 1
settings put system gyroscope_enabled 1
settings put system gyroscope_low_latency_mode 1
" 2>&1
      printf "${headers}{\"status\":\"ok\",\"msg\":\"Touch inject aplicado\"}"
      ;;
    /inject/global)
      rish -c "
settings put global window_animation_scale 0
settings put global transition_animation_scale 0
settings put global animator_duration_scale 0
settings put global game_mode_enabled 1
settings put global zero_latency_touch_mode 1
settings put global touch_boost_enabled 1
settings put global high_refresh_rate_enabled 1
settings put global force_high_refresh_rate 1
settings put global aggressive_wifi_to_mobile_handover 1
settings put global wifi_scan_throttle_enabled 0
settings put global bluetooth_a2dp_sink_priority_headset_default 100
" 2>&1
      printf "${headers}{\"status\":\"ok\",\"msg\":\"Global inject aplicado\"}"
      ;;
    /inject/secure)
      rish -c "
settings put secure long_press_timeout 300
settings put secure touch_slop 1
settings put secure tap_timeout 120
settings put secure multi_press_timeout 300
settings put secure double_tap_timeout 250
settings put secure key_repeat_delay 25
settings put secure key_repeat_timeout 400
" 2>&1
      printf "${headers}{\"status\":\"ok\",\"msg\":\"Secure inject aplicado\"}"
      ;;
    /inject/props)
      rish -c "
setprop persist.sys.gpu.boost 1
setprop persist.sys.perf.topApp.boost 1
setprop debug.hwui.renderer opengl
setprop persist.sys.force_highendgfx true
setprop dalvik.vm.heapsize 512m
setprop persist.sys.binder.boost 1
setprop debug.sf.hw 1
setprop debug.egl.hw 1
setprop debug.performance.tuning 1
setprop video.accelerate.hw 1
setprop persist.sys.ui.hw 1
setprop ro.config.nocheckin true
setprop debug.hwui.use_partial_updates false
" 2>&1
      printf "${headers}{\"status\":\"ok\",\"msg\":\"Props aplicadas\"}"
      ;;
    /inject/fps)
      # Comandos extra de FPS/optimización que no están en el .sh original
      rish -c "
settings put global adaptive_battery_management_enabled 0
settings put global app_standby_enabled 0
settings put global forced_app_standby_enabled 0
settings put system min_refresh_rate 60
settings put system peak_refresh_rate 120
settings put global gpu_debug_layers_gles
settings put global enable_gpu_debug_layers 0
settings put global angle_gl_driver_selection_values default
settings put global freeform_window_management 0
" 2>&1

      setprop persist.sys.miui.sf_cores 4 2>/dev/null
      setprop debug.sf.recomputecrop 0 2>/dev/null
      setprop persist.sys.purgeable_assets 1 2>/dev/null
      setprop ro.media.enc.jpeg.quality 100 2>/dev/null
      setprop persist.vendor.qti.telecom.lte.only false 2>/dev/null

      # CPU governor performance si existe
      for cpu in /sys/devices/system/cpu/cpu*/cpufreq/scaling_governor; do
        echo performance > "$cpu" 2>/dev/null
      done

      # GPU governor
      echo performance > /sys/class/kgsl/kgsl-3d0/devfreq/governor 2>/dev/null
      echo 1 > /sys/class/kgsl/kgsl-3d0/force_bus_on 2>/dev/null
      echo 1 > /sys/class/kgsl/kgsl-3d0/force_clk_on 2>/dev/null
      echo 1 > /sys/class/kgsl/kgsl-3d0/force_rail_on 2>/dev/null
      echo 0 > /sys/class/kgsl/kgsl-3d0/thermal_pwrlevel 2>/dev/null

      # Memoria / ZRAM
      echo 100 > /proc/sys/vm/swappiness 2>/dev/null
      echo 0 > /proc/sys/vm/page-cluster 2>/dev/null
      echo 3000 > /proc/sys/vm/dirty_expire_centisecs 2>/dev/null
      echo 500 > /proc/sys/vm/dirty_writeback_centisecs 2>/dev/null

      # Red - reducir latencia
      echo 1 > /proc/sys/net/ipv4/tcp_low_latency 2>/dev/null
      echo 0 > /proc/sys/net/ipv4/tcp_timestamps 2>/dev/null
      echo 1 > /proc/sys/net/ipv4/tcp_fastopen 2>/dev/null
      echo "bbr" > /proc/sys/net/ipv4/tcp_congestion_control 2>/dev/null

      # I/O scheduler
      for q in /sys/block/*/queue/scheduler; do
        echo deadline > "$q" 2>/dev/null || echo noop > "$q" 2>/dev/null
      done
      for q in /sys/block/*/queue/read_ahead_kb; do
        echo 512 > "$q" 2>/dev/null
      done

      printf "${headers}{\"status\":\"ok\",\"msg\":\"FPS + Optimización aplicada\"}"
      ;;
    *)
      printf "${headers}{\"status\":\"error\",\"msg\":\"Path no encontrado\"}"
      ;;
  esac
}

# Loop principal del servidor
while true; do
  REQUEST=$(echo "" | $NC_CMD -l -p $PORT -q 2 2>/dev/null || echo "" | nc -l -p $PORT 2>/dev/null)
  if [ -n "$REQUEST" ]; then
    RESPONSE=$(handle_request "$REQUEST")
    echo -e "$RESPONSE" | $NC_CMD -l -p $PORT -q 1 2>/dev/null &
  fi
  sleep 0.1
done
