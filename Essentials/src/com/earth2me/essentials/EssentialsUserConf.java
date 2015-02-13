package com.earth2me.essentials;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import java.io.File;
import java.io.IOException;
import java.util.Locale;
import java.util.UUID;
import java.util.logging.Level;
import org.bukkit.entity.Player;
import org.bukkit.Bukkit;


public class EssentialsUserConf extends EssentialsConf
{
	public final String username;
	public final UUID uuid;

	public EssentialsUserConf(final String username, final UUID uuid, final File configFile)
	{
		super(configFile);
		this.username = username;
		this.uuid = uuid;
	}

	@Override
	public boolean legacyFileExists()
	{
		final File file = new File(configFile.getParentFile(), username.toLowerCase() + ".yml");
		return file.exists();
	}

	@Override
	public void convertLegacyFile()
	{
		final File file = new File(configFile.getParentFile(), username.toLowerCase() + ".yml");
        
		try
		{
            File file2 = new File(configFile.getParentFile(), uuid.toString() + ".yml");
            boolean success = file.renameTo(file2);
            
            if(!success){
                Bukkit.getLogger().log(Level.WARNING, "Problem renaming user file for user: " + username);
            }else{
                Bukkit.getLogger().log(Level.INFO, "Renamed file for: " + username);
            }
		}
		catch (Exception ex)
		{
			Bukkit.getLogger().log(Level.WARNING, "Problem renaming user file for user: " + username, ex);
		}

		setProperty("lastAccountName", username);
	}
    
    @Override
    public boolean offlineUUIDFileExists(){
        final UUID fn = UUID.nameUUIDFromBytes(("OfflinePlayer:" + username).getBytes(Charsets.UTF_8));
        File offlineUUIDFile = new File(configFile.getParentFile(), fn + ".yml");
        return offlineUUIDFile.exists();
    }
    
    private File getOfflineUUIDFile(){
        final UUID fn = UUID.nameUUIDFromBytes(("OfflinePlayer:" + username).getBytes(Charsets.UTF_8));
		return new File(configFile.getParentFile(), fn.toString() + ".yml");
    }
    
    @Override
    public void convertOfflineUUIDFile(){
        File oldFile = getOfflineUUIDFile();
        try
		{
            //hopefully this will be the new uuid
            File newFile = new File(configFile.getParentFile(), uuid + ".yml");
            boolean success = oldFile.renameTo(newFile);
            
            if(!success){
                Bukkit.getLogger().log(Level.WARNING, "Problem renaming old uuid file for user: " + username);
            }else{
                Bukkit.getLogger().log(Level.INFO, "Renamed old uuid file for: " + username);
            }
		}
		catch (Exception ex)
		{
			Bukkit.getLogger().log(Level.WARNING, "Problem renaming old uuid file for user: " + username, ex);
		}

		setProperty("lastAccountName", username);
    }

	private File getAltFile()
	{
		final UUID fn = UUID.nameUUIDFromBytes(("OfflinePlayer:" + username.toLowerCase(Locale.ENGLISH)).getBytes(Charsets.UTF_8));
		return new File(configFile.getParentFile(), fn.toString() + ".yml");
	}

	@Override
	public boolean altFileExists()
	{
		if (username.equals(username.toLowerCase()))
		{
			return false;
		}
		return getAltFile().exists();
	}

	@Override
	public void convertAltFile()
	{
		try
		{
			Files.move(getAltFile(), new File(configFile.getParentFile(), uuid + ".yml"));
		}
		catch (IOException ex)
		{
			Bukkit.getLogger().log(Level.WARNING, "Failed to migrate user: " + username, ex);
		}
	}
}
