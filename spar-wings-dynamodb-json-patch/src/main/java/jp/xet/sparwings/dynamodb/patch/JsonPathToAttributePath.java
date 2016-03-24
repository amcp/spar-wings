/*
 * Copyright 2015-2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package jp.xet.sparwings.dynamodb.patch;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jackson.jsonpointer.JsonPointer;
import com.github.fge.jackson.jsonpointer.TokenResolver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * TODO for daisuke
 * 
 * @since #version#
 * @version $Id$
 * @author daisuke
 */
public class JsonPathToAttributePath implements Function<JsonPointer, String> {
	
	private static Logger logger = LoggerFactory.getLogger(JsonPathToAttributePath.class);
	
	private static Pattern ARRAY_PATTERN = Pattern.compile("(0|[1-9][0-9]+)");
	
	
	@Override
	public String apply(JsonPointer pointer) {
		logger.trace("pointer = {}", pointer);
		List<String> elements = new ArrayList<>();
		for (TokenResolver<JsonNode> tokenResolver : pointer) {
			String token = tokenResolver.getToken().getRaw();
			logger.trace("tokenResolver = {}", token);
			if (ARRAY_PATTERN.matcher(token).matches()) {
				String last = elements.get(elements.size() - 1);
				elements.set(elements.size() - 1, String.format("%s[%s]", last, token));
			} else {
				elements.add(token);
			}
		}
		return elements.stream().collect(Collectors.joining("."));
	}
}
