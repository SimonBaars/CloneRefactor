package com.simonbaars.clonerefactor.refactoring;

import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.stmt.Statement;
import com.simonbaars.clonerefactor.metrics.enums.CloneRelation.RelationType;
import com.simonbaars.clonerefactor.model.Sequence;

public class ExtractMethodFromSequence {
	public void tryToExtractMethod(Sequence s) {
		RelationType relation = s.getRelationType();
		MethodDeclaration decl = new MethodDeclaration();
		s.getAny().getContents().getNodes().forEach(node -> decl.getBody().get().addStatement((Statement)node));
	}
}
