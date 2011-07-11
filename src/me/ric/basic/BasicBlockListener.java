package me.ric.basic;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.bukkit.Material;
import org.bukkit.block.Block;
//import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.BlockPlaceEvent;


/**
* Handle all Block related events
*/
public class BasicBlockListener extends BlockListener {
//    private final Basic plugin;
	public HashMap<Player, Integer> diamondMiners = new HashMap<Player, Integer>();

    public BasicBlockListener(Basic instance) {
//        plugin = instance;
    }

    public void onBlockBreak(BlockBreakEvent event) {
        Block block = event.getBlock();

        if (block.getType() == Material.SAND) {
//        	Integer existingDiamondOre;
        	Integer updatedDiamondOre = 0;
//        if (block.getType() == Material.DIAMOND_ORE) {
        	Player player = event.getPlayer();
        	Integer existingDiamondOre=diamondMiners.remove(player);
        	if (existingDiamondOre == null) {
        		updatedDiamondOre=1;
        	}
        	else {
        		updatedDiamondOre=existingDiamondOre+1;
        	}
        	diamondMiners.put(player, updatedDiamondOre);
        	if (updatedDiamondOre>3) {
	        	player.sendMessage("You have just broken four sand");
        	}
        }
    }
    public void onBlockPlace(BlockPlaceEvent event) {
        Block block = event.getBlock();

        if (block.getType() == Material.TORCH) {
//            Block below = block.getFace(BlockFace.DOWN);
//            if (below.getType() == Material.SAND) {
//                System.out.println("Player just placed a sand block on a sand block");
//            }
//        }
//        if (block.getType() == Material.SAND) {
        	event.getPlayer().sendMessage("You have just placed a torch");
         System.out.println(event.getPlayer().getName() +" just placed a torch");
        }
    }
}
