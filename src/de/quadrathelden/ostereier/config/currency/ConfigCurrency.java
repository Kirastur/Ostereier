package de.quadrathelden.ostereier.config.currency;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import de.quadrathelden.ostereier.exception.OstereierException;
import de.quadrathelden.ostereier.text.OsterText;
import de.quadrathelden.ostereier.text.TextManager;
import de.quadrathelden.ostereier.tools.Message;

public class ConfigCurrency {

	public static final String CURRENCY_FILENAME = "currencies.yml";

	public static final String SECTION_SINGULAR = "singular";
	public static final String SECTION_PLURAL = "plural";

	protected List<OsterText> singular = new ArrayList<>();
	protected List<OsterText> plural = new ArrayList<>();

	public ConfigCurrency() {
	}

	public ConfigCurrency(List<OsterText> singular, List<OsterText> plural) {
		this.singular.addAll(singular);
		this.plural.addAll(plural);
	}

	public ConfigCurrency(Plugin plugin) throws OstereierException {
		saveDefaultCurrencyConfig(plugin);
		loadCurrency(plugin);
	}

	protected void loadSegment(ConfigurationSection configurationSection, List<OsterText> osterText) {
		for (String myKey : configurationSection.getKeys(false)) {
			String myText = configurationSection.getString(myKey);
			TextManager.addText(osterText, myKey, myText);
		}
	}

	protected void saveDefaultCurrencyConfig(Plugin plugin) {
		if (!new File(plugin.getDataFolder(), CURRENCY_FILENAME).exists()) {
			plugin.saveResource(CURRENCY_FILENAME, false);
		}
	}

	protected void loadCurrency(Plugin plugin) throws OstereierException {
		File currencyFile = new File(plugin.getDataFolder(), CURRENCY_FILENAME);
		if (!currencyFile.exists()) {
			throw new OstereierException(null, Message.CONFIG_CURRENCY_FILE_MISSING, CURRENCY_FILENAME);
		}
		FileConfiguration fileConfiguration = YamlConfiguration.loadConfiguration(currencyFile);

		loadSegment(fileConfiguration.getConfigurationSection(SECTION_SINGULAR), singular);
		loadSegment(fileConfiguration.getConfigurationSection(SECTION_PLURAL), plural);
	}

	public String findCurrency(int amount, String currencyName, CommandSender sender) {
		if (amount == 1) {
			String s = TextManager.findText(singular, currencyName, sender);
			if (!s.isEmpty()) {
				return s;
			}
		}
		String s = TextManager.findText(plural, currencyName, sender);
		if (!s.isEmpty()) {
			return s;
		}
		return currencyName;
	}

}
