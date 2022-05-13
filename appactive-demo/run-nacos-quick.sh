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


docker-compose -f docker-compose-nacos.yml up -d nacos mysql
sleep 20s

curl -X POST 'http://127.0.0.1:8848/nacos/v1/console/namespaces' -d 'customNamespaceId=appactiveDemoNamespaceId&namespaceName=appactiveDemoNamespaceName&namespaceDesc=appactiveDemoNamespaceDesc'

cd ../appactive-portal
sh baseline.sh 2 NACOS appactiveDemoNamespaceId

cd ../appactive-demo
export appactiveNamespaceId="appactiveDemoNamespaceId"
docker-compose -f docker-compose-quick.yml up -d storage storage-unit
sleep 15s
docker-compose -f docker-compose-quick.yml up -d product product-unit
sleep 15s
docker-compose -f docker-compose-quick.yml up -d frontend frontend-unit
sleep 3s
docker-compose -f docker-compose-quick.yml up -d gateway

sleep 3s
cd ../appactive-portal
sh baseline.sh 3


