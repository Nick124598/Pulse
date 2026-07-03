/**
 * 
 */
/**
 * 
 */
module MyVS {
	requires java.desktop;
	requires com.sun.jna;
	requires com.sun.jna.platform;
	requires swing.jnafilechooser;
	opens org.nick124.FileChooser to com.sun.jna;
	}