package textile.data;

public class BlockUpdateSettings {
	private static boolean debugStackTrace = true;
	
	public static boolean shouldDebugStackTrace() {
		return debugStackTrace;
	}
	
	public static void shouldDebugStackTrace(boolean enable) {
		BlockUpdateSettings.debugStackTrace = enable;
	}
}
