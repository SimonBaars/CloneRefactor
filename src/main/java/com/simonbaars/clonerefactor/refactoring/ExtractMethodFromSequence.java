package com.simonbaars.clonerefactor.refactoring;

import java.util.List;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.stmt.Statement;
import com.simonbaars.clonerefactor.ast.interfaces.RequiresNodeOperations;
import com.simonbaars.clonerefactor.metrics.enums.CloneRefactorability.Refactorability;
import com.simonbaars.clonerefactor.metrics.enums.CloneRelation.RelationType;
import com.simonbaars.clonerefactor.metrics.enums.RequiresNodeContext;
import com.simonbaars.clonerefactor.model.Sequence;

public class ExtractMethodFromSequence implements RequiresNodeContext, RequiresNodeOperations {
	public void tryToExtractMethod(Sequence s) {
		Refactorability ref = s.getRefactorability();
		if(ref == Refactorability.CANBEEXTRACTED) {
			RelationType relation = s.getRelationType();
			MethodDeclaration decl = new MethodDeclaration();
			s.getAny().getContents().getNodes().forEach(node -> decl.getBody().get().addStatement((Statement)node));
			if(relation == RelationType.SAMECLASS || relation == RelationType.SAMEMETHOD) {
				ClassOrInterfaceDeclaration cd = getClass(s.getAny().getContents().getNodes().get(0));
				cd.getMembers().add(decl);
			}
			List<Node> lowestNodes = lowestNodes(s.getAny().getContents().getNodes());
			lowestNodes.forEach(n -> lowestNodes.get(0).getParentNode().get().remove(n));
		}
	}
}
