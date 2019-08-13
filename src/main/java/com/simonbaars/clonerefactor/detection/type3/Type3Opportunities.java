package com.simonbaars.clonerefactor.detection.type3;

import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import com.github.javaparser.ast.Node;
import com.simonbaars.clonerefactor.ast.compare.CompareOutOfScope;
import com.simonbaars.clonerefactor.datatype.map.ListMap;
import com.simonbaars.clonerefactor.detection.interfaces.CalculatesPercentages;
import com.simonbaars.clonerefactor.detection.model.Sequence;
import com.simonbaars.clonerefactor.detection.model.location.Location;
import com.simonbaars.clonerefactor.detection.model.location.LocationContents;
import com.simonbaars.clonerefactor.settings.Settings;

public class Type3Opportunities implements Type3Calculation, CalculatesPercentages {
	private ListMap<Integer, FileLocations> opportunities = new ListMap<>();
	
	public void determineType3Opportunities(List<Sequence> clones) {
		outerloop: for(int i = 0; i<clones.size(); i++) {
			FileLocations fl = new FileLocations(clones.get(i));
			if(opportunities.containsKey(fl.hashCode())) {
				 List<FileLocations> otherFls = opportunities.get(fl.hashCode());
				 for(int j = 0; j<otherFls.size(); j++) {
					 FileLocations loc = otherFls.get(j);
					 if(isType3(fl, loc)) {
						 clones.remove(fl.getSeq());
						 clones.remove(loc.getSeq());
						 otherFls.remove(loc);
						 clones.add(merge(fl.getLocs(), loc.getLocs()));
						 i--;
						 continue outerloop;
					 }
				 }
			} 
			opportunities.addTo(fl.hashCode(), fl);
		}
	}
	
	private Sequence merge(List<Location> fl1, List<Location> fl2) {
		Sequence seq = new Sequence();
		for(int i = 0; i<fl1.size(); i++)
			seq.add(calculateDiff(fl1.get(i), fl2.get(i)) == 0 ? fl1.get(i).mergeWith(fl2.get(i)) : new Type3Location(fl1.get(i), fl2.get(i)));
		return seq;
	}

	public boolean isType3(FileLocations fl1, FileLocations fl2) {
		return IntStream.range(0, fl1.getLocs().size()).allMatch(i -> {
			Location l1 = fl1.getLocs().get(i);
			Location l2 = fl2.getLocs().get(i);
			if(l1.getFile() != l2.getFile())
				return false;
			if(l1.getRange().begin.isBefore(l2.getRange().begin)) {
				return checkType3Threshold(l1, l2);
			} else return checkType3Threshold(l2, l1);
		});
	}
	
	public boolean parentsEqual(Optional<Node> n1, Optional<Node> n2) {
		return n1.isPresent() && n2.isPresent() ? n1.get() == n2.get() : !n1.isPresent() && !n2.isPresent();
	}

	private boolean checkType3Threshold(Location l1, Location l2) {
		if(!(Settings.get().useLiteratureTypeDefinitions() || parentsEqual(l1.getLastNode().getParentNode(), l2.getFirstNode().getParentNode())))
			return false;
		int combinedSize = l1.getAmountOfNodes() + l2.getAmountOfNodes();
		LocationContents diff = calculateDiffContents(l1, l2);
		if(diff.getCompare().stream().anyMatch(e -> e instanceof CompareOutOfScope))
			return false;
		return calcPercentage(calculateDiff(diff), combinedSize) <= Settings.get().getType3GapSize();
	}
}
