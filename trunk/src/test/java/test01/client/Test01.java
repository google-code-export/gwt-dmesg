package test01.client;

import org.gwt.dmesg.client.Messages;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;

public class Test01 implements EntryPoint {

	public void onModuleLoad() {
		Label label = new Label(Messages.get("from.another.bundle"));
		RootPanel.get().add(label);
	}
}
