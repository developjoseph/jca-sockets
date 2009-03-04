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

	public ConformanceClientCli() {
		options = createOptions();
	}

	public void parseArguments(String... args) throws ParseException {
		CommandLineParser parser = new GnuParser();
		executionSeconds = getIntegerOption(parser, args);

	}

	private int getIntegerOption(CommandLineParser parser, String... args)
			throws ParseException {
		CommandLine commandLine = parser.parse(options, args, false);
		String secondsString = commandLine
				.getOptionValue(OPTION_EXECUTION_SECONDS).trim();
		int intValue = Integer.parseInt(secondsString);
		return intValue;
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

}
