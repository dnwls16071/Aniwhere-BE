package com.example.aniwhere.domain.user;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;

import java.util.Arrays;

@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@Getter
public enum Sex {
	male("남자"),
	female("여자");

	private final String description;

	Sex(String description) {
		this.description = description;
	}

	@JsonCreator
	public static Sex parsing(String input) {
		return Arrays.stream(Sex.values()).filter(type
		-> type.getDescription().equals(input)).findFirst().orElse(null);
	}
}
