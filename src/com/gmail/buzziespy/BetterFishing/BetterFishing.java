package com.gmail.buzziespy.BetterFishing;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

public final class BetterFishing extends JavaPlugin implements Listener{
	
	@Override
	public void onEnable()
	{
		//enable the listener
		getServer().getPluginManager().registerEvents(this, this);
	}
	
	@Override
	public void onDisable()
	{
		
	}
	
	//feature: alter the original fishing mechanic to give chance of pulling more than
	//one fish up at a time, as well as a small chance of reeling in nearly-worn boots.
	//Chances: 85% fish, 5% leather boots, 2% iron boots, 2% chain boots, 5% gold boots, 1% diamond boots
	//can store these as final variables at the top of the code
	
	//of that 85% fish, 20% of 1/2/3/4/5 fish caught
	//all boots should have between 13-17% total durability
	
	//This is intended to make fishing more fun, since currently fishing is slow and
	//tedious and not a great way to acquire food.
	@EventHandler
	public void reelingInRod(PlayerFishEvent e)
	{
		getLogger().info(""+e.getState().toString());
		
		if (e.getState().equals(PlayerFishEvent.State.CAUGHT_FISH))
		{
			//this returns an Item
			getLogger().info("" + e.getCaught().toString());
			
			//Item caughtLoot = (Item)e.getCaught();
			
			Location catchPoint = e.getHook().getLocation();
			
			Location playerLoc = e.getPlayer().getLocation();
			
			//the item's velocity is a regular drop-item velocity
			//need to recalculate this one from scratch to get the loot to fly towards the player
			Vector v = new Vector((playerLoc.getX()-catchPoint.getX())/12, (playerLoc.getY()-catchPoint.getY())/12 + 0.33, (playerLoc.getZ()-catchPoint.getZ())/12);
			
			//split code based on what is caught
			double x = Math.random();
			if (x < 0.85) //fish
			{
				int numFish = (int)(Math.random()*5 + 1);
				
				//swap out the vanilla caught fish with the new buffed(?) fish drops
				ItemStack i = new ItemStack(Material.RAW_FISH, numFish);
				Item fish = e.getPlayer().getWorld().dropItem(catchPoint, i);
				fish.setVelocity(v);
				
			}
			else if (x < 0.9) //leather boots
			{
				double durabilityModifier = 0.04 * Math.random();
				double durabilityPercent = 0.13 + durabilityModifier;
				
				//66 = max durability of leather boots
				short newDura = (short)(int)(66 * (1-durabilityPercent));
				
				ItemStack i = new ItemStack(Material.LEATHER_BOOTS, 1, newDura);
				Item ii = e.getPlayer().getWorld().dropItem(catchPoint, i);
				ii.setVelocity(v);
			}
			else if (x < 0.92) //chain boots
			{
				double durabilityModifier = 0.04 * Math.random();
				double durabilityPercent = 0.13 + durabilityModifier;
				
				//196 = max durability of chain boots
				short newDura = (short)(int)(196 * (1-durabilityPercent));
				
				ItemStack i = new ItemStack(Material.CHAINMAIL_BOOTS, 1, newDura);
				Item ii = e.getPlayer().getWorld().dropItem(catchPoint, i);
				ii.setVelocity(v);
			}
			else if (x < 0.94) //iron boots
			{
				double durabilityModifier = 0.04 * Math.random();
				double durabilityPercent = 0.13 + durabilityModifier;
				
				//196 = max durability of iron boots
				short newDura = (short)(int)(196 * (1-durabilityPercent));
				
				ItemStack i = new ItemStack(Material.IRON_BOOTS, 1, newDura);
				Item ii = e.getPlayer().getWorld().dropItem(catchPoint, i);
				ii.setVelocity(v);
			}
			else if (x < 0.99) //gold boots
			{
				double durabilityModifier = 0.04 * Math.random();
				double durabilityPercent = 0.13 + durabilityModifier;
				
				//92 = max durability of gold boots
				short newDura = (short)(int)(92 * (1-durabilityPercent));
				
				ItemStack i = new ItemStack(Material.GOLD_BOOTS, 1, newDura);
				Item ii = e.getPlayer().getWorld().dropItem(catchPoint, i);
				ii.setVelocity(v);
			}
			else //diamond boots
			{
				double durabilityModifier = 0.04 * Math.random();
				double durabilityPercent = 0.13 + durabilityModifier;
				
				//430 = max durability of gold boots
				short newDura = (short)(int)(430 * (1-durabilityPercent));
				
				ItemStack i = new ItemStack(Material.DIAMOND_BOOTS, 1, newDura);
				Item ii = e.getPlayer().getWorld().dropItem(catchPoint, i);
				ii.setVelocity(v);
			}
			
			//remove the vanilla-caught fish
			e.getCaught().remove();
			
		}
	}
	
	//feature: if TNT (or Creeper?) is detonated in the water, spawn a number of fish
	//depending on the depth of the TNT when it explodes and send them flying up out
	//of the water.  A number of the blown-up fish can be cooked (flash-fried).
	//Currently thinking a linear trend from 0-1 fish for 1-block-deep water to ~40 fish
	//for 20-block-deep water.  Obviously the vertical component of the fish item velocity
	//must be adjusted for each depth so the fish always breaks the surface at a visible
	//speed.
	//Note that the depth should only be calculated up to 64; it would be too easy to simply
	//place water up to sky limit and then detonate TNT at bedrock.
	@EventHandler
	public void blowingUpFish(EntityExplodeEvent e)
	{
		//make sure to look at only TNT and creepers, since other things can explode
		//too (eg. Ghast fireballs, Wither fireballs)
		//though allowing Withers to kill fish would be interesting...if you submerge
		//a Wither in water at the bottom(?) of a glass column filled with water and
		//place hoppers at the bottom...fish grinder?
		
		//getLogger().info("Boom!");
		//getLogger().info("" + e.getLocation().toString());
		if (e.getEntityType().equals(EntityType.CREEPER) || e.getEntityType().equals(EntityType.PRIMED_TNT))
		{
			//getLogger().info("" + e.getLocation().getBlock().toString());
			if (e.getLocation().getBlock().getType().equals(Material.WATER) || e.getLocation().getBlock().getType().equals(Material.STATIONARY_WATER))
			{
				//getLogger().info("Industrial fishing under way!");
				//count number of water blocks deep
				Block b = e.getLocation().getBlock();
				int depth = 1;
				while (b.getRelative(0, 1, 0).getType().equals(Material.WATER) || b.getRelative(0, 1, 0).getType().equals(Material.STATIONARY_WATER))
				{
					
					depth++;
					b = b.getRelative(0, 1, 0);
				}
				
				int fishCount = depth*2 - 1 + (int)(Math.random()*2);
				
				Location explosion = e.getLocation();
				
				getLogger().info("" + depth);
				
				for (int x = 0; x < fishCount; x++)
				{
					//randomly choose an angle and radius from explosion point
					//angle can be from 0 - 2*pi
					//radius between 0 and 1
					double angle = Math.random() * 2 * Math.PI;
					double radius = Math.random();
					
					//calculate location from angle and radius
					
					//Location base = new Location(e.getEntity().getWorld(), explosion.getX() + 1, explosion.getY(), explosion.getZ());
					//Vector zero = new Vector(1, 0, 0); //base vector for calculating angles 
					
					//inverting sign of Z because Minecraft is silly and made north a region where z < 0
					double newX = Math.cos(angle) * radius;
					double newZ = -(Math.sin(angle) * radius);
					
					Location blastFishLoc = new Location(e.getEntity().getWorld(), explosion.getX() + newX, explosion.getY(), explosion.getZ() + newZ);
					
					//calculate velocity vector based on radius - the farther away from explosion point,
					//the more horizontal and the less vertical the velocity becomes
					//y-component needs to be more or less constant to ensure fish break surface of water
					//so modulate x/z-components
					
					double yComp = depth/6 + 0.5;
					double xComp = radius * newX/6;
					double zComp = radius * newZ/6;
					
					Vector v = new Vector(xComp, yComp, zComp);
					
					//check for possibility of cooked fish; this becomes more likely the closer the fish is
					//to the explosion point - ie, if radius is 0, then 100% chance of fish being cooked,
					//and vice versa for radius 1 (0% chance of fish being cooked)
					
					double cookChance = Math.random();
					if (cookChance < (1-radius)) //spawn cooked fish
					{
						ItemStack f = new ItemStack(Material.COOKED_FISH);
						Item fish = e.getEntity().getWorld().dropItem(blastFishLoc, f);
						fish.setVelocity(v);
					}
					else //spawn raw fish
					{
						ItemStack f = new ItemStack(Material.RAW_FISH);
						Item fish = e.getEntity().getWorld().dropItem(blastFishLoc, f);
						fish.setVelocity(v);
					}
				}
				
			}
		}
	}
	
	

}
