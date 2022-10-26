package com.bird.runalert;

import com.google.inject.Provides;
import javax.inject.Inject;
import javax.sound.sampled.*;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.GameStateChanged;
import net.runelite.client.Notifier;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.task.Schedule;

import java.awt.*;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.temporal.ChronoUnit;


@Slf4j
@PluginDescriptor(
	name = "Bird Run Alert"
)

public class BirdRunAlertPlugin extends Plugin
{
	@Inject
	private Client client;

	@Inject
	private BirdRunAlertConfig config;

	@Inject
	private  Notifier notifier;

	@Override
	protected void startUp() throws Exception
	{
		log.info("Bird Run Alert started!");
	}

	@Override
	protected void shutDown() throws Exception
	{
		log.info("Bird Run Alert stopped!");
	}

	private Clip clip = null;

	@Subscribe
	public void onGameStateChanged(GameStateChanged gameStateChanged)
	{
	}

	@Subscribe
	public void onChatMessage(ChatMessage message)
	{
		if ((message.getMessage().toLowerCase()).contains("bird houses are ready to be dismantled") && message.getType().toString().equals("CONSOLE")) {
			InputStream fileStream = new BufferedInputStream(BirdRunAlertPlugin.class.getClassLoader().getResourceAsStream("bird_run_alert.wav"));
			try (AudioInputStream sound = AudioSystem.getAudioInputStream(fileStream))
			{
				clip = AudioSystem.getClip();
				FloatControl control = (FloatControl) clip.getControl(FloatControl.Type.VOLUME);
				float newVolume = ((config.volume() / control.getMaximum()) * control.getMinimum()) + control.getMinimum();
				control.setValue(newVolume);
				clip.open(sound);
				clip.setFramePosition(clip.getFrameLength());
				clip.loop(1);
			}
			catch (UnsupportedAudioFileException | IOException | LineUnavailableException e)
			{
				log.warn("Unable to load bird run alert sound", e);
			}
		}
	}


	@Provides
	BirdRunAlertConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(BirdRunAlertConfig.class);
	}
}
