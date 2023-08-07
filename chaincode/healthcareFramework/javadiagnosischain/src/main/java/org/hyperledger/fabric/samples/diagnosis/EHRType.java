package org.hyperledger.fabric.samples.diagnosis;

import org.hyperledger.fabric.contract.annotation.DataType;

@DataType
public enum EHRType {
    ECG, EEG, PCG, Chronic, hypatits
}
