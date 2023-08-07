/*
 * SPDX-License-Identifier: Apache-2.0
 */

package org.hyperledger.fabric.samples.log;

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

import io.ipfs.api.IPFS;
import io.ipfs.multihash.Multihash;

/**
 * Java implementation of the Fabric Car Contract described in the Writing Your
 * First Application tutorial
 */
@Contract(
        name = "LogContractImp",
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
public final class LogContractImp implements ContractInterface {

    private final Genson genson = new Genson();

    /**
     * Retrieves a log with the specified key from the ledger.
     *
     * @param ctx   the transaction context
     * @param logId the logId
     * @return the Car found on the ledger if there was one
     */
    @Transaction()
    public LogChainTx queryLog(final Context ctx, final String logId) {
        ChaincodeStub stub = ctx.getStub();
        String logState = stub.getStringState(logId);

        LogChainTx logChainTx = null;
        if (logState.isEmpty()) {
            String errorMessage = String.format("Log %s does not exist", logId);
            System.out.println(errorMessage);
        } else {
            logChainTx = genson.deserialize(logState, LogChainTx.class);
        }
        return logChainTx;
    }

    /**
     * Creates some initial Logs on the ledger.
     *
     * @param ctx the transaction context
     */
    
    @Transaction()
    public void initLedger(final Context ctx) {
        ChaincodeStub stub = ctx.getStub();

        String[] logData = {
                //"{\"requesterId\":\"p0\",\"timeStamp\":\"2023-02-02 03:48:05.591\",\"ipfsLog"
                  //      + "Address\":\"1220432ebc434ce3fca611439719831c84e1bdb34f478355c67fad8636e23c73b3a2\",\"requester"
                    //    + "Signature\":\"sig\",\"logChainTxId\":\"p0log0\"}"
                // "{ \"make\": \"Hyundai\", \"model\": \"Avante\", \"color\": \"black\",
                // \"owner\": \"Ahmed Tawfik\" }"
        };

        for (int i = 0; i < logData.length; i++) {
            String logId = String.format("r0log0");

            LogChainTx logChainTx = genson.deserialize(logData[i], LogChainTx.class);
            String logState = genson.serialize(logChainTx);
            stub.putStringState(logId, logState);
        }
    }
    
    /**
     * Creates a new log on the ledger.
     *
     * @param ctx            the transaction context
     * @param logIdrequesterId the key for the log transaction
     * @param requesterId      requester id
     * @param timestamp      the current datetime in milsec
     * @param ipfsLogAddress the ipfs address of log
     * @param requesterSig     the requester sig
     * @return the created log
     */
    @Transaction()
    public LogChainTx createLog(final Context ctx, final String logId, final String requesterId,
            final String timeStamp,
            final String ipfsEhrAddress, final String accessLogs, final String requesterSig) {
        ChaincodeStub stub = ctx.getStub();

        LogChainTx logChainTx = null;
        String logState = stub.getStringState(logId);

        if (!logState.isEmpty()) {
            String errorMessage = String.format("Log %s already exists", logId);
            System.out.println(errorMessage);
            logChainTx = queryLog(ctx, logId);
        } else {
            logChainTx = new LogChainTx(requesterId, timeStamp, ipfsEhrAddress, accessLogs, requesterSig, logId);
            logState = genson.serialize(logChainTx);
            stub.putStringState(logId, logState);
        }

        return logChainTx;
    }

    /**
     * Retrieves all Logs from the ledger.
     *
     * @param ctx the transaction context
     * @return array of Logs found on the ledger
     * @throws IOException
     */
    @Transaction()
    public String queryAllLogs(final Context ctx) throws IOException {
        ChaincodeStub stub = ctx.getStub();

        final String startKey = "log0";
        final String endKey = "log99";
        List<LogQueryResult> queryResults = new ArrayList<LogQueryResult>();

        QueryResultsIterator<KeyValue> results = stub.getQueryResult("{\"selector\":{\"_id\":{\"$gt\":null}}}");

        for (KeyValue result : results) {
            LogChainTx logChainTx = genson.deserialize(result.getStringValue(), LogChainTx.class);
            queryResults.add(new LogQueryResult(result.getKey(),
                    new LogChainTx(logChainTx.getRequesterId(), logChainTx.getTimeStamp(), logChainTx.getIpfsEhrAddress(), 
                    logChainTx.getAccessLogs(), logChainTx.getRequesterSignature(), 
                    logChainTx.getLogChainTxId())));
        }

        final String response = genson.serialize(queryResults);

        return response;
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
