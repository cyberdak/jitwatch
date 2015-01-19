/*
 * Copyright (c) 2013, 2014 Chris Newland.
 * Licensed under https://github.com/AdoptOpenJDK/jitwatch/blob/master/LICENSE-BSD
 * Instructions: https://github.com/AdoptOpenJDK/jitwatch/wiki
 */
package org.adoptopenjdk.jitwatch.launch;

import java.io.File;
import java.io.IOException;

import org.adoptopenjdk.jitwatch.core.HotSpotLogParser;
import org.adoptopenjdk.jitwatch.core.IJITListener;
import org.adoptopenjdk.jitwatch.core.ILogParseErrorListener;
import org.adoptopenjdk.jitwatch.core.JITWatchConfig;
import org.adoptopenjdk.jitwatch.model.JITEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LaunchHeadless implements IJITListener, ILogParseErrorListener
{
	private boolean showErrors;
	private static final Logger logger = LoggerFactory.getLogger(LaunchHeadless.class);

	public LaunchHeadless(String filename, boolean showErrors) throws IOException
	{
		this.showErrors = showErrors;

		JITWatchConfig config = new JITWatchConfig();

		HotSpotLogParser parser = new HotSpotLogParser(this);
		parser.setConfig(config);

		parser.processLogFile(new File(filename), this);
	}

	@Override
	public void handleLogEntry(String entry)
	{
		logger.info(entry);
	}

	@Override
	public void handleErrorEntry(String entry)
	{
		if (showErrors)
		{
			logger.error(entry);
		}
	}

	@Override
	public void handleJITEvent(JITEvent event)
	{
		logger.info(event.toString());
	}

	public static void main(String[] args) throws IOException
	{
		if (args.length < 1)
		{
			System.err.println("Usage: LaunchHeadless <hotspot log file> [logErrors (true|false)]");
			System.exit(-1);
		}

		final boolean showErrors = showErrors(args);

		new LaunchHeadless(args[0], showErrors);
	}

	private static boolean showErrors(String[] args)
	{
		return args.length == 2 && Boolean.valueOf(args[1]);
	}

	@Override
	public void handleReadComplete()
	{
		logger.info("Finished reading log file.");
	}

	@Override
	public void handleReadStart()
	{

	}

	@Override
	public void handleError(String title, String body)
	{
		logger.info("Parse Error: {}.{}", title, body);
	}
}