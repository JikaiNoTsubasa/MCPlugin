package fr.triedge.minecraft.plugin.magic;

import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.entity.SmallFireball;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.BlockIterator;

public class SpellFireBall extends Spell{

	public SpellFireBall(Player player, FileConfiguration config, Plugin plugin) {
		super(player, config,plugin);
	}

	@Override
	public String getName() {
		return "fireball";
	}

	@Override
	public void launch() {
		SmallFireball fire = player.launchProjectile(SmallFireball.class);
		fire.setMetadata("player", new FixedMetadataValue(plugin, player.getName()));
		fire.setIsIncendiary(true);
		fire.setYield(0);
		World world = player.getWorld();
		world.playEffect(player.getLocation(), Effect.FIREWORK_SHOOT, 1);
		// Double handed
		/*
		if (player.getInventory().getItemInOffHand().getType() == Material.BLAZE_ROD) {
			for (int i = 0 ; i < 10 ; ++i) {
				SmallFireball f = player.launchProjectile(SmallFireball.class);
				f.setIsIncendiary(true);
				f.setYield(10);
			}
			world.playEffect(player.getLocation(), Effect.FIREWORK_SHOOT, 1);
		}
		*/
		BlockIterator it = new BlockIterator(player.getEyeLocation(),1,20);
		while(it.hasNext()) {
			Location loc = it.next().getLocation();
			world.spawnParticle(Particle.FLAME, loc, 10);
			if (loc.getBlock().getType().isSolid()) {
				break;
			}
		}
	}

	@Override
	public float getDamageFactor() {
		return 1.1f;
	}

	@Override
	public String getDisplayName() {
		return "Boules de feu";
	}

}
