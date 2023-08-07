#!/bin/bash
#
# Copyright IBM Corp All Rights Reserved
#
# SPDX-License-Identifier: Apache-2.0
#
# Exit on first error
set -e

# don't rewrite paths for Windows Git Bash users
export MSYS_NO_PATHCONV=1
starttime=$(date +%s)
CC_SRC_LANGUAGE=${1:-"java"}
CC_SRC_LANGUAGE=`echo "$CC_SRC_LANGUAGE" | tr [:upper:] [:lower:]`

if [ "$CC_SRC_LANGUAGE" = "go" -o "$CC_SRC_LANGUAGE" = "golang" ] ; then
	CC_SRC_PATH="../chaincode/healthcareFramework/go/"
elif [ "$CC_SRC_LANGUAGE" = "javascript" ]; then
	CC_SRC_PATH="../chaincode/healthcareFramework/javascript/"
elif [ "$CC_SRC_LANGUAGE" = "java" ]; then
	CC_SRC_PATH="../chaincode/healthcareFramework/javaehrchain"
elif [ "$CC_SRC_LANGUAGE" = "typescript" ]; then
	CC_SRC_PATH="../chaincode/healthcareFramework/typescript/"
else
	echo The chaincode language ${CC_SRC_LANGUAGE} is not supported by this script
	echo Supported chaincode languages are: go, java, javascript, and typescript
	exit 1
fi

# clean out any old identites in the wallets
rm -rf javascript/wallet/*
rm -rf java/wallet/*
rm -rf typescript/wallet/*
rm -rf go/wallet/*

# launch network; create channel and join peer to channel
pushd ../test-network
./network.sh down
./network.sh up createChannel -ca -s couchdb
sleep 30
./network.sh deployCC -ccn ehrchain -ccv 1 -cci initLedger -ccl ${CC_SRC_LANGUAGE} -ccp ${CC_SRC_PATH}
CC_SRC_PATH="../chaincode/healthcareFramework/javadiagnosischain"
./network.sh deployDiagnosisCC -ccn diagnosischain -ccv 1 -cci initLedger -ccl ${CC_SRC_LANGUAGE} -ccp ${CC_SRC_PATH}
CC_SRC_PATH="../chaincode/healthcareFramework/javapolicychain"
./network.sh deployPolicyCC -ccn policychain -ccv 1 -cci initLedger -ccl ${CC_SRC_LANGUAGE} -ccp ${CC_SRC_PATH}
CC_SRC_PATH="../chaincode/healthcareFramework/javalogchain"
./network.sh deployLogCC -ccn logchain -ccv 1 -cci initLedger -ccl ${CC_SRC_LANGUAGE} -ccp ${CC_SRC_PATH}
popd

cat <<EOF

Total setup execution time : $(($(date +%s) - starttime)) secs ...

--------------------------------------------------------------------------------------
Java:

  Start by changing into the "java" directory:
    cd java

  Then, install dependencies and run the test using:
    mvn test

  The test will invoke the sample client app which perform the following:
    - Enroll admin, healthcare provider and patient and import them into the wallet (if they don't already exist there)
    - Submit a transaction to create a new ehr, diagnosis, policy, and log
    - Evaluate a transaction (query) to return details of ehr, diagnosis, policy, and log
    - Evaluate a transaction (query) to return the updated details of ehr, diagnosis, policy, and log

EOF
