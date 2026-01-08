// NotakApp (c) 2025 Baltasar MIT License <baltasarq@gmail.com>


package com.devbaltasarq.nottakapp.core;


import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.EnumMap;
import java.util.logging.Logger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.lang.reflect.Type;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;


/** The configuration for the app
  * @author baltasarq
  */
public class Config {
    private static final Logger LOG = Logger.getLogger( Config.class.getSimpleName() );

    public enum Key {
        APP_NAME,
        HOME_DIR,
        CONFIG_DIR,
        CONFIG_FILE_NAME,
        CONFIG_FILE_PATH,
        DATA_DIR_NAME,
        DATA_DIR_PATH,
        WIDTH,
        HEIGHT,
        TOP,
        LEFT
    }
    
    private Config(String appName, String dataDirName)
    {
        appName = appName.trim().toLowerCase();
        dataDirName = dataDirName.trim().toLowerCase();
        
        this.configValues = new EnumMap<>( Key.class );
        this.configValues.put( Key.APP_NAME, appName );
        this.configValues.put( Key.DATA_DIR_NAME, dataDirName );
        this.configValues.put( Key.CONFIG_FILE_NAME, appName + ".cfg.json" );
        this.init();
    }
    
    /** Assigns all the default values. */
    private void init()
    {
        this.configValues.put( Key.HOME_DIR,
                                System.getProperty( "user.home" ) );
        this.configValues.put( Key.CONFIG_DIR,
                                  this.buildConfigDirPath() );
        this.configValues.put( Key.CONFIG_FILE_PATH,
                                  this.buildConfigFilePath() );
        this.configValues.put( Key.DATA_DIR_PATH,
                                  this.buildDataDirPath() );
    }
    
    /** Builds the config directory path.
      * Beware that it needs the home directory already stored.
      * @return a string with the directory.
      */
    private String buildConfigDirPath()
    {
        String strConfigDirName = AppInfo.NAME.toLowerCase();
        String strHomeDir = this.configValues.get( Key.HOME_DIR );
        
        assert strHomeDir != null: "buildConfigDirectory(): "
                                    + "home dir can't be null";
        
        var configDirParent = new File( strHomeDir, ".config" );
        var toret = new File( configDirParent, strConfigDirName );
        
        return toret.getAbsolutePath();
    }
    
    /** Builds the config file path.
      * Beware that it needs the CONFIG_DIR entry before being executed.
      * @return the complete path to the config file.
      */
    private String buildConfigFilePath()
    {
        String strConfigDir = this.get( Key.CONFIG_DIR );
        String strConfigFileName = this.get( Key.CONFIG_FILE_NAME );
        
        assert strConfigDir != null: "buildConfigFilePath(): "
                                    + "config file path dir can't be null";
        
        assert strConfigFileName != null: "buildConfigFilePath(): "
                                    + "config file name can't be null";
        
        final var TORET = new File( strConfigDir, strConfigFileName );
        return TORET.getAbsolutePath();
    }
    
    /** Builds and creates the notes path.
      * Beware that HOME_DIR and DATA_DIR_NAME must have already been set.
      * @return the dir, as a string.
      */
    private String buildDataDirPath()
    {
        String strHomeDirPath = this.configValues.get( Key.HOME_DIR );
        String dataDirName = this.configValues.get( Key.DATA_DIR_NAME );
        
        assert strHomeDirPath != null: "buildDataDirPath(): home dir cannot be null";
        assert dataDirName != null: "buildDataDirPath(): data dir cannot be null";
        
        final var TORET = new File( strHomeDirPath, dataDirName );        
        return TORET.getAbsolutePath();
    }
    
    /** @return the associated value, or null if it does not exist.
      * @param key the key to ask for.
      */
    public String get(Key key)
    {
        return this.configValues.get( key );
    }
    
    /** Adds a new value to the configuration.
      * @param key the key of the value.
      * @param value the new value.
      */
    public void add(Key key, String value)
    {
        this.configValues.put( key, value );
    }
    
    private void createFilesIfNotExisting() throws IOException
    {
        final var CONFIG_DIR = new File( this.get( Key.CONFIG_DIR ) );
        final var CONFIG_PATH = new File( this.get( Key.CONFIG_FILE_PATH ) );
        final var DATA_DIR_PATH = new File( this.get( Key.DATA_DIR_PATH ) );
        
        if ( !CONFIG_DIR.exists() ) {
            if ( !CONFIG_DIR.mkdirs() ) {
                throw new IOException(
                                "Config: unable to create config directory: "
                                + CONFIG_DIR );
            }
        }
        
        if ( !CONFIG_PATH.exists() ) {
            if ( !CONFIG_PATH.createNewFile() ) {
                throw new IOException(
                            "Config: unable to create config file: "
                            + CONFIG_PATH );
            }
        }
        
        if ( !DATA_DIR_PATH.exists() ) {
            if ( !DATA_DIR_PATH.mkdirs() ) {
                throw new IOException(
                                "Config: unable to create data dir: "
                                + DATA_DIR_PATH );
            }
        }
    }
    
    /** Saves the configuration to the config file. */
    public void save()
    {
        final String FILE_PATH = this.get( Key.CONFIG_FILE_PATH );
        final var GSON = new Gson();
        final String CONTENTS = GSON.toJson( this.configValues );
        
        try {
            Files.deleteIfExists( Path.of( FILE_PATH ) );
            Files.writeString( Path.of( FILE_PATH ), CONTENTS,
                            StandardOpenOption.CREATE );
        } catch(IOException exc) {
            LOG.warning( "unable to save config file: " + exc.getMessage() );
        }
    }
    
    public static Config restore(String appName, String dataDirName)
    {
        if ( uniqueInstance != null ) {
            return uniqueInstance;
        }
        
        uniqueInstance = new Config( appName, dataDirName );
        final Gson GSON = new Gson();
        final String FILE_PATH = uniqueInstance.get( Key.CONFIG_FILE_PATH );
        
        try {    
            final String CONTENTS = Files.readString( Path.of( FILE_PATH ) );
            final Type TYPE = new TypeToken<Map<String, String>>(){}.getType();
            final Map<String, String> MAP = GSON.fromJson( CONTENTS, TYPE );
            
            if ( MAP != null ) {            
                for(final Map.Entry<String, String> ENTRY: MAP.entrySet()) {
                    final String STR_KEY = ENTRY.getKey().trim().toUpperCase();
                    final Key KEY = Key.valueOf( STR_KEY );

                    if ( KEY != null ) {
                        uniqueInstance.add( KEY, ENTRY.getValue() );
                    } else {
                        LOG.warning( "Config.restore(): key not found: " + STR_KEY );
                    }
                }
            }
        } catch(IOException exc) {
            LOG.warning( "unable to locate/create/load config file: "
                            + exc.getMessage() );
        }

        try {
            uniqueInstance.createFilesIfNotExisting();
        } catch(IOException exc) {
            LOG.warning( "unable to locate/create directories: "
                            + exc.getMessage() );
        }

        return uniqueInstance;
    }
    
    private Map<Key, String> configValues;
    private static Config uniqueInstance = null;
}
