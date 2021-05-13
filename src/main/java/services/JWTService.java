package services;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import java.util.Calendar;

/** 
 * This class serves as a static conveniance wrapper for the auth0 jwt library. It deals with the full 
 * lifecycle of JSON Web Tokens (JWTs), including their creation, decoding, and validation.
 * 
 * It does not deal with refresh tokens. Attempts to create tokens will always succed under the 
 * assumption that exteranl requests to create/refresh tokens have already been authorized.
 * 
 * By default, this service uses the HMAC256 algorithm to sign tokens, and will only be able to decode
 * or validate tokens signed using the same algorithm and secret.
 */
public class JWTService {
    /** Name of the issuer this service deals with per RFC 7519. */
    private static final String ISSUER = "auth0";

    /** How many minutes tokens are valid for before they expire. */
    private static final int MINUTES_TO_EXPIRATION = 5;

    /** Name of the environment variable used to inject the secret used in HMAC algorithms. */
    private static final String SECRET_ENV_VAR = "SECRET";

    /** Secret used for signing tokens with HMAC algorithms. */
    private static String SECRET = System.getenv(SECRET_ENV_VAR) == null || System.getenv(SECRET_ENV_VAR).isEmpty()
        ? "alakazam"
        : System.getenv(SECRET_ENV_VAR);

    /** The algorithm used to sign tokens. */
    private static Algorithm ALGORITHM = Algorithm.HMAC256(SECRET);

    /** Instance of {@link JWTVerifier} used to decode and validate tokens. */
    private static JWTVerifier VERIFIER = JWT.require(ALGORITHM)
        .withIssuer(ISSUER)
        .build();

    /**
     * Creates a fresh JWT. By default the token expires in five minutes from issuance.
     * @return A signed JWT with the user's claims.
     * @throws JWTCreationException Thrown if a Claim couldn't be converted to JSON or the secret 
     * used in the signing process was invalid
     */
    public static String create() throws JWTCreationException {
        Calendar expiration = Calendar.getInstance();
        expiration.add(Calendar.MINUTE, MINUTES_TO_EXPIRATION);

        return JWT.create()
            .withIssuer(ISSUER)
            .withExpiresAt(expiration.getTime())
            .sign(ALGORITHM);
    }

    /**
     * Decodes and returns a JWT token. This will only work for tokens signed with the same
     * algorithm and secret used in this class. By default, this class uses the HMAC256 algorithm
     * and a secret value injected by a system variable.
     * @param jwt The JWT to decode.
     * @return The decoded JWT.
     * @throws JWTVerificationException Thrown if the token has an invalid signature.
     */
    public static DecodedJWT decode(String jwt) throws JWTVerificationException {
        return VERIFIER.verify(jwt);
    }

    /**
     * Determines whether the provided JWT has been altered since signing. This will only work for
     * JWTs signed with the same algorithm and secret used in this class. By default, this class uses 
     * the HMAC256 algorithm and a secret value injected by a system variable.
     * @param jwt The JWT to validate.
     * @return Returns true if the JWT is valid and has not been modified since signing; returns false
     * otherwise.
     */
    public static boolean validate(String jwt) {
        try{
            VERIFIER.verify(jwt);
            return true;
        } catch(JWTVerificationException exception){
            return false;
        }
    }
}
