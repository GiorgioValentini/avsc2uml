package it.avsc2uml.model.print;

import it.avsc2uml.model.avro.AvroRecord;
import it.avsc2uml.model.rendering.PlantUmlRendering;
import lombok.Cleanup;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class PlantUmlPrinter {

	public static void printPlantUmlDiagram(File outputFile, AvroRecord avroRecord, boolean printEnums) throws IOException {
		@Cleanup FileWriter fileWriter = new FileWriter(outputFile);
		@Cleanup PrintWriter printWriter = new PrintWriter(fileWriter);

		PlantUmlRendering plantUmlRendering = new PlantUmlRendering(avroRecord, printEnums);

		printWriter.println("@startuml");
		printWriter.println(plantUmlRendering);
		printWriter.println("@enduml");
	}
}
