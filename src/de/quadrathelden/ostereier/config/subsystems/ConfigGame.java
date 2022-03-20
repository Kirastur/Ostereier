package de.quadrathelden.ostereier.config.subsystems;

import org.bukkit.configuration.ConfigurationSection;

public class ConfigGame {

	public static final String PARAM_GAME_PICKUP_SOUND_VOLUME = "pickupSoundVolume";
	public static final String PARAM_GAME_PICKUP_SOUND_PITCH = "pickupSoundPitch";
	public static final String PARAM_PROTECTED_AREA_AROUND_EGGS = "protectedAreaAroundEggs";
	public static final String PARAM_COLLECT_USING_PLAYER_LEFT_CLICK = "collectUsingPlayerLeftClick";
	public static final String PARAM_COLLECT_USING_PLAYER_RIGHT_CLICK = "collectUsingPlayerRightClick";
	public static final String PARAM_COLLECT_USING_PLAYER_MOVE_TO_EGG = "collectUsingPlayerMoveToEgg";
	public static final String PARAM_COLLECT_USING_PLAYER_PICKUP_ITEM = "collectUsingPlayerPickupItem";

	public static final double DEFAULT_GAME_PICKUP_SOUND_VOLUME = 1.0;
	public static final double DEFAULT_GAME_PICKUP_SOUND_PITCH = 0.0;
	public static final int DEFAULT_PROTECTED_AREA_AROUND_EGGS = 2;
	public static final boolean DEFAULT_COLLECT_USING_PLAYER_LEFT_CLICK = true;
	public static final boolean DEFAULT_COLLECT_USING_PLAYER_RIGHT_CLICK = true;
	public static final boolean DEFAULT_COLLECT_USING_PLAYER_MOVE_TO_EGG = false;
	public static final boolean DEFAULT_COLLECT_USING_PLAYER_PICKUP_ITEM = false;

	protected double soundVolume = DEFAULT_GAME_PICKUP_SOUND_VOLUME;
	protected double soundPitch = DEFAULT_GAME_PICKUP_SOUND_PITCH;
	protected int protectedAreaAroundEggs = DEFAULT_PROTECTED_AREA_AROUND_EGGS;
	protected boolean collectUsingPlayerLeftClick = DEFAULT_COLLECT_USING_PLAYER_LEFT_CLICK;
	protected boolean collectUsingPlayerRightClick = DEFAULT_COLLECT_USING_PLAYER_RIGHT_CLICK;
	protected boolean collectUsingPlayerMoveToEgg = DEFAULT_COLLECT_USING_PLAYER_MOVE_TO_EGG;
	protected boolean collectUsingPlayerPickupItem = DEFAULT_COLLECT_USING_PLAYER_PICKUP_ITEM;

	public ConfigGame() {
	}

	public ConfigGame(double soundVolume, double soundPitch, int protectedAreaAroundEggs,
			boolean collectUsingPlayerLeftClick, boolean collectUsingPlayerRightClick,
			boolean collectUsingPlayerMoveToEgg, boolean collectUsingPlayerPickupItem) {
		this.soundVolume = soundVolume;
		this.soundPitch = soundPitch;
		this.protectedAreaAroundEggs = protectedAreaAroundEggs;
		this.collectUsingPlayerLeftClick = collectUsingPlayerLeftClick;
		this.collectUsingPlayerRightClick = collectUsingPlayerRightClick;
		this.collectUsingPlayerMoveToEgg = collectUsingPlayerMoveToEgg;
		this.collectUsingPlayerPickupItem = collectUsingPlayerPickupItem;
	}

	public ConfigGame(ConfigurationSection configurationSection) {
		soundVolume = configurationSection.getDouble(PARAM_GAME_PICKUP_SOUND_VOLUME, DEFAULT_GAME_PICKUP_SOUND_VOLUME);
		soundPitch = configurationSection.getDouble(PARAM_GAME_PICKUP_SOUND_PITCH, DEFAULT_GAME_PICKUP_SOUND_PITCH);
		protectedAreaAroundEggs = configurationSection.getInt(PARAM_PROTECTED_AREA_AROUND_EGGS,
				DEFAULT_PROTECTED_AREA_AROUND_EGGS);
		collectUsingPlayerLeftClick = configurationSection.getBoolean(PARAM_COLLECT_USING_PLAYER_LEFT_CLICK,
				DEFAULT_COLLECT_USING_PLAYER_LEFT_CLICK);
		collectUsingPlayerRightClick = configurationSection.getBoolean(PARAM_COLLECT_USING_PLAYER_RIGHT_CLICK,
				DEFAULT_COLLECT_USING_PLAYER_RIGHT_CLICK);
		collectUsingPlayerMoveToEgg = configurationSection.getBoolean(PARAM_COLLECT_USING_PLAYER_MOVE_TO_EGG,
				DEFAULT_COLLECT_USING_PLAYER_MOVE_TO_EGG);
		collectUsingPlayerPickupItem = configurationSection.getBoolean(PARAM_COLLECT_USING_PLAYER_PICKUP_ITEM,
				DEFAULT_COLLECT_USING_PLAYER_PICKUP_ITEM);
	}

	public double getSoundVolume() {
		return soundVolume;
	}

	public double getSoundPitch() {
		return soundPitch;
	}

	public int getProtectedAreaAroundEggs() {
		return protectedAreaAroundEggs;
	}

	public boolean isCollectUsingPlayerLeftClick() {
		return collectUsingPlayerLeftClick;
	}

	public boolean isCollectUsingPlayerRightClick() {
		return collectUsingPlayerRightClick;
	}

	public boolean isCollectUsingPlayerMoveToEgg() {
		return collectUsingPlayerMoveToEgg;
	}

	public boolean isCollectUsingPlayerPickupItem() {
		return collectUsingPlayerPickupItem;
	}

}
