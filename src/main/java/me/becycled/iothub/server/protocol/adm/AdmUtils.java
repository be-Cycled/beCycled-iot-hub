package me.becycled.iothub.server.protocol.adm;

import java.text.MessageFormat;

/**
 * @author Suren Kalaychyan
 */
@SuppressWarnings("WhitespaceAround")
enum AdmUtils {;

    static final int IMEI_LENGTH = 15;

    static final int PACKET_HEADER_LENGTH = 4; // DEVICE_ID (2 bytes) + SIZE (1 byte) + TYPE (1 byte)

    static final byte AUTH_PACKET_SIGN = 0x03;
    static final int AUTH_PACKET_LENGTH = 66;
    static final byte ADM5_PACKET_SIGN = 0x01;
    static final int ADM5_PACKET_LENGTH = 66;
    static final int ADM6_MIN_PACKET_LENGTH = 34;

    enum AdmPacketType {

        AUTH,
        DATA_ADM5,
        DATA_ADM6;

        static AdmPacketType fromByteAndLength(final byte typeByte, final int packetLength) {
            if (typeByte == AUTH_PACKET_SIGN && packetLength == AUTH_PACKET_LENGTH) {
                return AUTH;
            }
            if (typeByte == ADM5_PACKET_SIGN && packetLength == ADM5_PACKET_LENGTH) {
                return DATA_ADM5;
            }
            if ((typeByte & 0b00000011) == 0 && packetLength >= ADM6_MIN_PACKET_LENGTH) {
                return DATA_ADM6;
            }

            throw new IllegalArgumentException(MessageFormat.format("Unknown packet type with byte: {0} and packet length: {1}.",
                typeByte, packetLength));
        }
    }

    enum AdmHardwareType {

        ASC6,
        ADM100,
        ADM200,
        ADM300,
        ADM600,
        ADM007,
        UNKNOWN;

        @SuppressWarnings("ReturnCount")
        static AdmHardwareType fromByte(final byte typeByte) {
            switch (typeByte) {
                case 0x00:
                    return ASC6;
                case 0x0B:
                    return ADM100;
                case 0x03:
                    return ADM200;
                case 0x0A:
                    return ADM300;
                case 0x05:
                    return ADM600;
                case 0x21:
                    return ADM007;
                default:
                    return UNKNOWN;
            }
        }
    }

    enum AdmReplyType {

        UNSUPPORTED,
        TCP_ACK,
        BY_RECORD_COUNT;

        static AdmReplyType fromByte(final byte typeByte) {
            switch (typeByte) {
                case 0x00:
                    return UNSUPPORTED;
                case 0x01:
                    return TCP_ACK;
                case 0x02:
                    return BY_RECORD_COUNT;
                default:
                    throw new IllegalArgumentException(MessageFormat.format("Unknown reply type with byte: {0}.",
                        typeByte));
            }
        }
    }
}
