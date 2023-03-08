package it.avsc2uml;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import picocli.CommandLine;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Paths;

public class OptionsValidationTest {

	@Test
	void correctAvroSchemaFileTest() throws IOException {
		File avroFile = Paths.get(System.getProperty("build.directory"), "correctAvroSchemaFileTest.avsc").toFile();
		avroFile.createNewFile();

		new Avsc2Uml().setAvscFile(avroFile);
	}

	@Test
	void wrongAvroSchemaFileTest() throws IOException {
		File avroFile = Paths.get(System.getProperty("build.directory"), "wrongAvroSchemaFileTest.wrong").toFile();
		avroFile.createNewFile();

		Avsc2Uml avsc2Uml = new Avsc2Uml();
		CommandLine cmd = new CommandLine(avsc2Uml);

		StringWriter sw = new StringWriter();
		cmd.setErr(new PrintWriter(sw));

		cmd.execute("--avro-schema-file=" + avroFile.getPath(), "--output-file=useless");

		Assertions.assertTrue(sw.toString().contains("for option '--avro-schema-file'"));
	}
}
