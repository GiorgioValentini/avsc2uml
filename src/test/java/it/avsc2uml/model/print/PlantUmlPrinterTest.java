package it.avsc2uml.model.print;

import it.avsc2uml.model.avro.AvroRecord;
import org.apache.avro.Schema;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

public class PlantUmlPrinterTest {

	@Test
	void printEnumsTest() throws IOException {
		Schema schema = new Schema.Parser().parse(new File("src/test/resources/generic-schema.avsc"));

		AvroRecord stringObjectMap = AvroRecord.of(schema);

		PlantUmlPrinter.printPlantUmlDiagram(new File("target/withEnums.puml"), stringObjectMap, true);
	}

	@Test
	void withoutEnumsTest() throws IOException {
		Schema schema = new Schema.Parser().parse(new File("src/test/resources/generic-schema.avsc"));

		AvroRecord stringObjectMap = AvroRecord.of(schema);

		PlantUmlPrinter.printPlantUmlDiagram(new File("target/withoutEnums.puml"), stringObjectMap, false);
	}
}
