package com.simonbaars.clonerefactor.settings;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import com.simonbaars.clonerefactor.refactoring.RefactoringStrategy;

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
	
	// Comparing by tokens only
	private boolean useLiteratureTypeDefinitions;
	
	// Type-specific settings
	private double type2VariabilityPercentage;
	private double type3GapSize;
	
	// Transform the AST and refactor the code
	private RefactoringStrategy refactoringStrategy;
	
	private Settings() {
		try (InputStream input = Settings.class.getClassLoader().getResourceAsStream(CLONEREFACTOR_PROPERTIES)) {
            Properties prop = new Properties();
            prop.load(input);

            cloneType = CloneType.valueOf(prop.getProperty("clone_type"));
            scope = Scope.valueOf(prop.getProperty("scope"));
            minAmountOfLines = Integer.parseInt(prop.getProperty("min_lines"));
            minAmountOfTokens = Integer.parseInt(prop.getProperty("min_tokens"));
            minAmountOfNodes = Integer.parseInt(prop.getProperty("min_statements"));
            useLiteratureTypeDefinitions = prop.getProperty("use_literature_type_definitions").equals("true");
            type2VariabilityPercentage = percentageStringToDouble(prop.getProperty("max_type2_variability_percentage"));
            type3GapSize = percentageStringToDouble(prop.getProperty("max_type3_gap_size"));
            refactoringStrategy = RefactoringStrategy.valueOf(prop.getProperty("refactoring_strategy"));
        } catch (IOException ex) {
            throw new IllegalStateException("Could not get settings! Please check for the existence of the properties file!");
        }
	}
	
	public static Settings get() {
		return settings;
	}

	private float percentageStringToDouble(String property) {
		return Float.parseFloat(property.endsWith("%") ? property.substring(0, property.length()-1) : property);
	}

	@Override
	public String toString() {
		return String.format(
				"Settings [cloneType=%s, scope=%s, minAmountOfLines=%s, minAmountOfTokens=%s, minAmountOfNodes=%s, useLiteratureTypeDefinitions=%s, type2VariabilityPercentage=%s, type3GapSize=%s, refactoringStrategy=%s]",
				cloneType, scope, minAmountOfLines, minAmountOfTokens, minAmountOfNodes, useLiteratureTypeDefinitions,
				type2VariabilityPercentage, type3GapSize, refactoringStrategy);
	}
	
	/*
	 * Getters and setters
	 */

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
		return useLiteratureTypeDefinitions;
	}

	public double getType2VariabilityPercentage() {
		return type2VariabilityPercentage;
	}
	
	public double getType3GapSize() {
		return type3GapSize;
	}

	public boolean isUseLiteratureTypeDefinitions() {
		return useLiteratureTypeDefinitions;
	}

	public void setUseLiteratureTypeDefinitions(boolean useLiteratureTypeDefinitions) {
		this.useLiteratureTypeDefinitions = useLiteratureTypeDefinitions;
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

	public static Settings getSettings() {
		return settings;
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
}
