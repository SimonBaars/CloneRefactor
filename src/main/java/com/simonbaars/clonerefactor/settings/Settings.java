package com.simonbaars.clonerefactor.settings;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import com.simonbaars.clonerefactor.refactoring.enums.RefactoringStrategy;

public class Settings {
	
	private static final String CLONEREFACTOR_PROPERTIES = "clonerefactor.properties";
	private static final Settings settings = new Settings();

	// General
	private CloneType cloneType;
	private Scope scope;
	
	// Clone detection thresholds
	private int minAmountOfLines;
	private int minAmountOfTokens;
	private int minAmountOfNodes;
	private int minCloneClassSize;
	
	// Type-specific settings
	private double type2VariabilityPercentage;
	private double type3GapSize;
	
	// Transform the AST and refactor the code
	private RefactoringStrategy refactoringStrategy;
	
	private boolean printProgress;

	private Settings(Builder builder) {
		this.cloneType = builder.cloneType;
		this.scope = builder.scope;
		this.minAmountOfLines = builder.minAmountOfLines;
		this.minAmountOfTokens = builder.minAmountOfTokens;
		this.minAmountOfNodes = builder.minAmountOfNodes;
		this.minCloneClassSize = builder.minCloneClassSize;
		this.type2VariabilityPercentage = builder.type2VariabilityPercentage;
		this.type3GapSize = builder.type3GapSize;
		this.refactoringStrategy = builder.refactoringStrategy;
		this.printProgress = builder.printProgress;
	}
	
	private Settings() {
		try (InputStream input = Settings.class.getClassLoader().getResourceAsStream(CLONEREFACTOR_PROPERTIES)) {
            Properties prop = new Properties();
            prop.load(input);

            cloneType = CloneType.valueOf(prop.getProperty("clone_type"));
            scope = Scope.valueOf(prop.getProperty("scope"));
            minAmountOfLines = Integer.parseInt(prop.getProperty("min_lines"));
            minAmountOfTokens = Integer.parseInt(prop.getProperty("min_tokens"));
            minAmountOfNodes = Integer.parseInt(prop.getProperty("min_statements"));
            setMinCloneClassSize(Integer.parseInt(prop.getProperty("min_clone_class_size")));
            type2VariabilityPercentage = percentageStringToDouble(prop.getProperty("max_type2_variability_percentage"));
            type3GapSize = percentageStringToDouble(prop.getProperty("max_type3_gap_size"));
            refactoringStrategy = RefactoringStrategy.valueOf(prop.getProperty("refactoring_strategy"));
            printProgress = prop.getProperty("print_progress").equalsIgnoreCase("true");
		} catch (IOException ex) {
            throw new IllegalStateException("Could not get settings! Please check for the existence of the properties file!");
        }
	}
	
	private float percentageStringToDouble(String property) {
		return Float.parseFloat(property.endsWith("%") ? property.substring(0, property.length()-1) : property);
	}

	@Override
	public String toString() {
		return String.format(
				"Settings [cloneType=%s, scope=%s, minAmountOfLines=%s, minAmountOfTokens=%s, minAmountOfNodes=%s, minCloneClassSize=%s, type2VariabilityPercentage=%s, type3GapSize=%s, refactoringStrategy=%s]",
				cloneType, scope, minAmountOfLines, minAmountOfTokens, minAmountOfNodes, minCloneClassSize, type2VariabilityPercentage, type3GapSize, refactoringStrategy);
	}
	
	/*
	 * Getters and setters
	 */
	public static Settings get() {
		return settings;
	}

	public CloneType getCloneType() {
		return cloneType;
	}

	public int getMinAmountOfLines() {
		return minAmountOfLines;
	}

	public int getMinAmountOfTokens() {
		return minAmountOfTokens;
	}

	public int getMinAmountOfNodes() {
		return minAmountOfNodes;
	}

	public boolean useLiteratureTypeDefinitions() {
		return !getCloneType().isRefactoringOriented();
	}

	public double getType2VariabilityPercentage() {
		return type2VariabilityPercentage;
	}
	
	public double getType3GapSize() {
		return type3GapSize;
	}

	public void setCloneType(CloneType cloneType) {
		this.cloneType = cloneType;
	}

	public void setMinAmountOfLines(int minAmountOfLines) {
		this.minAmountOfLines = minAmountOfLines;
	}

	public void setMinAmountOfTokens(int minAmountOfTokens) {
		this.minAmountOfTokens = minAmountOfTokens;
	}

	public void setMinAmountOfNodes(int minAmountOfNodes) {
		this.minAmountOfNodes = minAmountOfNodes;
	}

	public void setType2VariabilityPercentage(double type2VariabilityPercentage) {
		this.type2VariabilityPercentage = type2VariabilityPercentage;
	}

	public void setType3GapSize(double type3GapSize) {
		this.type3GapSize = type3GapSize;
	}

	public Scope getScope() {
		return scope;
	}

	public void setScope(Scope scope) {
		this.scope = scope;
	}

	public RefactoringStrategy getRefactoringStrategy() {
		return refactoringStrategy;
	}

	public void setRefactoringStrategy(RefactoringStrategy refactoringStrategy) {
		this.refactoringStrategy = refactoringStrategy;
	}

	public int getMinCloneClassSize() {
		return minCloneClassSize;
	}

	public void setMinCloneClassSize(int minCloneClassSize) {
		this.minCloneClassSize = minCloneClassSize;
	}

	public boolean isPrintProgress() {
		return printProgress;
	}

	public void setPrintProgress(boolean printProgress) {
		this.printProgress = printProgress;
	}

	/**
	 * Creates builder to build {@link Settings}.
	 * @return created builder
	 */
	public static Builder builder() {
		return new Builder();
	}

	/**
	 * Builder to build {@link Settings}.
	 */
	public static final class Builder {
		private CloneType cloneType;
		private Scope scope;
		private int minAmountOfLines;
		private int minAmountOfTokens;
		private int minAmountOfNodes;
		private int minCloneClassSize;
		private double type2VariabilityPercentage;
		private double type3GapSize;
		private RefactoringStrategy refactoringStrategy;
		private boolean printProgress;

		private Builder() {
			Settings s = settings;
			this.cloneType = s.cloneType;
			this.scope = s.scope;
			this.minAmountOfLines = s.minAmountOfLines;
			this.minAmountOfTokens = s.minAmountOfTokens;
			this.minAmountOfNodes = s.minAmountOfNodes;
			this.minCloneClassSize = s.minCloneClassSize;
			this.type2VariabilityPercentage = s.type2VariabilityPercentage;
			this.type3GapSize = s.type3GapSize;
			this.refactoringStrategy = s.refactoringStrategy;
			this.printProgress = s.printProgress;
		}

		public Builder withCloneType(CloneType cloneType) {
			this.cloneType = cloneType;
			return this;
		}

		public Builder withScope(Scope scope) {
			this.scope = scope;
			return this;
		}

		public Builder withMinAmountOfLines(int minAmountOfLines) {
			this.minAmountOfLines = minAmountOfLines;
			return this;
		}

		public Builder withMinAmountOfTokens(int minAmountOfTokens) {
			this.minAmountOfTokens = minAmountOfTokens;
			return this;
		}

		public Builder withMinAmountOfNodes(int minAmountOfNodes) {
			this.minAmountOfNodes = minAmountOfNodes;
			return this;
		}

		public Builder withMinCloneClassSize(int minCloneClassSize) {
			this.minCloneClassSize = minCloneClassSize;
			return this;
		}

		public Builder withType2VariabilityPercentage(double type2VariabilityPercentage) {
			this.type2VariabilityPercentage = type2VariabilityPercentage;
			return this;
		}

		public Builder withType3GapSize(double type3GapSize) {
			this.type3GapSize = type3GapSize;
			return this;
		}

		public Builder withRefactoringStrategy(RefactoringStrategy refactoringStrategy) {
			this.refactoringStrategy = refactoringStrategy;
			return this;
		}

		public Builder withPrintProgress(boolean printProgress) {
			this.printProgress = printProgress;
			return this;
		}

		public Settings build() {
			return new Settings(this);
		}
	}
}
