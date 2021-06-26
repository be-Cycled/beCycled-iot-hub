package me.becycled.iothub.server.protocol;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.time.Instant;
import java.util.Objects;

/**
 * @author Suren Kalaychyan
 */
public final class Telemetry {

    private Integer trackerId;
    private Instant fixTime;
    private Instant serverTime;
    private Double latitude;
    private Double longitude;
    private Double altitude;
    private Integer speed;
    private Integer course;

    private Telemetry() {
    }

    //region GETTERS & SETTERS

    public Integer getTrackerId() {
        return trackerId;
    }

    public void setTrackerId(final Integer trackerId) {
        this.trackerId = trackerId;
    }

    public Instant getFixTime() {
        return fixTime;
    }

    public void setFixTime(final Instant fixTime) {
        this.fixTime = fixTime;
    }

    public Instant getServerTime() {
        return serverTime;
    }

    public void setServerTime(final Instant serverTime) {
        this.serverTime = serverTime;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(final Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(final Double longitude) {
        this.longitude = longitude;
    }

    public Double getAltitude() {
        return altitude;
    }

    public void setAltitude(final Double altitude) {
        this.altitude = altitude;
    }

    public Integer getSpeed() {
        return speed;
    }

    public void setSpeed(final Integer speed) {
        this.speed = speed;
    }

    public Integer getCourse() {
        return course;
    }

    public void setCourse(final Integer course) {
        this.course = course;
    }

    //endregion GETTERS & SETTERS

    @Override
    @SuppressWarnings("CyclomaticComplexity")
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final Telemetry that = (Telemetry) o;
        return Objects.equals(trackerId, that.trackerId)
            && Objects.equals(fixTime, that.fixTime)
            && Objects.equals(serverTime, that.serverTime)
            && Objects.equals(latitude, that.latitude)
            && Objects.equals(longitude, that.longitude)
            && Objects.equals(altitude, that.altitude)
            && Objects.equals(speed, that.speed)
            && Objects.equals(course, that.course);
    }

    @Override
    public int hashCode() {
        return Objects.hash(trackerId, fixTime, serverTime, latitude, longitude, altitude, speed, course);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
            .append("trackerId", trackerId)
            .append("fixTime", fixTime)
            .append("serverTime", serverTime)
            .append("latitude", latitude)
            .append("longitude", longitude)
            .append("altitude", altitude)
            .append("speed", speed)
            .append("course", course)
            .toString();
    }

    public Builder editor() {
        return this.new Builder(this);
    }

    public static Builder builder() {
        return new Telemetry().new Builder();
    }

    /**
     * @author Suren Kalaychyan
     */
    public final class Builder {

        private Builder() {
        }

        private Builder(final Telemetry telemetry) {
            if (telemetry == null) {
                throw new IllegalArgumentException("Telemetry cannot be null");
            }
        }

        public Builder withTrackerId(final Integer trackerId) {
            Telemetry.this.trackerId = trackerId;
            return this;
        }

        public Builder withFixTime(final Instant fixTime) {
            Telemetry.this.fixTime = fixTime;
            return this;
        }

        public Builder withServerTime(final Instant serverTime) {
            Telemetry.this.serverTime = serverTime;
            return this;
        }

        public Builder withLatitude(final Double latitude) {
            Telemetry.this.latitude = latitude;
            return this;
        }

        public Builder withLongitude(final Double longitude) {
            Telemetry.this.longitude = longitude;
            return this;
        }

        public Builder withAltitude(final Double altitude) {
            Telemetry.this.altitude = altitude;
            return this;
        }

        public Builder withSpeed(final Integer speed) {
            Telemetry.this.speed = speed;
            return this;
        }

        public Builder withCourse(final Integer course) {
            Telemetry.this.course = course;
            return this;
        }

        public Telemetry build() {
            return Telemetry.this;
        }
    }
}
