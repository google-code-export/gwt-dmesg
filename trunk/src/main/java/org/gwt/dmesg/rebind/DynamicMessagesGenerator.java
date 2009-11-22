package org.gwt.dmesg.rebind;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
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
import com.google.gwt.i18n.client.impl.LocaleInfoImpl;
import com.google.gwt.user.rebind.ClassSourceFileComposerFactory;
import com.google.gwt.user.rebind.SourceWriter;

/**
 * Dynamic message generator.
 * 
 * <p>
 * Generates <code>$wnd['messages'] = { ... };</code> blocks based on the locale, for dynamic i18n support.
 * </p>
 * 
 * @since 1.0
 * 
 * @author Gergely Kiss
 * 
 * @see org.gwt.dmesg.client.MessageBundleImpl
 */
public class DynamicMessagesGenerator extends Generator {
	/**
	 * The token representing the locale property controlling Localization.
	 */
	private static final String PROP_LOCALE = "locale";

	@Override
	@SuppressWarnings("unchecked")
	public String generate(TreeLogger logger, GeneratorContext context, String typeName)
			throws UnableToCompleteException {
		logger.log(Type.TRACE, "Entering DMG.generate()");

		TypeOracle typeOracle = context.getTypeOracle();
		PropertyOracle propertyOracle = context.getPropertyOracle();

		String locale = null;

		try {
			locale = propertyOracle.getPropertyValue(logger, PROP_LOCALE);
		} catch (BadPropertyValueException e) {
			logger.log(TreeLogger.TRACE, "MessageBundle used without I18N module, using defaults",
					e);
			return LocaleInfoImpl.class.getName();
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
		String properties = locale.equals("default") ? "Messages.properties" : "Messages_" + locale
				+ ".properties";

		PrintWriter pw = context.tryCreate(logger, packageName, className);
		if (pw != null) {
			ClassSourceFileComposerFactory factory = new ClassSourceFileComposerFactory(
					packageName, className);
			factory.setSuperclass(targetClass.getQualifiedSourceName());

			SourceWriter writer = factory.createSourceWriter(context, pw);
			writer.println("public native void load() /*-{");
			writer.println("  $wnd['messages'] = {");
			LocalizedProperties messages = new LocalizedProperties();

			ClassLoader classLoader = getClass().getClassLoader();
			InputStream str = null;
			try {
				str = classLoader.getResourceAsStream(properties);
				if (str != null) {
					messages.load(str, "UTF-8");
				}
			} catch (UnsupportedEncodingException e) {
				// UTF-8 should always be defined
				logger.log(TreeLogger.ERROR, "UTF-8 encoding is not defined", e);
				throw new UnableToCompleteException();
			} catch (IOException e) {
				logger.log(TreeLogger.ERROR, "Exception reading messages", e);
				throw new UnableToCompleteException();
			}

			Map<String, String> msgs = messages.getPropertyMap();
			boolean first = true;
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

			writer.println("  }");
			writer.println("}-*/;");

			writer.commit(logger);
		}

		return qualName;
	}

}
