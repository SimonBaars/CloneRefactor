public class Clone2 {
	public String toCode() throws SocketNullException
	{
		TranslatorBlock translatorBlock = this.getRequiredTranslatorBlockAtSocket(0);
		if (translatorBlock instanceof NumberBlock)
		{
			String number;
			number = translatorBlock.toCode();
			String setupCode = "pinMode( " + number + " , INPUT);";
			translator.addSetupCommand(setupCode);
			String ret = "digitalRead( ";
			ret = ret + number;
			ret = ret + ")";
			return codePrefix + ret + codeSuffix;
		}
		else
		{
			translator.addDefinitionCommand(ARDUBLOCK_DIGITAL_READ_DEFINE);
			String ret = "__ardublockDigitalRead(";
			
			ret = ret + translatorBlock.toCode();
			ret = ret + ")";
			return codePrefix + ret + codeSuffix;
		}
		
	}
}