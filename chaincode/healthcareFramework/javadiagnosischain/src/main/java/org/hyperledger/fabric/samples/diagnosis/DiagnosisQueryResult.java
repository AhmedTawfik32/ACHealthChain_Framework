/*
 * SPDX-License-Identifier: Apache-2.0
 */

package org.hyperledger.fabric.samples.diagnosis;

import java.util.Objects;

import org.hyperledger.fabric.contract.annotation.DataType;
import org.hyperledger.fabric.contract.annotation.Property;

import com.owlike.genson.annotation.JsonProperty;

/**
 * CarQueryResult structure used for handling result of query
 *
 */
@DataType()
public final class DiagnosisQueryResult {
    @Property()
    private final String diagnosisId;

    @Property()
    private final DiagnosisChainTx diagnosisChainTx;

    public DiagnosisQueryResult(@JsonProperty("diagnosisId") final String diagnosisId,
            @JsonProperty("diagnosisChainTx") final DiagnosisChainTx diagnosisChainTx) {
        this.diagnosisId = diagnosisId;
        this.diagnosisChainTx = diagnosisChainTx;
    }

    public String getDiagnosisId() {
        return diagnosisId;
    }

    public DiagnosisChainTx getDiagnosisChainTx() {
        return diagnosisChainTx;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }

        if ((obj == null) || (getClass() != obj.getClass())) {
            return false;
        }

        DiagnosisQueryResult other = (DiagnosisQueryResult) obj;

        Boolean diagnosisChainTxsAreEquals = this.getDiagnosisChainTx().equals(other.getDiagnosisChainTx());
        Boolean diagnosisIdsAreEquals = this.getDiagnosisId().equals(other.getDiagnosisId());

        return diagnosisChainTxsAreEquals && diagnosisIdsAreEquals;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getDiagnosisId(), this.getDiagnosisChainTx());
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName()
                + "@" + Integer.toHexString(hashCode())
                + " [diagnosisId=" + diagnosisId + ", diagnosisChainTx="
                + diagnosisChainTx + "]";
    }
}
