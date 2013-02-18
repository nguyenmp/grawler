package com.nguyenmp.grawler.utils;

import org.ccil.cowan.tagsoup.Parser;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.XMLReader;

import javax.xml.transform.*;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamResult;
import java.io.StringReader;
import java.io.StringWriter;

public class XMLParser {

	/**
	 * returns a document of formatted xml stuff.
	 * uses tagsoup to process through bad xhtml.
	 * @param xmlString A string that should be XML.
	 * @return A Document transformed from parsing the string which should be XML.
	 * @throws ParsingException if the xml string could not be parsed
	 */
	public static Document getDocumentFromString(String xmlString) throws ParsingException {
		Document doc;

		try {
			XMLReader reader = new Parser();
			reader.setFeature(Parser.namespacesFeature, false);
			reader.setFeature(Parser.namespacePrefixesFeature, false);

			Transformer transformer = TransformerFactory.newInstance().newTransformer();

			DOMResult result = new DOMResult();
			transformer.transform(new SAXSource(reader, new InputSource(new StringReader(xmlString))), result);


			doc = (Document) result.getNode();
		} catch (SAXNotSupportedException e) {
			e.printStackTrace();
			throw new ParsingException(e.getMessage());
		} catch (TransformerConfigurationException e) {
			e.printStackTrace();
			throw new ParsingException(e.getMessage());
		} catch (TransformerException e) {
			e.printStackTrace();
			throw new ParsingException(e.getMessage());
		} catch (SAXNotRecognizedException e) {
			e.printStackTrace();
			throw new ParsingException(e.getMessage());
		}

		return doc;
	}

	/**
	 * returns the first child with the specified id.  null if no child have id
	 * @param root the root element
	 * @param attributeName the name of the attribute
	 * @param attributeValue the value of the attribute
	 * @return the Element that has the specified value for the specified attribute.
	 */
	public static Element getChildFromAttribute(Element root, String attributeName, String attributeValue) {
		NodeList list = root.getChildNodes();
		for (int i = 0; i < list.getLength(); i++) {
			Node child = list.item(i);

			if (child.getNodeType() == Node.ELEMENT_NODE) {
				Element element = (Element) child;
				if (element.getAttribute(attributeName).equals(attributeValue)) {
					return element;
				}
			}
		}

		return null;
	}

	/**
	 * returns the first child with the specified name.  null if no child have id
	 * @param element the element to search
	 * @param name the name of the child to find
	 * @return the Node of the Child with the specified name
	 */
	public static Node getChildFromName(Element element, String name) {
		NodeList list = element.getChildNodes();

		if (list != null && name != null) {
//			System.out.println("Searching for " + name + " in " + element);
			for (int i = 0; i < list.getLength(); i++) {
				Node child = list.item(i);
//				System.out.println(child.getNodeName());
				if (child.getNodeName().equals(name))
					return child;
			}
		}
		return null;
	}

	/**
	 * Converts the node and it's children to an XML String
	 * @param node The node to convert.
	 * @return The XML String
	 * @throws javax.xml.transform.TransformerException when it is not possible to create a Transformer
	 * instance or if an unrecoverable error occurs during the course of the transformation
	 */
	static String nodeToString(Node node) throws TransformerException {
		// Set up the output transformer
		TransformerFactory transfac = TransformerFactory.newInstance();
		Transformer trans = transfac.newTransformer();
		trans.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
		trans.setOutputProperty(OutputKeys.INDENT, "yes");

		// Print the DOM node

		StringWriter sw = new StringWriter();
		StreamResult result = new StreamResult(sw);
		DOMSource source = new DOMSource(node);
		trans.transform(source, result);

		//return as string
		String xmlString = sw.toString();
		return xmlString;
	}
}