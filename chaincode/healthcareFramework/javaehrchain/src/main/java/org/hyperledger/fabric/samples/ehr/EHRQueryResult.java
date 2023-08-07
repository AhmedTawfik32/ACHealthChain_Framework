/*
 * SPDX-License-Identifier: Apache-2.0
 */

package org.hyperledger.fabric.samples.ehr;

import java.util.Objects;

import org.hyperledger.fabric.contract.annotation.DataType;
import org.hyperledger.fabric.contract.annotation.Property;

import com.owlike.genson.annotation.JsonProperty;

/**
 * CarQueryResult structure used for handling result of query
 *
 */
@DataType()
public final class EHRQueryResult {
    @Property()
    private final String ehrId;

    @Property()
    private final EHRChainTx ehrChainTx;

    public EHRQueryResult(@JsonProperty("ehrId") final String ehrId,
            @JsonProperty("ehrChainTx") final EHRChainTx ehrChainTx) {
        this.ehrId = ehrId;
        this.ehrChainTx = ehrChainTx;
    }

    public String getEhrId() {
        return ehrId;
    }

    public EHRChainTx getEhrChainTx() {
        return ehrChainTx;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }

        if ((obj == null) || (getClass() != obj.getClass())) {
            return false;
        }

        EHRQueryResult other = (EHRQueryResult) obj;

        Boolean ehrChainTxsAreEquals = this.getEhrChainTx().equals(other.getEhrChainTx());
        Boolean ehrIdsAreEquals = this.getEhrId().equals(other.getEhrId());

        return ehrChainTxsAreEquals && ehrIdsAreEquals;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getEhrId(), this.getEhrChainTx());
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName()
                + "@" + Integer.toHexString(hashCode())
                + " [ehrId=" + ehrId + ", ehrChainTx="
                + ehrChainTx + "]";
    }
}
