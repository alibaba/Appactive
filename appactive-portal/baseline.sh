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

type=$1

if [ `expr $type % 2` == 0 ]
then
  for file in $(ls ../appactive-demo/data/); do
    if [[ "$file" == *"path-address"* ]]; then
      echo "continue"
      continue
    fi
    echo "$(date "+%Y-%m-%d %H:%M:%S") 应用 ${file} 基线推送中";
    cp -f ./rule/idSource.json "../appactive-demo/data/$file/"
    cp -f ./rule/transformerBetween.json "../appactive-demo/data/$file/idTransformer.json"
    cp -f ./rule/idUnitMapping.json "../appactive-demo/data/$file/"
    cp -f ./rule/dbProperty.json "../appactive-demo/data/$file/mysql-product"
    arr=(${file//-/ })
    unitFlag=${arr[1]}
    echo "{\"unitFlag\":\"${unitFlag}\"}" > "../appactive-demo/data/$file/machine.json"
    echo "$(date "+%Y-%m-%d %H:%M:%S") 应用 ${file} 基线推送完成"
  done
fi

if [ `expr $type % 3` == 0 ]
then
  idSource=$(cat ./rule/idSource.json)
  idTransformer=$(cat ./rule/idTransformer.json)
  idUnitMapping=$(cat ./rule/idUnitMapping.json)

  gatewayRule="{\"idSource\" : $idSource, \"idTransformer\" : $idTransformer, \"idUnitMapping\" : $idUnitMapping}"
  data="{\"key\" : \"459236fc-ed71-4bc4-b46c-69fc60d31f18_test1122\", \"value\" : $gatewayRule}"
  echo $data
  echo "$(date "+%Y-%m-%d %H:%M:%S") gateway 基线推送结果: " && curl --header "Content-Type: application/json" \
  --request POST \
  --data "$data" \
  127.0.0.1:8090/set
fi



