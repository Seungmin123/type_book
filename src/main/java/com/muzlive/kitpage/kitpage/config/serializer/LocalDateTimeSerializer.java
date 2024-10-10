package com.muzlive.kitpage.kitpage.config.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import org.springframework.context.i18n.LocaleContextHolder;

public class LocalDateTimeSerializer<T> extends JsonSerializer<T> {

    public static final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");

    @Override
    public void serialize(T value, JsonGenerator gen, SerializerProvider serializers)
            throws IOException {
        OffsetDateTime offset = ((LocalDateTime)value).atOffset(ZoneOffset.UTC);
        ZoneId zoneId = LocaleContextHolder.getTimeZone().toZoneId();

        gen.writeString(offset.atZoneSameInstant(zoneId).toLocalDateTime().format(dtf));
    }
}
