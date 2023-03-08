package it.avsc2uml.model.avro;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;


/**
 * Wrapper of the enum allowed values list
 */
@RequiredArgsConstructor
@Getter
public class AvroEnum {

	private final List<String> values;
}
