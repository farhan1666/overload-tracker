package com.overloadtracker;

import net.runelite.client.plugins.Plugin;
import net.runelite.client.ui.overlay.infobox.Counter;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.nio.Buffer;

class OverloadTracker extends Counter
{
   OverloadTracker(BufferedImage img, Plugin plugin, int amount)
    {
        super(img, plugin, amount);
    }

    private Color textColor;

    private BufferedImage image;

    @Override
    public Color getTextColor() {
        return this.textColor;
    }

    public void setTextColor(Color color) {
        this.textColor = color;
    }

}
