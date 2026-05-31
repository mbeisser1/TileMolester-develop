/*
*
*    Copyright (C) 2003 Kent Hansen.
*
*    This file is part of Tile Molester.
*
*    Tile Molester is free software; you can redistribute it and/or modify
*    it under the terms of the GNU General Public License as published by
*    the Free Software Foundation; either version 2 of the License, or
*    (at your option) any later version.
*
*    Tile Molester is distributed in the hope that it will be useful,
*    but WITHOUT ANY WARRANTY; without even the implied warranty of
*    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
*    GNU General Public License for more details.
*
*/

package tm.utils;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Document;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class XMLParser {

	/**
	 * Parses an XML file into a DOM document.
	 * @param file file to read or parse
	 * @return parsed XML Document
	 **/
	public static Document parse(File file)
			throws SAXException, SAXParseException, ParserConfigurationException, IOException {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setValidating(true);
		DocumentBuilder builder = factory.newDocumentBuilder();
		InputStream inputStream = new FileInputStream(file);
		InputSource is = new InputSource(inputStream);
		is.setEncoding("UTF-8");
		is.setSystemId(file.toURI().toString());

		builder.setEntityResolver(createEntityResolver(file));

		builder.setErrorHandler(
				new org.xml.sax.ErrorHandler() { // ignore fatal errors (an exception is guaranteed)
					/**
					 * SAX fatal error handler (ignored).
					 * @param exception SAX parse exception
					 **/
					public void fatalError(SAXParseException exception)
							throws SAXException {
						throw exception;
					}

					// treat validation errors as fatal
					/**
					 * SAX validation error handler.
					 * @param e event object
					 **/
					public void error(SAXParseException e)
							throws SAXParseException {
						throw e;
					}

					// dump warnings too
					/**
					 * SAX warning handler.
					 * @param err SAX warning exception
					 **/
					public void warning(SAXParseException err)
							throws SAXParseException {
						TMLog.warning("XML parse warning, line " + err.getLineNumber()
								+ ", uri " + err.getSystemId() + ": " + err.getMessage(), err);
					}
				});
		return builder.parse(is);
	}

	/**
	 * Resolves external DTD paths (e.g. {@code resources/tmres.dtd}) relative to the XML file,
	 * the working directory, or the classpath.
	 **/
	private static EntityResolver createEntityResolver(File contextFile) {
		return (publicId, systemId) -> {
			try {
				InputSource resolved = resolveEntity(systemId, contextFile);
				if (resolved == null) {
					throw new SAXException("Could not find external entity: " + systemId);
				}
				return resolved;
			} catch (IOException e) {
				throw new SAXException("Could not read external entity: " + systemId, e);
			}
		};
	}

	private static InputSource resolveEntity(String systemId, File contextFile) throws IOException {
		if (systemId == null || systemId.isEmpty()) {
			return null;
		}
		String normalized = systemId.replace('\\', '/');
		int slash = normalized.lastIndexOf('/');
		String baseName = slash >= 0 ? normalized.substring(slash + 1) : normalized;

		InputSource fromFile = openIfExists(new File(normalized));
		if (fromFile != null) {
			return fromFile;
		}

		if (contextFile != null) {
			File parent = contextFile.getParentFile();
			if (parent != null) {
				fromFile = openIfExists(new File(parent, normalized));
				if (fromFile != null) {
					return fromFile;
				}
				fromFile = openIfExists(new File(parent, baseName));
				if (fromFile != null) {
					return fromFile;
				}
			}
		}

		fromFile = openIfExists(new File("resources", baseName));
		if (fromFile != null) {
			return fromFile;
		}
		fromFile = openIfExists(new File(baseName));
		if (fromFile != null) {
			return fromFile;
		}

		String resourcePath = normalized.startsWith("/") ? normalized : "/" + normalized;
		InputStream classpath = XMLParser.class.getResourceAsStream(resourcePath);
		if (classpath == null) {
			classpath = XMLParser.class.getResourceAsStream("/" + baseName);
		}
		if (classpath == null) {
			classpath = XMLParser.class.getResourceAsStream("/resources/" + baseName);
		}
		if (classpath != null) {
			InputSource src = new InputSource(classpath);
			src.setSystemId(resourcePath);
			return src;
		}

		return null;
	}

	private static InputSource openIfExists(File file) throws IOException {
		if (!file.isFile()) {
			return null;
		}
		InputSource src = new InputSource(new FileInputStream(file));
		src.setSystemId(file.toURI().toString());
		return src;
	}

	/**
	 * Extracts concatenated text content from a DOM node.
	 * @param n DOM node whose text content is extracted
	 * @return concatenated text node content
	 **/
	public static String getNodeValue(Node n) {
		String value = "";
		if (n != null) {
			NodeList children = n.getChildNodes();
			for (int j = 0; j < children.getLength(); j++) {
				Node child = children.item(j);
				if (child.getNodeType() == Node.TEXT_NODE) {
					value = value + child.getNodeValue();
				}
			}
		}
		return value;
	}

}