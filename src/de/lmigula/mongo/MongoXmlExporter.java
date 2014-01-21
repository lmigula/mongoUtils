package de.lmigula.mongo;

import java.util.Iterator;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.CDATASection;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.mongodb.BasicDBList;
import com.mongodb.DBObject;

public class MongoXmlExporter {

	private Document doc = null;

	private void appendToNode(Element node, String key, Object obj) {
		Element subElement = doc.createElement(key);

		if (obj instanceof BasicDBList) {
			BasicDBList list = (BasicDBList) obj;
			for (int i = 0; i < list.size(); i++) {
				Object listObj = list.get(i);
				appendToNode(subElement, "listEntry", listObj);
			}
		} else if (obj instanceof DBObject) {
			DBObject dbObj = (DBObject) obj;
			Set<String> keySet = dbObj.keySet();
			if (keySet != null) {
				Iterator<String> keyIt = keySet.iterator();
				while (keyIt.hasNext()) {
					String dbObjkey = keyIt.next();
					Object tmpObj = dbObj.get(dbObjkey);
					appendToNode(subElement, dbObjkey, tmpObj);
				}
			}
		} else if (obj instanceof String) {
			CDATASection cdata = doc.createCDATASection(obj.toString());

			subElement.appendChild(cdata);
		} else {
			String foo = String.valueOf(obj);
			subElement.appendChild(doc.createTextNode(foo));
		}
		node.appendChild(subElement);
	}

	public Document createXML(DBObject dbObj) {
		DocumentBuilderFactory docFactory = DocumentBuilderFactory
				.newInstance();
		DocumentBuilder docBuilder = null;
		try {
			docBuilder = docFactory.newDocumentBuilder();
			doc = docBuilder.newDocument();
			Element rootElement = doc.createElement("root");

			Element dbObjNode = doc.createElement("dbOject");
			rootElement.appendChild(dbObjNode);
			Set<String> keySet = dbObj.keySet();
			if (keySet != null) {
				Iterator<String> keyIt = keySet.iterator();
				while (keyIt.hasNext()) {
					String key = keyIt.next();
					Object tmpObj = dbObj.get(key);
					appendToNode(dbObjNode, key, tmpObj);
				}
			}
			doc.appendChild(rootElement);

		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return doc;
		// root elements

	}

}
