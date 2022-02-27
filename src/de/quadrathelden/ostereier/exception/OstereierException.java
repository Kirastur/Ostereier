package de.quadrathelden.ostereier.exception;

import org.bukkit.command.CommandSender;

import de.quadrathelden.ostereier.text.TextManager;
import de.quadrathelden.ostereier.tools.Message;

public class OstereierException extends Exception {

	private static final long serialVersionUID = 1L;

	public static final String JAVA_EXCEPTION = "Java Exception";

	protected final String contextName;
	protected final Message message;
	protected final String errorDetails;

	public OstereierException(Message message) {
		super(buildMessage(null, message, null), null, false, false);
		this.contextName = null;
		this.message = message;
		this.errorDetails = null;
	}

	public OstereierException(String contextName, Message message, String errorDetails) {
		super(buildMessage(contextName, message, errorDetails), null, false, false);
		this.contextName = contextName;
		this.message = message;
		this.errorDetails = errorDetails;
	}

	public OstereierException(String contextName, Message message, String errorDetails, Throwable cause) {
		super(buildMessage(contextName, message, errorDetails), cause, false, false);
		this.contextName = contextName;
		this.message = message;
		this.errorDetails = errorDetails;
	}

	public String getContextName() {
		return contextName;
	}

	public Message getOsterMessage() {
		return message;
	}

	public String getErrorDetails() {
		return errorDetails;
	}

	protected static String buildMessage(String contextName, String errorDescription, String errorDetails) {
		if ((contextName != null) && (!contextName.isEmpty())) {
			errorDescription = contextName + ": " + errorDescription;
		}
		if ((errorDetails != null) && (!errorDetails.isEmpty())) {
			errorDescription = errorDescription + ": " + errorDetails;
		}
		return errorDescription;
	}

	protected static String buildMessage(String contextName, Message message, String errorDetails) {
		return buildMessage(contextName, message.toString(), errorDetails);
	}

	public String getLocalizedFullErrorMessage(TextManager textManager, CommandSender sender) {
		String errorText = textManager.findText(message.name(), sender);
		if (errorText.isEmpty()) {
			return getMessage();
		} else {
			return buildMessage(contextName, errorText, errorDetails);
		}
	}
}