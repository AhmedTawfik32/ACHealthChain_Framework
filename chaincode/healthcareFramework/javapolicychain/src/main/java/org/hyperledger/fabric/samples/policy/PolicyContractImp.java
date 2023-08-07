/*
 * SPDX-License-Identifier: Apache-2.0
 */

package org.hyperledger.fabric.samples.policy;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.hyperledger.fabric.contract.Context;
import org.hyperledger.fabric.contract.ContractInterface;
import org.hyperledger.fabric.contract.annotation.Contact;
import org.hyperledger.fabric.contract.annotation.Contract;
import org.hyperledger.fabric.contract.annotation.Default;
import org.hyperledger.fabric.contract.annotation.Info;
import org.hyperledger.fabric.contract.annotation.License;
import org.hyperledger.fabric.contract.annotation.Transaction;
import org.hyperledger.fabric.shim.ChaincodeStub;
import org.hyperledger.fabric.shim.ledger.KeyValue;
import org.hyperledger.fabric.shim.ledger.QueryResultsIterator;
import com.owlike.genson.Genson;

/**
 * Java implementation of the Fabric Car Contract described in the Writing Your
 * First Application tutorial
 */
@Contract(
        name = "PolicyContractImp",
        info = @Info(
                title = "Healthcare contract",
                description = "The hyperlegendary healthcare contract",
                version = "0.0.1-SNAPSHOT",
                license = @License(
                        name = "Apache 2.0 License",
                        url = "http://www.apache.org/licenses/LICENSE-2.0.html"),
                contact = @Contact(
                        email = "health.care@example.com",
                        name = "Healthcare",
                        url = "https://hyperledger.example.com")))
@Default
public final class PolicyContractImp implements ContractInterface {

    private final Genson genson = new Genson();

    /**
     * Retrieves a policy with the specified key from the ledger.
     *
     * @param ctx   the transaction context
     * @param policyId the policyId
     * @return the Car found on the ledger if there was one
     */
    @Transaction()
    public PolicyChainTx queryPolicy(final Context ctx, final String policyId) {
        ChaincodeStub stub = ctx.getStub();
        String policyState = stub.getStringState(policyId);

        PolicyChainTx policyChainTx = null;
        if (policyState.isEmpty()) {
            String errorMessage = String.format("Policy %s does not exist", policyId);
            System.out.println(errorMessage);
        } else {
            policyChainTx = genson.deserialize(policyState, PolicyChainTx.class);
        }
        return policyChainTx;
    }

    @Transaction()
    public String queryPolicyByHpId(final Context ctx, final String granteeId, final String granterId, final String endDate, final String policyValidation) {
        ChaincodeStub stub = ctx.getStub();
        //String endDate = "2023/12/30";
        QueryResultsIterator<KeyValue> policyResults = stub.getQueryResult("{\"selector\":{\"granterId\":{\"$eq\":\"" + granterId + "\"},\"grantee" 
        + "Id\":{\"$eq\":\"" + granteeId + "\"},\"endDate\":{\"$gt\":\"" + endDate + "\"},\"policy"
        + "Validation\":{\"$eq\":\"" + policyValidation + "\"}}}");
        List<PolicyQueryResult> queryResults = new ArrayList<PolicyQueryResult>();
        for (KeyValue result : policyResults) {
            PolicyChainTx policyChainTx = genson.deserialize(result.getStringValue(), PolicyChainTx.class);
            queryResults.add(new PolicyQueryResult(result.getKey(),
                    new PolicyChainTx(policyChainTx.getGranterId(), policyChainTx.getGranteeId(), 
                    policyChainTx.getTimeStamp(), policyChainTx.getEHRType(), policyChainTx.getIpfsEhrAddress(), policyChainTx.getEnvelopedSymKey(), 
                    policyChainTx.getStartDate(), policyChainTx.getEndDate(), policyChainTx.getPolicyValidation(), policyChainTx.getGranterSignature(), 
                    policyChainTx.getPolicyChainTxId())));
        }

        final String response = genson.serialize(queryResults);

        return response;
    }

    @Transaction()
    public String queryPolicyByEHRType(final Context ctx, final String granteeId, final String granterId, 
    final String ehrType, final String endDate, final String policyValidation) {
        ChaincodeStub stub = ctx.getStub();
        QueryResultsIterator<KeyValue> policyResults = stub.getQueryResult("{\"selector\":{\"granterId\":{\"$eq\":\"" + granterId + "\"},\"grantee" 
        + "Id\":{\"$eq\":\"" + granteeId + "\"},\"eHRType\":{\"$eq\":\"" + ehrType + "\"},\"endDate\":{\"$gt\":" + endDate + "},\"policy"
        + "Validation\":{\"$eq\":\"" + policyValidation + "\"}}}");
        List<PolicyQueryResult> queryResults = new ArrayList<PolicyQueryResult>();
        for (KeyValue result : policyResults) {
            PolicyChainTx policyChainTx = genson.deserialize(result.getStringValue(), PolicyChainTx.class);
            String ipfsEhrHashAdd = policyChainTx.getIpfsEhrAddress();
            queryResults.add(new PolicyQueryResult(result.getKey(),
                    new PolicyChainTx(policyChainTx.getGranterId(), policyChainTx.getGranteeId(), 
                    policyChainTx.getTimeStamp(), policyChainTx.getEHRType(), ipfsEhrHashAdd, policyChainTx.getEnvelopedSymKey(),  
                    policyChainTx.getStartDate(), policyChainTx.getEndDate(), policyChainTx.getPolicyValidation(), policyChainTx.getGranterSignature(), 
                    policyChainTx.getPolicyChainTxId())));
        }

        final String response = genson.serialize(queryResults);

        return response;
    }

    /**
     * Creates some initial Policys on the ledger.
     *
     * @param ctx the transaction context
     */
    
    @Transaction()
    public void initLedger(final Context ctx) {
        ChaincodeStub stub = ctx.getStub();

        String[] policyData = {
        //        "{\"granterId\":\"p0\",\"timeStamp\":\"2023-02-02 03:48:05.591\",\"ipfsPolicy"
          //              + "Address\":\"1220432ebc434ce3fca611439719831c84e1bdb34f478355c67fad8636e23c73b3a2\",\"granter"
            //            + "Signature\":\"sig\",\"policyChainTxId\":\"p0policy0\"}"
                // "{ \"make\": \"Hyundai\", \"model\": \"Avante\", \"color\": \"black\",
                // \"owner\": \"Ahmed Tawfik\" }"
        };

        for (int i = 0; i < policyData.length; i++) {
            String policyId = String.format("p0pol0");

            PolicyChainTx policyChainTx = genson.deserialize(policyData[i], PolicyChainTx.class);
            String policyState = genson.serialize(policyChainTx);
            stub.putStringState(policyId, policyState);
        }
    }
    
    
    /**
     * Creates a new policy on the ledger.
     *
     * @param ctx            the transaction context
     * @param policyIdgranterId the key for the policy transaction
     * @param granterId      granter id
     * @param timestamp      the current datetime in milsec
     * @param ipfsPolicyAddress the ipfs address of policy
     * @param granterSig     the granter sig
     * @return the created policy
     */
    @Transaction()
    public PolicyChainTx createPolicy(final Context ctx, final String policyId, final String granterId,
            final String hpId, final String timeStamp, final String ehrType, final String ipfsEhrAddress,
            final String envelopedSymKey, final String startDate, final String endDate, final String isValid, 
            final String granterSig) {
        ChaincodeStub stub = ctx.getStub();

        PolicyChainTx policyChainTx = null;
        String policyState = stub.getStringState(policyId);

        if (!policyState.isEmpty()) {
            String errorMessage = String.format("Policy %s already exists", policyId);
            System.out.println(errorMessage);
            policyChainTx = queryPolicy(ctx, policyId);
        } else {
            policyChainTx = new PolicyChainTx(granterId, hpId, timeStamp, ehrType, ipfsEhrAddress, 
            envelopedSymKey, startDate, endDate, isValid, granterSig, policyId);
            policyState = genson.serialize(policyChainTx);
            stub.putStringState(policyId, policyState);
        }

        return policyChainTx;
    }

    /**
     * Retrieves all Policys from the ledger.
     *
     * @param ctx the transaction context
     * @return array of Policys found on the ledger
     * @throws IOException
     */
    @Transaction()
    public String queryAllPolicys(final Context ctx) throws IOException {
        ChaincodeStub stub = ctx.getStub();

        final String startKey = "p0pol0";
        final String endKey = "p99pol99";
        List<PolicyQueryResult> queryResults = new ArrayList<PolicyQueryResult>();

        QueryResultsIterator<KeyValue> results = stub.getStateByRange(startKey, endKey);

        for (KeyValue result : results) {
            PolicyChainTx policyChainTx = genson.deserialize(result.getStringValue(), PolicyChainTx.class);
            String ipfsEhrHashAdd = policyChainTx.getIpfsEhrAddress();
            //String ehr = IPFSStorage.getDataUsingIPFSAdd(ipfsPolicyHashAdd);
            queryResults.add(new PolicyQueryResult(result.getKey(),
                    new PolicyChainTx(policyChainTx.getGranterId(), policyChainTx.getGranteeId(), 
                    policyChainTx.getTimeStamp(), policyChainTx.getEHRType(), ipfsEhrHashAdd, 
                    policyChainTx.getEnvelopedSymKey(), policyChainTx.getStartDate(), policyChainTx.getEndDate(), 
                    policyChainTx.getPolicyValidation(), policyChainTx.getGranterSignature(), 
                    policyChainTx.getPolicyChainTxId())));
        }

        final String response = genson.serialize(queryResults);

        return response;
    }

    /**
     * Update the Policy on the ledger.
     *
     * @param ctx            the transaction context
     * @param policyId          the key
     * @param timestamp      the current datetime in milsec
     * @return the updated Policy
     */
    @Transaction()
    public PolicyChainTx updatePolicy(final Context ctx, final String policyId, final String ehrType,
            final String endDate, final String isValid) {
        ChaincodeStub stub = ctx.getStub();

        String policyState = stub.getStringState(policyId);

        if (policyState.isEmpty()) {
            String errorMessage = String.format("Policy %s does not exist", policyId);
            System.out.println(errorMessage);
        }

        PolicyChainTx policyChainTx = genson.deserialize(policyState, PolicyChainTx.class);

        PolicyChainTx newPolicyChainTx = new PolicyChainTx(policyChainTx.getGranterId(), policyChainTx.getGranteeId(), 
        policyChainTx.getTimeStamp(), ehrType, policyChainTx.getIpfsEhrAddress(), policyChainTx.getEnvelopedSymKey(), 
        policyChainTx.getStartDate(), endDate, isValid, policyChainTx.getGranterSignature(), 
        policyChainTx.getPolicyChainTxId());
        String newPolicyState = genson.serialize(newPolicyChainTx);
        stub.putStringState(policyId, newPolicyState);

        return newPolicyChainTx;
    }

}
