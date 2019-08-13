package com.simonbaars.clonerefactor.refactoring;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.Status;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;

import com.simonbaars.clonerefactor.context.context.interfaces.RequiresNodeContext;
import com.simonbaars.clonerefactor.settings.Settings;

public class GitChangeCommitter implements RequiresNodeContext {
	private static final String CLONEREFACTOR_BRANCH_NAME = "CloneRefactor";
	private static final String CLONEREFACTOR_AUTHOR_NAME = "CloneRefactor";
	private static final String CLONEREFACTOR_AUTHOR_EMAIL = "clonerefactor@gmail.com";
	private final Repository repo;
	private final Git git;
	
	public GitChangeCommitter() {
		this.repo = null;
		this.git = null;
	}
	
	public GitChangeCommitter(Path path) {
		Optional<Repository> opt = createRepo(path);
		if(!opt.isPresent()) {
			Settings.get().setRefactoringStrategy(Settings.get().getRefactoringStrategy().revertToNonGit());
		}
		repo = opt.orElse(null);
		if(opt.isPresent()) {
			git = new Git(repo);
			try {
				if(!repo.getFullBranch().endsWith(CLONEREFACTOR_BRANCH_NAME))
					git.checkout().setCreateBranch(true).setName(CLONEREFACTOR_BRANCH_NAME).call();
			} catch (GitAPIException | IOException e) {
				e.printStackTrace();
			}
		} else git = null;
	}
	
	public Optional<Repository> createRepo(Path path) {
		FileRepositoryBuilder repositoryBuilder = new FileRepositoryBuilder().setMustExist( true ).setGitDir(new File(path+File.separator+".git"));
		try {
			return Optional.of(repositoryBuilder.build());
		} catch (IOException e) {
			return createNewRepo(path);
		}
	}

	private Optional<Repository> createNewRepo(Path path) {
		try {
			Repository rep = FileRepositoryBuilder.create(new File(path.toString()+File.separator+".git"));
			rep.create();
			try(Git git = new Git(rep)){
				git.add().addFilepattern(".").call();
				git.commit().setAuthor(CLONEREFACTOR_AUTHOR_NAME, CLONEREFACTOR_AUTHOR_EMAIL).setMessage("Initial commit").call();
				return Optional.of(rep);
			}
		} catch (IOException | GitAPIException e1) {
			e1.printStackTrace();
			return Optional.empty();
		} 
	}
	
	public void commitFormat(String className) {
		commit("Formatted "+className);
	}
	
	public void commit(String message) {
		try {
			Status status = git.status().call();
			if(status.hasUncommittedChanges()) {
				git.add().addFilepattern(".").call();
				git.commit().setAuthor(CLONEREFACTOR_AUTHOR_NAME, CLONEREFACTOR_AUTHOR_EMAIL).setMessage(message).call();
			}
		} catch (GitAPIException e) {
			e.printStackTrace();
		}
	}
	
	public boolean doCommit() {
		return repo != null && git != null;
	}
}
