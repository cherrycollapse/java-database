package step.learning.services.hash;

import javax.inject.Singleton;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Singleton
public class Sha1HashService implements HashService {
    @Override
    public String hash( String data ) {
        try {
            MessageDigest md = MessageDigest.getInstance( "SHA" ) ;
            byte[] hash = md.digest( data.getBytes() ) ;
            StringBuilder sb = new StringBuilder() ;
            for( byte b : hash ) {
                sb.append( String.format("%02x", b & 0xFF ) ) ;
            }
            return sb.toString() ;
        }
        catch( NoSuchAlgorithmException ex ) {
            System.out.println( ex.getMessage() ) ;
            return null ;
        }
    }
}