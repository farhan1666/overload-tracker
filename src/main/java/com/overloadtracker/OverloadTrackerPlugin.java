package com.overloadtracker;

import javax.inject.Inject;


import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.VarbitChanged;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.infobox.InfoBoxManager;
import org.apache.commons.lang3.ArrayUtils;

import java.awt.*;

@Slf4j
@PluginDescriptor(
	name = "Overload Tracker",
		description = "Counts the number of ticks since your overload last gave you stats so you can time your brews perfectly."
)
public class OverloadTrackerPlugin extends Plugin
{
	@Inject
	private Client client;

	@Inject
	private ItemManager itemManager;

	@Inject
	private InfoBoxManager infoBoxManager;

	private static int OVERLOAD_REFRESH_RATE = 25;
	private static final int NMZ_MAP_REGION_ID = 9033;

	private int currentTick;

	private int lastRaidVarb;

	private OverloadTracker counter;


	@Override
	protected void startUp() throws Exception
	{
		currentTick = -1;
	}

	@Override
	protected void shutDown() throws Exception
	{
		infoBoxManager.removeIf(t -> t instanceof OverloadTracker);
		lastRaidVarb = -1;
	}

	@Subscribe
	public void onVarbitChanged(VarbitChanged event)
	{
		int raidVarb = client.getVar(Varbits.IN_RAID);
		if (lastRaidVarb != raidVarb)
		{
			removeOverloadTracker();
			lastRaidVarb = raidVarb;
		}
	}

	@Subscribe
	public void onGameStateChanged(GameStateChanged gameStateChanged)
	{
		if (gameStateChanged.getGameState() == GameState.LOADING && !isInNightmareZone())
		{
			removeOverloadTracker();
		}
	}

	@Subscribe
	public void onChatMessage(ChatMessage event) {
		if (event.getMessage().startsWith("You drink some of your") && event.getMessage().contains("overload"))
		{
			currentTick = OVERLOAD_REFRESH_RATE + 1;
			createOverloadTracker();
		}
		if (event.getMessage().contains("The effects of overload have worn off"))
		{
			removeOverloadTracker();
		}
	}

	@Subscribe
	public void onGameTick(GameTick event)
	{
		if (currentTick != -1)
		{
			if (currentTick > 1)
			{
				currentTick--;
			}
			else
			{
				currentTick = OVERLOAD_REFRESH_RATE;
				counter.setTextColor(Color.WHITE);
			}
			if (currentTick < 4 && currentTick > 1)
			{
				counter.setTextColor(Color.YELLOW);
			}
			if (currentTick == 1)
			{
				counter.setImage(itemManager.getImage(6684));
				counter.setTextColor(Color.RED);
			}
		}
		if (counter != null)
		{
			counter.setCount(currentTick);
		}
	}

	private void createOverloadTracker()
	{
		if (counter == null)
		{
			counter = new OverloadTracker(itemManager.getImage(ItemID.OVERLOAD_4_20996), this, currentTick);
			counter.setTooltip("Overload tick");
			infoBoxManager.addInfoBox(counter);
		}
		else
		{
			counter.setCount(OVERLOAD_REFRESH_RATE + 1);
			counter.setTextColor(Color.WHITE);
		}
	}

	private void removeOverloadTracker()
	{
		if (counter == null)
		{
			return;
		}

		infoBoxManager.removeInfoBox(counter);
		counter = null;
	}

	private boolean isInNightmareZone()
	{
		return client.getLocalPlayer() != null && client.getLocalPlayer().getWorldLocation().getPlane() > 0 && ArrayUtils.contains(client.getMapRegions(), NMZ_MAP_REGION_ID);
	}


}
