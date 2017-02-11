/**
 * 
 */
package mevenk.xsdtojava;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;

import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

import mevenk.xmltojava.XMLToJava;

/**
 * @author VENKATESH
 *
 */
public class XJCProcessExecutor {

	static final String lineSeparator = XMLToJava.lineSeparator;

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

	public void executeXJCCommand(String dirForJavaClasses, String packageName, String XSDFilePath,
			Document resultPaneDocument) throws BadLocationException {

		try {
			Runtime runtime = Runtime.getRuntime();
			Process xjcProcess = runtime
					.exec(new String[] { "xjc", "-d", dirForJavaClasses, "-p", packageName, XSDFilePath });

			/*BufferedReader xjcCommandBufferedReader = new BufferedReader(
					new InputStreamReader(xjcProcess.getInputStream()));

			String xjcCommandOutputLineLine = null;
			while ((xjcCommandOutputLineLine = xjcCommandBufferedReader.readLine()) != null) {
				resultPaneDocument.insertString(resultPaneDocument.getLength(),
						lineSeparator + xjcCommandOutputLineLine, null);
			}*/

			int exitValue = xjcProcess.waitFor();

			if (exitValue == 0) {
				resultPaneDocument.insertString(resultPaneDocument.getLength(),
						lineSeparator + lineSeparator + XJCCommandResult.SUCCESS, null);
			} else {
				resultPaneDocument.insertString(resultPaneDocument.getLength(),
						lineSeparator + lineSeparator + XJCCommandResult.FAIL + " - Exit value : " + exitValue, null);
			}
		} catch (Exception exception) {

			StringWriter exceptionStringWriter = new StringWriter();
			exception.printStackTrace(new PrintWriter(exceptionStringWriter));
			resultPaneDocument.insertString(resultPaneDocument.getLength(), lineSeparator + lineSeparator
					+ XJCCommandResult.FAIL + lineSeparator + lineSeparator + exceptionStringWriter.toString() + lineSeparator,
					null);
		}

	}

}
