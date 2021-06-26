package me.becycled.iothub.server.protocol.adm;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import me.becycled.iothub.server.protocol.Telemetry;

import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import static me.becycled.iothub.server.protocol.adm.AdmUtils.IMEI_LENGTH;

/**
 * @author Suren Kalaychyan
 */
@SuppressWarnings("PMD.LinguisticNaming")
public final class AdmPacketDecoder extends ByteToMessageDecoder {

    @Override
    protected void decode(final ChannelHandlerContext ctx, final ByteBuf in, final List<Object> out) {
        final List<AdmPacket> packets = new ArrayList<>();
        while (in.readableBytes() > 0) {
            in.skipBytes(2); // DEVICE_ID
            final int length = in.readUnsignedByte();
            final byte type = in.readByte();

            final AdmPacket packet = new AdmPacket(AdmUtils.AdmPacketType.fromByteAndLength(type, length));
            switch (packet.getType()) {
                case AUTH:
                    readImei(in, packet);
                    break;

                case DATA_ADM5:
                    readAdm5(in, packet);
                    break;

                case DATA_ADM6:
                    readAdm6(in, type, packet);
                    break;

                default:
                    throw new IllegalStateException(MessageFormat.format("Unsupported packet type: {0}.",
                        packet.getType()));
            }

            packets.add(packet);
        }

        out.add(packets);
    }

    private static void readImei(final ByteBuf buffer, final AdmPacket packet) {
        final byte[] imeiBytes = new byte[IMEI_LENGTH];
        buffer.readBytes(imeiBytes);
        final String imei = new String(imeiBytes, StandardCharsets.UTF_8);
        packet.setIdentifier(imei);

        packet.setHardwareType(AdmUtils.AdmHardwareType.fromByte(buffer.readByte()));
        packet.setReplyType(AdmUtils.AdmReplyType.fromByte(buffer.readByte()));
        buffer.skipBytes(44); // UNUSED

        packet.setCrc(buffer.readByte());
    }

    @SuppressWarnings("ExecutableStatementCount")
    private static void readAdm5(final ByteBuf buffer, final AdmPacket packet) {
        final Telemetry.Builder builder = Telemetry.builder();

        buffer.skipBytes(1 + 2 + 1); // Cycl, GPSpntr, SoftVersion
        buffer.skipBytes(2); // Статусные флаги
        buffer.skipBytes(4); // Acc

        // Напряжение на входах (мВ).
        buffer.skipBytes(2);
        buffer.skipBytes(2);
        buffer.skipBytes(2);
        buffer.skipBytes(2);
        buffer.skipBytes(2);
        buffer.skipBytes(2);

        buffer.skipBytes(1); // PinSet

        buffer.skipBytes(2); // Напряжение питания (мВ).
        buffer.skipBytes(2); // Напряжение батареи (мВ).

        buffer.skipBytes(1); // 0 - валидные, 1 - невалидные

        final long time = buffer.readUnsignedIntLE(); // Время с GPS по Гринвичу. С полуночи в секундах.
        final long date = buffer.readUnsignedIntLE(); // Дата с GPS по Гринвичу.
        final int year = (int) ((date % 100) + 2000); // Year = Date mod 100 + 2000
        final int month = (int) ((date % 10_000) / 100); // Month = (Date mod 10000) div 100
        final int day = (int) (date / 10_000); // Day = Date div 10000
        final Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        calendar.set(year, month - 1, day, 0, 0, 0);
        final long epoch = calendar.getTimeInMillis() + time * 1000;
        builder.withFixTime(Instant.ofEpochMilli(epoch));

        builder.withLatitude((double) buffer.readFloatLE());
        builder.withLongitude((double) buffer.readFloatLE());
        builder.withAltitude((double) buffer.readFloatLE());
        builder.withSpeed((int) buffer.readFloatLE());
        builder.withCourse((int) buffer.readFloatLE());

        buffer.skipBytes(4); // HDOP
        buffer.skipBytes(1); // Satellites

        packet.setCrc(buffer.readByte());

        packet.getRecords().add(builder.build());
    }

    @SuppressWarnings({"ExecutableStatementCount", "PMD.NcssCount"})
    private static void readAdm6(final ByteBuf buffer, final byte type, final AdmPacket packet) {
        final Telemetry.Builder builder = Telemetry.builder();

        buffer.skipBytes(1 + 2); // Soft - номер версии прошивки, GPSpntr - номер пакета по порядку
        buffer.skipBytes(2); // Статусные флаги

        builder.withLatitude((double) buffer.readFloatLE());
        builder.withLongitude((double) buffer.readFloatLE());
        builder.withCourse((int) (buffer.readUnsignedShortLE() * 0.1));
        builder.withSpeed((int) (buffer.readUnsignedShortLE() * 0.1));
        buffer.skipBytes(1); // Acc
        builder.withAltitude((double) buffer.readUnsignedShortLE());

        buffer.skipBytes(1); // HDOP
        buffer.skipBytes(1); // Satellites

        final long epoch = buffer.readUnsignedIntLE() * 1000;
        builder.withFixTime(Instant.ofEpochMilli(epoch));

        buffer.skipBytes(2); // Напряжение питания (мВ).
        buffer.skipBytes(2); // Напряжение батареи (мВ).

        if ((type & 0b0000_0100) != 0) { // Акселерометр, выходы, события по входам
            buffer.skipBytes(1 + 1 + 1); // VIB - текущий уровень вибрации, VIB_COUNT - суммарный показатель вибрации, OUT - состояние выходов
            buffer.skipBytes(1); // Индикация событий на входах IN 0..7, 1 - возникло событие, 0 - события не было
        }

        if ((type & 0b0000_1000) != 0) { // Напряжение на входах IN 0..5, мВ
            buffer.skipBytes(2);
            buffer.skipBytes(2);
            buffer.skipBytes(2);
            buffer.skipBytes(2);
            buffer.skipBytes(2);
            buffer.skipBytes(2);
        }

        if ((type & 0b0001_0000) != 0) { // Значение счетчиков на входах IN 6..7, всего импульсов или импульсов в секунду
            buffer.skipBytes(4);
            buffer.skipBytes(4);
        }

        if ((type & 0b0010_0000) != 0) { // Датчики уровня топлива и температуры
            buffer.skipBytes(2);
            buffer.skipBytes(2);
            buffer.skipBytes(2);

            buffer.skipBytes(1);
            buffer.skipBytes(1);
            buffer.skipBytes(1);
        }

        if ((type & 0b0100_0000) != 0) { // Данные с CAN шины
            final int canLength = buffer.readUnsignedByte(); // Длина всего CAN блока с данными, включая байт длины
            buffer.skipBytes(canLength - 1);
        }

        if ((type & 0b1000_0000) != 0) { // Виртуальный одометр
            buffer.skipBytes(4);
        }

        packet.getRecords().add(builder.build());
    }
}
