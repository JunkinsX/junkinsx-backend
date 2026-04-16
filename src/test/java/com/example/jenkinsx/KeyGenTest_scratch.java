import com.jcraft.jsch.JSch;
import com.jcraft.jsch.KeyPair;
import java.io.ByteArrayOutputStream;

public class KeyGenTest {
    public static void main(String[] args) throws Exception {
        JSch jsch = new JSch();
        KeyPair kpair = KeyPair.genKeyPair(jsch, KeyPair.RSA, 2048);
        
        ByteArrayOutputStream privOut = new ByteArrayOutputStream();
        kpair.writePrivateKey(privOut);
        String privKey = privOut.toString();
        
        ByteArrayOutputStream pubOut = new ByteArrayOutputStream();
        kpair.writePublicKey(pubOut, "JunkinsX");
        String pubKey = pubOut.toString();
        
        System.out.println("Private Key:\n" + privKey);
        System.out.println("Public Key:\n" + pubKey);
        
        kpair.dispose();
    }
}
