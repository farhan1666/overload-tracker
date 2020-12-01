package com.overloadtracker;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class OverloadTrackerPluginTest
{
	public static void main(String[] args) throws Exception
	{
		ExternalPluginManager.loadBuiltin(OverloadTrackerPlugin.class);
		RuneLite.main(args);
	}
}