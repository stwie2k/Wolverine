#!/bin/bash

# 获取进程名参数，如果没有提供则默认为 com.alibaba.wireless
#
# 杀死主客
# ./force_stop_app.sh
# 杀死商家版
# ./force_stop_app.sh com.alibaba.wireless.seller

PACKAGE_NAME=${1:-"com.alibaba.wireless.seller"}

# 初始化循环次数计数器
LOOP_COUNT=0

check_and_force_stop() {
  while true; do
      # 检查进程是否存在
      PROCESS_EXISTS=$(adb shell "pgrep -f $PACKAGE_NAME")

      if [ -n "$PROCESS_EXISTS" ]; then
          LOOP_COUNT=$((LOOP_COUNT + 1))
          echo -e "\033[36mLoop count: $LOOP_COUNT Process exists, forcing stop...\033[35m"
          adb shell "am force-stop $PACKAGE_NAME"
          adb shell ps -ef|grep "com.alibaba\|m_d_\|wolverine\|phoenix"

      else
          echo -e "\033[31m=======>It takes $LOOP_COUNT times to kill process: $PACKAGE_NAME\033[0m"
          break
      fi
  done
}


echo "Wait App Init..."

sleep 3

# 第一次立即执行
if ! check_and_force_stop; then
    echo "No running processes found after the first attempt."
    exit 0
fi

# 等待一会重试
echo "Waiting for 5 seconds before checking again..."
sleep 5

# 再次检查
if ! check_and_force_stop; then
    echo "No running processes found after the second attempt."
    exit 0
fi

echo "Processes were still running after two attempts."
