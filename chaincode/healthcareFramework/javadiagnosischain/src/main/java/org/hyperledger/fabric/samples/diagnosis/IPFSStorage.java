package org.hyperledger.fabric.samples.diagnosis;

import java.io.IOException;

import io.ipfs.api.IPFS;
import io.ipfs.api.MerkleNode;
import io.ipfs.api.NamedStreamable;
import io.ipfs.multihash.Multihash;

public final class IPFSStorage {

    public IPFSStorage() {

    }

    public static String getDataUsingIPFSAdd(final String ipfsEHRAdd) {

        String ehr = "";
        try {
            System.out.println("Connecting to IPFS from initLedger!!!!");
            IPFS ipfs = new IPFS("/ip4/127.0.0.1/tcp/5001");
            ipfs.refs.local();
            System.out.println("Conncted to IPFS: " + ipfs.version());
            // Get data from IPFS
            Multihash multihash = Multihash.fromHex(ipfsEHRAdd);
            byte[] rawDataBytes = null;
            rawDataBytes = ipfs.cat(multihash);
            ehr = new String(rawDataBytes);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return ehr;
    }

    public static String storeDataOnIPFS(final String ehr) {
        String hashResponse = "";
        try {

            // Save data into IPFS
            System.out.println("Connecting to IPFS from initLedger!!!!");
            IPFS ipfs = new IPFS("/ip4/127.0.0.1/tcp/5001");
            ipfs.refs.local();
            System.out.println("Conncted to IPFS: " + ipfs.version());
            byte[] stringInBytes = ehr.getBytes();
            NamedStreamable.ByteArrayWrapper dataBytes = new NamedStreamable.ByteArrayWrapper(stringInBytes);
            MerkleNode dataBytesArrayResponse = ipfs.add(dataBytes);
            hashResponse = dataBytesArrayResponse.hash.toHex().toString();
            System.out.println("Hash response: " + hashResponse);
        } catch (Exception ex) {
            System.out.println("The IPFS error is: " + ex);
        }
        return hashResponse;
    }
}
