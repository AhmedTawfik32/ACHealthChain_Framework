package org.hyperledger.fabric.samples.ehr;

import org.hyperledger.fabric.contract.annotation.DataType;

@DataType
public enum EHRType {
    ECG, EEG, PCG, Chronic, hypatits
}
