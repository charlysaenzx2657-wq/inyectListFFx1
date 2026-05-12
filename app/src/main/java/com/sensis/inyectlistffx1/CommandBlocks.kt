package com.sensis.inyectlistffx1

object CommandBlocks {

    fun get(type: String): List<String> = when (type) {
        "TOUCH"  -> touchCmds
        "GLOBAL" -> globalCmds
        "SECURE" -> secureCmds
        "PROPS"  -> propsCmds
        "FPS"    -> fpsCmds
        "ALL"    -> touchCmds + globalCmds + secureCmds + propsCmds + fpsCmds
        else     -> emptyList()
    }

    // ── BLOQUE TOUCH (del .sh original) ──────────────────────────────────
    private val touchCmds = listOf(
        "settings put system pointer_speed 7",
        "settings put system pointer_location 0",
        "settings put system show_touches 0",
        "settings put system pointer_gesture_enabled 1",
        "settings put system scroll_friction 0.008",
        "settings put system touch_exploration_enabled 0",
        "settings put system fling_velocity_multiplier 1.5",
        "settings put system fling_max_velocity 24000",
        "settings put system fling_min_velocity 50",
        "settings put system overscroll_distance 0",
        "settings put system overfling_distance 4",
        "settings put system scroll_inertia_factor 1.0",
        "settings put system scroll_deceleration 0.97",
        "settings put system touch_scroll_velocity 1.5",
        "settings put system zoom_sensitivity 1.0",
        "settings put system edge_gesture_threshold 10",
        "settings put system palm_rejection_level 0",
        "settings put system touch_size_max 25",
        "settings put system touch_size_min 1",
        "settings put system touch_aspect_ratio_max 4.0",
        "settings put system palm_area_threshold 1800",
        "settings put system edge_touch_exclusion_zone 0",
        "settings put system top_edge_exclusion 0",
        "settings put system bottom_edge_exclusion 0",
        "settings put system left_edge_exclusion 0",
        "settings put system right_edge_exclusion 0",
        "settings put system touch_sensitivity_mode 2",
        "settings put system high_touch_sensitivity_enable 1",
        "settings put system glove_mode_enabled 1",
        "settings put system glove_touch_threshold 3",
        "settings put system game_mode_palm_rejection 0",
        "settings put system touch_noise_filter_level 0",
        "settings put system touch_path_smoothing 0",
        "settings put system palm_velocity_threshold 50",
        "settings put system palm_rejection_grace_time 0",
        "settings put system palm_min_area 500",
        "settings put system palm_zone_mask 0",
        "settings put system multitouch_enabled 1",
        "settings put system max_touch_points 10",
        "settings put system ghost_touch_filter 1",
        "settings put system ghost_touch_timeout 100",
        "settings put system min_touch_separation 8",
        "settings put system touch_id_track_threshold 5",
        "settings put system extended_touch_slot 1",
        "settings put system touch_interpolation_enabled 1",
        "settings put system touch_interpolation_factor 0.1",
        "settings put system active_touch_tracking_mode 1",
        "settings put system multitouch_report_latency 4",
        "settings put system multitouch_event_buffer 2",
        "settings put system multitouch_position_prediction 0",
        "settings put system touch_merge_threshold 15",
        "settings put system touch_lost_timeout 50",
        "settings put system touch_parallax_compensation 0",
        "settings put system touch_pressure_scale 1.0",
        "settings put system touch_pressure_min 1",
        "settings put system touch_pressure_max 255",
        "settings put system touch_pressure_curve 0",
        "settings put system touch_major_scale 1.0",
        "settings put system touch_minor_scale 1.0",
        "settings put system touch_orientation_enabled 1",
        "settings put system touch_hover_enabled 1",
        "settings put system touch_hover_distance_max 30",
        "settings put system hover_sensitivity 5",
        "settings put system pressure_tap_threshold 30",
        "settings put system touch_pressure_saturation 200",
        "settings put system touch_pressure_gain 1.5",
        "settings put system touch_pressure_offset 0",
        "settings put system touch_pressure_normalize 1",
        "settings put system input_event_rate 240",
        "settings put system input_kernel_buffer 64",
        "settings put system input_read_mode 1",
        "settings put system motion_event_compression 0",
        "settings put system motion_event_batch_interval 0",
        "settings put system motion_prediction_enabled 0",
        "settings put system touch_response_time 1",
        "settings put system touch_idle_polling_reduction 0",
        "settings put system touch_idle_report_interval 4",
        "settings put system touch_boost_level 7",
        "settings put system input_boost_duration 200",
        "settings put system touch_wakeup_enabled 1",
        "settings put system touch_double_tap_enabled 1",
        "settings put system touch_gesture_enabled 1",
        "settings put system gyroscope_enabled 1",
        "settings put system gyroscope_low_latency_mode 1",
        "settings put system gyroscope_calibration_mode 1",
        "settings put system accelerometer_enabled 1",
        "settings put system accelerometer_low_latency 1",
        "settings put system sensor_fusion_enabled 1",
        "settings put system display_adaptive_rate 1",
        "settings put system display_hfr_enable 1",
        "settings put system perf_boost_enable 1",
        "settings put system touch_report_rate 240",
        "settings put system touch_scan_rate 240"
    )

    // ── BLOQUE GLOBAL (del .sh original) ─────────────────────────────────
    private val globalCmds = listOf(
        "settings put global window_animation_scale 0",
        "settings put global transition_animation_scale 0",
        "settings put global animator_duration_scale 0",
        "settings put global game_mode_enabled 1",
        "settings put global zero_latency_touch_mode 1",
        "settings put global touch_boost_enabled 1",
        "settings put global high_refresh_rate_enabled 1",
        "settings put global force_high_refresh_rate 1",
        "settings put global aggressive_wifi_to_mobile_handover 1",
        "settings put global wifi_scan_throttle_enabled 0",
        "settings put global network_avoid_bad_wifi 1",
        "settings put global connectivity_samples_valid_duration_in_seconds 3600",
        "settings put global inet_condition_debounce_up_delay 100",
        "settings put global inet_condition_debounce_down_delay 500",
        "settings put global tcp_default_init_rwnd 60",
        "settings put global wifi_enhanced_auto_join 1",
        "settings put global wifi_network_show_rssi 1",
        "settings put global adaptive_battery_management_enabled 0",
        "settings put global app_standby_enabled 0",
        "settings put global forced_app_standby_enabled 0",
        "settings put global enable_gpu_debug_layers 0",
        "settings put global freeform_window_management 0",
        "settings put global cached_apps_freezer disabled"
    )

    // ── BLOQUE SECURE (del .sh original) ─────────────────────────────────
    private val secureCmds = listOf(
        "settings put secure long_press_timeout 300",
        "settings put secure touch_slop 1",
        "settings put secure tap_timeout 120",
        "settings put secure multi_press_timeout 300",
        "settings put secure double_tap_timeout 250",
        "settings put secure key_repeat_delay 25",
        "settings put secure key_repeat_timeout 400",
        "settings put secure accessibility_enabled 0",
        "settings put secure speak_password 0",
        "settings put secure screensaver_enabled 0"
    )

    // ── BLOQUE PROPS (del .sh original) ──────────────────────────────────
    private val propsCmds = listOf(
        "setprop persist.sys.gpu.boost 1",
        "setprop persist.sys.perf.topApp.boost 1",
        "setprop debug.hwui.renderer opengl",
        "setprop persist.sys.force_highendgfx true",
        "setprop dalvik.vm.heapsize 512m",
        "setprop dalvik.vm.heapgrowthlimit 256m",
        "setprop dalvik.vm.heapminfree 8m",
        "setprop dalvik.vm.heapmaxfree 32m",
        "setprop dalvik.vm.heaptargetutilization 0.75",
        "setprop persist.sys.binder.boost 1",
        "setprop debug.sf.hw 1",
        "setprop debug.egl.hw 1",
        "setprop debug.performance.tuning 1",
        "setprop video.accelerate.hw 1",
        "setprop persist.sys.ui.hw 1",
        "setprop debug.hwui.use_partial_updates false",
        "setprop persist.honor.touch.sensitivity 7",
        "setprop persist.huawei.touch.sensitivity 7",
        "setprop persist.sys.strictmode.disable true",
        "setprop persist.sys.purgeable_assets 1",
        "setprop persist.sys.binder.boost 1",
        "setprop ro.config.nocheckin true",
        "setprop pm.sleep_mode 1",
        "setprop wifi.supplicant_scan_interval 180"
    )

    // ── BLOQUE FPS EXTRA (comandos que NO están en el .sh original) ───────
    private val fpsCmds = listOf(
        // Refresh rate máximo
        "settings put system min_refresh_rate 60",
        "settings put system peak_refresh_rate 120",
        "settings put system user_refresh_rate 120",
        "settings put system display_refresh_rate 120",

        // GPU Adreno — forzar máximo clock
        "echo performance > /sys/class/kgsl/kgsl-3d0/devfreq/governor 2>/dev/null || true",
        "echo 1 > /sys/class/kgsl/kgsl-3d0/force_bus_on 2>/dev/null || true",
        "echo 1 > /sys/class/kgsl/kgsl-3d0/force_clk_on 2>/dev/null || true",
        "echo 1 > /sys/class/kgsl/kgsl-3d0/force_rail_on 2>/dev/null || true",
        "echo 0 > /sys/class/kgsl/kgsl-3d0/thermal_pwrlevel 2>/dev/null || true",
        "cat /sys/class/kgsl/kgsl-3d0/max_gpuclk 2>/dev/null > /sys/class/kgsl/kgsl-3d0/gpuclk 2>/dev/null || true",

        // GPU Mali
        "echo always_on > /sys/devices/platform/*/mali*/power_policy 2>/dev/null || true",
        "echo performance > /sys/devices/platform/*/mali*/dvfs_policy 2>/dev/null || true",

        // CPU governor performance
        "for f in /sys/devices/system/cpu/cpu*/cpufreq/scaling_governor; do echo performance > \$f 2>/dev/null; done",
        "for f in /sys/devices/system/cpu/cpu*/cpufreq/scaling_min_freq; do cat /sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_max_freq 2>/dev/null > \$f 2>/dev/null; done",

        // HWUI / renderizado
        "setprop debug.hwui.overdraw false",
        "setprop debug.hwui.show_dirty_regions false",
        "setprop debug.hwui.profile false",
        "setprop debug.hwui.layer_cache_size 512",
        "setprop debug.hwui.texture_cache_size 72",
        "setprop debug.hwui.path_cache_size 32",
        "setprop debug.hwui.drop_shadow_cache_size 6",
        "setprop debug.hwui.text_large_cache_width 2048",
        "setprop debug.hwui.text_large_cache_height 512",
        "setprop debug.hwui.disable_scissor_opt false",
        "setprop persist.vendor.gpu.boost 1",
        "setprop vendor.opengles.version 196610",

        // SurfaceFlinger
        "service call SurfaceFlinger 1008 i32 1 2>/dev/null || true",
        "setprop debug.sf.disable_backpressure 1",
        "setprop debug.sf.latch_unsignaled 1",
        "setprop ro.surface_flinger.max_frame_buffer_acquired_buffers 3",

        // Dalvik / ART compilación rápida
        "setprop dalvik.vm.dex2oat-filter speed",
        "setprop dalvik.vm.image-dex2oat-filter speed",
        "setprop dalvik.vm.dex2oat-threads 4",
        "setprop dalvik.vm.boot-dex2oat-threads 4",
        "setprop pm.dexopt.boot speed",
        "setprop pm.dexopt.first-boot speed",

        // I/O Scheduler — lectura más rápida de assets del juego
        "for q in /sys/block/*/queue/scheduler; do echo deadline > \$q 2>/dev/null || echo cfq > \$q 2>/dev/null || true; done",
        "for q in /sys/block/*/queue/read_ahead_kb; do echo 512 > \$q 2>/dev/null || true; done",
        "for q in /sys/block/*/queue/nr_requests; do echo 256 > \$q 2>/dev/null || true; done",
        "for q in /sys/block/*/queue/add_random; do echo 0 > \$q 2>/dev/null || true; done",

        // Thermal gaming
        "setprop persist.vendor.thermal.config thermal_info_config_gaming.json 2>/dev/null || true",
        "setprop persist.thermal.config gaming 2>/dev/null || true",

        // Xiaomi / MIUI
        "settings put global miui_optimization 0",
        "setprop persist.sys.miui.sf_cores 4 2>/dev/null || true",
        "setprop persist.miui.extm.enable 0 2>/dev/null || true",

        // Samsung
        "setprop persist.vendor.game_opt.enable 1 2>/dev/null || true",

        // OPPO / Realme
        "setprop persist.sys.oplus.game_mode 1 2>/dev/null || true",

        // Prioridad de proceso — Free Fire y CoD
        "cmd game mode 2 com.dts.freefireth 2>/dev/null || true",
        "cmd game mode 2 com.garena.game.codm 2>/dev/null || true",
        "cmd game mode 2 com.activision.callofduty.shooter 2>/dev/null || true",
        "settings put global game_driver_opt_in_apps com.dts.freefireth 2>/dev/null || true",
        "settings put global game_driver_opt_in_apps com.garena.game.codm 2>/dev/null || true"
    )
}
