package com.exedio.cops;

/*
 * This class has been stripped from all components not used for http authentication,
 * and moved into the cops package.
 * 
 * If you want to use the Base64 class, better get the original version.
 */

/**
 * Encodes and decodes to and from Base64 notation.
 *
 * <p>
 * Change Log:
 * </p>
 * <ul>
 *  <li>v2.1 - Cleaned up javadoc comments and unused variables and methods. Added
 *   some convenience methods for reading and writing to and from files.</li>
 *  <li>v2.0.2 - Now specifies UTF-8 encoding in places where the code fails on systems
 *   with other encodings (like EBCDIC).</li>
 *  <li>v2.0.1 - Fixed an error when decoding a single byte, that is, when the
 *   encoded data was a single byte.</li>
 *  <li>v2.0 - I got rid of methods that used booleans to set options. 
 *   Now everything is more consolidated and cleaner. The code now detects
 *   when data that's being decoded is gzip-compressed and will decompress it
 *   automatically. Generally things are cleaner. You'll probably have to
 *   change some method calls that you were making to support the new
 *   options format (<tt>int</tt>s that you "OR" together).</li>
 *  <li>v1.5.1 - Fixed bug when decompressing and decoding to a             
 *   byte[] using <tt>decode( String s, boolean gzipCompressed )</tt>.      
 *   Added the ability to "suspend" encoding in the Output Stream so        
 *   you can turn on and off the encoding if you need to embed base64       
 *   data in an otherwise "normal" stream (like an XML file).</li>  
 *  <li>v1.5 - Output stream pases on flush() command but doesn't do anything itself.
 *      This helps when using GZIP streams.
 *      Added the ability to GZip-compress objects before encoding them.</li>
 *  <li>v1.4 - Added helper methods to read/write files.</li>
 *  <li>v1.3.6 - Fixed OutputStream.flush() so that 'position' is reset.</li>
 *  <li>v1.3.5 - Added flag to turn on and off line breaks. Fixed bug in input stream
 *      where last buffer being read, if not completely full, was not returned.</li>
 *  <li>v1.3.4 - Fixed when "improperly padded stream" error was thrown at the wrong time.</li>
 *  <li>v1.3.3 - Fixed I/O streams which were totally messed up.</li>
 * </ul>
 *
 * <p>
 * I am placing this code in the Public Domain. Do with it as you will.
 * This software comes with no guarantees or warranties but with
 * plenty of well-wishing instead!
 * Please visit <a href="http://iharder.net/base64">http://iharder.net/base64</a>
 * periodically to check for updates or to contribute improvements.
 * </p>
 *
 * @author Robert Harder
 * @author rob@iharder.net
 * @version 2.1
 */
final class Base64
{
    
/* ********  P R I V A T E   F I E L D S  ******** */  
    
    
    /** The equals sign (=) as a byte. */
    private final static byte EQUALS_SIGN = (byte)'=';
    
    
    /** Preferred encoding. */
    private final static String PREFERRED_ENCODING = "UTF-8";
    
    
    /** The 64 valid Base64 values. */
    private final static byte[] ALPHABET;
    private final static byte[] _NATIVE_ALPHABET = /* May be something funny like EBCDIC */
    {
        (byte)'A', (byte)'B', (byte)'C', (byte)'D', (byte)'E', (byte)'F', (byte)'G',
        (byte)'H', (byte)'I', (byte)'J', (byte)'K', (byte)'L', (byte)'M', (byte)'N',
        (byte)'O', (byte)'P', (byte)'Q', (byte)'R', (byte)'S', (byte)'T', (byte)'U', 
        (byte)'V', (byte)'W', (byte)'X', (byte)'Y', (byte)'Z',
        (byte)'a', (byte)'b', (byte)'c', (byte)'d', (byte)'e', (byte)'f', (byte)'g',
        (byte)'h', (byte)'i', (byte)'j', (byte)'k', (byte)'l', (byte)'m', (byte)'n',
        (byte)'o', (byte)'p', (byte)'q', (byte)'r', (byte)'s', (byte)'t', (byte)'u', 
        (byte)'v', (byte)'w', (byte)'x', (byte)'y', (byte)'z',
        (byte)'0', (byte)'1', (byte)'2', (byte)'3', (byte)'4', (byte)'5', 
        (byte)'6', (byte)'7', (byte)'8', (byte)'9', (byte)'+', (byte)'/'
    };
    
    /** Determine which ALPHABET to use. */
    static
    {
        byte[] __bytes;
        try
        {
            __bytes = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/".getBytes( PREFERRED_ENCODING );
        }   // end try
        catch (java.io.UnsupportedEncodingException use)
        {
            __bytes = _NATIVE_ALPHABET; // Fall back to native encoding
        }   // end catch
        ALPHABET = __bytes;
    }   // end static
    
    
    /** 
     * Translates a Base64 value to either its 6-bit reconstruction value
     * or a negative number indicating some other meaning.
     **/
    private final static byte[] DECODABET =
    {   
        -9,-9,-9,-9,-9,-9,-9,-9,-9,                 // Decimal  0 -  8
        -5,-5,                                      // Whitespace: Tab and Linefeed
        -9,-9,                                      // Decimal 11 - 12
        -5,                                         // Whitespace: Carriage Return
        -9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,     // Decimal 14 - 26
        -9,-9,-9,-9,-9,                             // Decimal 27 - 31
        -5,                                         // Whitespace: Space
        -9,-9,-9,-9,-9,-9,-9,-9,-9,-9,              // Decimal 33 - 42
        62,                                         // Plus sign at decimal 43
        -9,-9,-9,                                   // Decimal 44 - 46
        63,                                         // Slash at decimal 47
        52,53,54,55,56,57,58,59,60,61,              // Numbers zero through nine
        -9,-9,-9,                                   // Decimal 58 - 60
        -1,                                         // Equals sign at decimal 61
        -9,-9,-9,                                      // Decimal 62 - 64
        0,1,2,3,4,5,6,7,8,9,10,11,12,13,            // Letters 'A' through 'N'
        14,15,16,17,18,19,20,21,22,23,24,25,        // Letters 'O' through 'Z'
        -9,-9,-9,-9,-9,-9,                          // Decimal 91 - 96
        26,27,28,29,30,31,32,33,34,35,36,37,38,     // Letters 'a' through 'm'
        39,40,41,42,43,44,45,46,47,48,49,50,51,     // Letters 'n' through 'z'
        -9,-9,-9,-9                                 // Decimal 123 - 126
        /*,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,     // Decimal 127 - 139
        -9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,     // Decimal 140 - 152
        -9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,     // Decimal 153 - 165
        -9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,     // Decimal 166 - 178
        -9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,     // Decimal 179 - 191
        -9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,     // Decimal 192 - 204
        -9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,     // Decimal 205 - 217
        -9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,     // Decimal 218 - 230
        -9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,     // Decimal 231 - 243
        -9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9         // Decimal 244 - 255 */
    };
    
    // I think I end up not using the BAD_ENCODING indicator.
    //private final static byte BAD_ENCODING    = -9; // Indicates error in encoding
    private final static byte WHITE_SPACE_ENC = -5; // Indicates white space in encoding
    private final static byte EQUALS_SIGN_ENC = -1; // Indicates equals sign in encoding

    
    /** Defeats instantiation. */
    private Base64(){}
    
    
    
    

    
    
    
/* ********  D E C O D I N G   M E T H O D S  ******** */
    
    
    /**
     * Decodes four bytes from array <var>source</var>
     * and writes the resulting bytes (up to three of them)
     * to <var>destination</var>.
     * The source and destination arrays can be manipulated
     * anywhere along their length by specifying 
     * <var>srcOffset</var> and <var>destOffset</var>.
     * This method does not check to make sure your arrays
     * are large enough to accomodate <var>srcOffset</var> + 4 for
     * the <var>source</var> array or <var>destOffset</var> + 3 for
     * the <var>destination</var> array.
     * This method returns the actual number of bytes that 
     * were converted from the Base64 encoding.
     * 
     *
     * @param source the array to convert
     * @param srcOffset the index where conversion begins
     * @param destination the array to hold the conversion
     * @param destOffset the index where output will be put
     * @return the number of decoded bytes converted
     * @since 1.3
     */
    private static int decode4to3( byte[] source, int srcOffset, byte[] destination, int destOffset )
    {
        // Example: Dk==
        if( source[ srcOffset + 2] == EQUALS_SIGN )
        {
            // Two ways to do the same thing. Don't know which way I like best.
            //int outBuff =   ( ( DECODABET[ source[ srcOffset    ] ] << 24 ) >>>  6 )
            //              | ( ( DECODABET[ source[ srcOffset + 1] ] << 24 ) >>> 12 );
            int outBuff =   ( ( DECODABET[ source[ srcOffset    ] ] & 0xFF ) << 18 )
                          | ( ( DECODABET[ source[ srcOffset + 1] ] & 0xFF ) << 12 );
            
            destination[ destOffset ] = (byte)( outBuff >>> 16 );
            return 1;
        }
        
        // Example: DkL=
        else if( source[ srcOffset + 3 ] == EQUALS_SIGN )
        {
            // Two ways to do the same thing. Don't know which way I like best.
            //int outBuff =   ( ( DECODABET[ source[ srcOffset     ] ] << 24 ) >>>  6 )
            //              | ( ( DECODABET[ source[ srcOffset + 1 ] ] << 24 ) >>> 12 )
            //              | ( ( DECODABET[ source[ srcOffset + 2 ] ] << 24 ) >>> 18 );
            int outBuff =   ( ( DECODABET[ source[ srcOffset     ] ] & 0xFF ) << 18 )
                          | ( ( DECODABET[ source[ srcOffset + 1 ] ] & 0xFF ) << 12 )
                          | ( ( DECODABET[ source[ srcOffset + 2 ] ] & 0xFF ) <<  6 );
            
            destination[ destOffset     ] = (byte)( outBuff >>> 16 );
            destination[ destOffset + 1 ] = (byte)( outBuff >>>  8 );
            return 2;
        }
        
        // Example: DkLE
        else
        {
            try{
            // Two ways to do the same thing. Don't know which way I like best.
            //int outBuff =   ( ( DECODABET[ source[ srcOffset     ] ] << 24 ) >>>  6 )
            //              | ( ( DECODABET[ source[ srcOffset + 1 ] ] << 24 ) >>> 12 )
            //              | ( ( DECODABET[ source[ srcOffset + 2 ] ] << 24 ) >>> 18 )
            //              | ( ( DECODABET[ source[ srcOffset + 3 ] ] << 24 ) >>> 24 );
            int outBuff =   ( ( DECODABET[ source[ srcOffset     ] ] & 0xFF ) << 18 )
                          | ( ( DECODABET[ source[ srcOffset + 1 ] ] & 0xFF ) << 12 )
                          | ( ( DECODABET[ source[ srcOffset + 2 ] ] & 0xFF ) <<  6)
                          | ( ( DECODABET[ source[ srcOffset + 3 ] ] & 0xFF )      );

            
            destination[ destOffset     ] = (byte)( outBuff >> 16 );
            destination[ destOffset + 1 ] = (byte)( outBuff >>  8 );
            destination[ destOffset + 2 ] = (byte)( outBuff       );

            return 3;
            }catch( Exception e){
                System.out.println(""+source[srcOffset]+ ": " + ( DECODABET[ source[ srcOffset     ] ]  ) );
                System.out.println(""+source[srcOffset+1]+  ": " + ( DECODABET[ source[ srcOffset + 1 ] ]  ) );
                System.out.println(""+source[srcOffset+2]+  ": " + ( DECODABET[ source[ srcOffset + 2 ] ]  ) );
                System.out.println(""+source[srcOffset+3]+  ": " + ( DECODABET[ source[ srcOffset + 3 ] ]  ) );
                return -1;
            }   //e nd catch
        }
    }   // end decodeToBytes
    
    
    
    
    /**
     * Very low-level access to decoding ASCII characters in
     * the form of a byte array. Does not support automatically
     * gunzipping or any other "fancy" features.
     *
     * @param source The Base64 encoded data
     * @param off    The offset of where to begin decoding
     * @param len    The length of characters to decode
     * @return decoded data
     * @since 1.3
     */
    private static byte[] decode( byte[] source, int off, int len )
    {
        int    len34   = len * 3 / 4;
        byte[] outBuff = new byte[ len34 ]; // Upper limit on size of output
        int    outBuffPosn = 0;
        
        byte[] b4        = new byte[4];
        int    b4Posn    = 0;
        int    i         = 0;
        byte   sbiCrop   = 0;
        byte   sbiDecode = 0;
        for( i = off; i < off+len; i++ )
        {
            sbiCrop = (byte)(source[i] & 0x7f); // Only the low seven bits
            sbiDecode = DECODABET[ sbiCrop ];
            
            if( sbiDecode >= WHITE_SPACE_ENC ) // White space, Equals sign or better
            {
                if( sbiDecode >= EQUALS_SIGN_ENC )
                {
                    b4[ b4Posn++ ] = sbiCrop;
                    if( b4Posn > 3 )
                    {
                        outBuffPosn += decode4to3( b4, 0, outBuff, outBuffPosn );
                        b4Posn = 0;
                        
                        // If that was the equals sign, break out of 'for' loop
                        if( sbiCrop == EQUALS_SIGN )
                            break;
                    }   // end if: quartet built
                    
                }   // end if: equals sign or better
                
            }   // end if: white space, equals sign or better
            else
            {
                System.err.println( "Bad Base64 input character at " + i + ": " + source[i] + "(decimal)" );
                return null;
            }   // end else: 
        }   // each input character
                                   
        byte[] out = new byte[ outBuffPosn ];
        System.arraycopy( outBuff, 0, out, 0, outBuffPosn ); 
        return out;
    }   // end decode
    
    
    
    
    /**
     * Decodes data from Base64 notation, automatically
     * detecting gzip-compressed data and decompressing it.
     *
     * @param s the string to decode
     * @return the decoded data
     * @since 1.4
     */
    public static byte[] decode( String s )
    {   
        byte[] bytes;
        try
        {
            bytes = s.getBytes( PREFERRED_ENCODING );
        }   // end try
        catch( java.io.UnsupportedEncodingException uee )
        {
            bytes = s.getBytes();
        }   // end catch
		//</change>
        
        // Decode
        bytes = decode( bytes, 0, bytes.length );
        
        
        // Check to see if it's gzip-compressed
        // GZIP Magic Two-Byte Number: 0x8b1f (35615)
        if( bytes != null && bytes.length >= 4 )
        {
            
            int head = ((int)bytes[0] & 0xff) | ((bytes[1] << 8) & 0xff00);       
            if( java.util.zip.GZIPInputStream.GZIP_MAGIC == head ) 
            {
                java.io.ByteArrayInputStream  bais = null;
                java.util.zip.GZIPInputStream gzis = null;
                java.io.ByteArrayOutputStream baos = null;
                byte[] buffer = new byte[2048];
                int    length = 0;

                try
                {
                    baos = new java.io.ByteArrayOutputStream();
                    bais = new java.io.ByteArrayInputStream( bytes );
                    gzis = new java.util.zip.GZIPInputStream( bais );

                    while( ( length = gzis.read( buffer ) ) >= 0 )
                    {
                        baos.write(buffer,0,length);
                    }   // end while: reading input

                    // No error? Get new bytes.
                    bytes = baos.toByteArray();

                }   // end try
                catch( java.io.IOException e )
                {
                    // Just return originally-decoded bytes
                }   // end catch
                finally
                {
                    try{ baos.close(); } catch( Exception e ){}
                    try{ gzis.close(); } catch( Exception e ){}
                    try{ bais.close(); } catch( Exception e ){}
                }   // end finally

            }   // end if: gzipped
        }   // end if: bytes.length >= 2
        
        return bytes;
    }   // end decode

    
}   // end class Base64
