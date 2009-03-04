package com.googlecode.jcasockets;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class ConformanceClientCli {
	public static final String OPTION_HELP = "h";
	public static final String OPTION_EXECUTION_SECONDS = "t";
	private Options options;
	private int executionSeconds;
	private boolean helpRequested;

	public ConformanceClientCli() {
		options = createOptions();
	}

	public void parseArguments(String... args) throws ParseException {
		CommandLineParser parser = new GnuParser();
		CommandLine commandLine = parser.parse(options, args, false);
		executionSeconds = getIntegerOption(commandLine, OPTION_EXECUTION_SECONDS, 0);
		helpRequested = getBooleanOption(commandLine, OPTION_HELP);
	}

	private boolean getBooleanOption(CommandLine commandLine, String optionString) {
		return commandLine.hasOption(optionString);
	}


	private int getIntegerOption(CommandLine commandLine, String optionString, int defaultValue) throws ParseException {
		String optionValueString = getStringValue(commandLine, optionString);
		return  optionValueString == null ? defaultValue : Integer.parseInt(optionValueString);
	}

	private String getStringValue(CommandLine commandLine, String optionString) {
		String optionValue = commandLine.getOptionValue(optionString);
		return optionValue == null ? null : optionValue.trim();
	}
	public int getExecutionSeconds() {
		return executionSeconds;
	}

	private Options createOptions() {
		Options options = new Options();

		options.addOption(OPTION_HELP, "help", false,
				"Displays the Help information");

		addOptionWithArgument(options, OPTION_EXECUTION_SECONDS, "config",
				"Configuration file containing command line options",
				"path to config file");

		return options;
	}

	private void addOptionWithArgument(Options options, String shortOptionName,
			String optionName, String optionDescription, String optionArgument) {
		Option option = new Option(shortOptionName, optionName, true,
				optionDescription);
		option.setArgName(optionArgument);
		options.addOption(option);
	}

	public boolean isHelpRequested() {
		return helpRequested;
	}

}
