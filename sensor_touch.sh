#!/bin/bash
# SENSIS GOOD FF - Sensor Touch via getevent
# Honor X5C Plus - /dev/input/event4

PREV_X=0
PREV_Y=0
PREV_TIME=0
LAST_LEVEL=-1

apply_level() {
    local level=$1
    if [ "$level" != "$LAST_LEVEL" ]; then
        case $level in
            2) # RAPIDO
                settings put system pointer_speed 7
                settings put system scroll_friction 0.1
                ;;
            1) # MEDIO
                settings put system pointer_speed 5
                settings put system scroll_friction 0.4
                ;;
            *) # LENTO
                settings put system pointer_speed 2
                settings put system scroll_friction 0.7
                ;;
        esac
        LAST_LEVEL=$level
    fi
}

getevent -lt /dev/input/event4 | while read line; do
    if echo "$line" | grep -q "ABS_MT_POSITION_X"; then
        X=$(echo "$line" | awk '{print $NF}')
        X=$((16#$X))
        T=$(echo "$line" | awk '{print $1}' | tr -d '[')

        if [ "$PREV_X" != "0" ] && [ "$PREV_TIME" != "0" ]; then
            DX=$((X - PREV_X))
            if [ $DX -lt 0 ]; then DX=$((-DX)); fi
            
            # Velocidad aproximada basada en delta X
            if [ $DX -gt 80 ]; then
                apply_level 2  # RAPIDO
            elif [ $DX -gt 25 ]; then
                apply_level 1  # MEDIO
            else
                apply_level 0  # LENTO
            fi
        fi
        PREV_X=$X
        PREV_TIME=$T
    fi
done
