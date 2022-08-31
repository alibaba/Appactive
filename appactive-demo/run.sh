#
# Copyright 1999-2022 Alibaba Group Holding Ltd.
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#

docker-compose build
docker-compose up -d nacos mysql
sleep 20
docker-compose up -d storage storage-unit
sleep 15
docker-compose up -d product product-unit
sleep 15
docker-compose up -d frontend frontend-unit
sleep 3
docker-compose up -d gateway

# docker-compose up --no-recreate
# docker-compose down
