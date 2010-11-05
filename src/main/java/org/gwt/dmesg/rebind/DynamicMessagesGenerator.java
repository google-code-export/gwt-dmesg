package org.gwt.dmesg.rebind;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.tapestry.util.text.LocalizedProperties;

import com.google.gwt.core.ext.BadPropertyValueException;
import com.google.gwt.core.ext.Generator;
import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.PropertyOracle;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.TreeLogger.Type;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.NotFoundException;
import com.google.gwt.core.ext.typeinfo.TypeOracle;
import com.google.gwt.user.rebind.ClassSourceFileComposerFactory;
import com.google.gwt.user.rebind.SourceWriter;

/**
 * Dynamic message generator.
 * <p>
 * Generates <code>$wnd['messages'] = { ... };</code> blocks based on the
 * locale, for dynamic i18n support.
 * </p>
 * 
 * @since 1.0
 * @author Gergely Kiss
 * @see org.gwt.dmesg.client.MessageBundleImpl
 */
public class DynamicMessagesGenerator extends Generator {
	/**
	 * The token representing the locale property controlling Localization.
	 */
	private static final String PROP_LOCALE = "locale";

	/**
	 * The token representing the message bundles.
	 */
	private static final String PROP_MESSAGEBUNDLES = "messageBundles";

	@Override
	public String generate(TreeLogger logger, GeneratorContext context, String typeName)
			throws UnableToCompleteException {
		logger.log(Type.TRACE, "Entering DMG.generate()");

		TypeOracle typeOracle = context.getTypeOracle();
		PropertyOracle propertyOracle = context.getPropertyOracle();

		String locale = null;

		try {
			locale = propertyOracle.getSelectionProperty(logger, PROP_LOCALE).getCurrentValue();
		} catch (BadPropertyValueException e) {
			logger.log(TreeLogger.TRACE, "MessageBundle used without I18N module, using defaults",
					e);
			locale = "default";
		}

		List<String> bundles;

		try {
			bundles = propertyOracle.getConfigurationProperty(PROP_MESSAGEBUNDLES).getValues();
		} catch (BadPropertyValueException e) {
			logger.log(TreeLogger.ERROR, PROP_MESSAGEBUNDLES
					+ " should be defined with 'set-configuration-property'. "
					+ "Please also check that you inherited from the DynamicMessages module.");
			throw new UnableToCompleteException();
		}

		if (bundles.isEmpty()) {
			logger.log(TreeLogger.TRACE, "No bundles specified in configuration property "
					+ PROP_MESSAGEBUNDLES + ". Using 'Messages'");
			bundles.add(0, "Messages");
		}

		JClassType targetClass;

		try {
			targetClass = typeOracle.getType(typeName);
		} catch (NotFoundException e) {
			logger.log(TreeLogger.ERROR, "No such type", e);
			throw new UnableToCompleteException();
		}

		String packageName = targetClass.getPackage().getName();
		String className = targetClass.getName().replace('.', '_') + "_";
		if (!locale.equals("default")) {
			className += locale;
		}
		String qualName = packageName + "." + className;

		PrintWriter pw = context.tryCreate(logger, packageName, className);
		if (pw != null) {
			ClassSourceFileComposerFactory factory = new ClassSourceFileComposerFactory(
					packageName, className);
			factory.setSuperclass(targetClass.getQualifiedSourceName());

			SourceWriter writer = factory.createSourceWriter(context, pw);
			writer.println("public native void load() /*-{");
			writer.println("  $wnd['messages'] = {");

			appendBundles(logger, writer, locale, bundles);

			writer.println("  }");
			writer.println("}-*/;");

			writer.commit(logger);
		}

		return qualName;
	}

	@SuppressWarnings("unchecked")
	private void appendBundles(TreeLogger logger, SourceWriter writer, String locale,
			List<String> bundles) throws UnableToCompleteException {

		LocalizedProperties messages = new LocalizedProperties();

		boolean first = true;
		for (String bundle : bundles) {
			String localizedBundle = locale.equals("default") ? bundle + ".properties" : bundle
					+ "_" + locale + ".properties";

			ClassLoader classLoader = getClass().getClassLoader();
			InputStream str = null;
			try {
				str = classLoader.getResourceAsStream(localizedBundle);
				if (str != null) {
					messages.load(str, "UTF-8");
				} else {
					logger.log(TreeLogger.ERROR, "Message bundle not found: " + localizedBundle);
					throw new UnableToCompleteException();
				}
			} catch (UnsupportedEncodingException e) {
				// UTF-8 should always be defined
				logger.log(TreeLogger.ERROR, "UTF-8 encoding is not defined", e);
				throw new UnableToCompleteException();
			} catch (IOException e) {
				logger.log(TreeLogger.ERROR, "Failed to read messages from bundle: "
						+ localizedBundle, e);
				throw new UnableToCompleteException();
			}
		}

		Map<String, String> msgs = messages.getPropertyMap();
		for (Entry<String, String> entry : msgs.entrySet()) {
			String key = entry.getKey().replaceAll("\"", "\\\"");
			String value = entry.getValue().replaceAll("\"", "\\\"");

			if (first) {
				first = false;
			} else {
				writer.println(",");
			}

			writer.print("    \"" + key + "\": \"" + value + "\"");
		}
	}

}
