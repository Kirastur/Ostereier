package de.quadrathelden.ostereier.config.subsystem;

import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;

import de.quadrathelden.ostereier.text.OsterText;
import de.quadrathelden.ostereier.text.TextManager;

public class ConfigEditor {

	public static final String MESSAGE_STATUSBAR_READWRITE = "statusbarReadWrite";
	public static final String MESSAGE_STATUSBAR_READONLY = "statusbarReadOnly";

	public static final String PARAM_SWAP_MOUSE_BUTTONS_FOR_EGGS = "swapMouseButtonsForEggs";
	public static final boolean DEFAULT_SWAP_MOUSE_BUTTONS_FOR_EGGS = true;
	public static final String DEFAULT_STATUSBAR_READWRITE = "§aEasterEgg editor is active (%s)";
	public static final String DEFAULT_STATUSBAR_READONLY = "§4EasterEgg editor is active - read only";

	protected OsterText statusbarReadWriteText = new OsterText(MESSAGE_STATUSBAR_READWRITE,
			DEFAULT_STATUSBAR_READWRITE);
	protected OsterText statusbarReadOnlyText = new OsterText(MESSAGE_STATUSBAR_READONLY, DEFAULT_STATUSBAR_READONLY);
	protected boolean swapMouseButtonsForEggs = DEFAULT_SWAP_MOUSE_BUTTONS_FOR_EGGS;

	public ConfigEditor() {
	}

	public ConfigEditor(boolean swapMouseButtonsForEggs, OsterText statusbarReadWriteText,
			OsterText statusbarReadOnlyText) {
		this.swapMouseButtonsForEggs = swapMouseButtonsForEggs;
		this.statusbarReadWriteText = statusbarReadWriteText;
		this.statusbarReadOnlyText = statusbarReadOnlyText;
	}

	public ConfigEditor(ConfigurationSection configurationSection, TextManager textManager) {
		swapMouseButtonsForEggs = configurationSection.getBoolean(PARAM_SWAP_MOUSE_BUTTONS_FOR_EGGS,
				DEFAULT_SWAP_MOUSE_BUTTONS_FOR_EGGS);
		OsterText newStatusbarReadWriteText = textManager.findOsterText(MESSAGE_STATUSBAR_READWRITE);
		if (newStatusbarReadWriteText != null) {
			statusbarReadWriteText = newStatusbarReadWriteText;
		}
		OsterText newStatusbarReadOnlyText = textManager.findOsterText(MESSAGE_STATUSBAR_READONLY);
		if (newStatusbarReadOnlyText != null) {
			statusbarReadOnlyText = newStatusbarReadOnlyText;
		}
	}

	public boolean isSwapMouseButtonsForEggs() {
		return swapMouseButtonsForEggs;
	}

	public String getStatusbarReadWriteText(CommandSender sender) {
		return statusbarReadWriteText.getText(sender);
	}

	public String getStatusbarReadOnlyText(CommandSender sender) {
		return statusbarReadOnlyText.getText(sender);
	}

}
