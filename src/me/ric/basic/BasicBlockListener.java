package me.ric.basic;

import org.bukkit.Material;
import org.bukkit.block.Block;
//import org.bukkit.block.BlockFace;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.BlockPlaceEvent;


/**
* Handle events for all Player related events
* @author Dinnerbone
*/
public class BasicBlockListener extends BlockListener {
//    private final Basic plugin;

    public BasicBlockListener(Basic instance) {
//        plugin = instance;
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
