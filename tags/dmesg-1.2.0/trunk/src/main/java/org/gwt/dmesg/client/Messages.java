package org.gwt.dmesg.client;

import com.google.gwt.core.client.GWT;

/**
 * Dynamic message source for Google Web Toolkit.
 * 
 * <p>
 * Provides ResourceBundle-like message access. Uses the GWT i18n conventions
 * (locale property, deferred binding, .properties resource), but <b>without the
 * need of creating Java classes and functions</b> for every message bundle and
 * message.
 * </p>
 * 
 * <p>
 * The DynamicMessages module integrates with the original GWT I18N module, so
 * both can be used at the same time. Please note that similarly to how the
 * original approach works, <b>inheriting from I18N is only necessary if a
 * non-default locale is introduced</b>.
 * </p>
 * 
 * <p>
 * Also, the properties file encoding is required to be <b>UTF-8</b> (as with
 * GWT static string localization).
 * </p>
 * 
 * <p>
 * Notes:<br>
 * <ul>
 * <li>The default bundle is called <b>Messages</b> (Messages.properties,
 * Messages_en.properties, etc.) it is used when you don't specify any bundles.
 * <li>You may specify additional bundles using the <code>messageBundles</code>
 * property in your {module}.gwt.xml descriptor. <b>If you extend this property,
 * you must specify all your bundles</b> (including Messages if you used that as
 * well).
 * <li>The bundles must be accessible as a class path resource - at the root of
 * the classpath (*.properties should be placed in the root of the JAR/WAR)
 * <li>The global JavaScript variable <code>$wnd['messages']</code> will contain the bundles -
 * this poses a possible conflict with other, user-defined JS variables
 * <li>Message bundles won't have namespaces. If you define the same key in more
 * bundles, then the last one will be used (assuming the order you specified
 * them in the module xmls).
 * </ul>
 * </p>
 * 
 * <p>
 * Usage:<br>
 * <p>
 * Simple labels:<br>
 * <code>
 * Messages.get("my.label");
 * </code>
 * </p>
 * <p>
 * Parametric labels:<br>
 * <code>
 * Messages.get("my.parametric.label", "a", 1, new Date());
 * </code>
 * </p>
 * </p>
 * 
 * @since 1.0
 * 
 * @author Gergely Kiss
 */
public class Messages {
	static {
		// Initializing the current bundle with GWT deferred binding
		// This ensures that we have the $wnd['messages'] javascript object,
		// which can be referenced by get()
		MessageBundleImpl bundle = GWT.create(MessageBundleImpl.class);
		bundle.load();
	}

	/**
	 * Returns the message for the specified key.
	 * 
	 * <p>
	 * Note: when using arguments, only the first occurrence of
	 * <code>{...}</code> is replaced for each argument!
	 * </p>
	 * 
	 * @param key
	 *            The message key, as supplied in the bundle
	 * @param args
	 *            Arguments used in the message (optional). The arguments may be
	 *            specified in the bundle with the usual <code>{...}</code>
	 *            syntax.
	 * 
	 * @return The message associated with the key, or the key if no message was
	 *         found
	 * 
	 * @since 1.0
	 */
	public static String get(String key, Object... args) {
		String value = get(key);
		if (args == null) {
			return value;
		} else {
			for (int i = 0; i < args.length; i++) {
				value = value.replace("{" + i + "}", args[i].toString());
			}
			return value;
		}
	}

	/**
	 * Returns the message for the specified key.
	 * 
	 * @param key
	 *            The message key, as supplied in the bundle
	 * 
	 * @return The message associated with the key, or the key if no message was
	 *         found
	 * 
	 * @since 1.0
	 */
	public static native String get(String key)
	/*-{
		var value = $wnd['messages'][key];
		return value == null ? key : value;
	}-*/;
}
