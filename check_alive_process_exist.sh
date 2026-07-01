#!/bin/bash

# 执行 adb shell 命令并查找指定关键词
initial_ps_output=$(adb shell ps -ef | grep "com.alibaba\|m_d_\|wolverine\|phoenix")

# 从输出中解析用户 ID（假设用户 ID 在每行的第一个字段）
user_ids=$(echo "$initial_ps_output" | awk '{print $1}' | sort | uniq)

# 为每个解析到的用户 ID 执行新的 adb shell 命令
for user_id in $user_ids; do
    echo "Processes for user ID: $user_id"
    adb shell ps -ef | grep "$user_id"
    echo "-----------------------------"
done