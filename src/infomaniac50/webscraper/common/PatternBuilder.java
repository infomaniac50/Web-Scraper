package infomaniac50.webscraper.common;

import java.util.regex.Pattern;

public class PatternBuilder {
	private static final String patternAll = ".*";
	
	public static Pattern buildDefaultPattern(String expression)
	{
		StringBuilder builder = new StringBuilder();
		
		builder.append(patternAll);
		builder.append(expression);
		builder.append(patternAll);
		
		return buildPattern(builder);
	}
	
	
	public static Pattern buildBetweenPattern(String startWord, String stopWord)
	{		
		StringBuilder builder = new StringBuilder();
		
		builder.append(patternAll);
		builder.append(startWord);
		builder.append("(");
		builder.append(patternAll);
		builder.append(")");
		builder.append(stopWord);
		builder.append(patternAll);
		
		return buildPattern(builder);		
	}
	
	public static Pattern buildPattern(StringBuilder expression)
	{
		return Pattern.compile(expression.toString());
	}
}
