package de.quadrathelden.ostereier.integrations.betonquest;

import de.quadrathelden.ostereier.text.OsterText;
import de.quadrathelden.ostereier.text.TextManager;
import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.config.Config;

public class BetonQuestHook {

	public static final String OBJECTIVE_NAME_DE = "ostereier";
	public static final String OBJECTIVE_NAME_EN = "eastereggs";

	protected final TextManager textManager;

	public BetonQuestHook(TextManager textManager) {
		this.textManager = textManager;
	}

	protected void registerObjective() {
		BetonQuest.getInstance().registerObjectives(OBJECTIVE_NAME_DE, BetonQuestOstereierObjective.class);
		BetonQuest.getInstance().registerObjectives(OBJECTIVE_NAME_EN, BetonQuestOstereierObjective.class);
	}

	protected boolean updateBetonMessage(TextManager textManager) {
		boolean hasChanged = false;
		OsterText betonText = textManager.findOsterText(BetonQuestOstereierObjective.BETONQUEST_NOTIFY_NAME);
		if (betonText == null) {
			return false;
		}
		for (String myLocale : betonText.getLocales()) {
			String myValue = betonText.getText(myLocale);
			if (myLocale.isEmpty()) {
				myLocale = "en";
			}
			if (myLocale.length() > 2) {
				myLocale = myLocale.substring(0, 2);
			}
			String messageName = String.format("messages.%s.%s", myLocale,
					BetonQuestOstereierObjective.BETONQUEST_NOTIFY_NAME);
			String messageValue = Config.getString(messageName);
			if ((messageValue == null) || messageValue.isEmpty() || !messageValue.equals(myValue)) {
				Config.setString(messageName, myValue);
				hasChanged = true;
			}
		}
		return hasChanged;
	}

	public boolean initializeIntegration() {
		registerObjective();
		return updateBetonMessage(textManager);
	}
}