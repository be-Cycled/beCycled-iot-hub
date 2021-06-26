package me.becycled.iothub.server.protocol.adm;

import me.becycled.iothub.server.protocol.Telemetry;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author Suren Kalaychyan
 */
public final class AdmPacket {

    private final AdmUtils.AdmPacketType type;
    private AdmUtils.AdmHardwareType hardwareType;
    private AdmUtils.AdmReplyType replyType;
    private byte crc;

    private String identifier;
    private final List<Telemetry> records = new ArrayList<>();

    public AdmPacket(final AdmUtils.AdmPacketType type) {
        this.type = type;
    }

    //region GETTERS & SETTERS

    public AdmUtils.AdmPacketType getType() {
        return type;
    }

    public AdmUtils.AdmHardwareType getHardwareType() {
        return hardwareType;
    }

    public void setHardwareType(final AdmUtils.AdmHardwareType hardwareType) {
        this.hardwareType = hardwareType;
    }

    public AdmUtils.AdmReplyType getReplyType() {
        return replyType;
    }

    public void setReplyType(final AdmUtils.AdmReplyType replyType) {
        this.replyType = replyType;
    }

    public byte getCrc() {
        return crc;
    }

    public void setCrc(final byte crc) {
        this.crc = crc;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(final String identifier) {
        this.identifier = identifier;
    }

    public List<Telemetry> getRecords() {
        return records;
    }

    //endregion GETTERS & SETTERS

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final AdmPacket that = (AdmPacket) o;
        return crc == that.crc
            && type == that.type
            && hardwareType == that.hardwareType
            && replyType == that.replyType
            && Objects.equals(identifier, that.identifier)
            && Objects.equals(records, that.records);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, hardwareType, replyType, crc, identifier, records);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
            .append("type", type)
            .append("hardwareType", hardwareType)
            .append("replyType", replyType)
            .append("crc", crc)
            .append("identifier", identifier)
            .append("records", records)
            .toString();
    }
}
