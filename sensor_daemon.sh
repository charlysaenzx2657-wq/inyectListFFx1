#!/bin/bash
PREV_X=0
LAST_LEVEL=-1

getevent -lt /dev/input/event4 | while read line; do
    if echo "$line" | grep -q "ABS_MT_POSITION_X"; then
        X=$(echo "$line" | awk '{print $NF}')
        X=$((16#$X))

        if [ "$PREV_X" != "0" ]; then
            DX=$((X - PREV_X))
            if [ $DX -lt 0 ]; then DX=$((-DX)); fi

            if [ $DX -gt 80 ]; then
                settings put system pointer_speed 7
                settings put system scroll_friction 0.1
            elif [ $DX -gt 25 ]; then
                settings put system pointer_speed 5
                settings put system scroll_friction 0.4
            else
                settings put system pointer_speed 2
                settings put system scroll_friction 0.7
            fi
        fi
        PREV_X=$X
    fi
done
