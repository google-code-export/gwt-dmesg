package org.gwt.dmesg.client;

import java.util.Date;

public interface TestMessages extends com.google.gwt.i18n.client.Messages {
	public String myLabel();

	public String parametric(String string, int i, Date date);
}
