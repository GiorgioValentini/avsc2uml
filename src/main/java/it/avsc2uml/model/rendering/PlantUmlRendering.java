package it.avsc2uml.model.rendering;

import it.avsc2uml.Utility;
import it.avsc2uml.model.avro.AvroEnum;
import it.avsc2uml.model.avro.AvroRecord;
import lombok.Getter;

import static it.avsc2uml.Utility.appendAndNewLine;


/**
 * The container of the plantuml model
 */
@Getter
public class PlantUmlRendering {

	private final StringBuilder records = new StringBuilder();
	private final StringBuilder enums = new StringBuilder();
	private final StringBuilder connections = new StringBuilder();

	private final boolean renderingEnums;

	public PlantUmlRendering(AvroRecord avroRecord, boolean renderingEnums) {
		this.renderingEnums = renderingEnums;
		toLanguageNotation(avroRecord);
	}

	private void addRecord(StringBuilder record) {
		Utility.appendAndNewLine(records, record);
	}

	private void addEnum(StringBuilder _enum) {
		Utility.appendAndNewLine(enums, _enum);
	}

	private void addConnection(String connection) {
		Utility.appendAndNewLine(connections, connection);
	}

	public void merge(PlantUmlRendering plantUmlRendering) {
		records.append(plantUmlRendering.getRecords());
		enums.append(plantUmlRendering.getEnums());
		connections.append(plantUmlRendering.getConnections());
	}


	/**
	 * Converts AVRO schema to the UML language specific notation
	 */
	private void toLanguageNotation(AvroRecord avroRecord) {
		convertToUMLNotation(avroRecord);
		DFS(avroRecord);
	}

	@Override
	public String toString() {
		return records.append(enums).append(connections).toString();
	}

	private void convertToUMLNotation(AvroRecord avroRecord) {
		StringBuilder mapStringBuilder = new StringBuilder();
		StringBuilder enumStringBuilder = new StringBuilder();

		appendAndNewLine(mapStringBuilder, String.format("map %s {", avroRecord.name()));

		avroRecord.forEachPrimitive(entry -> appendPrimitiveField(mapStringBuilder, entry.getKey(), (String) entry.getValue()));

		avroRecord.forEachRecord(entry -> {
			appendAndNewLine(mapStringBuilder, String.format("\t%s=>", entry.getKey()));

			addConnection(
				String.format(
					"%s::%s --> %s",
					avroRecord.name(),
					entry.getKey(),
					((AvroRecord) entry.getValue()).name()
				)
			);
		});

		avroRecord.forEachEnum(entry -> {
			if (renderingEnums) {
				appendEnum(mapStringBuilder, enumStringBuilder, entry.getKey(), (AvroEnum) entry.getValue(), avroRecord);
			} else {
				appendPrimitiveField(mapStringBuilder, entry.getKey(), "enum");
			}
		});

		appendAndNewLine(mapStringBuilder, "}");

		addRecord(mapStringBuilder);
	}


	/**
	 * AVRO schema DFS traversal
	 */
	private void DFS(AvroRecord avroRecord) {
		avroRecord.forEach(
			(fieldName, fieldSchema) -> {
				if (fieldSchema instanceof AvroRecord) {
					this.merge(new PlantUmlRendering((AvroRecord) fieldSchema, renderingEnums));
				}
			}
		);
	}

	private void appendPrimitiveField(StringBuilder mapStringBuilder, String fieldName, String fieldSchema) {
		appendAndNewLine(mapStringBuilder, String.format("\t%s=>%s", fieldName, fieldSchema));
	}

	private void appendEnum(StringBuilder mapStringBuilder, StringBuilder enumStringBuilder, String fieldName, AvroEnum avroEnum, AvroRecord avroRecord) {
		appendAndNewLine(mapStringBuilder, String.format("\t%s=>", fieldName));

		appendAndNewLine(enumStringBuilder, String.format("object %s {", fieldName));
		avroEnum.getValues()
			.forEach(value -> appendAndNewLine(enumStringBuilder, value));
		appendAndNewLine(enumStringBuilder, "}");

		addEnum(enumStringBuilder);
		addConnection(
			String.format(
				"%s::%s --> %s",
				avroRecord.name(),
				fieldName,
				fieldName
			)
		);
	}
}
