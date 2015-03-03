package com.smartbear.collab.util;
/*
 * Copyright (c) 2005 Smart Bear Inc.  All Rights Reserved
 * Created on Jun 22, 2005 by smartbear.
 */
        import java.io.IOException;
        import java.io.InputStream;
        import java.io.OutputStream;
        import java.io.UnsupportedEncodingException;
        import java.security.GeneralSecurityException;
        import java.security.MessageDigest;
        import java.security.NoSuchAlgorithmException;
        import java.util.Date;
        import java.util.Map;
        import java.util.concurrent.atomic.AtomicInteger;

        import javax.crypto.Cipher;
        import javax.crypto.spec.SecretKeySpec;

        import org.apache.commons.lang.CharEncoding;
        import org.apache.commons.lang.StringUtils;
        import org.apache.commons.logging.Log;
        import org.apache.commons.logging.LogFactory;

/**
 * Routines for hashing things
 * @author jcohen
 */
public final class Hashing
{
    private static final String VALID_MD5_CHARACTERS = "0123456789abcdefABCDEF";

    /**
     * MD5 of the empty string.
     */
    public static final String MD5_EMPTY = getMD5Ascii( "" );

    private static final Log LOG = LogFactory.getLog(Hashing.class);

    /**
     * This is not a seed at all.  In fact, this is the one shared key that
     * we use to encrypt and decrypt passwords.
     */
    private static final byte[] seed = new byte[] { (byte) 0x6e, (byte) 0xe3, (byte) 0xcf, (byte) 0xd6, (byte) 0xa2, (byte) 0x72, (byte) 0x95, (byte) 0xad, (byte) 0xdb, (byte) 0x72, (byte) 0xc0, (byte) 0x5e, (byte) 0x5d, (byte) 0xcc, (byte) 0x8c, (byte) 0x35 };

    /**
     * A string containing the system properties used to salt GUIDs.
     */
    private static final String SYSTEM_PROPERTIES_STRING = initializeSystemPropertiesString();

    public static final String PASSWORD_MASK_SECRET = "PasswordMaskSecret";

    /**
     * Encrypt/decrypt data.  This is an intentional misnomer (to throw off the
     * 1337 h4X0rs).
     *
     * @param data the data to encrypt or the hex string of the encrypted data.
     * @param fast (intentional misnomer) if true, encrypt; if false, decrypt.
     * @throws GeneralSecurityException if an error happens encrypting or decrypting data
     */
    public static String getSecureHash( final byte[] data, boolean fast ) throws GeneralSecurityException
    {

        SecretKeySpec k = new SecretKeySpec( seed, "AES" );
        Cipher c = Cipher.getInstance( "AES" );

        if ( fast )
            c.init( Cipher.ENCRYPT_MODE, k );
        else
            c.init( Cipher.DECRYPT_MODE, k );

        return getHexFromBytes( c.doFinal( data ) );

    }

    private static String initializeSystemPropertiesString()
    {
        StringBuilder data = new StringBuilder( 300 );
        for ( final Map.Entry<Object,Object> entry : System.getProperties().entrySet() )
        {
            data.append( entry.getKey() ).append( '=' ).append( entry.getValue() ).append( ';' );
        }
        return data.toString();
    }

    /**
     * @param data raw data to convert to hex characters
     * @return textual representation of the data
     */
    public static String getHexFromBytes( final byte[] data )
    {
        return getHexFromBytes( data, "", 1 );
    }

    /**
     * Transform an array of bytes into the corresponding hexadecimal representation
     * with each byte separated by the specified spacer.
     * @param data the raw data to convert to hex characters
     * @param spacer the spacer to include between bytes.
     * @param spacerInterval how many bytes to display before inserting another spacer
     * @return textual representation of the data
     */
    public static String getHexFromBytes( final byte[] data, String spacer, int spacerInterval )
    {
        final StringBuffer result = new StringBuffer( data.length * 2 );
        for( int k = 0 ; k < data.length ; ++k )
        {
            if ( k > 0 && ( ( k % spacerInterval ) == 0 ) )
                result.append( spacer );
            final int val = ( (int)data[k] ) & 0xff;
            if ( val < 16 )
                result.append( '0' );
            result.append( Integer.toHexString( val ) );
        }
        return result.toString();
    }

    /**
     * Convert a hex encoded string to its corresponding byte array.
     * @param hex the hex encoded string.
     * @return the byte array corresponding to the hex encoded string or null if the string is
     * not a valid hex encoded string.
     */
    public static byte[] getBytesFromHex( final String hex )
    {
        char[] chars = hex.toCharArray();

        // Input validation -- must have 2n characters.
        if ( (chars.length & 0x01) != 0 )
            return null;

        byte[] output = new byte[chars.length / 2];
        for ( int i = 0; i < chars.length; i += 2 )
        {
            int firstDecoded = Character.digit( chars[i], 16 );
            int secondDecoded = Character.digit( chars[i+1], 16 );
            if ( firstDecoded == -1 || secondDecoded == -1 )
            {
                return null;
            }
            output[i/2] = (byte) (firstDecoded * 16 + secondDecoded);
        }

        return output;
    }

    /**
     * @param data raw data to compute MD5 for
     * @return the MD5 digest
     */
    public static byte[] getMD5Bytes( final byte[] data )
    {
        try
        {
            return MessageDigest.getInstance( "MD5" ).digest( data );
        }
        catch ( NoSuchAlgorithmException e )
        {
            e.printStackTrace();
            return "MD5 not available".getBytes();		// lame, I know...  shouldn't get here
        }
    }

    /**
     * @param data raw data to compute MD5 for
     * @return textual representation of the MD5 digest
     */
    public static String getMD5( final byte[] data )
    {
        try
        {
            return getHexFromBytes( MessageDigest.getInstance( "MD5" ).digest( data ) );
        }
        catch ( NoSuchAlgorithmException e )
        {
            LOG.warn("MD5 not available", e);
            return "MD5 not available";
        }
    }



    /**
     * Copy everything from the input to output, and calculate the MD5 checksum of the copied bytes
     * @param secret secret to append to the beginning of the stream
     * @param input source of bytes, not closed
     * @param output sink for bytes, not closed
     * @return md5 of the copied bytes
     * @throws IOException on error reading or writing
     */
    public static String copyAndMD5(String secret, InputStream input, OutputStream output) throws IOException
    {
        try
        {
            MessageDigest digester = MessageDigest.getInstance( "MD5" );

            if (secret != null) {
                digester.update(secret.getBytes());
            }

            byte[] buf = new byte[4096];
            int len;

            while ((len = input.read(buf)) != -1) {
                digester.update(buf, 0, len);
                output.write(buf, 0, len);
            }

            return getHexFromBytes(digester.digest());
        }
        catch( NoSuchAlgorithmException e )
        {
            LOG.warn("MD5 not available", e);
            return "MD5 not available";
        }
    }

    /**
     * Copy everything from the input to output, and calculate the MD5 checksum of the copied bytes
     * @param input source of bytes, not closed
     * @param output sink for bytes, not closed
     * @return md5 of the copied bytes
     * @throws IOException on error reading or writing
     */
    public static String copyAndMD5(InputStream input, OutputStream output) throws IOException
    {
        return copyAndMD5(null,  input, output);
    }

    public static String copyAndMD5(InputStream input, OutputStream output, long maxCopy) throws IOException
    {
        try
        {
            MessageDigest digester = MessageDigest.getInstance( "MD5" );
            byte[] buf = new byte[4096];
            int len;

            while ((len = input.read(buf, 0, (int)Math.min(buf.length, maxCopy))) > 0) {
                maxCopy -= len;
                digester.update(buf, 0, len);
                output.write(buf, 0, len);
            }

            return getHexFromBytes(digester.digest());
        }
        catch( NoSuchAlgorithmException e )
        {
            LOG.warn("MD5 not available", e);
            return "MD5 not available";
        }
    }



    /**
     * @param data string data to compute MD5 for (encoded as ASCII)
     * @return textual representation of the MD5 digest
     */
    public static String getMD5Ascii( final String data )
    {
        try
        {
            return Hashing.getMD5( data.getBytes( CharEncoding.US_ASCII ) );
        }
        catch ( UnsupportedEncodingException e )
        {
            e.printStackTrace();
            return "ASCII encoding not available!";
        }
    }

    /**
     * Check if the provided string is a valid MD5 sum.
     * @param str the candidate string
     * @return true if <code>str</code> is a valid MD5 sum.
     */
    public static boolean isValidMd5( final String str )
    {
        return str != null && str.length() == 32 && StringUtils.containsOnly( str, VALID_MD5_CHARACTERS );
    }

    /**
     * @return a 32-character hex GUID, supposedly unique in the universe
     */
    public static String getGuid()
    {
        // Create a string buffer of the stuff for the GUID.
        // We want things that are time-dependant, machine-dependant,
        // JVM-dependant, user-dependant, and count-dependant.
        final StringBuilder data = new StringBuilder(300);
        data.append( System.currentTimeMillis() );
        data.append( SYSTEM_PROPERTIES_STRING );
        // Very unlikely to matter given the timestamp is also a factor, but just in
        // case, the get and update of guidUseCounter needs to be synchronized.
        data.append( guidUseCounter.getAndIncrement() );

        data.append( new Date().toGMTString() );
        return getMD5( data.toString().getBytes() );
    }

    private static AtomicInteger guidUseCounter = new AtomicInteger( 1 );
}
