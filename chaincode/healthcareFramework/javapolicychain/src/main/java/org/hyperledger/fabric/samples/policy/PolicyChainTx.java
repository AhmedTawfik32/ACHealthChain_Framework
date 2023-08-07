package org.hyperledger.fabric.samples.policy;

import org.hyperledger.fabric.contract.annotation.DataType;
import org.hyperledger.fabric.contract.annotation.Property;
import com.owlike.genson.annotation.JsonProperty;
@DataType
public final class PolicyChainTx {

    @Property
    private final String granterId;

    @Property
    private final String granteeId;

    @Property
    private final String timeStamp;

    @Property
    private final String ehrType;

    @Property
    private final String ipfsEhrAddress;

    @Property
    private final String envelopedSymKey;

    @Property
    private final String startDate;

    @Property
    private final String endDate;

    @Property
    private final String isValidPolicy;

    @Property
    private final String granterSignature;

    @Property
    private final String policyChainTxId;

    public PolicyChainTx(@JsonProperty("granterId") final String granterId,
            @JsonProperty("granteeId") final String granteeId,
            @JsonProperty("timeStamp") final String timeStamp,
            @JsonProperty("ehrType") final String ehrType,
            @JsonProperty("ipfsEhrAddress") final String ipfsEhrAddress,
            @JsonProperty("envelopedSymKey") final String envelopedSymKey,
            @JsonProperty("startDate") final String startDate,
            @JsonProperty("endDate") final String endDate,
            @JsonProperty("isValidPolicy") final String isValidPolicy,
            @JsonProperty("granterSignature") final String granterSignature,
            @JsonProperty("policyChainTxId") final String policyChainTxId) {
        this.granterId = granterId;
        this.granteeId = granteeId;
        this.timeStamp = timeStamp;
        this.ehrType = ehrType;
        this.ipfsEhrAddress = ipfsEhrAddress;
        this.envelopedSymKey = envelopedSymKey;
        this.startDate = startDate;
        this.endDate = endDate;
        this.isValidPolicy = isValidPolicy;
        this.granterSignature = granterSignature;
        this.policyChainTxId = policyChainTxId;
    }

    public String getGranterId() {
        return granterId;
    }

    public String getGranteeId() {
        return granteeId;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public String getEHRType() {
        return ehrType;
    }

    public String getIpfsEhrAddress() {
        return ipfsEhrAddress;
    }

    public String getEnvelopedSymKey() {
        return envelopedSymKey;
    }

    public String getStartDate() {
        return startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public String getGranterSignature() {
        return granterSignature;
    }

    public String getPolicyValidation() {
        return isValidPolicy;
    }

    public String getPolicyChainTxId() {
        return policyChainTxId;
    }

    @Override
    public String toString() {
        return "Policy: granter ID: "
                + granterId + " Timestamp: "
                + timeStamp + " IPFS address of EHR: "
                + ipfsEhrAddress;
    }

}
