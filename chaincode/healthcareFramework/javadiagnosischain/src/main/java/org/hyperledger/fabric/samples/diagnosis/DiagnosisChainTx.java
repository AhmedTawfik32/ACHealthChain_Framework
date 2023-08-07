package org.hyperledger.fabric.samples.diagnosis;

import org.hyperledger.fabric.contract.annotation.DataType;
import org.hyperledger.fabric.contract.annotation.Property;

import com.owlike.genson.annotation.JsonProperty;

@DataType
public final class DiagnosisChainTx {

    @Property
    private final String healthcareProviderId;

    @Property
    private final String timeStamp;

    @Property
    private final String patientId;

    @Property
    private final String ipfsDiagnosisAddress;

    @Property
    private final String healthcareProviderSignature;

    @Property
    private final String diagnosisChainTxId;

    public DiagnosisChainTx(@JsonProperty("healthcareProviderId") final String healthcareProviderId,
            @JsonProperty("timeStamp") final String timeStamp,
            @JsonProperty("patientId") final String patientId,
            @JsonProperty("ipfsDiagnosisAddress") final String ipfsDiagnosisAddress,
            @JsonProperty("healthcareProviderSignature") final String healthcareProviderSignature,
            @JsonProperty("diagnosisChainTxId") final String diagnosisChainTxId) {
        this.healthcareProviderId = healthcareProviderId;
        this.timeStamp = timeStamp;
        this.ipfsDiagnosisAddress = ipfsDiagnosisAddress;
        this.healthcareProviderSignature = healthcareProviderSignature;
        this.patientId = patientId;
        this.diagnosisChainTxId = diagnosisChainTxId;
    }

    public String getHealthcareProviderId() {
        return healthcareProviderId;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public String getIpfsDiagnosisAddress() {
        return ipfsDiagnosisAddress;
    }

    public String getHealthcareProviderSignature() {
        return healthcareProviderSignature;
    }

    public String getPatientId() {
        return patientId;
    }

    public String getDiagnosisChainTxId() {
        return diagnosisChainTxId;
    }

    @Override
    public String toString() {
        return "Patient ID: "
                + healthcareProviderId + " Timestamp: "
                + timeStamp + " IPFS address of EHR: "
                + ipfsDiagnosisAddress;
    }

}
