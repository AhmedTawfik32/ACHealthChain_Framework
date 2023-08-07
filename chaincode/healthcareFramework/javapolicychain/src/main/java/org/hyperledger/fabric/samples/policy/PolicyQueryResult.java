/*
 * SPDX-License-Identifier: Apache-2.0
 */

package org.hyperledger.fabric.samples.policy;

import java.util.Objects;

import org.hyperledger.fabric.contract.annotation.DataType;
import org.hyperledger.fabric.contract.annotation.Property;

import com.owlike.genson.annotation.JsonProperty;

/**
 * CarQueryResult structure used for handling result of query
 *
 */
@DataType()
public final class PolicyQueryResult {
    @Property()
    private final String policyId;

    @Property()
    private final PolicyChainTx policyChainTx;

    public PolicyQueryResult(@JsonProperty("policyId") final String policyId,
            @JsonProperty("policyChainTx") final PolicyChainTx policyChainTx) {
        this.policyId = policyId;
        this.policyChainTx = policyChainTx;
    }

    public String getPolicyId() {
        return policyId;
    }

    public PolicyChainTx getPolicyChainTx() {
        return policyChainTx;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }

        if ((obj == null) || (getClass() != obj.getClass())) {
            return false;
        }

        PolicyQueryResult other = (PolicyQueryResult) obj;

        Boolean policyChainTxsAreEquals = this.getPolicyChainTx().equals(other.getPolicyChainTx());
        Boolean policyIdsAreEquals = this.getPolicyId().equals(other.getPolicyId());

        return policyChainTxsAreEquals && policyIdsAreEquals;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getPolicyId(), this.getPolicyChainTx());
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName()
                + "@" + Integer.toHexString(hashCode())
                + " [policyId=" + policyId + ", policyChainTx="
                + policyChainTx + "]";
    }
}
