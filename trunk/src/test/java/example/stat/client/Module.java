package example.stat.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;

public class Module implements EntryPoint {

	public void onModuleLoad() {
		TestMessages messages = GWT.create(TestMessages.class);
		String[] a = new String[10];
		a[0] = messages.example();
		a[1] = messages.example();
		a[2] = messages.example();
		a[3] = messages.example();
		a[4] = messages.example();
		a[5] = messages.example();
		a[6] = messages.example();
		a[7] = messages.example();
		a[8] = messages.example();
		a[9] = messages.example();
	}
}
