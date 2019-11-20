package fr.triedge.minecraft.plugin.magic;

import org.bukkit.Effect;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;

public class SpellSnowBall extends Spell{

	public SpellSnowBall(Player player, FileConfiguration config, Plugin plugin) {
		super(player, config,plugin);
	}

	@Override
	public float getDamageFactor() {
		return 1.1f;
	}

	@Override
	public String getName() {
		return "snowball";
	}

	@Override
	public void launch() {
		Snowball snow = player.launchProjectile(Snowball.class);
		snow.setMetadata("player", new FixedMetadataValue(plugin, player.getName()));
		snow.setGravity(false);
		World world = player.getWorld();
		world.playEffect(player.getLocation(), Effect.FIREWORK_SHOOT, 1);
		/*
		if (player.getInventory().getItemInOffHand().getType() == Material.BLAZE_ROD) {
			for (int i = 0 ; i < 10 ; ++i) {
				Snowball s = player.launchProjectile(Snowball.class);
				s.setGravity(false);
			}
			world.playEffect(player.getLocation(), Effect.FIREWORK_SHOOT, 1);
		}*/
	}

	@Override
	public String getDisplayName() {
		return "Boules de neige";
	}

}
