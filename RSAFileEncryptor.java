
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.PrivateKey;
import java.security.spec.X509EncodedKeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.io.ByteArrayOutputStream;
import javax.crypto.Cipher;
import java.util.Arrays;

//Application 3: Encrypting and Decrypting the file

//General idea: since encryption of the image file cannot be performed using RSA2048 in one go,
//I will handle the process in smaller chunks

//The image is 74193 bytes & RSA handles a maximum of 190 bytes
//74193/190 = 391 chunks & the encrypted file will be 391 x 256 = 100096 bytes

public class RSAFileEncryptor {
    public static void main(String[] args) throws Exception {

        //reading the public and private key files here
        byte[] publicKeyBytes = Files.readAllBytes(Path.of("public.key"));
        byte[] privateKeyBytes = Files.readAllBytes(Path.of("private.key"));

        //creating a KeyFactory to reconstruct RSA keys from stored bytes
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");

        //reconstruct the public key using its X.509 encoding
        X509EncodedKeySpec publicKeySpec =
                new X509EncodedKeySpec(publicKeyBytes);

        PublicKey publicKey = keyFactory.generatePublic(publicKeySpec);

        //reconstruct the private key using its PKCS#8 encoding
        PKCS8EncodedKeySpec privateKeySpec =
                new PKCS8EncodedKeySpec(privateKeyBytes);

        PrivateKey privateKey = keyFactory.generatePrivate(privateKeySpec);

        System.out.println("RSA keys loaded successfully!");

//Encryption:
        // // Sample message to test RSA encryption
        // String message = "Hello, RSA!";

        // // Convert the message into bytes because Cipher operates on byte arrays
        // byte[] messageBytes = message.getBytes(StandardCharsets.UTF_8);

        // // Create an RSA cipher using OAEP padding with SHA-256
        // Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA-256AndMGF1Padding");

        // // Initialize the cipher for encryption using our public key
        // cipher.init(Cipher.ENCRYPT_MODE, publicKey);

        // // Encrypt the message
        // byte[] encryptedBytes = cipher.doFinal(messageBytes);

        // System.out.println("Original message: " + message);
        // System.out.println("Encrypted message size: " + encryptedBytes.length + " bytes");

        //read the original image as raw bytes
        byte[] fileBytes = Files.readAllBytes(Path.of("CodingExercise.JPG"));

        System.out.println("Original file size: " + fileBytes.length + " bytes");

        //RSA-2048 supports up to 190 plaintext bytes
        int blockSize = 190;

        //rnitialize the RSA cipher using the public key
        Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA-256AndMGF1Padding");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);

        //storing all the encrypted chunks together (creates a temp. buffer in memry)
        ByteArrayOutputStream encryptedOutput = new ByteArrayOutputStream();

        //go through the image in chunks of 190 bytes and combine all the chunks into on byte array
        for (int i = 0; i < fileBytes.length; i += blockSize) {

            //makig sure the last chunk does not exceed the file size
            int end = Math.min(i + blockSize, fileBytes.length);

            //extract the current chunk
            byte[] block = Arrays.copyOfRange(fileBytes, i, end);

            //encrypt the chunk using RSA
            byte[] encryptedBlock = cipher.doFinal(block);

            //add the encrypted chunk to the output
            encryptedOutput.write(encryptedBlock);
        }

        //convert the combined encrypted chunks into a byte array
        byte[] encryptedBytes = encryptedOutput.toByteArray();

        Files.write(Path.of("encrypted.bin"), encryptedBytes);

        System.out.println("Encrypted file size: " + encryptedBytes.length + " bytes");
        System.out.println("Encrypted file saved to encrypted.bin");


//Decryption:
        //cipher set to decryption mode using the PRIVATE KEY
        cipher.init(Cipher.DECRYPT_MODE, privateKey);

        //readig the encrypted file
        byte[] encryptedFileBytes = Files.readAllBytes(Path.of("encrypted.bin"));

        //each RSA-2048 encrypted block is 256 bytes
        int encryptedBlockSize = 256;

        //storing all the decrypted chunks together
        ByteArrayOutputStream decryptedOutput = new ByteArrayOutputStream();

        //going through the encrypted file in chunks of 256 bytes
        for (int i = 0; i < encryptedFileBytes.length; i += encryptedBlockSize) {

            //extract the current encrypted chunk
            byte[] block = Arrays.copyOfRange(
                    encryptedFileBytes, i, i + encryptedBlockSize
            );

            //decrypt the chunk using the private key
            byte[] decryptedBlock = cipher.doFinal(block);

            // Add the decrypted chunk to the output
            decryptedOutput.write(decryptedBlock);
        }

        //combine all decrypted chunks into a byte array
        byte[] decryptedBytes = decryptedOutput.toByteArray();

        Files.write(Path.of("decrypted.JPG"), decryptedBytes);

        //final check to make sure the decrypted file matches the encrypted one
        boolean matches = Arrays.equals(fileBytes, decryptedBytes);

        System.out.println("Decrypted file size: " + decryptedBytes.length + " bytes");
        System.out.println("Decrypted file saved to decrypted.JPG");
        System.out.println("Decryption successful: " + matches);

    }
}
