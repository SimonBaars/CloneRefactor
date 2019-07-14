package com.simonbaars.clonerefactor.metrics.context;

import static com.simonbaars.clonerefactor.metrics.context.CloneLocation.LocationType.CLASSLEVEL;
import static com.simonbaars.clonerefactor.metrics.context.CloneLocation.LocationType.CONSTRUCTORLEVEL;
import static com.simonbaars.clonerefactor.metrics.context.CloneLocation.LocationType.ENUMLEVEL;
import static com.simonbaars.clonerefactor.metrics.context.CloneLocation.LocationType.INTERFACELEVEL;
import static com.simonbaars.clonerefactor.metrics.context.CloneLocation.LocationType.METHODLEVEL;
import static com.simonbaars.clonerefactor.metrics.context.CloneLocation.LocationType.OUTSIDE;

import java.util.ArrayList;
import java.util.List;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.simonbaars.clonerefactor.metrics.context.CloneLocation.LocationType;
import com.simonbaars.clonerefactor.metrics.context.interfaces.MetricEnum;
import com.simonbaars.clonerefactor.model.Sequence;
import com.simonbaars.clonerefactor.model.location.Location;

public class CloneLocation implements MetricEnum<LocationType> {
	public enum LocationType{
		METHODLEVEL,
		CONSTRUCTORLEVEL,
		CLASSLEVEL,
		INTERFACELEVEL,
		ENUMLEVEL,
		OUTSIDE;
	}

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
		if(getConstructor(node)!=null && (!(node instanceof ConstructorDeclaration) || i == 0))
			return CONSTRUCTORLEVEL;
		ClassOrInterfaceDeclaration class1 = getClass(node);
		if(class1 != null) {
			if(class1.isInterface())
				return INTERFACELEVEL;
			else return CLASSLEVEL;
		}
		if(getEnum(node)!=null)
			return ENUMLEVEL;
		return OUTSIDE;
	}
	
}
