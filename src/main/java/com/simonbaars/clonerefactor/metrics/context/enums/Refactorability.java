package com.simonbaars.clonerefactor.metrics.context.enums;

public enum Refactorability {
	CANBEEXTRACTED("Can Be Extracted"), //Can be extracted
	OVERLAPS("Overlap In Clone Class"),
	NOSTATEMENT("Top-level Node is not a Statement"),
	NOEXTRACTIONBYCONTENTTYPE("Is Not A Partial Method"), //When the clone is not a partial method
	PARTIALBLOCK("Spans Part of a Block"), //When the clone spans part of a block
	COMPLEXCONTROLFLOW("Complex Control Flow"), //When the clone spans break, continue or return statements. However, exceptions apply:
						// - All flows end in return (however not fully implemented)
						// - The for loop that is being `continue` or `break` is included
    ;
	
	private final String name;
	
	private Refactorability(String name) {
		this.name = name;
	}
	
	@Override
	public String toString() {
		return name;
	}
}