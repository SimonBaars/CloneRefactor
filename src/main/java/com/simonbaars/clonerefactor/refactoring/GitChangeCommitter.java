package com.simonbaars.clonerefactor.refactoring;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.simonbaars.clonerefactor.metrics.context.interfaces.RequiresNodeContext;
import com.simonbaars.clonerefactor.model.Sequence;
import com.simonbaars.clonerefactor.settings.Settings;

public class GitChangeCommitter implements RequiresNodeContext {
	private final Repository repo;
	private final Git git;
	
	public GitChangeCommitter(Path path) {
		Optional<Repository> opt = createRepo(path);
		if(!opt.isPresent()) {
			Settings.get().setRefactoringStrategy(Settings.get().getRefactoringStrategy().revertToNonGit());
		}
		repo = opt.orElse(null);
		if(opt.isPresent()) {
			git = new Git(repo);
			try {
				git.branchCreate().setName("CloneRefactor").call();
			} catch (GitAPIException e) {
				e.printStackTrace();
			}
		} else git = null;
	}
	
	public Optional<Repository> createRepo(Path path) {
		FileRepositoryBuilder repositoryBuilder = new FileRepositoryBuilder().setMustExist( true ).setGitDir(path.toFile());
		try {
			return Optional.of(repositoryBuilder.build());
		} catch (IOException e) {
			return createNewRepo(path);
		}
	}

	private Optional<Repository> createNewRepo(Path path) {
		try {
			Repository rep = FileRepositoryBuilder.create(new File(path.toString()+File.separator+"clonerefactor.git"));
			rep.create();
			return Optional.of(rep);
		} catch (IOException e1) {
			return Optional.empty();
		}
	}
	
	public void commit(Sequence s, String methodName) {
		try {
			git.add().addFilepattern(".").call();
			git.commit().setAuthor("CloneRefactor", "clonerefactor@gmail.com").setMessage("Created unified method in "+s.getRelation().getFirstClass().getNameAsString()+"\n\n"+generateDescription(s, methodName)).call();
		} catch (GitAPIException e) {
			e.printStackTrace();
		}
	}

	private String generateDescription(Sequence s, String methodName) {
		StringBuilder b = new StringBuilder("CloneRefactor refactored a clone class with "+s.size()+" clone instances. For the common code we created a new method and named this method \""+methodName+"\". These clone instances have an "+s.getRelation()+" relation with each other. ");
		if(s.getRelation().isEffectivelyUnrelated()) {
			b.append("Because there is no location we could place the generated method, as at least one clone instance is unrelated with the rest, we created a new "+whatIsIt(s.getRelation().getFirstClass())+". We named this "+whatIsIt(s.getRelation().getFirstClass()));
		} else {
			b.append("The newly created method has been placed in");
		}
		b.append(" "+s.getRelation().getFirstClass().getNameAsString()+". Each duplicated fragment has been replaced with a call to this method.");
		return b.toString();
	}

	private String whatIsIt(ClassOrInterfaceDeclaration firstClass) {
		return firstClass.isInterface() ? "interface" : "class";
	}
}
