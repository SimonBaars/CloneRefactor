package com.simonbaars.clonerefactor.context.context.analyze;

import static com.simonbaars.clonerefactor.context.context.enums.LocationType.CLASSLEVEL;
import static com.simonbaars.clonerefactor.context.context.enums.LocationType.CONSTRUCTORLEVEL;
import static com.simonbaars.clonerefactor.context.context.enums.LocationType.ENUMLEVEL;
import static com.simonbaars.clonerefactor.context.context.enums.LocationType.INTERFACELEVEL;
import static com.simonbaars.clonerefactor.context.context.enums.LocationType.METHODLEVEL;
import static com.simonbaars.clonerefactor.context.context.enums.LocationType.OUTSIDE;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.simonbaars.clonerefactor.context.context.enums.LocationType;
import com.simonbaars.clonerefactor.context.context.interfaces.DeterminesMetric;
import com.simonbaars.clonerefactor.detection.model.Sequence;
import com.simonbaars.clonerefactor.detection.model.location.Location;

public class CloneLocation implements DeterminesMetric<LocationType> {
	@Override
	public LocationType get(Sequence sequence) {
		return get(sequence.getLocations().get(0));
	}
	
	public LocationType get(Location l) {
		List<LocationType> locations = new ArrayList<>();
		for(int i = 0; i<l.getContents().getNodes().size(); i++) {
			locations.add(getLocation(l.getContents().getNodes().get(i), i));
		}
		return locations.stream().sorted().reduce((first, second) -> second).get();
	}

	private LocationType getLocation(Node node, int i) {
		if(getMethod(node).isPresent() && (!(node instanceof MethodDeclaration) || i == 0))
			return METHODLEVEL;
		if(getConstructor(node).isPresent() && (!(node instanceof ConstructorDeclaration) || i == 0))
			return CONSTRUCTORLEVEL;
		Optional<ClassOrInterfaceDeclaration> class1 = getClass(node);
		if(class1.isPresent()) {
			if(class1.get().isInterface())
				return INTERFACELEVEL;
			else return CLASSLEVEL;
		}
		if(getEnum(node).isPresent())
			return ENUMLEVEL;
		return OUTSIDE;
	}
	
}
