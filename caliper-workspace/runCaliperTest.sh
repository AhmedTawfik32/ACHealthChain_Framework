npm init -y

npm install --only=prod \
    @hyperledger/caliper-cli@0.5.0

px caliper bind \
    --caliper-bind-sut fabric:2.4
        
npm install --only=prod @hyperledger/caliper-cli@0.5.0

npx caliper bind --caliper-bind-sut fabric:2.4 

sudo chmod +x *

#Change private key of networkConfig file

npx caliper launch manager \
    --caliper-workspace . \
    --caliper-benchconfig benchmarks/myHealthcareBenchmark.yaml \
    --caliper-networkconfig networks/networkConfig.yaml

