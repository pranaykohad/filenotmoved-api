package com.filenotmoved.user_service.util;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.locationtech.jts.geom.Point;

import java.io.IOException;

public class PointSerializer extends JsonSerializer<Point> {

    @Override
    public void serialize(Point value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        if (value == null) {
            gen.writeNull();
        } else {
            gen.writeString(value.getY() + "," + value.getX());
        }
    }
}
