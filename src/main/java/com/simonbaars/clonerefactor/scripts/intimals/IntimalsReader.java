package com.simonbaars.clonerefactor.scripts.intimals;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.github.javaparser.Position;
import com.github.javaparser.Range;
import com.simonbaars.clonerefactor.detection.model.Sequence;
import com.simonbaars.clonerefactor.detection.model.location.Location;
import com.simonbaars.clonerefactor.scripts.intimals.model.PatternLocation;
import com.simonbaars.clonerefactor.scripts.intimals.model.matches.Match;
import com.simonbaars.clonerefactor.scripts.intimals.model.matches.Pattern;
import com.simonbaars.clonerefactor.scripts.intimals.model.sourcefiles.SourceFile;
import com.simonbaars.clonerefactor.scripts.intimals.model.sourcefiles.SourceFiles;

public class IntimalsReader {
	private static int clusterNum = 1;
	private static String clusterLoc = "/Users/sbaars/Documents/Kim/jhotdraw-4-folds/cluster_"+clusterNum+"/";
	private static String matchesLoc = "/Users/sbaars/Documents/Kim/jhotdraw_source/output-4-fold/cluster_"+clusterNum+"-5-matches.xml";
	
	public static void main(String[] args) throws SAXException, IOException, ParserConfigurationException {
		List<Sequence> s = new IntimalsReader().loadIntimalsClones();
		System.out.println(Arrays.toString(s.toArray()));
	}

	private List<Sequence> loadIntimalsClones() throws ParserConfigurationException, SAXException, IOException {
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(new File(matchesLoc));
		doc.getDocumentElement().normalize();
		List<Pattern> patterns = parseMatches(doc.getElementsByTagName("match"));
		SourceFiles files = loadSourceCode(dBuilder, patterns);
		return createCloneClasses(patterns, files);
	}

	private List<Sequence> createCloneClasses(List<Pattern> patterns, SourceFiles files) {
		List<Sequence> clones = new ArrayList<>();
		for(Pattern pattern : patterns) {
			Sequence s = new Sequence();
			for(Match m : pattern.getMatches())
				s.add(createLocation(files, m));
			clones.add(s);
		}
		return clones;
	}

	private Location createLocation(SourceFiles files, Match m) {
		List<Location> locations = new ArrayList<>();
		Location rootLoc = null;
		for(com.simonbaars.clonerefactor.scripts.intimals.model.matches.Node n : m.getNodes()) {
			Location loc = files.get(m.getFile()).getLoc(n.getId());
			if(n.isRoot()) {
				rootLoc = new PatternLocation(loc);
			} else {
				locations.add(loc);
			}
		}
		return rootLoc;
	}

	private SourceFiles loadSourceCode(DocumentBuilder dBuilder, List<Pattern> patterns) throws SAXException, IOException {
		SourceFiles sourceFiles = new SourceFiles();
		for(Pattern pattern : patterns) {
			for(Match match : pattern.getMatches()) {
				if(!sourceFiles.getSourceFiles().containsKey(match.getFile())) {
					SourceFile file = new SourceFile();
					Document doc = dBuilder.parse(new File(clusterLoc+match.getXMLFile()));
					collectLocations(match.getFilePath(clusterLoc), file, doc.getDocumentElement());
					sourceFiles.getSourceFiles().put(match.getFile(), file);
				}
			}
		}
		return sourceFiles;
	}
	
	public void collectLocations(Path filePath, SourceFile file, Node node) {
	    NodeList nodeList = node.getChildNodes();
	    for (int i = 0; i < nodeList.getLength(); i++) {
	        Node currentNode = nodeList.item(i);
	        if (currentNode.getNodeType() == Node.ELEMENT_NODE) {
	            Element e = (Element)currentNode;
	            String id = e.getAttribute("ID");
	            if(id!=null && !id.isEmpty()) {
	            	file.getSourceLocations().put(Integer.parseInt(id), new Location(filePath, new Range(new Position(Integer.parseInt(e.getAttribute("LineNr")), Integer.parseInt(e.getAttribute("ColNr"))), new Position(Integer.parseInt(e.getAttribute("EndLineNr")), Integer.parseInt(e.getAttribute("EndColNr"))))));
	            }
	            collectLocations(filePath, file, currentNode);
	        }
	    }
	}


	private List<Pattern> parseMatches(NodeList matches) {
		List<Pattern> patterns = new ArrayList<>();
		for(int i = 0; i<matches.getLength(); i++) {
			Node node = matches.item(i);
			if(node.getNodeType() == Node.ELEMENT_NODE) {
				Element element = (Element)node;
				Match match = new Match(element.getAttribute("FullName"), parseNodes(element.getElementsByTagName("node")));
				int id = Integer.parseInt(element.getAttribute("PatternID"));
				Pattern pattern = patterns.stream().filter(e -> e.getId() == id).findAny().orElse(null);
				if(pattern == null) {
					pattern = new Pattern(id);
					patterns.add(pattern);
				}
				pattern.getMatches().add(match);
			}
		}
		return patterns;
	}

	private List<com.simonbaars.clonerefactor.scripts.intimals.model.matches.Node> parseNodes(NodeList nodes) {
		List<com.simonbaars.clonerefactor.scripts.intimals.model.matches.Node> parsedNodes = new ArrayList<>();
		for(int i = 0; i<nodes.getLength(); i++) {
			Node node = nodes.item(i);
			if(node.getNodeType() == Node.ELEMENT_NODE) {
				Element element = (Element)node;
				parsedNodes.add(new com.simonbaars.clonerefactor.scripts.intimals.model.matches.Node(Integer.parseInt(element.getAttribute("ID")), element.getAttribute("Name").equals("?_root")));
			}
		}
		return parsedNodes;
	}
}
