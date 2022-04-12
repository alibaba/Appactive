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

# sh run-nacos.sh 1
type=$1

if [ $type == 1 ]
then
  docker-compose -f docker-compose-nacos.yml up -d nacos mysql
fi

# sh run-nacos.sh 2 79e007b8-4b3e-40b2-9c8a-06bd5335498b
if [ $type == 2 ]
then
  temp=$2
  export appactiveNamespaceId="${temp}"
  docker-compose -f docker-compose-nacos.yml build
  docker-compose -f docker-compose-nacos.yml up -d storage storage-unit
  sleep 15s
  docker-compose -f docker-compose-nacos.yml up -d product product-unit
  sleep 15s
  docker-compose -f docker-compose-nacos.yml up -d frontend frontend-unit
  sleep 3s
  docker-compose -f docker-compose-nacos.yml up -d gateway
fi

# docker-compose stop storage storage-unit product product-unit frontend frontend-unit