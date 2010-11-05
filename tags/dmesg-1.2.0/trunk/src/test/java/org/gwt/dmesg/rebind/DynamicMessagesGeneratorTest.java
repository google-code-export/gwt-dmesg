package org.gwt.dmesg.rebind;

import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

import com.google.gwt.dev.Compiler;

/**
 * A test case for validating expected generated JavaScript output.
 * 
 * @author Gergely Kiss
 */
public class DynamicMessagesGeneratorTest {

	/**
	 * Asserts that the generated JavaScript matches the expected test_01.js.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testGenerate01() throws Exception {
		assertJS("test01.Test01");
	}

	void assertJS(String module) {
		File genDir = new File("target/gen");
		try {
			FileUtils.deleteQuietly(genDir);
			FileUtils.forceMkdir(genDir);

			String[] args = { "-logLevel", "INFO", "-gen", genDir.getAbsolutePath(), "-style",
					"DETAILED", "-war", genDir.getAbsolutePath(), module };
			Compiler.main(args);
		} catch (IOException e) {
			e.printStackTrace();
			fail(e.getLocalizedMessage());
		}
	}
}
