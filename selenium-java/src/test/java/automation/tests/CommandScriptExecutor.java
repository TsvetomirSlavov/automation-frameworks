package automation.tests;

import java.io.IOException;

import static automation.tests.SharedDriver.isPlatform;

public class CommandScriptExecutor {
    private static String cmd = null;

    public static void executeCommandScript(String script) {
        try {
            Runtime runtime = Runtime.getRuntime();

            if(isPlatform().equalsIgnoreCase("WINDOWS")) {
                cmd = "cmd /c start, ";
            } else if (isPlatform().equalsIgnoreCase("MAC")) {
                cmd = "";
            }

            runtime.exec(cmd + script);
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}
