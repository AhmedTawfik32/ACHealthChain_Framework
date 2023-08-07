/*
 * SPDX-License-Identifier: Apache-2.0
 */

package org.hyperledger.fabric.samples.diagnosis;

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
        name = "DiagnosisContractImp",
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
public final class DiagnosisContractImp implements ContractInterface {

    private final Genson genson = new Genson();

    /**
     * Retrieves a diagnosis with the specified key from the ledger.
     *
     * @param ctx   the transaction context
     * @param diagnosisId the diagnosisId
     * @return the Car found on the ledger if there was one
     */
    @Transaction()
    public DiagnosisChainTx queryDiagnosis(final Context ctx, final String diagnosisId) {
        ChaincodeStub stub = ctx.getStub();
        String diagnosisState = stub.getStringState(diagnosisId);
        //Response response = stub.invokeChaincodeWithStringArgs("", null,"");

        DiagnosisChainTx diagnosisChainTx = null;
        if (diagnosisState.isEmpty()) {
            String errorMessage = String.format("Diagnosis %s does not exist", diagnosisId);
            System.out.println(errorMessage);
        } else {
            diagnosisChainTx = genson.deserialize(diagnosisState, DiagnosisChainTx.class);
        }
        return diagnosisChainTx;
    }

    /**
     * Retrieves a diagnosis with the specified key from the ledger.
     *
     * @param ctx   the transaction context
     * @param patientId the patient Id
     * @return the Car found on the ledger if there was one
     */
    @Transaction()
    public String queryDiagnosisByPatientId(final Context ctx, final String patientId) {
        ChaincodeStub stub = ctx.getStub();
        QueryResultsIterator<KeyValue> diagnosisResults = stub.getQueryResult("{\"selector\":{\"patientId\":{\"$eq\":\"" + patientId + "\"}}}");
        List<DiagnosisQueryResult> queryResults = new ArrayList<DiagnosisQueryResult>();

        for (KeyValue result : diagnosisResults) {
            DiagnosisChainTx diagnosisChainTx = genson.deserialize(result.getStringValue(), DiagnosisChainTx.class);
            String ipfsDiagnosisHashAdd = diagnosisChainTx.getIpfsDiagnosisAddress();
            queryResults.add(new DiagnosisQueryResult(result.getKey(),
                    new DiagnosisChainTx(diagnosisChainTx.getHealthcareProviderId(), diagnosisChainTx.getTimeStamp(),
                    diagnosisChainTx.getPatientId(), ipfsDiagnosisHashAdd, diagnosisChainTx.getHealthcareProviderSignature(), 
                    diagnosisChainTx.getDiagnosisChainTxId())));
        }
        final String response = genson.serialize(queryResults);
        return response;
    }


    /**
     * Retrieves a diagnosis with the specified key from the ledger.
     *
     * @param ctx   the transaction context
     * @param hpId the healthcare provider Id
     * @return the Car found on the ledger if there was one
     */
    @Transaction()
    public String queryDiagnosisByHpId(final Context ctx, final String hpId) {
        ChaincodeStub stub = ctx.getStub();
        QueryResultsIterator<KeyValue> diagnosisResults = stub.getQueryResult("{\"selector\":{\"healthcareProviderId\":{\"$eq\":\"" + hpId + "\"}}}");
        List<DiagnosisQueryResult> queryResults = new ArrayList<DiagnosisQueryResult>();

        for (KeyValue result : diagnosisResults) {
            DiagnosisChainTx diagnosisChainTx = genson.deserialize(result.getStringValue(), DiagnosisChainTx.class);
            String ipfsDiagnosisHashAdd = diagnosisChainTx.getIpfsDiagnosisAddress();
            queryResults.add(new DiagnosisQueryResult(result.getKey(),
                    new DiagnosisChainTx(diagnosisChainTx.getHealthcareProviderId(), diagnosisChainTx.getTimeStamp(),
                    diagnosisChainTx.getPatientId(), ipfsDiagnosisHashAdd, diagnosisChainTx.getHealthcareProviderSignature(), 
                    diagnosisChainTx.getDiagnosisChainTxId())));
        }
        final String response = genson.serialize(queryResults);
        return response;
    }

    /**
     * Creates some initial Diagnosiss on the ledger.
     *
     * @param ctx the transaction context
     */
    
    @Transaction()
    public void initLedger(final Context ctx) {
        ChaincodeStub stub = ctx.getStub();

        String[] diagnosisData = {
        //        "{\"healthcareProviderId\":\"hp0\",\"timeStamp\":\"2023-02-04 03:48:05.591\", \"patientId\":\"p0ehr0\" ,\"ipfsDiagnosis"
          //              + "Address\":\"1220432ebc434ce3fca611439719831c84e1bdb34f478355c67fad8636e23c73b3a2\",\"healthcareProvider"
            //            + "Signature\":\"sig\",\"diagnosisChainTxId\":\"hp0diagnosis0\"}"
                // "{ \"make\": \"Hyundai\", \"model\": \"Avante\", \"color\": \"black\",
                // \"owner\": \"Ahmed Tawfik\" }"
        };

        for (int i = 0; i < diagnosisData.length; i++) {
            String diagnosisId = String.format("hp0diagnosis0");

            DiagnosisChainTx diagnosisChainTx = genson.deserialize(diagnosisData[i], DiagnosisChainTx.class);
            String diagnosisState = genson.serialize(diagnosisChainTx);
            stub.putStringState(diagnosisId, diagnosisState);
        }
    }
    
    /**
     * Creates a new diagnosis on the ledger.
     *
     * @param ctx            the transaction context
     * @param diagnosisId the key for the diagnosis transaction
     * @param hpId      healthcare provider id
     * @param timestamp      the current datetime in milsec
     * @param ipfsDiagnosisAddress the ipfs address of diagnosis
     * @param patientSig     the patient sig
     * @return the created diagnosis
     */
    @Transaction()
    public DiagnosisChainTx createDiagnosis(final Context ctx, final String diagnosisId, final String hpId,
            final String timeStamp, final String patientId, 
            final String ipfsDiagnosisAddress, final String hpSig) {
        ChaincodeStub stub = ctx.getStub();
        DiagnosisChainTx diagnosisChainTx = null;
        String diagnosisState = stub.getStringState(diagnosisId);

        if (!diagnosisState.isEmpty()) {
            String errorMessage = String.format("Diagnosis %s already exists", diagnosisId);
            System.out.println(errorMessage);
            diagnosisChainTx = queryDiagnosis(ctx, diagnosisId);
        } else {
            diagnosisChainTx = new DiagnosisChainTx(hpId, timeStamp, patientId, ipfsDiagnosisAddress, hpSig, diagnosisId);
            diagnosisState = genson.serialize(diagnosisChainTx);
            stub.putStringState(diagnosisId, diagnosisState);
        }

        return diagnosisChainTx;
    }

    /**
     * Retrieves all Diagnosiss from the ledger.
     *
     * @param ctx the transaction context
     * @return array of Diagnosises found on the ledger
     * @throws IOException
     */
    @Transaction()
    public String queryAllDiagnosises(final Context ctx) throws IOException {
        ChaincodeStub stub = ctx.getStub();

        final String startKey = "hp0diagnosis0";
        final String endKey = "hp99diagnosis99";
        List<DiagnosisQueryResult> queryResults = new ArrayList<DiagnosisQueryResult>();

        QueryResultsIterator<KeyValue> results = stub.getStateByRange(startKey, endKey);

        for (KeyValue result : results) {
            DiagnosisChainTx diagnosisChainTx = genson.deserialize(result.getStringValue(), DiagnosisChainTx.class);
            String ipfsDiagnosisHashAdd = diagnosisChainTx.getIpfsDiagnosisAddress();
            String diagnosis = IPFSStorage.getDataUsingIPFSAdd(ipfsDiagnosisHashAdd);
            queryResults.add(new DiagnosisQueryResult(result.getKey(),
                    new DiagnosisChainTx(diagnosisChainTx.getHealthcareProviderId(), diagnosisChainTx.getTimeStamp(),
                    diagnosisChainTx.getPatientId(), diagnosis, diagnosisChainTx.getHealthcareProviderSignature(), 
                    diagnosisChainTx.getDiagnosisChainTxId())));
        }
        final String response = genson.serialize(queryResults);
        return response;
    }

    /**
     * Update the Diagnosis on the ledger.
     *
     * @param ctx            the transaction context
     * @param diagnosisId          the key
     * @param timestamp      the current datetime in milsec
     * @param ipfsDiagnosisAddress hash address of Diagnosis
     * @return the updated Diagnosis
     */
    @Transaction()
    public DiagnosisChainTx updateDiagnosis(final Context ctx, final String diagnosisId, final String timestamp,
            final String ipfsDiagnosisAddress) {
        ChaincodeStub stub = ctx.getStub();

        String diagnosisState = stub.getStringState(diagnosisId);

        if (diagnosisState.isEmpty()) {
            String errorMessage = String.format("Diagnosis %s does not exist", diagnosisId);
            System.out.println(errorMessage);
        }

        DiagnosisChainTx diagnosisChainTx = genson.deserialize(diagnosisState, DiagnosisChainTx.class);
        DiagnosisChainTx newDiagnosisChainTx = new DiagnosisChainTx(diagnosisChainTx.getHealthcareProviderId(), timestamp, 
        diagnosisChainTx.getPatientId(), ipfsDiagnosisAddress, diagnosisChainTx.getHealthcareProviderSignature(), 
        diagnosisId);
        String newDiagnosisState = genson.serialize(newDiagnosisChainTx);
        stub.putStringState(diagnosisId, newDiagnosisState);

        return newDiagnosisChainTx;
    }

}
