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

import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;

import javax.json.Json;
import javax.json.stream.JsonParser;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.Result;

/**
 * Converts JSON stream to XML stream. Uses XML representation of JSON defined in XSLT 3.0.
 * 
 * @see <a href="https://www.w3.org/TR/xslt-30/#json">22 Processing JSON Data</a>
 * @author Martynas Jusevičius &lt;martynas@atomgraph.com&gt;
 */
public class JsonStreamXMLWriter
{

	public static final String XPATH_FUNCTIONS_NS = "http://www.w3.org/2005/xpath-functions";
	public static final String REPLACEMENT_CHAR = "\uFFFD";
	private static final XMLOutputFactory XOF = XMLOutputFactory.newInstance();

	static
	{
		XOF.setProperty(XMLOutputFactory.IS_REPAIRING_NAMESPACES, true);
	}

	private final JsonParser parser;
	private final XMLStreamWriter writer;

	public JsonStreamXMLWriter(Reader reader, Writer stream) throws XMLStreamException
	{
		this(Json.createParser(reader), getXMLOutputFactory().createXMLStreamWriter(stream));
	}

	public JsonStreamXMLWriter(Reader reader, OutputStream stream) throws XMLStreamException
	{
		this(Json.createParser(reader), getXMLOutputFactory().createXMLStreamWriter(stream));
	}

	public JsonStreamXMLWriter(Reader reader, OutputStream stream, String encoding)
		throws XMLStreamException
	{
		this(Json.createParser(reader),
			getXMLOutputFactory().createXMLStreamWriter(stream, encoding));
	}

	public JsonStreamXMLWriter(Reader reader, Result result) throws XMLStreamException
	{
		this(Json.createParser(reader), getXMLOutputFactory().createXMLStreamWriter(result));
	}

	public JsonStreamXMLWriter(Reader reader, XMLStreamWriter writer)
	{
		this(Json.createParser(reader), writer);
	}

	public JsonStreamXMLWriter(InputStream is, Writer stream) throws XMLStreamException
	{
		this(Json.createParser(is), getXMLOutputFactory().createXMLStreamWriter(stream));
	}

	public JsonStreamXMLWriter(InputStream is, OutputStream stream) throws XMLStreamException
	{
		this(Json.createParser(is), getXMLOutputFactory().createXMLStreamWriter(stream));
	}

	public JsonStreamXMLWriter(InputStream is, OutputStream stream, String encoding)
		throws XMLStreamException
	{
		this(Json.createParser(is), getXMLOutputFactory().createXMLStreamWriter(stream, encoding));
	}

	public JsonStreamXMLWriter(InputStream is, Result result) throws XMLStreamException
	{
		this(Json.createParser(is), getXMLOutputFactory().createXMLStreamWriter(result));
	}

	public JsonStreamXMLWriter(InputStream is, XMLStreamWriter writer)
	{
		this(Json.createParser(is), writer);
	}

	public JsonStreamXMLWriter(JsonParser parser, Writer stream) throws XMLStreamException
	{
		this(parser, getXMLOutputFactory().createXMLStreamWriter(stream));
	}

	public JsonStreamXMLWriter(JsonParser parser, OutputStream stream) throws XMLStreamException
	{
		this(parser, getXMLOutputFactory().createXMLStreamWriter(stream));
	}

	public JsonStreamXMLWriter(JsonParser parser, OutputStream stream, String encoding)
		throws XMLStreamException
	{
		this(parser, getXMLOutputFactory().createXMLStreamWriter(stream, encoding));
	}

	public JsonStreamXMLWriter(JsonParser parser, Result result) throws XMLStreamException
	{
		this(parser, getXMLOutputFactory().createXMLStreamWriter(result));
	}

	public JsonStreamXMLWriter(JsonParser parser, XMLStreamWriter writer)
	{
		this.parser = parser;
		this.writer = writer;
	}

	public void convert(String encoding, String version) throws XMLStreamException
	{
		convert(getWriter(), encoding, version);
	}

	public void convert(XMLStreamWriter writer, String encoding, String version)
		throws XMLStreamException
	{
		convert(getParser(), writer, encoding, version);
	}

	public static void convert(JsonParser parser, XMLStreamWriter writer, String encoding,
		String version) throws XMLStreamException
	{
		writer.writeStartDocument(encoding, version);
		writer.setDefaultNamespace(XPATH_FUNCTIONS_NS);

		write(parser, writer);

		writer.writeEndDocument();
		writer.flush();

		parser.close();
	}

	public static void write(JsonParser parser, XMLStreamWriter writer) throws XMLStreamException
	{
		String keyName = null;
		while (parser.hasNext())
		{
			JsonParser.Event event = parser.next();

			switch (event)
			{
				case START_ARRAY :
					writer.writeStartElement(XPATH_FUNCTIONS_NS, "array");
					if (keyName != null)
					{
						writer.writeAttribute("key", keyName);
						keyName = null;
					}
					break;
				case END_ARRAY :
					writer.writeEndElement();
					break;
				case START_OBJECT :
					writer.writeStartElement(XPATH_FUNCTIONS_NS, "map");
					if (keyName != null)
					{
						writer.writeAttribute("key", keyName);
						keyName = null;
					}
					break;
				case END_OBJECT :
					writer.writeEndElement();
					break;
				case VALUE_FALSE :
					writer.writeStartElement(XPATH_FUNCTIONS_NS, "boolean");
					if (keyName != null)
					{
						writer.writeAttribute("key", keyName);
						keyName = null;
					}
					writer.writeCharacters("false");
					writer.writeEndElement();
					break;
				case VALUE_TRUE :
					writer.writeStartElement(XPATH_FUNCTIONS_NS, "boolean");
					if (keyName != null)
					{
						writer.writeAttribute("key", keyName);
						keyName = null;
					}
					writer.writeCharacters("true");
					writer.writeEndElement();
					break;
				case KEY_NAME :
					keyName = replaceInvalidXMLChars(parser.getString(), REPLACEMENT_CHAR);
					break;
				case VALUE_STRING :
					writer.writeStartElement(XPATH_FUNCTIONS_NS, "string");
					if (keyName != null)
					{
						writer.writeAttribute("key", keyName);
						keyName = null;
					}
					writer.writeCharacters(
						replaceInvalidXMLChars(parser.getString(), REPLACEMENT_CHAR));
					writer.writeEndElement();
					break;
				case VALUE_NUMBER :
					writer.writeStartElement(XPATH_FUNCTIONS_NS, "number");
					if (keyName != null)
					{
						writer.writeAttribute("key", keyName);
						keyName = null;
					}
					writer.writeCharacters(
						replaceInvalidXMLChars(parser.getString(), REPLACEMENT_CHAR));
					writer.writeEndElement();
					break;
				case VALUE_NULL :
					writer.writeEmptyElement(XPATH_FUNCTIONS_NS, "null");
					if (keyName != null)
					{
						writer.writeAttribute("key", keyName);
						keyName = null;
					}
					break;
			}

			writer.flush();
		}
	}

	public static String replaceInvalidXMLChars(String text, String replacement)
	{
		if (null == text || text.isEmpty())
			return text;

		final int len = text.length();
		char current = 0;
		int codePoint = 0;
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < len; i++)
		{
			current = text.charAt(i);
			boolean surrogate = false;
			if (Character.isHighSurrogate(current) && i + 1 < len
				&& Character.isLowSurrogate(text.charAt(i + 1)))
			{
				surrogate = true;
				codePoint = text.codePointAt(i++);
			}
			else
				codePoint = current;

			if ((codePoint == 0x9) || (codePoint == 0xA) || (codePoint == 0xD)
				|| ((codePoint >= 0x20) && (codePoint <= 0xD7FF))
				|| ((codePoint >= 0xE000) && (codePoint <= 0xFFFD))
				|| ((codePoint >= 0x10000) && (codePoint <= 0x10FFFF)))
			{
				sb.append(current);
				if (surrogate)
					sb.append(text.charAt(i));
			}
			else
				sb.append(replacement);
		}

		return sb.toString();
	}

	protected JsonParser getParser()
	{
		return parser;
	}

	protected XMLStreamWriter getWriter()
	{
		return writer;
	}

	protected static XMLOutputFactory getXMLOutputFactory()
	{
		return XOF;
	}

}
