package de.quadrathelden.ostereier.commands;

import static de.quadrathelden.ostereier.commands.ParamType.NONE;
import static de.quadrathelden.ostereier.commands.ParamType.PLAYER;
import static de.quadrathelden.ostereier.commands.ParamType.WORLDS;

public enum AdminAction implements Action {

	EDITORSTART("editorstart", WORLDS),
	EDITORSTOP("editorstop", NONE),
	GAMESTART("gamestart", WORLDS),
	GAMESTOP("gamestop", WORLDS),
	GAMEAUTO("gameauto", WORLDS),
	GAMEAUTOALL("gameautoall", NONE),
	CHECKWORLD("checkworld", WORLDS),
	SHOP("shop", PLAYER),
	SHOPNEAREST("shopnearest", NONE),
	INFO("info", NONE),
	RELOAD("reload", NONE),
	HELP("help", NONE);

	private final String command;
	private final ParamType param1;

	private AdminAction(String command, ParamType param1) {
		this.command = command;
		this.param1 = param1;
	}

	@Override
	public String getCommand() {
		return command;
	}

	@Override
	public int getParamCount() {
		if (param1 != NONE) {
			return 1;
		}
		return 0;
	}

	@Override
	public ParamType getParam(int position) {
		if (position == 1) {
			return param1;
		}
		return NONE;
	}

	@Override
	public int findPosition(ParamType param) {
		if (param == param1) {
			return 1;
		}
		return 0;
	}

}
