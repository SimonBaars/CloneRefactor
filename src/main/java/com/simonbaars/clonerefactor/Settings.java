package com.simonbaars.clonerefactor;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import com.simonbaars.clonerefactor.compare.CloneType;

public class Settings {
	
	private static final String CLONEREFACTOR_PROPERTIES = "clonerefactor.properties";
	
	private static final Settings settings = new Settings();

	private final CloneType cloneType;
	
	// Clone detection thresholds
	private final int minAmountOfLines;
	private final int minAmountOfTokens;
	private final int minAmountOfNodes;
	
	private final boolean compareByTokens;
	
	private final double type2VariabilityPercentage;
	private final int type3DifferentBlocks;
	
	private Settings() {
		try (InputStream input = Settings.class.getClassLoader().getResourceAsStream(CLONEREFACTOR_PROPERTIES)) {
            Properties prop = new Properties();
            prop.load(input);

            cloneType = CloneType.valueOf(prop.getProperty("clone_type"));
            minAmountOfLines = Integer.parseInt(prop.getProperty("min_lines"));
            minAmountOfTokens = Integer.parseInt(prop.getProperty("min_tokens"));
            minAmountOfNodes = Integer.parseInt(prop.getProperty("min_statements"));
            compareByTokens = Boolean.getBoolean(prop.getProperty("token_comparison"));
            type2VariabilityPercentage = convertToType2VariabilityPercentage(prop.getProperty("max_type2_variability_percentage"));
            type3DifferentBlocks = Integer.parseInt(prop.getProperty("max_type3_different_paths"));
        } catch (IOException ex) {
            throw new RuntimeException("Could not get settings! Please check for the existence of the properties file!");
        }
	}
	
	public static Settings get() {
		return settings;
	}

	private double convertToType2VariabilityPercentage(String property) {
		return Double.parseDouble(property.endsWith("%") ? property.substring(0, property.length()-1) : property);
	}

	public static String getClonerefactorProperties() {
		return CLONEREFACTOR_PROPERTIES;
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

	public boolean isCompareByTokens() {
		return compareByTokens;
	}

	public double getType2VariabilityPercentage() {
		return type2VariabilityPercentage;
	}

	public int getType3DifferentBlocks() {
		return type3DifferentBlocks;
	}
	
	
}
