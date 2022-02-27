package de.quadrathelden.ostereier.text;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class OsterText {

	private final String name;
	protected Map<String, String> textMap = new HashMap<>();

	public OsterText(String name) {
		this.name = name;
	}

	public OsterText(String name, String defaultText) {
		this.name = name;
		textMap.put("", defaultText);
	}

	public OsterText(String name, Map<String, String> values) {
		this.name = name;
		textMap.putAll(values);
	}

	public String getName() {
		return name;
	}

	public String getText() {
		String s = textMap.get("");
		if (s != null) {
			return s;
		} else {
			return "";
		}
	}

	public String getText(String locale) {
		if (locale != null) {

			// 1st try: take the full language (e.g. "de_de")
			if (locale.length() >= 5) {
				String s = textMap.get(locale.substring(0, 5));
				if (s != null) {
					return s;
				}
			}

			// 2nd try: take the group language (e.g. "de")
			if (locale.length() >= 2) {
				String s = textMap.get(locale.substring(0, 2));
				if (s != null) {
					return s;
				}
			}
		}

		// No localized string found, return default
		return getText();
	}

	public String getText(CommandSender sender) {
		if (sender instanceof Player player) {
			String locale = player.getLocale();
			return getText(locale);
		} else {
			return getText();
		}
	}

	void addText(String locale, String text) {
		textMap.put(locale, text);
	}

}
