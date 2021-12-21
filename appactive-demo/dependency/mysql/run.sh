docker rm -f appactive-mysql
docker run --name appactive-mysql --hostname appactive-mysql -e MYSQL_ROOT_PASSWORD=demo_appactiive_pw -e MYSQL_DATABASE=product -p 3306:3306 -d mysql:5.7 --character-set-server=utf8mb4
