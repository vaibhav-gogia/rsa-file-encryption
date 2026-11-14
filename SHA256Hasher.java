import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;

//Application 2: Generate SHA256 Hash of the provided image file
public class SHA256Hasher {

    public static void main(String[] args) throws Exception {
        
        //reading the provided image into a byte array
        byte[] fileBytes = Files.readAllBytes(Path.of("CodingExercise.JPG"));

        MessageDigest digest = MessageDigest.getInstance("SHA-256");

        //calculating the hash of the file
        byte[] hashBytes = digest.digest(fileBytes);
        //converting each byte of the hash into hexad formar
        StringBuilder hexHash = new StringBuilder();

        for (byte b : hashBytes) {
            hexHash.append(String.format("%02x", b & 0xff));
        }
        System.out.println(hexHash.toString());
    }
}
