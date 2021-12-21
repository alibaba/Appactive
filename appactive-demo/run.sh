docker-compose build
docker-compose up -d nacos mysql
sleep 20s
docker-compose up -d storage storage-unit
sleep 15s
docker-compose up -d product product-unit
sleep 15s
docker-compose up -d frontend frontend-unit
sleep 3s
docker-compose up -d gateway

# docker-compose up --no-recreate
# docker-compose down
