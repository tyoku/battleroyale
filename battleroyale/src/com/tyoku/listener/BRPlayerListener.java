package com.tyoku.listener;

import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.scheduler.BukkitTask;

import com.tyoku.BattleRoyale;
import com.tyoku.dto.BRGameStatus;
import com.tyoku.dto.BRPlayer;
import com.tyoku.dto.BRPlayerStatus;
import com.tyoku.tasks.CountOfDead;
import com.tyoku.util.BRUtils;

public class BRPlayerListener implements Listener {
	private Logger log;
	private BattleRoyale plugin;

	public BRPlayerListener(BattleRoyale battleRoyale) {
		this.plugin = battleRoyale;
		this.log = battleRoyale.getLogger();
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
	    Player player = event.getPlayer();

	    //プリセット位置へプレイヤーを飛ばす
	    int x = this.plugin.getConfig().getInt("classroom.pos.x");
	    int y = this.plugin.getConfig().getInt("classroom.pos.y");
	    int z = this.plugin.getConfig().getInt("classroom.pos.z");

	    if(y == -100){
		    x = player.getLocation().getBlockX();
		    y = player.getLocation().getBlockY();
		    z = player.getLocation().getBlockZ();
		    this.plugin.getConfig().set("classroom.pos.x",x);
		    this.plugin.getConfig().set("classroom.pos.y",y);
		    this.plugin.getConfig().set("classroom.pos.z",z);
		    this.plugin.saveConfig();
	    }
	    World w = player.getWorld();
	    Location nLoc = new Location(w, x, y, z);
	    this.log.info(String.format("プレイヤーを(X:%d Y:%d Z:%d)へ転送", x,y,z));
        player.teleport(nLoc);

		//プレイヤーリスト作成
		BRPlayer brps = new BRPlayer();
		brps.setName(player.getName());
		String appendMsg = "";
		if(BRGameStatus.OPENING.equals(this.plugin.getBrManager().getGameStatus())
				|| BRGameStatus.PREPARE.equals(this.plugin.getBrManager().getGameStatus())
				){
			brps.setStatus(BRPlayerStatus.PLAYING);
		}else{
			brps.setStatus(BRPlayerStatus.DEAD);
			appendMsg = "ゲームは既に始まっています。次回、ご参加ください。";
		}
		this.plugin.getPlayerStat().put(brps.getName(), brps);

        player.sendMessage(ChatColor.GOLD + "バトロワへようこそ！"+appendMsg);

	}

	@EventHandler
	public void onPlayerRespawn(PlayerRespawnEvent event) {
	    Player player = event.getPlayer();

	    //プリセット位置へプレイヤーを飛ばす
	    int x = this.plugin.getConfig().getInt("classroom.pos.x");
	    int y = this.plugin.getConfig().getInt("classroom.pos.y");
	    int z = this.plugin.getConfig().getInt("classroom.pos.z");

	    World w = player.getWorld();
	    Location nLoc = new Location(w, x, y, z);
        player.teleport(nLoc);
	    this.log.info(String.format("プレイヤーを(X:%d Y:%d Z:%d)へ転送", x,y,z));
        player.sendMessage(ChatColor.GOLD + "しばらくそこでおとなしくしててください。");
	}

	@EventHandler
	public void onPlayerMove(PlayerMoveEvent event){
	    Player player = event.getPlayer();
	    BRPlayer brp = this.plugin.getPlayerStat().get(player.getName());

	    if(!player.isOnline()
	    		|| BRPlayerStatus.DEAD.equals(brp.getStatus())
	    		|| !BRGameStatus.PLAYING.equals(this.plugin.getBrManager().getGameStatus())
	    		){

	        player.sendMessage(ChatColor.RED + "RETURN");
	    	return;
	    }
	    if(!BRUtils.isGameArea(this.plugin, player)){
	        player.sendMessage(ChatColor.RED + "ゲームエリア外");
	        @SuppressWarnings("unused")
			BukkitTask task = new CountOfDead(this.plugin, player).runTaskLater(plugin, 20);
	    }else{
	        player.sendMessage(ChatColor.GOLD + "ゲームエリア内");
	    }
	}
}
