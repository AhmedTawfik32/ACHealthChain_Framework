package org.hyperledger.fabric.samples.ehr;

import org.hyperledger.fabric.contract.annotation.DataType;
import org.hyperledger.fabric.contract.annotation.Property;
import com.owlike.genson.annotation.JsonProperty;

@DataType
public final class EHRChainTx {

    @Property
    private final String patientId;

    @Property
    private final String timeStamp;

    @Property
    private final String ipfsEhrAddress;

    @Property
    private final String patientSignature;

    @Property
    private final String ehrChainTxId;

    public EHRChainTx(@JsonProperty("patientId") final String patientId,
            @JsonProperty("timeStamp") final String timeStamp,
            @JsonProperty("ipfsEhrAddress") final String ipfsEhrAddress,
            @JsonProperty("patientSignature") final String patientSignature,
            @JsonProperty("ehrChainTxId") final String ehrChainTxId) {
        this.patientId = patientId;
        this.timeStamp = timeStamp;
        this.ipfsEhrAddress = ipfsEhrAddress;
        this.patientSignature = patientSignature;
        this.ehrChainTxId = ehrChainTxId;
    }

    public String getPatientId() {
        return patientId;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public String getIpfsEhrAddress() {
        return ipfsEhrAddress;
    }

    public String getPatientSignature() {
        return patientSignature;
    }

    public String getEhrChainTxId() {
        return ehrChainTxId;
    }

    @Override
    public String toString() {
        return "Patient ID: "
                + patientId + " Timestamp: "
                + timeStamp + " IPFS address of EHR: "
                + ipfsEhrAddress;
    }

}
