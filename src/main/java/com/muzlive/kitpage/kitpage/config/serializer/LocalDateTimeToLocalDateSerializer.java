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

public class LocalDateTimeToLocalDateSerializer extends JsonSerializer<LocalDateTime> {

	DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");

	@Override
	public void serialize(LocalDateTime value, JsonGenerator gen, SerializerProvider serializerProvider) throws IOException {
		OffsetDateTime offset = value.atOffset(ZoneOffset.UTC);
		ZoneId zoneId = LocaleContextHolder.getTimeZone().toZoneId();

		gen.writeString(offset.atZoneSameInstant(zoneId).format(dtf));
	}
}
