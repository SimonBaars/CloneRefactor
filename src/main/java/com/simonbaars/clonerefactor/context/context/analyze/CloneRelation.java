package com.simonbaars.clonerefactor.context.context.analyze;

import static com.simonbaars.clonerefactor.context.context.enums.RelationType.ANCESTOR;
import static com.simonbaars.clonerefactor.context.context.enums.RelationType.COMMONHIERARCHY;
import static com.simonbaars.clonerefactor.context.context.enums.RelationType.EXTERNALANCESTOR;
import static com.simonbaars.clonerefactor.context.context.enums.RelationType.EXTERNALSUPERCLASS;
import static com.simonbaars.clonerefactor.context.context.enums.RelationType.FIRSTCOUSIN;
import static com.simonbaars.clonerefactor.context.context.enums.RelationType.NODIRECTSUPERCLASS;
import static com.simonbaars.clonerefactor.context.context.enums.RelationType.NOINDIRECTSUPERCLASS;
import static com.simonbaars.clonerefactor.context.context.enums.RelationType.SAMECLASS;
import static com.simonbaars.clonerefactor.context.context.enums.RelationType.SAMEINTERFACE;
import static com.simonbaars.clonerefactor.context.context.enums.RelationType.SAMEMETHOD;
import static com.simonbaars.clonerefactor.context.context.enums.RelationType.SIBLING;
import static com.simonbaars.clonerefactor.context.context.enums.RelationType.SUPERCLASS;
import static com.simonbaars.clonerefactor.context.context.enums.RelationType.UNRELATED;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.simonbaars.clonerefactor.ast.ASTHolder;
import com.simonbaars.clonerefactor.context.context.interfaces.DeterminesMetric;
import com.simonbaars.clonerefactor.context.context.relation.SeekClassHierarchy;
import com.simonbaars.clonerefactor.context.context.relation.SeekInterfaceHierarchy;
import com.simonbaars.clonerefactor.context.model.ComparingClasses;
import com.simonbaars.clonerefactor.context.model.Relation;
import com.simonbaars.clonerefactor.detection.model.Sequence;

public class CloneRelation implements DeterminesMetric<Relation>, SeekClassHierarchy, SeekInterfaceHierarchy { 
	
	private static final String JAVA_OBJECT_CLASS_NAME = "Object";
	private final Map<String, ClassOrInterfaceDeclaration> classes;
	
	public CloneRelation() {
		this.classes = ASTHolder.getClasses();
	}
	
	public Relation getLocation(Node n1, Node n2) {
		Optional<ClassOrInterfaceDeclaration> class1 = getClass(n1);
		Optional<ClassOrInterfaceDeclaration> class2 = getClass(n2);
		
		if(!class1.isPresent() || !class2.isPresent())
			return new Relation(UNRELATED);
		
		ComparingClasses cc = new ComparingClasses(class1.get(), class2.get());
		ComparingClasses rev = cc.reverse();
		return findRelation(n1, n2, cc, rev);
	}

	private Relation findRelation(Node n1, Node n2, ComparingClasses cc, ComparingClasses rev) {
		final Relation relation = new Relation();
		relation.setRelationIfNotYetDetermined(SAMEMETHOD, () -> isMethod(cc, n1, n2));
		relation.setRelationIfNotYetDetermined(SAMECLASS, () -> isSameClass(cc));
		relation.setRelationIfNotYetDetermined(SUPERCLASS, () -> isSuperClass(cc));
		relation.setRelationIfNotYetDetermined(SUPERCLASS, () -> isSuperClass(rev));
		relation.setRelationIfNotYetDetermined(SIBLING, () -> isSibling(cc));
		relation.setRelationIfNotYetDetermined(ANCESTOR, () -> isAncestor(cc));
		relation.setRelationIfNotYetDetermined(ANCESTOR, () -> isAncestor(rev));
		relation.setRelationIfNotYetDetermined(FIRSTCOUSIN, () -> isFirstCousin(cc));
		relation.setRelationIfNotYetDetermined(COMMONHIERARCHY, () -> sameHierarchy(classes, cc));
		relation.setRelationIfNotYetDetermined(SAMEINTERFACE, () -> sameInterface(classes, cc));
		relation.setRelationIfNotYetDetermined(NODIRECTSUPERCLASS, () -> noSuperclass(cc));
		relation.setRelationIfNotYetDetermined(NOINDIRECTSUPERCLASS, () -> noIndirectSuperclass(cc));
		relation.setRelationIfNotYetDetermined(EXTERNALSUPERCLASS, () -> hasExternalSuperclass(cc));
		relation.setRelationIfNotYetDetermined(EXTERNALANCESTOR, () -> uses(cc));
		return relation;
	}

	private Optional<ClassOrInterfaceDeclaration[]> noIndirectSuperclass(ComparingClasses cc) {
		if(cc.getClassOne().isInterface() || cc.getClassTwo().isInterface())
			return Optional.empty();
		Optional<ClassOrInterfaceDeclaration> withoutSuperclass = findWithoutSuperclass(cc.getClassOne());
		if(withoutSuperclass.isPresent()) {
			Optional<ClassOrInterfaceDeclaration> withoutSuperclass2 = findWithoutSuperclass(cc.getClassTwo());
			if(withoutSuperclass2.isPresent())
				return uses(withoutSuperclass.get(), withoutSuperclass2.get());
		}
		return Optional.empty();
	}

	private Optional<ClassOrInterfaceDeclaration> findWithoutSuperclass(ClassOrInterfaceDeclaration classDecl) {
		if(noSuperclass(classDecl))
			return Optional.of(classDecl);
		for(ClassOrInterfaceType type : classDecl.getExtendedTypes()) {
			String name = getFullyQualifiedName(type);
			if(classes.containsKey(name))
				return findWithoutSuperclass(classes.get(name));
		}
		return Optional.empty();
	}

	private Optional<ClassOrInterfaceDeclaration[]> noSuperclass(ComparingClasses cc) {
		return noSuperclass(cc.getClassOne()) && noSuperclass(cc.getClassTwo()) ? uses(cc) : Optional.empty();
	}

	private boolean noSuperclass(ClassOrInterfaceDeclaration classDecl) {
		NodeList<ClassOrInterfaceType> extendedTypes = classDecl.getExtendedTypes();
		return extendedTypes.isEmpty() || (extendedTypes.size() == 1 && extendedTypes.get(0).asString().equals(JAVA_OBJECT_CLASS_NAME));
	}

	private Optional<ClassOrInterfaceDeclaration[]> isSameClass(ComparingClasses cc) {
		if(cc.getClassOne()  == cc.getClassTwo())
			return uses(cc.getClassOne());
		return Optional.empty();
	}
	
	private Optional<ClassOrInterfaceDeclaration[]> hasExternalSuperclass(ComparingClasses cc) {
		if(!cc.hasExtendedTypes())
			return Optional.empty();
		ClassOrInterfaceType superclassC1 = cc.getClassOne().getExtendedTypes().get(0);
		ClassOrInterfaceType superclassC2 = cc.getClassTwo().getExtendedTypes().get(0);
		return !superclassC1.getNameAsString().equals(JAVA_OBJECT_CLASS_NAME) && 
			   !superclassC2.getNameAsString().equals(JAVA_OBJECT_CLASS_NAME) && 
				getFullyQualifiedName(superclassC1).equals(getFullyQualifiedName(superclassC2)) ? uses(cc) : Optional.empty();
	}
	
	private Optional<ClassOrInterfaceDeclaration[]> isSibling(ComparingClasses cc){
		return isSiblingOrCousin(cc, 1, 1);
	}
	
	private Optional<ClassOrInterfaceDeclaration[]> isFirstCousin(ComparingClasses cc){
		return isSiblingOrCousin(cc, 2, 2);
	}

	private Optional<ClassOrInterfaceDeclaration[]> isSiblingOrCousin(ComparingClasses cc, int c1GoUp, int c2GoUp) {
		ClassOrInterfaceDeclaration parent1 = goUp(cc.getClassOne(), c1GoUp);
		ClassOrInterfaceDeclaration parent2 = goUp(cc.getClassTwo(), c2GoUp);
		return parent1 == parent2 ? uses(parent1) : Optional.empty();
	}
	
	private ClassOrInterfaceDeclaration goUp(ClassOrInterfaceDeclaration classDecl, int i) {
		if(i>0 && !classDecl.getExtendedTypes().isEmpty()) {
			String fullyQualifiedName = getFullyQualifiedName(classDecl.getExtendedTypes(0));
			if(classes.containsKey(fullyQualifiedName))
				return goUp(classes.get(fullyQualifiedName), i-1);
		}
		return classDecl;
	}

	private Optional<ClassOrInterfaceDeclaration[]> isAncestor(ComparingClasses cc) {
		if(!cc.getClassOne().getExtendedTypes().isEmpty()) {
			String fullyQualifiedName = getFullyQualifiedName(cc.getClassOne().getExtendedTypes(0));
			if(!classes.containsKey(fullyQualifiedName))
				return Optional.empty();
			ComparingClasses superCC = new ComparingClasses(classes.get(fullyQualifiedName), cc.getClassTwo());
			
			Optional<ClassOrInterfaceDeclaration[]> superclass = isSuperClass(superCC);
			if(superclass.isPresent())
				return superclass;
			else return isAncestor(superCC);
		}
		return Optional.empty();
	}

	private Optional<ClassOrInterfaceDeclaration[]> isMethod(ComparingClasses cc, Node n1, Node n2) {
		Optional<MethodDeclaration> m1 = getMethod(n1);
		if(m1.isPresent()) {
			Optional<MethodDeclaration> m2 = getMethod(n2);
			if(m2.isPresent() && m1.get() == m2.get())
				return uses(cc.getClassOne());
		}
		return Optional.empty();
	}

	private Optional<ClassOrInterfaceDeclaration[]> isSuperClass(ComparingClasses cc) {
		return cc.getClassOne().getExtendedTypes().stream().filter(e -> {
			String fullyQualifiedName = getFullyQualifiedName(e);
			if(!classes.containsKey(fullyQualifiedName))
				return false;
			return getFullyQualifiedName(cc.getClassTwo()).equals(fullyQualifiedName);
		}).map(e -> new ClassOrInterfaceDeclaration[] {cc.getClassTwo()}).findAny();
	}

	@Override
	public Relation get(Sequence clone) {
		List<Relation> locations = new ArrayList<>();
		for(int j = 0; j<clone.getLocations().size(); j++) {
			for(int z = j+1; z<clone.getLocations().size(); z++) {
				locations.add(getLocation(clone.getLocations().get(j).getFirstNode(), clone.getLocations().get(z).getFirstNode()));
			}
		}
		Relation r = locations.stream().reduce((first, second) -> first.getType().compareTo(second.getType()) < 0 ? first : second).get();
		Set<ClassOrInterfaceDeclaration> intersectingClasses = locations.stream().filter(l -> l.getType().equals(r.getType())).flatMap(l -> l.getIntersectingClasses().stream()).collect(Collectors.toSet());
		return new Relation(r.getType(), new ArrayList<>(intersectingClasses));
	}
}
