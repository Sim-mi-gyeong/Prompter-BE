package com.prompter.response;

import java.util.List;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TagResponse {
	private final List<String> tags;

	public static SummaryResponse of(List<String> tags) {
		return SummaryResponse.builder()
			.tags(tags)
			.build();
	}
}
