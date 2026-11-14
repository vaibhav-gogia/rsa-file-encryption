import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PublicKey;
import java.security.PrivateKey;

import java.nio.file.Files;
import java.nio.file.Path;

// Application 1: RSA Key Generator

public class RSAKeyGenerator {
    public static void main(String[] args) throws Exception {

        //create a key pair generator using RSA algo
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");

        keyGen.initialize(2048);

        //generate the actual public and private key pair
        KeyPair keyPair = keyGen.generateKeyPair();

        //extract the key pair
        PublicKey publicKey = keyPair.getPublic();
        PrivateKey privateKey = keyPair.getPrivate();

        //convert the keys into their standard encoded byte formats
        byte[] publicKeyBytes = publicKey.getEncoded(); //(necessary to convert mod/exp into standard binary)
        byte[] privateKeyBytes = privateKey.getEncoded();

        //save the keys to separate files
        Files.write(Path.of("public.key"), publicKeyBytes);
        Files.write(Path.of("private.key"), privateKeyBytes);

        //System.out.println(publicKey);
        System.out.println("RSA key pair generated successfully!");
        System.out.println("They keys have been saved to public.key and private.key");

    }
}