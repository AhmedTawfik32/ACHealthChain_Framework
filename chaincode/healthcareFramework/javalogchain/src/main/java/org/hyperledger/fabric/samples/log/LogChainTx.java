package org.hyperledger.fabric.samples.log;
import org.hyperledger.fabric.contract.annotation.DataType;
import org.hyperledger.fabric.contract.annotation.Property;

import com.owlike.genson.annotation.JsonProperty;

@DataType
public final class LogChainTx {

    @Property
    private final String requesterId;

    @Property
    private final String timeStamp;

    @Property
    private final String ipfsEhrAddress;

    @Property
    private final String accessLogs;

    @Property
    private final String requesterSignature;

    @Property
    private final String logChainTxId;

    public LogChainTx(@JsonProperty("requesterId") final String requesterId,
            @JsonProperty("timeStamp") final String timeStamp,
            @JsonProperty("ipfsEhrAddress") final String ipfsEhrAddress,
            @JsonProperty("accessLogs") final String accessLogs,
            @JsonProperty("requesterSignature") final String requesterSignature,
            @JsonProperty("logChainTxId") final String logChainTxId) {
        this.requesterId = requesterId;
        this.timeStamp = timeStamp;
        this.ipfsEhrAddress = ipfsEhrAddress;
        this.accessLogs = accessLogs;
        this.requesterSignature = requesterSignature;
        this.logChainTxId = logChainTxId;
    }

    public String getRequesterId() {
        return requesterId;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public String getIpfsEhrAddress() {
        return ipfsEhrAddress;
    }

    public String getAccessLogs() {
        return accessLogs;
    }

    public String getRequesterSignature() {
        return requesterSignature;
    }

    public String getLogChainTxId() {
        return logChainTxId;
    }

    @Override
    public String toString() {
        return "Requester ID: "
                + requesterId + " Timestamp: "
                + timeStamp + " IPFS address of EHR: "
                + ipfsEhrAddress;
    }

}
