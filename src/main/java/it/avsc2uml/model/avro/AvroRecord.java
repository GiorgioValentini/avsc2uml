package it.avsc2uml.model.avro;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import org.apache.avro.Schema;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;


/**
 * The wrapper of original AVRO schema model
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Accessors(fluent = true) @Getter
public class AvroRecord extends LinkedHashMap<String, Object> {

	private final @NonNull String name;

	public static AvroRecord of(Schema schema) {
		AvroRecord schemaMap = new AvroRecord(schema.getName());

		schema.getFields().forEach(field -> {
			switch (field.schema().getType()) {
				case RECORD -> schemaMap.put(field.name(), of(field.schema()));
				case UNION -> {
					Optional<Schema> recordTypeSchema = searchForRecordTypeInTypesList(field);
					recordTypeSchema.ifPresent(rts -> schemaMap.put(field.name(), of(rts)));

					Optional<Schema> enumTypeSchema = searchForEnumTypeInTypesList(field);
					enumTypeSchema.ifPresentOrElse(
						ets -> schemaMap.put(field.name(), new AvroEnum(ets.getEnumSymbols())),
						() -> schemaMap.put(field.name(), concatTypesOfField(field))
					);
				}
				case ENUM -> schemaMap.put(field.name(), new AvroEnum(field.schema().getEnumSymbols()));
				default -> schemaMap.put(field.name(), field.schema().getType().getName().toLowerCase());
			}
		});

		return schemaMap;
	}

	/**
	 * Discover the types of a field and concatenate them
	 * @param field - the field in wich discover types
	 * @return string containing the concatenated field types
	 */
	private static String concatTypesOfField(Schema.Field field) {
		return field.schema()
			.getTypes()
			.stream()
			.map(s -> s.getType().getName().toLowerCase())
			.collect(Collectors.joining(", "));
	}

	/**
	 * Look into field types for RECORD
	 * @param field - the field in wich discover types
	 * @return an optional eventually containing the RECORD type
	 */
	private static Optional<Schema> searchForRecordTypeInTypesList(Schema.Field field) {
		return searchForSpecificTypeInTypesList(field, Schema.Type.RECORD);
	}

	/**
	 * Look into field types for ENUM
	 * @param field - the field in wich discover types
	 * @return an optional eventually containing the ENUM type
	 */
	private static Optional<Schema> searchForEnumTypeInTypesList(Schema.Field field) {
		return searchForSpecificTypeInTypesList(field, Schema.Type.ENUM);
	}

	private static Optional<Schema> searchForSpecificTypeInTypesList(Schema.Field field, Schema.Type type) {
		return field.schema()
			.getTypes()
			.stream()
			.filter(s -> s.getType().equals(type))
			.findAny();
	}

	/**
	 * Applies the function given in input over the fields that has a primitive type
	 * @param action - the function to be applied
	 */
	public void forEachPrimitive(Consumer<Map.Entry<String, Object>> action) {
		filterByEntryValueClass(String.class)
			.forEach(action);
	}

	/**
	 * Applies the function given in input over the fields that has an enum type
	 * @param action - the function to be applied
	 */
	public void forEachEnum(Consumer<Map.Entry<String, Object>> action) {
		filterByEntryValueClass(AvroEnum.class)
			.forEach(action);
	}

	/**
	 * Applies the function given in input over the fields that has a record type
	 * @param action - the function to be applied
	 */
	public void forEachRecord(Consumer<Map.Entry<String, Object>> action) {
		filterByEntryValueClass(AvroRecord.class)
			.forEach(action);
	}

	private Stream<Map.Entry<String, Object>> filterByEntryValueClass(Class<?> _class) {
		return this.entrySet()
			.stream()
			.filter(entry -> _class.isInstance(entry.getValue()));
	}
}
