package org.hyperledger.fabric.samples.log;

import org.hyperledger.fabric.contract.annotation.DataType;

@DataType
public enum EHRType {
    ECG, EEG, PCG, Chronic, hypatits
}
