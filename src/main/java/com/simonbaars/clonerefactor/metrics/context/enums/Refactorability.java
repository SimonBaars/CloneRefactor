package com.simonbaars.clonerefactor.metrics.context.enums;
public enum Refactorability{
		CANBEEXTRACTED, //Can be extracted
		NOEXTRACTIONBYCONTENTTYPE, //When the clone is not a partial method
		PARTIALBLOCK, //When the clone spans part of a block (TODO: can we make the clone smaller to not make it a partial block, or should we turn it into a type 3 clone?)
		COMPLEXCONTROLFLOW, //When the clone spans break, continue or return statements. However, exceptions apply:
							// - All flows end in return (however not fully implemented)
							// - The for loop that is being `continue` or `break` is included
	}