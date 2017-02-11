/**
 * 
 */
package mevenk.xsdtojava;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * @author VENKATESH
 *
 */
public class XJRProcessExecutor {

	public enum XJRCommandResult {
		SUCCESS, FAIL
	}

	static Object[] executeXJRCommand(String dirForJavaClasses, String packageName, String XSDFilePath)
			throws IOException, InterruptedException {

		XJRCommandResult xjcCommandResult = XJRCommandResult.FAIL;

		Runtime runtime = Runtime.getRuntime();
		Process xjcProcess = runtime
				.exec(new String[] { "xjc", "-d", dirForJavaClasses, "-p", packageName, XSDFilePath });
		int exitValue = xjcProcess.waitFor();

		if (exitValue == 0) {
			xjcCommandResult = XJRCommandResult.SUCCESS;
		}

		BufferedReader xjcCommandBufferedReader = new BufferedReader(
				new InputStreamReader(xjcProcess.getInputStream()));
		
		String line = null;
		while((line = xjcCommandBufferedReader.readLine()) != null){
			System.out.println(line);
		}

		String xjcCommandOutput = xjcCommandBufferedReader.toString();

		Object[] xjcReturnObjectArray = { xjcCommandResult, xjcCommandOutput };

		return xjcReturnObjectArray;

	}

}
