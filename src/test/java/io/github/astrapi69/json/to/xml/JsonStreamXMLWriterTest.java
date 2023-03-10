/**
 * The MIT License
 *
 * Copyright (C) 2022 Asterios Raptis
 *
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package io.github.astrapi69.json.to.xml;

import static org.junit.jupiter.api.Assertions.*;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.nio.charset.StandardCharsets;

import javax.xml.stream.XMLStreamException;

import org.junit.jupiter.api.Test;

/**
 *
 * @author Martynas Jusevičius <martynas@atomgraph.com>
 */
public class JsonStreamXMLWriterTest
{

	@Test
	public void testJsonKeyWithInvalidXMLChar() throws IOException, XMLStreamException
	{
		String jsonString = "{ \"a\\fb\": \"c\" }"; // form feed in the key (invalid in XML 1.0,
													// valid in XML 1.1)
		InputStream json = new ByteArrayInputStream(jsonString.getBytes(StandardCharsets.UTF_8));
		ByteArrayOutputStream xml = new ByteArrayOutputStream();

		try (
			Reader reader = new BufferedReader(new InputStreamReader(json, StandardCharsets.UTF_8)))
		{
			new JsonStreamXMLWriter(reader,
				new BufferedWriter(new OutputStreamWriter(xml, StandardCharsets.UTF_8)))
					.convert(StandardCharsets.UTF_8.name(), "1.0");
			String xmlString = xml.toString(StandardCharsets.UTF_8.name());
			assertTrue(xmlString.contains(JsonStreamXMLWriter.REPLACEMENT_CHAR)); //
		}
	}

	@Test
	public void testJsonValueWithInvalidXMLChar() throws IOException, XMLStreamException
	{
		String jsonString = "{ \"a\": \"b\\fc\" }"; // form feed in the value (invalid in XML 1.0,
													// valid in XML 1.1)
		InputStream json = new ByteArrayInputStream(jsonString.getBytes(StandardCharsets.UTF_8));
		ByteArrayOutputStream xml = new ByteArrayOutputStream();

		try (
			Reader reader = new BufferedReader(new InputStreamReader(json, StandardCharsets.UTF_8)))
		{
			new JsonStreamXMLWriter(reader,
				new BufferedWriter(new OutputStreamWriter(xml, StandardCharsets.UTF_8)))
					.convert(StandardCharsets.UTF_8.name(), "1.0");
			String xmlString = xml.toString(StandardCharsets.UTF_8.name());
			assertTrue(xmlString.contains(JsonStreamXMLWriter.REPLACEMENT_CHAR)); // form feed has
																					// been replaced
		}
	}

}
