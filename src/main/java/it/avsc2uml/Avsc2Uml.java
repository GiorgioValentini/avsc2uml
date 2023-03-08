package it.avsc2uml;

import it.avsc2uml.model.avro.AvroRecord;
import it.avsc2uml.model.print.PlantUmlPrinter;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.Schema;
import picocli.CommandLine;
import picocli.CommandLine.Model.CommandSpec;

import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;
import java.util.concurrent.Callable;

import static it.avsc2uml.model.avro.AvroRecord.of;
import static picocli.CommandLine.Command;
import static picocli.CommandLine.Option;
import static picocli.CommandLine.ParameterException;
import static picocli.CommandLine.Spec;

@Slf4j
@Command(name = "avsc2uml", mixinStandardHelpOptions = true, version = "avsc2uml 1.0.0",
	description = "Rendering an AVRO schema file (.avsc) into a plantuml diagram")
public class Avsc2Uml implements Callable<Integer> {

	@Spec
	CommandSpec spec;
	private File avroSchemaFile;

	@Option(names = {"--avro-schema-file"}, required = true, paramLabel = "INPUT-AVSC", description = "Avro schema file to be rendered as a diagram. Requires a .avsc extension.")
	public void setAvscFile(File file) {
		FileNameExtensionFilter fileNameExtensionFilter = new FileNameExtensionFilter("", "avsc");

		if (!fileNameExtensionFilter.accept(file)) {
			throw new ParameterException(spec.commandLine(),
				String.format(
					"Invalid value '%s' for option '--avro-schema-file': specify a file with .avsc extension",
					file.getPath()
				)
			);
		}

		avroSchemaFile = file;
	}

	@Option(names = {"--output-file"}, required = true, paramLabel = "OUTPUT-PLANTUML", description = "The output diagram file")
	private File outputFile;

	@Option(names = {"-e", "--enum"}, description = "whether to display enum values", paramLabel = "ENUM-DISPLAY", defaultValue = "false")
	boolean printEnumValues;

	public static void main(String[] args) {
		int exitCode = new CommandLine(new Avsc2Uml()).execute(args);
		System.exit(exitCode);
	}

	@Override
	public Integer call() throws Exception {
		log.info("Schema file parsing...");
		Schema schema = new Schema.Parser().parse(avroSchemaFile);
		AvroRecord schemaAsMap = of(schema);
		log.info("Schema file parsed");

		log.info("Printing output file...");
		PlantUmlPrinter.printPlantUmlDiagram(outputFile, schemaAsMap, printEnumValues);
		log.info("Output file printed");

		return 0;
	}

}