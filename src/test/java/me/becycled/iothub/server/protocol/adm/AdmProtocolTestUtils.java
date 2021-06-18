package me.becycled.iothub.server.protocol.adm;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang3.ArrayUtils;

/**
 * @author Suren Kalaychyan
 */
enum AdmProtocolTestUtils {;

    static final String AUTH_IMEI = "100000000000000";

    static byte[] VALID_AUTH_PACKET;
    static byte[] VALID_AUTH_RESPONSE;

    static byte[] VALID_ADM6_PACKET_WITH_SINGLE_DATA_RECORD;
    static byte[] VALID_ADM6_PACKET_WITH_SINGLE_DATA_RECORD_RESPONSE;

    static byte[] VALID_ADM6_PACKET_WITH_SEVERAL_DATA_RECORDS;
    static byte[] VALID_ADM6_PACKET_WITH_SEVERAL_DATA_RECORDS_RESPONSE;

    static {
        try {
            VALID_AUTH_PACKET = hexStringToBytes("01 00 42 03 31 30 30 30 30 30 30 30 30 30 30 30 30 30 30 0B 02 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 78");
            VALID_AUTH_RESPONSE = hexStringToBytes("2A 2A 2A 31 2A 0D 0A");

            VALID_ADM6_PACKET_WITH_SINGLE_DATA_RECORD = hexStringToBytes("01 00 43 3C 43 50 07 10 00 FE BC 33 42 C6 FA 17 42 18 0A 00 00 00 00 00 06 78 47 9B 9C 59 57 66 13 03 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00");
            VALID_ADM6_PACKET_WITH_SINGLE_DATA_RECORD_RESPONSE = hexStringToBytes("2A 2A 2A 31 2A 0D 0A");

            VALID_ADM6_PACKET_WITH_SEVERAL_DATA_RECORDS = hexStringToBytes("01 00 43 3C 49 15 1C 10 00 47 4D 48 42 F2 E3 2C 42 29 0D 26 00 1A 6A 00 10 17 80 A1 9C 59 A0 31 F9 01 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 01 00 43 3C 49 14 1C 10 00 43 4D 48 42 F6 E3 2C 42 3C 05 25 00 19 69 00 10 17 7E A1 9C 59 A0 31 E9 01 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 01 00 43 3C 49 13 1C 10 00 40 4D 48 42 F7 E3 2C 42 26 0D 21 00 15 66 00 10 17 7A A1 9C 59 B0 31 EB 01 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00");
            VALID_ADM6_PACKET_WITH_SEVERAL_DATA_RECORDS_RESPONSE = hexStringToBytes("2A 2A 2A 31 2A 0D 0A");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private static byte[] hexStringToBytes(final String hexString) throws DecoderException {
        if (hexString == null) {
            return ArrayUtils.EMPTY_BYTE_ARRAY;
        }
        return Hex.decodeHex(hexString.replaceAll("\\s+", "")
            .toCharArray());
    }
}
