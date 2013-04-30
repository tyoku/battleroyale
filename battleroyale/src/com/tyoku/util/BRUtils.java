package com.tyoku.util;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.map.MapView;
import org.bukkit.map.MapView.Scale;

import com.tyoku.BattleRoyale;
import com.tyoku.map.BrMapRender;

public class BRUtils {

    static public Integer String2Integer(String str) {
    	Integer ret = null;
        try {
        	ret = Integer.parseInt(str);
            return ret;
        } catch(NumberFormatException e) {
        	return null;
        }
    }

    /**
     * オンラインプレイヤー全員にメッセージを送ります。
     * @param plugin
     * @param message
     */
    static public void announce(BattleRoyale plugin, String message){
    	for (Player p : plugin.getServer().getOnlinePlayers()) {
            p.sendMessage(message);
        }
    }

    /**
     * プレイヤーがBRゲームエリア内に存在するか否かを判定する。
     * @param plugin
     * @param player
     * @return
     */
    static public boolean isGameArea(BattleRoyale plugin, Player player){
	    int x1 = plugin.getConfig().getInt("gamearea.pos1.x");
	    int z1 = plugin.getConfig().getInt("gamearea.pos1.z");
	    int x2 = plugin.getConfig().getInt("gamearea.pos2.x");
	    int z2 = plugin.getConfig().getInt("gamearea.pos2.z");
	    int w = 0;
	    if(x1 < x2){
	    	w = x1;
	    	x1 = x2;
	    	x2 = w;
	    }
	    if(z1 < z2){
	    	w = z1;
	    	z1 = z2;
	    	z2 = w;
	    }
	    int xp = player.getLocation().getBlockX();
	    int zp = player.getLocation().getBlockZ();
	    if(x1 >= xp && xp >= x2 && z1 >= zp && zp >= z2){
	    	return true;
	    }

    	return false;
    }

    /**
     * プレイヤーがBRゲームエリア内に存在するか否かを判定する。
     * @param plugin
     * @param player
     * @return
     */
    static public boolean isAlertArea(BattleRoyale plugin, Player player){
	    int x1 = plugin.getConfig().getInt("gamearea.pos1.x");
	    int z1 = plugin.getConfig().getInt("gamearea.pos1.z");
	    int x2 = plugin.getConfig().getInt("gamearea.pos2.x");
	    int z2 = plugin.getConfig().getInt("gamearea.pos2.z");
	    int w = 0;
	    if(x1 < x2){
	    	w = x1;
	    	x1 = x2;
	    	x2 = w;
	    }
	    if(z1 < z2){
	    	w = z1;
	    	z1 = z2;
	    	z2 = w;
	    }

	    int bufferArea = 5;
	    x1 -= bufferArea;
	    z1 -= bufferArea;
	    x2 += bufferArea;
	    z2 += bufferArea;

	    int xp = player.getLocation().getBlockX();
	    int zp = player.getLocation().getBlockZ();
	    if(x1 >= xp && xp >= x2 && z1 >= zp && zp >= z2){
	    	return false;
	    }

    	return true;
    }

    /**
     * 指定のプレイヤーを指定カウント後に爆死させる。
     * @param player
     * @param count
     */
    static public void deadCount(Player player, int count){
		for (int i = count; i > 0; i--) {
			try {
				player.sendMessage(ChatColor.RED + "爆発まで・・・"+i+"秒。");
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				System.out.println(e);
			}
		}
		float explosionPower = 4F;
		player.sendMessage(ChatColor.RED + "ByeBye!! BOOOOOOOOOOOM!!!");
		player.getWorld().createExplosion(player.getLocation(), explosionPower);
		player.setHealth(0);
    }

    /**
     * プレイヤーを部屋に飛ばす
     * @param plugin
     * @param player
     */
    static public void teleportRoom(BattleRoyale plugin, Player player){
	    Location nLoc = getRoomLocation(plugin, player.getWorld());
        player.teleport(nLoc);
        player.sendMessage(ChatColor.GOLD + "しばらくそこでおとなしくしててください。");
    }

    /**
     * 設定した部屋のロケーションを取得する。
     * @param plugin
     * @param world
     * @return
     */
    static public Location getRoomLocation(BattleRoyale plugin, World world){
	    //プリセット位置へプレイヤーを飛ばす
	    int x = plugin.getConfig().getInt("classroom.pos.x");
	    int y = plugin.getConfig().getInt("classroom.pos.y");
	    int z = plugin.getConfig().getInt("classroom.pos.z");
	    return new Location(world, x, y, z);
    }

    /**
     * 指定の番号を振ったBRマップを作成する。
     * @param plugin
     * @param player
     * @param mapNo
     * @return
     */
    static public ItemStack getBRMap(BattleRoyale plugin,Player player, short id){
	    ItemStack tmap = new ItemStack( Material.MAP, 1 ,id);
	    MapView mapview = plugin.getServer().getMap(id);
	    mapview.addRenderer(new BrMapRender());
		mapview.setCenterX(plugin.getConfig().getInt("classroom.pos.x"));
		mapview.setCenterZ(plugin.getConfig().getInt("classroom.pos.z"));
	    mapview.setWorld(player.getWorld());
	    mapview.setScale( Scale.FAR );
        return tmap;
    }

}
