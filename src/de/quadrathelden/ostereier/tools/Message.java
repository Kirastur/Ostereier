package de.quadrathelden.ostereier.tools;

public enum Message {

	OK("OK"),
	ERROR("ERROR"),
	JAVA_EXCEPTION("Java Exception Error"),

	ORCHESTRATOR_ALREADY_RUNNING("Can't start orchestrator, another instance is already running"),

	CONFIG_EGG_MATERIAL_MISSING("Material for egg is missing"),
	CONFIG_EGG_MATERIAL_UNKNOWN("Unknown Material"),
	CONFIG_EGG_SOUND_UNKNOWN("Unknown Sound"),
	CONFIG_EGG_ONLY_EGGS_ALLOWED("You can only use eggs as Material"),
	CONFIG_EGG_MODE_WRONG("Missing or unknown egg mode"),
	CONFIG_EGG_NOT_SOLID("You can use only solid blocks as eggs"),
	CONFIG_EGG_CURRENCY_MISSING("Reward currency is missing"),
	CONFIG_EGG_FILE_MISSING("Cannot find egg config file"),
	CONFIG_TEMPLATE_EGG_NOT_FOUND("Egg not found"),
	CONFIG_TEMPLATE_PROBABILITY_RANGE("Probability out of range for egg"),
	CONFIG_TEMPLATE_NO_EGGS("Template must contains at least one egg"),
	CONFIG_TEMPLATE_FILE_MISSING("Cannot find template config file"),
	CONFIG_SPAWNPOINT_WORLD_UNKNOWN("World not found"),
	CONFIG_SPAWNPOINT_TEMPLATE_MISSING("Template not found"),
	CONFIG_SPAWNPOINT_FILE_CREATE_ERROR("Cannot create config file"),
	CONFIG_SPAWNPOINT_FILE_MISSING("Cannot find spawnpoint config file"),
	CONFIG_SPAWNPOINT_FILE_WRITE_ERROR("Error writing to spawnpoint file"),
	CONFIG_SPAWNPOINT_DUPLICATE("Can't add spawnpoint, because there is already an egg defined for this location"),
	CONFIG_ECONOMY_PROVIDER_MISSING("EconomyProvider is missing"),
	CONFIG_ECONOMY_PROVIDER_UNKNOWN("Unknown EconomyProvider"),
	CONFIG_CURRENCY_FILE_MISSING("Cannot find currency file"),
	CONFIG_IPPS_NEGATIVE("Negative score"),
	CONFIG_IPPS_FILE_CREATE_ERROR("Cannot create playerscore file"),
	CONFIG_IPPS_FILE_MISSING("Cannot find score config file"),
	CONFIG_IPPS_FILE_WRITE_ERROR("Error writing to score file"),
	CONFIG_SHOP_MATERIAL_MISSING("Material for shop item is missing"),
	CONFIG_SHOP_MATERIAL_UNKNOWN("Unknown Material"),
	CONFIG_SHOP_PRICE_MISSING("Price for shop item is missing"),
	CONFIG_SHOP_CURRENCY_MISSING("Currency for shop item price is missing"),
	CONFIG_SHOP_ENCHANTMENT_LEVEL_TOO_HIGH("Enchantment Level above allowed maximum"),
	CONFIG_SHOP_ENCHANTMENT_NOT_ALLOWED("This type of enchantment not allowed for this material"),
	CONFIG_SHOP_AMOUNT_TOO_BIG("This material cannot carry the given Amount as ItemStack size"),
	CONFIG_SHOP_FILE_MISSING("Cannot find offer config file"),
	CONFIG_MESSAGE_FILE_MISSING("Cannot find message file"),
	CONFIG_CALENDAR_DATEFORMAT_WRONG("Can't parse DateTime for calendar"),
	CONFIG_STATISTIC_OUTPUT_PROVIDER_MISSING("StatisticOutputProvider is missing"),
	CONFIG_STATISTIC_OUTPUT_PROVIDER_UNKNOWN("Unknown StatisticOutputProvider"),
	CONFIG_STATISTIC_ILLEGAL_DATE_TIME_FORMAT("Illegal DateTime format"),
	CONFIG_STATISTIC_ILLEGAL_INTERVAL_SIZE("Illegal interval size"),

	ECONOMY_TNE_NOT_AVAIL("Error connecting to TNE"),
	ECONOMY_TNE_CURRENCY_MISSING("TNE Currency not found"),
	ECONOMY_TNE_ERROR("TNE error"),
	ECONOMY_VAULT_NOT_AVAIL("Error connecting to Vault"),
	ECONOMY_VAULT_FAILURE("Vault Economy Provider failure"),
	ECONOMY_VAULT_NOT_IMPLEMENTED("Vault reports the requested function not implemented"),
	ECONOMY_CUSTOM_NOT_AVAIL("Error connecting to Custom Economy Provider, no response from event"),

	EDITOR_NO_TEMPLATES("No templates found, cannnot start editor"),
	EDITOR_PLACE_DUPLICATE("This place is already occupied by another egg"),
	EDITOR_PLACE_EGG_BELOW("An egg cannot be placed on top of another egg"),
	EDITOR_PLACE_NOT_EMPTY("The place for the egg is not empty"),
	EDITOR_PLACE_WOULD_HOVER("An egg cannot hover"),
	EDITOR_PLACE_GROUND_NOT_SOLID("An egg can only placed on a solid block"),
	EDITOR_REMOVE_NO_EGG("There is no egg at this location"),
	EDITOR_ALREADY_ACTIVE("Editor is already active"),

	SHOP_ITEM_SIGNATURE_MISSING("The Item has no Ostereier-Signatur"),
	SHOP_PLAYER_FORBIDDEN("Not allowed to open the Ostereier-Shop"),
	SHOP_PLAYER_NOT_ENOUGH_MONEY("Not enough money to afford the item"),
	SHOP_PLAYER_NOT_ENOUGH_ITEMS("Not enough items to trade them"),
	SHOP_PLAYER_INVENTORY_FULL("Player inventory is full"),

	MODE_DISABLED("Ostereier engine is already disabled"),
	MODE_ECONOMY_PROVIDER_NOT_READY("Economy Provider is not ready"),
	MODE_GAME_RUNNING("In at least one world the game is started"),
	MODE_MULTIWORLD_SETUP_WRONG(
			"In multiworld mode you must place all eggs in one editor world. Please fix your setup"),
	MODE_MULTIWORLD_EDITOR_WRONG(
			"In multiworld mode you must place all eggs in one editor world. Please use that word for further editing"),
	MODE_GAME_ALREADY_ACTIVE("The game is already started in this world"),
	MODE_CITIZENS_INTEGRATION_FAILED("Could not find Citizens plugin"),

	API_EDITOR_NOT_ACTIVE("The easteregg editor is not active"),

	CMD_MODE_MISSING("Mode is missing"),
	CMD_MODE_UNKNOWN("Unknown mode"),
	CMD_ELEMENT_MISSING("Element is missing"),
	CMD_ELEMENT_UNKNOWN("Unknown element"),
	CMD_WORLD_MISSING("World is missing"),
	CMD_WORLD_UNKNOWN("Unknown world"),
	CMD_PLAYER_MISSING("Player is missing"),
	CMD_PLAYER_UNKNOWN("Unknown player"),
	CMD_TEMPLATE_MISSING("Template is missing"),
	CMD_TEMPLATE_UNKNOWN("Unknown template"),
	CMD_WORLD_NOT_DETECTED("The target world could not not detected. Please use /osteradmin"),
	CMD_SHOP_NOT_A_PLAYER("The shop GUI can only opened by a player"),
	CMD_ACTION_UNKNOWN("Unknown action"),
	CMD_FORBIDDEN("Missing permission"),
	CMD_TOO_MANY_PARAMETERS("Too many parameters");

	private final String messageText;

	private Message(String messageText) {
		this.messageText = messageText;
	}

	@Override
	public String toString() {
		return messageText;
	}

}
