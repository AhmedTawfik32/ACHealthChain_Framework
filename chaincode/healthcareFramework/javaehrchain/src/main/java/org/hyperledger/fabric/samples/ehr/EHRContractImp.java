/*
 * SPDX-License-Identifier: Apache-2.0
 */

package org.hyperledger.fabric.samples.ehr;

import java.security.KeyPair;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECPublicKey;
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
import org.hyperledger.fabric.shim.Chaincode.Response;
import org.hyperledger.fabric.shim.ledger.KeyValue;
import org.hyperledger.fabric.shim.ledger.QueryResultsIterator;
import com.owlike.genson.Genson;

import io.ipfs.api.IPFS;
import io.ipfs.multihash.Multihash;

/**
 * Java implementation of the Fabric Car Contract described in the Writing Your
 * First Application tutorial
 */
@Contract(
        name = "EHRContractImp",
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
public final class EHRContractImp implements ContractInterface {

    private final Genson genson = new Genson();

    private KeyPair keyPair = null;

    /**
     * Retrieves a ehr with the specified key from the ledger.
     *
     * @param ctx   the transaction context
     * @param ehrId the ehrId
     * @return the Car found on the ledger if there was one
     */
    @Transaction()
    public EHRChainTx queryEHR(final Context ctx, final String ehrId) {
        ChaincodeStub stub = ctx.getStub();
        String ehrState = stub.getStringState(ehrId);
        
        EHRChainTx ehrChainTx = null;
        if (ehrState.isEmpty()) {
            String errorMessage = String.format("EHR %s does not exist", ehrId);
            System.out.println(errorMessage);
        } else {
            ehrChainTx = genson.deserialize(ehrState, EHRChainTx.class);
        }
        return ehrChainTx;
    }

    /**
     * deletes a ehr with the specified key from the ledger.
     *
     * @param ctx   the transaction context
     * @param ehrId the ehrId
     * @return the Car found on the ledger if there was one
     */
    @Transaction()
    public void deleteEHR(final Context ctx, final String ehrId) {
        ChaincodeStub stub = ctx.getStub();
        String ehrState = stub.getStringState(ehrId);
        
        if (!ehrState.isEmpty()) {
            stub.delState(ehrId);
        } 
    }

    /**
     * Gets a EC pK 
     *
     * @param ctx   the transaction context
     * @return the PK
     */
    @Transaction()
    public ECPublicKey getEHRChainPK(final Context ctx) {
         return (ECPublicKey) keyPair.getPublic();
    }

    /**
     * Retrieves a ehr with the patient id from the ledger.
     *
     * @param ctx   the transaction context
     * @param patientId the ehrId
     * @return the Car found on the ledger if there was one
     */
    @Transaction()
    public EHRChainTx queryEHRByPatientId(final Context ctx, final String patientId) {
        ChaincodeStub stub = ctx.getStub();
        QueryResultsIterator<KeyValue> ehrList = stub.getQueryResult("{\"selector\":{\"patientId\":{\"$eq\":\"" + patientId + "\"}}}");
        EHRChainTx ehrChainTx = null;
        for (KeyValue result : ehrList) {
            ehrChainTx = genson.deserialize(result.getStringValue(), EHRChainTx.class);
        }
        return ehrChainTx;
    }


    /**
     * Retrieves a ehr with the healthcare privider and patient id from the ledger.
     *
     * @param ctx   the transaction context
     * @param hpId the healthcare provider id
     * @param patientId the ehrId
     * @return the EHR found on the ledger if there was one
     */
    @Transaction()
    public String queryEHRByHpId(final Context ctx, final String hpId, final String patientId, final String endDate, final String policyValidation) {
        String response = "AccessDenied";
        ChaincodeStub stub = ctx.getStub();
        Response res = stub.invokeChaincodeWithStringArgs("policychain", "queryPolicyByHpId", hpId, patientId, endDate, policyValidation, "mychannel");
        String payload = res.getStringPayload();
        List<EHRQueryResult> queryResults = new ArrayList<EHRQueryResult>();
        EHRChainTx ehrChainTx = null;
        if (payload.contains(hpId)) {
            QueryResultsIterator<KeyValue> ehrList = stub.getQueryResult("{\"selector\":{\"patientId\":{\"$eq\":\"" + patientId + "\"}}}");
            for (KeyValue result : ehrList) {
                ehrChainTx = genson.deserialize(result.getStringValue(), EHRChainTx.class);
                queryResults.add(new EHRQueryResult(result.getKey(),
                    new EHRChainTx(ehrChainTx.getPatientId(), ehrChainTx.getTimeStamp(), ehrChainTx.getIpfsEhrAddress(),
                            ehrChainTx.getPatientSignature(), ehrChainTx.getEhrChainTxId())));
            }
            response = genson.serialize(queryResults);
        }
        return response;
    }

    @Transaction()
    public String queryEHRTypeByHpId(final Context ctx, final String hpId, final String patientId, final String ehrType, 
    final String endDate, final String policyValidation) {
        String response = "AccessDenied";
        ChaincodeStub stub = ctx.getStub();
        Response res = stub.invokeChaincodeWithStringArgs("policychain", "queryPolicyByEHRType", hpId, patientId, ehrType, endDate, policyValidation, "mychannel");
        String payload = res.getStringPayload();
        EHRChainTx ehrChainTx = null;
        if (payload.contains(hpId)) {
            QueryResultsIterator<KeyValue> ehrList = stub.getQueryResult("{\"selector\":{\"patientId\":{\"$eq\":\"" + patientId + "\"}}}");
            for (KeyValue result : ehrList) {
                ehrChainTx = genson.deserialize(result.getStringValue(), EHRChainTx.class);
            }
            response = genson.serialize(ehrChainTx);
        }
        return response;
    }
    
    /**
     * Creates some initial EHRs on the ledger.
     *
     * @param ctx the transaction context
     */
    @Transaction()
    public void initLedger(final Context ctx) throws Exception {
        ChaincodeStub stub = ctx.getStub();

        String[] ehrData = {
                "{\"patientId\":\"p0\",\"timeStamp\":\"2023-02-02 03:48:05.591\",\"ipfsEhr"
                        + "Address\":\"QmZJMx4SHAPDUu9UuUBuwM25s4vZJE7ErrKa5vRPfL4eNV\",\"patient"
                        + "Signature\":\"sig\",\"ehrChainTxId\":\"p0ehr0\"}"
        };

        keyPair = ECCryptography.getECKeys();

        for (int i = 0; i < ehrData.length; i++) {
            String ehrId = String.format("p0ehr0");

            EHRChainTx ehrChainTx = genson.deserialize(ehrData[i], EHRChainTx.class);
            String ehrState = genson.serialize(ehrChainTx);
            stub.putStringState(ehrId, ehrState);
        }
    }

    /**
     * Creates a new ehr on the ledger.
     *
     * @param ctx            the transaction context
     * @param ehrIdpatientId the key for the ehr transaction
     * @param patientId      patient id
     * @param timestamp      the current datetime in milsec
     * @param ipfsEHRAddress the ipfs address of ehr
     * @param patientSig     the patient sig
     * @return the created ehr
     */
    @Transaction()
    public EHRChainTx createEHR(final Context ctx, final String ehrId, final String patientId,
            final String timeStamp,
            final String ipfsEHRAddress, final String patientSig) {
        ChaincodeStub stub = ctx.getStub();

        EHRChainTx ehrChainTx = null;
        String ehrState = stub.getStringState(ehrId);

        if (!ehrState.isEmpty()) {
            ehrChainTx = updateEHR(ctx, ehrId, timeStamp, ipfsEHRAddress);
        } else {
            ehrChainTx = new EHRChainTx(patientId, timeStamp, ipfsEHRAddress, patientSig, ehrId);
            ehrState = genson.serialize(ehrChainTx);
            stub.putStringState(ehrId, ehrState);
        }

        return ehrChainTx;
    }

    /**
     * Retrieves all EHRs from the ledger.
     *
     * @param ctx the transaction context
     * @return array of EHRs found on the ledger
     */
    @Transaction()
    public String queryAllEHRs(final Context ctx) {
        ChaincodeStub stub = ctx.getStub();
        final String startKey = "p0ehr0";
        final String endKey = "p99ehr99";
        List<EHRQueryResult> queryResults = new ArrayList<EHRQueryResult>();

        QueryResultsIterator<KeyValue> results = stub.getStateByRange(startKey, endKey);

        for (KeyValue result : results) {
            EHRChainTx ehrChainTx = genson.deserialize(result.getStringValue(), EHRChainTx.class);
            String ipfsEHRHashAdd = ehrChainTx.getIpfsEhrAddress();
            String ehr = getDataUsingIPFSAdd(ipfsEHRHashAdd);
            queryResults.add(new EHRQueryResult(result.getKey(),
                    new EHRChainTx(ehrChainTx.getPatientId(), ehrChainTx.getTimeStamp(), ehrChainTx.getIpfsEhrAddress(),
                            ehrChainTx.getPatientSignature(), ehrChainTx.getEhrChainTxId())));
        }

        final String response = genson.serialize(queryResults);

        return response;
    }

    /**
     * Update the EHR on the ledger.
     *
     * @param ctx            the transaction context
     * @param ehrId          the key
     * @param timestamp      the current datetime in milsec
     * @param ipfsEHRAddress hash address of EHR
     * @return the updated EHR
     */
    @Transaction()
    public EHRChainTx updateEHR(final Context ctx, final String ehrId, final String timestamp,
            final String ipfsEHRAddress) {
        ChaincodeStub stub = ctx.getStub();

        String ehrState = stub.getStringState(ehrId);
        //---------------------------------------------------------------------------------------
        try { 
            KeyPair pairA = ECCryptography.getECKeys();
            KeyPair pairB = ECCryptography.getECKeys();
            ECPublicKey partyAPubKey = (ECPublicKey) pairA.getPublic();
            ECPrivateKey partyAPrivKey = (ECPrivateKey) pairA.getPrivate();
            ECPublicKey partyBPubKey = (ECPublicKey) pairB.getPublic();
            ECPrivateKey partyBPrivKey = (ECPrivateKey) pairB.getPrivate();
            final String ehrJsonString = "Omar Ahmed";
            String enc1 = ECCryptography.encryptECC(ehrJsonString, partyAPrivKey, partyBPubKey);
            System.out.println("Enc1: " + enc1);
            //---------------------------------------------------------------------------------------
            System.out.println("Dec: " + ECCryptography.decryptECC(enc1, partyBPrivKey, partyAPubKey));
            //---------------------------------------------------------------------------------------
        } catch (Exception e) { 
            System.out.println(e);
        }

        if (ehrState.isEmpty()) {
            String errorMessage = String.format("EHR %s does not exist", ehrId);
            System.out.println(errorMessage);
        }

        EHRChainTx ehrChainTx = genson.deserialize(ehrState, EHRChainTx.class);

        EHRChainTx newEHRChainTx = new EHRChainTx(ehrChainTx.getPatientId(), timestamp, ipfsEHRAddress,
                ehrChainTx.getPatientSignature(), ehrId);
        String newEHRState = genson.serialize(newEHRChainTx);
        stub.putStringState(ehrId, newEHRState);

        return newEHRChainTx;
    }

    public static String getDataUsingIPFSAdd(final String ipfsEHRAdd) {

        String ehr = ipfsEHRAdd;
        try {
            IPFS ipfs = new IPFS("/ip4/127.0.0.1/tcp/5001");
            // Get data from IPFS
            Multihash multihash = Multihash.fromBase58(ipfsEHRAdd);
            byte[] rawDataBytes = null;
            rawDataBytes = ipfs.cat(multihash);
            ehr = new String(rawDataBytes);
        } catch (Exception e) {
            System.out.println("IPFS Error:" + e);
        }

        return ehr;
    }
    
}
