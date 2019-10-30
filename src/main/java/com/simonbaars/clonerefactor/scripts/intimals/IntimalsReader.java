package com.simonbaars.clonerefactor.scripts.intimals;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.simonbaars.clonerefactor.scripts.intimals.model.Pattern;

public class IntimalsReader {
	private static int clusterNum = 1;
	private static String clusterLoc = "/Users/sbaars/Documents/Kim/jhotdraw-4-folds/cluster_"+clusterNum+"/";
	private static String matchesLoc = "/Users/sbaars/Documents/Kim/jhotdraw_source/output-4-fold/cluster_"+clusterNum+"-5-matches.xml";
	
	public static void main(String[] args) throws SAXException, IOException, ParserConfigurationException {
		File matchesFile = new File(matchesLoc);
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(matchesFile);
		doc.getDocumentElement().normalize();
		parseMatches(doc.getElementsByTagName("matches"));
	}

	private static List<Pattern> parseMatches(NodeList matches) {
		for(int i = 0; i<matches.getLength(); i++) {
			Node node = matches.item(i);
			if(node.getNodeType() == Node.ELEMENT_NODE) {
				Element element = (Element)node;
				parseNodes(element.getElementsByTagName("node"));
			}
		}
		return null;
	}

	private static List<com.simonbaars.clonerefactor.scripts.intimals.model.Node> parseNodes(NodeList nodes) {
		List<com.simonbaars.clonerefactor.scripts.intimals.model.Node> parsedNodes = new ArrayList<>();
		for(int i = 0; i<nodes.getLength(); i++) {
			Node node = nodes.item(i);
			if(node.getNodeType() == Node.ELEMENT_NODE) {
				Element element = (Element)node;
				parsedNodes.add(new com.simonbaars.clonerefactor.scripts.intimals.model.Node(Integer.parseInt(element.getAttribute("ID")), element.getAttribute("Name").equals("?_root")));
			}
		}
		return parsedNodes;
	}
}
