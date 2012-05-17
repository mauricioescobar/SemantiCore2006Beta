package semanticore.general.util;

import java.io.PrintStream;

public final class Console {
    private static PrintStream stream = null;

    public static void write(Object data) {
	if (Console.stream != null)
	    stream.println(data);
    }

    public static void setPrintStream(PrintStream stream) {
	Console.stream = stream;
    }
}
