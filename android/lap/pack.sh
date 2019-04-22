#! /bin/sh

read -p "2. please input version(1.0.0):" b

if [ ! $b ]; then
  b="1.0.0"
fi

echo "version=$b"


# 2. 清理一下上次构建记录
gradle -q clean
# 3. 开始构建,执行命令：gradle pack -Pasversion=1.0 -Piszip=true
gradle -q pack -Pasversion=$b


