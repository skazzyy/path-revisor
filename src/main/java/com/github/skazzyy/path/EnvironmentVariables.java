package com.github.skazzyy.path;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.jna.platform.win32.Advapi32Util;
import com.sun.jna.platform.win32.WinReg;

/**
 * Common environment variables that can be reused to make a more readable path.
 * 
 * This is not meant to be an exhaustive list of environment variables, but a list of those most commonly used in the path.
 */
public enum EnvironmentVariables {

    // Naming standard not ideal, but seems simple enough to match the enum name to the string value
    // Values should be in alphabetical order

    /**
     * Defaults to
     */
    Path,

    /**
     * Defaults to C:\Program Files
     */
    ProgramFiles,

    /**
     * Defaults to C:\Program Files (x86)
     */
    ProgramFilesx86("ProgramFiles(x86)"),

    /**
    * Defaults to %SystemRoot%\system32;%SystemRoot%;%SystemRoot%\System32\Wbem;%SYSTEMROOT%\System32\WindowsPowerShell\v1.0\
    */
    SystemRoot;

    protected final static Logger LOGGER = LoggerFactory.getLogger(EnvironmentVariables.class);

    /**
     * This is the key within {@link WinReg#HKEY_LOCAL_MACHINE} that the system's environment variables are stored
     */
    public final static String SYSTEM_ENVIRONMENT_REGISTRY_KEY = "SYSTEM\\CurrentControlSet\\Control\\Session Manager\\Environment";

    /**
     * This is the key within {@link WinReg#HKEY_CURRENT_USER} that the current user's environment variables are stored
     */
    public final static String CURRENT_USER_ENVIRONMENT_REGISTRY_KEY = "Environment";

    protected final String variable;

    /**
     * Default constructor, use the name as the variable
     */
    private EnvironmentVariables() {
        this(null);
    }

    /**
     * @param variable the actual value that is used in Windows
     */
    private EnvironmentVariables(String variable) {
        this.variable = variable;
    }

    public String variable() {
        return variable == null ? name() : variable;
    }

    /**
     * Returns the environment variable ready for use, wrapped in % characters
     */
    public String wrap() {
        return "%" + variable() + "%";
    }

    /**
     * @return the environment variable for the current process
     */
    public String getValue() {
        return System.getenv(variable());
    }

    /**
     * Get the value of the environment variable from the registry.
     * 
     * @return the contents of the environment variable from the registry <br>
     *         <b>Note</b>: This does NOT expand the key <br>
     *         <b>Note</b>: This does NOT give the environment variable of the process, but the registry
     */
    public String getSystemRegistryValue() {
        return Advapi32Util.registryGetStringValue(WinReg.HKEY_LOCAL_MACHINE, SYSTEM_ENVIRONMENT_REGISTRY_KEY, variable());
    }

    /**
     * Get the value of the environment variable from the registry.
     * 
     * @return the contents of the environment variable from the registry <br>
     *         <b>Note</b>: This does NOT expand the key <br>
     *         <b>Note</b>: This does NOT give the environment variable of the process, but the registry
     */
    public String getUserRegistryValue() {
        if (Advapi32Util.registryValueExists(WinReg.HKEY_CURRENT_USER, CURRENT_USER_ENVIRONMENT_REGISTRY_KEY, variable())) {
            return Advapi32Util.registryGetStringValue(WinReg.HKEY_CURRENT_USER, CURRENT_USER_ENVIRONMENT_REGISTRY_KEY, name());
        } 
        LOGGER.info("A user level value does not exist for {} for the current user.  Defaulting to the system value.", variable());
        return getSystemRegistryValue();
    }

}
