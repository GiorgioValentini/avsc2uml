package it.avsc2uml.model.avro;

import org.apache.avro.Schema;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

public class AvroRecordTest {

	@Test
	void correctParsingTest() throws IOException {
		Schema schema = new Schema.Parser().parse(new File("src/test/resources/example-schema.avsc"));

		AvroRecord stringObjectMap = AvroRecord.of(schema);

		assertAll(
			() -> assertEquals("ExampleEvent", stringObjectMap.name()),
			() -> assertTrue(stringObjectMap.containsKey("source")),
			() -> assertTrue(stringObjectMap.containsKey("specversion")),
			() -> assertEquals("string", stringObjectMap.get("source")),
			() -> assertEquals("null, string", stringObjectMap.get("dataschema")),
			() -> assertTrue(stringObjectMap.get("operation") instanceof AvroEnum),
			() -> assertTrue(stringObjectMap.get("data") instanceof AvroRecord)
		);

		AvroEnum operation = (AvroEnum) stringObjectMap.get("operation");
		assertArrayEquals(new String[]{"CREATE", "UPDATE", "DELETE"}, operation.getValues().toArray());

		AvroRecord data = (AvroRecord) stringObjectMap.get("data");
		assertAll(
			() -> assertEquals("Event", data.name()),
			() -> assertTrue(data.containsKey("objectId")),
			() -> assertEquals("string", data.get("objectId"))
		);
	}
}
