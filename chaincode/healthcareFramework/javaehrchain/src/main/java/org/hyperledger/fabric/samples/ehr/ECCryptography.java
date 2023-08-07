package org.hyperledger.fabric.samples.ehr;

import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.security.Security;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECPublicKey;
import java.security.spec.ECGenParameterSpec;
import java.util.Arrays;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.KeyAgreement;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

/**
 *
 * @author AhmedTawfik
 */
public class ECCryptography {

    // Code Available at: https://zhishenyong.com/ecc-asymmetric-encryption

    public static KeyPair getECKeys() throws Exception {
        Security.addProvider(new BouncyCastleProvider());
        ECGenParameterSpec spec = new ECGenParameterSpec("secp256r1");
        KeyPairGenerator gen = KeyPairGenerator.getInstance("ECDH", "BC");
        gen.initialize(spec, new SecureRandom());
        KeyPair pair = gen.generateKeyPair();
        return pair;
    }

    public static String encryptECC(final String text, final ECPrivateKey partyAPrivKey,
            final ECPublicKey partyBPubKey) {
        String cipherString = "";
        try {
            // ---------------------------------------------
            // 1. Generate the pre-master shared secret
            KeyAgreement ka = KeyAgreement.getInstance("ECDH", "BC");
            ka.init(partyAPrivKey);
            ka.doPhase(partyBPubKey, true);
            byte[] sharedSecret = ka.generateSecret();

            // 2. (Optional) Hash the shared secret.
            // Alternatively, you don't need to hash it.
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            messageDigest.update(sharedSecret);
            byte[] digest = messageDigest.digest();

            // 3. (Optional) Split up hashed shared secret into an initialization vector and
            // a session key
            // Alternatively, you can just use the shared secret as the session key and not
            // use an iv.
            int digestLength = digest.length;
            byte[] iv = Arrays.copyOfRange(digest, 0, (digestLength + 1) / 2);
            byte[] sessionKey = Arrays.copyOfRange(digest, (digestLength + 1) / 2, digestLength);

            // 4. Create a secret key from the session key and initialize a cipher with the
            // secret key
            SecretKey secretKey = new SecretKeySpec(sessionKey, 0, sessionKey.length, "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding", "BC");
            IvParameterSpec ivSpec = new IvParameterSpec(iv);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivSpec);

            // 5. Encrypt whatever message you want to send
            byte[] encryptMeBytes = text.getBytes(StandardCharsets.UTF_8);
            byte[] cipherText = cipher.doFinal(encryptMeBytes);
            cipherString = Base64.getEncoder().encodeToString(cipherText);

        } catch (Exception ex) {
            System.out.println("Not supported encryption");
        }
        return cipherString;
    }

    public static String decryptECC(final String cipherText, final ECPrivateKey partyBPrivKey,
            final ECPublicKey partyAPubKey) {
        String originalData = "";
        try {
            // 1. Generate the pre-master shared secret
            KeyAgreement ka = KeyAgreement.getInstance("ECDH", "BC");
            ka.init(partyBPrivKey);
            ka.doPhase(partyAPubKey, true);
            byte[] sharedSecret = ka.generateSecret();

            // 2. (Optional) Hash the shared secret.
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            messageDigest.update(sharedSecret);
            byte[] digest = messageDigest.digest();

            // 3. (Optional) Split up hashed shared secret into an initialization vector and
            // a session key
            int digestLength = digest.length;
            byte[] iv = Arrays.copyOfRange(digest, 0, (digestLength + 1) / 2);
            byte[] sessionKey = Arrays.copyOfRange(digest, (digestLength + 1) / 2, digestLength);

            // 4. Create a secret key from the session key and initialize a cipher with the
            // secret key
            SecretKey secretKey = new SecretKeySpec(sessionKey, 0, sessionKey.length, "AES");
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding", "BC");
            IvParameterSpec ivSpec = new IvParameterSpec(iv);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, ivSpec);
            String decryptMe = cipherText; // Message received from Party A
            byte[] decryptMeBytes = Base64.getDecoder().decode(decryptMe);
            byte[] textBytes = cipher.doFinal(decryptMeBytes);
            originalData = new String(textBytes);

        } catch (Exception e) {
            System.out.println("Not supported decryption");
        }
        return originalData;
    }
}
