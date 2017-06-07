/**
 * 
 */
package mevenk.xsdtojava;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import mevenk.xmltojava.XMLToJava;

/**
 * @author VENKATESH
 *
 */
public class XJCProcessExecutor {

	static final String lineSeparator = XMLToJava.LINE_SEPARATOR;

	private enum XJCCommandResult {
		SUCCESS("JAVA Conversion Success"), FAIL("JAVA Conversion Fail(XJC Command Fail)");
		private final String commandResultText;

		private XJCCommandResult(final String commandResultText) {
			this.commandResultText = commandResultText;
		}

		@Override
		public String toString() {
			return commandResultText;
		}
	}

	public void executeXJCCommand(String dirForJavaClasses, String packageName, String XSDFilePath) {

		try {
			Runtime runtime = Runtime.getRuntime();
			Process xjcProcess = runtime
					.exec(new String[] { "xjc", "-d", dirForJavaClasses, "-p", packageName, XSDFilePath });

			BufferedReader xjcCommandBufferedReader = new BufferedReader(
					new InputStreamReader(xjcProcess.getInputStream()));

			String xjcCommandOutputLineLine = null;
			while ((xjcCommandOutputLineLine = xjcCommandBufferedReader.readLine()) != null) {
				System.out.println(xjcCommandOutputLineLine);
			}

			int exitValue = xjcProcess.waitFor();

			if (exitValue == 0) {
				System.out.println(lineSeparator + XJCCommandResult.SUCCESS);
			} else {
				System.out.println(lineSeparator + XJCCommandResult.FAIL + " - Exit value : " + exitValue);
			}
		} catch (Exception exception) {
			exception.printStackTrace();
		}

	}

}
