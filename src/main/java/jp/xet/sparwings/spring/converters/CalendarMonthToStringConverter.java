/*
 * Copyright 2015 Miyamoto Daisuke.
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
package jp.xet.sparwings.spring.converters;

import java.text.SimpleDateFormat;

import jp.xet.baseunits.time.CalendarMonth;

import com.google.common.base.Preconditions;

import org.springframework.core.convert.converter.Converter;

/**
 * {@link Converter} implementation calss to convert from {@link String} to {@link CalendarMonth}.
 * 
 * @since #version#
 * @author daisuke
 */
public class CalendarMonthToStringConverter implements Converter<CalendarMonth, String> {
	
	private static final String DEFAULT_PATTERN = "yyyy-MM";
	
	private String pattern;
	
	
	/**
	 * Create instance.
	 */
	public CalendarMonthToStringConverter() {
		this(DEFAULT_PATTERN);
	}
	
	/**
	 * Create instance with format
	 * 
	 * @param pattern see {@link SimpleDateFormat}
	 * @throws NullPointerException if argument is {@code null}
	 */
	public CalendarMonthToStringConverter(String pattern) {
		Preconditions.checkNotNull(pattern);
		this.pattern = pattern;
	}
	
	@Override
	public String convert(CalendarMonth source) {
		return source == null ? null : source.toString(pattern);
	}
}
