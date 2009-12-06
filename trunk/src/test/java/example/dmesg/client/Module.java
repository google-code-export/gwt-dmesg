package example.dmesg.client;

import org.gwt.dmesg.client.Messages;

import com.google.gwt.core.client.EntryPoint;

public class Module implements EntryPoint {

	public void onModuleLoad() {
		String[] a = new String[10];
		a[0] = Messages.get("example");
		a[1] = Messages.get("example");
		a[2] = Messages.get("example");
		a[3] = Messages.get("example");
		a[4] = Messages.get("example");
		a[5] = Messages.get("example");
		a[6] = Messages.get("example");
		a[7] = Messages.get("example");
		a[8] = Messages.get("example");
		a[9] = Messages.get("example");
	}
}
