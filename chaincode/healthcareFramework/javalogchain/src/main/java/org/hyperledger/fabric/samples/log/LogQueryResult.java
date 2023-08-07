/*
 * SPDX-License-Identifier: Apache-2.0
 */

package org.hyperledger.fabric.samples.log;

import java.util.Objects;

import org.hyperledger.fabric.contract.annotation.DataType;
import org.hyperledger.fabric.contract.annotation.Property;

import com.owlike.genson.annotation.JsonProperty;

/**
 * CarQueryResult structure used for handling result of query
 *
 */
@DataType()
public final class LogQueryResult {
    @Property()
    private final String logId;

    @Property()
    private final LogChainTx logChainTx;

    public LogQueryResult(@JsonProperty("logId") final String logId,
            @JsonProperty("logChainTx") final LogChainTx logChainTx) {
        this.logId = logId;
        this.logChainTx = logChainTx;
    }

    public String getLogId() {
        return logId;
    }

    public LogChainTx getLogChainTx() {
        return logChainTx;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }

        if ((obj == null) || (getClass() != obj.getClass())) {
            return false;
        }

        LogQueryResult other = (LogQueryResult) obj;

        Boolean logChainTxsAreEquals = this.getLogChainTx().equals(other.getLogChainTx());
        Boolean logIdsAreEquals = this.getLogId().equals(other.getLogId());

        return logChainTxsAreEquals && logIdsAreEquals;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.getLogId(), this.getLogChainTx());
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName()
                + "@" + Integer.toHexString(hashCode())
                + " [logId=" + logId + ", logChainTx="
                + logChainTx + "]";
    }
}
