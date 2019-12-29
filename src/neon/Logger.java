package neon;

public class Logger {
	public enum LoggerType { 
	    INFO, WARNING, ERROR, FATAL_ERROR; 
	} 
	
	private static final String ANSI_RESET = "\u001B[0m";
	
	private static final String ANSI_BLACK = "\u001B[30m";
	private static final String ANSI_RED = "\u001B[31m";
	private static final String ANSI_GREEN = "\u001B[32m";
	private static final String ANSI_YELLOW = "\u001B[33m";
	private static final String ANSI_BLUE = "\u001B[34m";
	private static final String ANSI_PURPLE = "\u001B[35m";
	private static final String ANSI_CYAN = "\u001B[36m";
	private static final String ANSI_WHITE = "\u001B[37m";
	
	private static final String ANSI_BLACK_BOLD = "\033[1;30m";
	private static final String ANSI_RED_BOLD = "\033[1;31m";
	private static final String ANSI_GREEN_BOLD = "\033[1;32m"; 
	private static final String ANSI_YELLOW_BOLD = "\033[1;33m";
	private static final String ANSI_BLUE_BOLD = "\033[1;34m";
	private static final String ANSI_PURPLE_BOLD = "\033[1;35m";
	private static final String ANSI_CYAN_BOLD = "\033[1;36m"; 
	private static final String ANSI_WHITE_BOLD = "\033[1;37m";
	
	private static final String ANSI_BLACK_BACKGROUND = "\u001B[40m";
	private static final String ANSI_RED_BACKGROUND = "\u001B[41m";
	private static final String ANSI_GREEN_BACKGROUND = "\u001B[42m";
	private static final String ANSI_YELLOW_BACKGROUND = "\u001B[43m";
	private static final String ANSI_BLUE_BACKGROUND = "\u001B[44m";
	private static final String ANSI_PURPLE_BACKGROUND = "\u001B[45m";
	private static final String ANSI_CYAN_BACKGROUND = "\u001B[46m";
	private static final String ANSI_WHITE_BACKGROUND = "\u001B[47m";
	
	public Logger() {
		System.out.println("Logger initialised");
	}
	
	public void logInfo(String text) {
		log(text,LoggerType.INFO);
	}
	
	public void logWarning(String text) {
		log(text,LoggerType.WARNING);
	}
	
	public void logError(String text) {
		log(text,LoggerType.ERROR);
	}
	
	public void logFatalError(String text) {
		log(text,LoggerType.FATAL_ERROR);
	}
	
	public void log(String text,LoggerType outputType) {
		if (outputType == LoggerType.INFO) {
			System.out.println("[  INFO  ] " + text);
		} else if (outputType == LoggerType.WARNING) {
			System.out.println(ANSI_YELLOW + ANSI_BLACK_BACKGROUND + "[  WARN  ] " + text + ANSI_RESET);
		} else if (outputType == LoggerType.ERROR) {
			System.out.println(ANSI_RED + ANSI_BLACK_BACKGROUND + "[ ERROR  ] " + text + ANSI_RESET);
		} else if (outputType == LoggerType.FATAL_ERROR) {
			System.out.println(ANSI_WHITE + ANSI_RED_BACKGROUND + "[CRITICAL] " + text + ANSI_RESET);
		} else {
			System.out.println("[ LOGGER ] Internal error with logger processing : " + text);
		}
	}
}
