docker rm -f appactive-nacos
docker run --name appactive-nacos --hostname appactive-nacos -e MODE=standalone -p 8848:8848 -d nacos/nacos-server:2.0.2
