package de.quadrathelden.ostereier.text;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.CommandSender;

import de.quadrathelden.ostereier.api.OstereierOrchestrator;

public class TextManager {

	protected List<OsterText> osterTexts = new ArrayList<>();

	public TextManager(OstereierOrchestrator orchestrator) {
		// Do nothing
	}

	public static String escapeUTF8(String intlString) {
		StringBuilder b = new StringBuilder();
		for (char c : intlString.toCharArray()) {
			if (c >= 128)
				b.append("\\u").append(String.format("%04X", (int) c));
			else {
				if ((c == '\\') || (c == '\"'))
					b.append('\\');
				b.append(c);
			}
		}
		return b.toString();
	}

	public static OsterText findOsterText(List<OsterText> osterTextContainer, String name) {
		for (OsterText myOsterText : osterTextContainer) {
			if (myOsterText.getName().equals(name)) {
				return myOsterText;
			}
		}
		return null;
	}

	public static String findText(List<OsterText> osterTextContainer, String name, CommandSender sender) {
		OsterText myOsterText = findOsterText(osterTextContainer, name);
		if (myOsterText != null) {
			return myOsterText.getText(sender);
		} else {
			return "";
		}
	}

	public static void addText(List<OsterText> osterTextContainer, String key, String text) {
		TextKey textKey = TextKey.from(key);
		String name = textKey.name();
		String locale = textKey.locale();
		OsterText myOsterText = findOsterText(osterTextContainer, name);
		if (myOsterText == null) {
			myOsterText = new OsterText(name);
			osterTextContainer.add(myOsterText);
		}
		myOsterText.addText(locale, text);
	}

	public OsterText findOsterText(String name) {
		return findOsterText(osterTexts, name);
	}

	public String findText(String name, CommandSender sender) {
		return findText(osterTexts, name, sender);
	}

	public void addText(String key, String text) {
		addText(osterTexts, key, text);
	}

	public void clear() {
		osterTexts.clear();
	}

}
