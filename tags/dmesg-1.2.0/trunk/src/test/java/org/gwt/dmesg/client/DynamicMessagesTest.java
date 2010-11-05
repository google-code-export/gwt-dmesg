package org.gwt.dmesg.client;

import java.util.Date;

import com.google.gwt.benchmarks.client.Benchmark;
import com.google.gwt.core.client.GWT;

/**
 * Tests and benchmarks for Dynamic GWT {@link Messages}.
 * 
 * @author Gergely Kiss
 */
public class DynamicMessagesTest extends Benchmark {
	private TestMessages messages;

	@Override
	protected void gwtSetUp() throws Exception {
		if (messages == null) {
			messages = GWT.create(TestMessages.class);
		}
	}

	@Override
	public String getModuleName() {
		return "org.gwt.dmesg.TestModule";
	}

	/**
	 * Tests {@link Messages#get(String, Object...)}.
	 */
	public void testGetMessage() {
		// Simple label test
		assertEquals("Label", Messages.get("my.label"));

		// Missing label test
		assertEquals("my.missing.label", Messages.get("my.missing.label"));

		// Parametric label test
		assertEquals("String: {0}, Integer: {1}, Date: {2}", Messages.get("parametric"));
		assertEquals("String: a, Integer: {1}, Date: {2}", Messages.get("parametric", "a"));
		assertEquals("String: b, Integer: 2, Date: Thu Jan 01 01:00:00 CET 1970", Messages.get(
				"parametric", "b", 2, new Date(0)));

		// UTF-8 encoding test
		assertEquals("áéíóőúű", Messages.get("utf8"));

		// Different bundle test
		assertEquals("test", Messages.get("from.another.bundle"));
	}

	/**
	 * Tests enum localization (programmatic localization example).
	 */
	public void testEnumLocalization() {
		for (MyEnum e : MyEnum.values()) {
			// Messages are the same as the enum constants - for easy asserts
			String label = Messages.get("myEnum." + e.ordinal());
			assertEquals(e.name(), label);
		}
	}

	/**
	 * Static string localization benchmark.
	 */
	public void testStaticMessageBenchmark() {
		String label;
		for (int i = 0; i < 1000; i++) {
			label = messages.myLabel();
		}
	}

	/**
	 * Static string localization benchmark with parameters.
	 */
	public void testStaticParametricBenchmark() {
		String label;
		for (int i = 0; i < 1000; i++) {
			label = messages.parametric("a", 1, new Date(0));
		}
	}

	/**
	 * Dynamic string localization benchmark.
	 */
	public void testDynamicMessageBenchmark() {
		String label;
		for (int i = 0; i < 1000; i++) {
			label = Messages.get("my.label");
		}
	}

	/**
	 * Dynamic string localization benchmark with parameters.
	 */
	public void testDynamicParametricBenchmark() {
		String label;
		for (int i = 0; i < 1000; i++) {
			label = Messages.get("parametric", "a", 1, new Date(0));
		}
	}
}
