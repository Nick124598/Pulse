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
	requires com.fasterxml.jackson.core;
	requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.annotation;
	opens org.nick124.FileChooser to com.sun.jna;
	}