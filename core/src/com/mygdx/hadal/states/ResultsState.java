package com.mygdx.hadal.states;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map.Entry;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.actors.ArtifactIcon;
import com.mygdx.hadal.actors.Backdrop;
import com.mygdx.hadal.actors.MenuWindow;
import com.mygdx.hadal.actors.Text;
import com.mygdx.hadal.equip.Loadout;
import com.mygdx.hadal.managers.AssetList;
import com.mygdx.hadal.managers.GameStateManager;
import com.mygdx.hadal.save.UnlockArtifact;
import com.mygdx.hadal.save.UnlockEquip;
import com.mygdx.hadal.schmucks.bodies.Player;
import com.mygdx.hadal.server.Packets;
import com.mygdx.hadal.server.SavedPlayerFields;
import com.mygdx.hadal.server.SavedPlayerFieldsExtra;
import com.mygdx.hadal.utils.Stats;

/**
 * The Results screen appears at the end of levels and displays the player's results
 * In this screen, the player can return to the hub when all players are ready.
 * @author Zachary Tu
 */
public class ResultsState extends GameState {

	//This table contains the options for the title.
	private Table table, tableInfo, tableInfoOuter, tableArtifact;
	private ScrollPane infoScroll;
	
	//These are all of the display and buttons visible to the player.
	private Text readyOption, forceReadyOption, infoPlayerName;
	
	//This is the playstate that the results state is placed on top of. Used to access the state's message window
	private PlayState ps;
	
	//This is a list of all the saved player fields (scores) from the completed playstate
	private ArrayList<SavedPlayerFields> scores;
	
	//This i sa mapping of players in the completed playstate mapped to whether they're ready to return to the hub.
	private HashMap<SavedPlayerFields, Boolean> ready;
	
	//this text is displayed at the top of the state and usually indicates victory or loss
	private String text;
	
	//Dimentions and position of the results menu
	private final static int width = 1000;
	private final static int baseHeight = 90;
	private final static int titleHeight = 40;
	private final static int rowHeight = 50;
	private final static int nameWidth = 400;
	private static final float scale = 0.4f;
	private static final int maxNameLen = 30;
	
	private final static int infoWidth = 280;
	private final static int infoHeight = 500;
	private final static int infoRowHeight = 20;
	private static final float infoTextScale = 0.25f;
	private static final float infoPadY = 15.0f;
	private static final float infoPadYSmall = 5.0f;
	
	public static final int infoNameHeight = 40;
	public static final int infoScrollHeight = 440;
	public static final int infoNamePadding = 20;
	
	public static final float artifactTagSize = 40.0f;
	private final static float artifactTagOffsetX = -100.0f;
	private final static float artifactTagOffsetY = -60.0f;
	private final static float artifactTagTargetWidth = 200.0f;
	
	/**
	 * Constructor will be called whenever the game transitions into a results state
	 * @param gsm
	 */
	public ResultsState(final GameStateManager gsm, String text, PlayState ps) {
		super(gsm);
		this.text = text;
		this.ps = ps;
		
		//First, we obtain the list of scores, depending on whether we are the server or client.
		scores = new ArrayList<SavedPlayerFields>();
		
		if (ps.isServer()) {
			scores = new ArrayList<SavedPlayerFields>(HadalGame.server.getScores().values());
			gsm.getRecord().updateScore(scores.get(0).getScore(), ps.level);
		} else {
			scores = new ArrayList<SavedPlayerFields>(HadalGame.client.getScores().values());
		}
		
		//Then, we sort according to score and give the winner(s) a win.
		Collections.sort(scores, new Comparator<SavedPlayerFields>() {

			@Override
			public int compare(SavedPlayerFields a, SavedPlayerFields b) {
				return b.getScore() - a.getScore();
			}
		});
		
		if (ps.isServer()) {
			int winningScore = scores.get(0).getScore();
			for (Entry<Integer, SavedPlayerFields> player: HadalGame.server.getScores().entrySet()) {
				if (player.getValue().getScore() == winningScore) {
					player.getValue().win();
				}
				
				if (player.getKey() == 0) {
					savePlayerLoadout(0, ps.getPlayer());
				} else {
					if (HadalGame.server.getPlayers().get(player.getKey()) != null) {
						savePlayerLoadout(player.getKey(), HadalGame.server.getPlayers().get(player.getKey()));
					}
				}
			}
			
			HadalGame.server.sendToAllTCP(new Packets.SyncExtraResultsInfo(HadalGame.server.getScoresExtra()));
		}
		
		//Finally we initialize the ready map with everyone set to not ready.
		ready = new HashMap<SavedPlayerFields, Boolean>();
		for (SavedPlayerFields score: scores) {
			ready.put(score, false);
		}
	}
	
	@Override
	public void show() {
		stage = new Stage() {
			{
				//table height scales to the number of players in the game
				int tableHeight = baseHeight + titleHeight * 2 + rowHeight * scores.size();
				
				addActor(new Backdrop(AssetList.RESULTS_CARD.toString()));
				addActor(new MenuWindow(0, HadalGame.CONFIG_HEIGHT - tableHeight, width, tableHeight));
				table = new Table();
				table.setPosition(0, HadalGame.CONFIG_HEIGHT - tableHeight);
				table.setSize(width, tableHeight);
				addActor(table);
				syncScoreTable();
				
				tableInfoOuter = new Table();

				infoPlayerName = new Text("", 0, 0, false);
				infoPlayerName.setScale(infoTextScale);
				
				addActor(new MenuWindow(HadalGame.CONFIG_WIDTH - infoWidth, HadalGame.CONFIG_HEIGHT - infoHeight, infoWidth, infoHeight));
				tableInfo = new Table();
				tableArtifact = new Table();

				infoScroll = new ScrollPane(tableInfo, GameStateManager.getSkin());
				infoScroll.setFadeScrollBars(false);
				
				tableInfoOuter.add(infoPlayerName).pad(infoNamePadding).height(infoNameHeight).row();
				tableInfoOuter.add(tableArtifact).height(infoNameHeight).row();
				tableInfoOuter.add(infoScroll).width(infoWidth).height(infoScrollHeight);
				tableInfoOuter.setPosition(HadalGame.CONFIG_WIDTH - infoWidth, HadalGame.CONFIG_HEIGHT - infoHeight);
				tableInfoOuter.setSize(infoWidth, infoHeight);
				
				addActor(tableInfoOuter);
			}
		};
		
		//we pull up and lock the playstate's message window so players can chat in the aftergame.
		if (!ps.getMessageWindow().isActive()) {
			ps.getMessageWindow().toggleWindow();
		}
		ps.getMessageWindow().setLocked(true);
		stage.addActor(ps.getMessageWindow().tableOuter);
		stage.addActor(ps.getMessageWindow().tableInner);
		
		gsm.getApp().fadeIn();
		app.newMenu(stage);
	}

	/**
	 * This is called whenever we set the displayed table of scores.
	 * This is done once at the start and once again whenever a player readies themselves.
	 */
	public void syncScoreTable() {
		table.clear();
		
		Text title = new Text(text, 0, 0, false);
		title.setScale(scale);
		
		Text playerLabel = new Text("PLAYER", 0, 0, false);
		playerLabel.setScale(scale);
		
		Text killsLabel = new Text("KILLS", 0, 0, false);
		killsLabel.setScale(scale);
		
		Text deathsLabel = new Text("DEATHS", 0, 0, false);
		deathsLabel.setScale(scale);
		
		Text scoreLabel = new Text("SCORE", 0, 0, false);
		scoreLabel.setScale(scale);
		
		Text statusLabel = new Text("STATUS", 0, 0, false);
		statusLabel.setScale(scale);
		
		table.add(title).height(titleHeight).colspan(5).row();
		table.add(playerLabel).width(nameWidth).height(titleHeight).padRight(20);
		table.add(killsLabel).height(titleHeight).padRight(20);
		table.add(deathsLabel).height(titleHeight).padRight(20);
		table.add(scoreLabel).height(titleHeight).padRight(20);
		table.add(statusLabel).height(titleHeight).row();
		
		for (SavedPlayerFields score: scores) {
			
			Text name = new Text(score.getNameAbridged(true, maxNameLen), 0, 0, false);
			name.setColor(Color.RED);
			name.setScale(scale);
			
			//mousing over player name brings up window with extra information
			name.addListener(new ClickListener() {
		        
				@Override
				public void enter (InputEvent event, float x, float y, int pointer, Actor fromActor) {
					super.enter(event, x, y, pointer, fromActor);
					syncInfoTable(score.getConnID());
				}
		    });
			
			Text kills = new Text(score.getKills() + " ", 0, 0, false);
			kills.setScale(scale);
			Text death = new Text(score.getDeaths() + " ", 0, 0, false);
			death.setScale(scale);
			Text points = new Text(score.getScore() + " ", 0, 0, false);
			points.setScale(scale);
			Text status = new Text(ready.get(score) ? "READY" : "WAITING", 0, 0, false);
			status.setScale(scale);

			table.add(name).width(nameWidth).height(rowHeight).padBottom(25);
			table.add(kills).height(rowHeight).padBottom(25);
			table.add(death).height(rowHeight).padBottom(25);
			table.add(points).height(rowHeight).padBottom(25);
			table.add(status).height(rowHeight).padBottom(25).row();
		}
		
		readyOption = new Text("RETURN TO LOADOUT?", 0, 0, true);
		readyOption.setColor(Color.RED);
		
		readyOption.addListener(new ClickListener() {
	        
			@Override
			public void clicked(InputEvent e, float x, float y) {
	        	
	        	//When pressed, the ready option indicates to the server that that player is ready.
	        	if (ps.isServer()) {
	        		readyPlayer(0);
	        	} else {
	        		HadalGame.client.sendTCP(new Packets.ClientReady());
	        	}
	        }
	    });
		readyOption.setScale(scale);
		
		forceReadyOption = new Text("FORCE RETURN?", 0, 0, true);
		forceReadyOption.setColor(Color.RED);
		
		forceReadyOption.addListener(new ClickListener() {
	        
			@Override
			public void clicked(InputEvent e, float x, float y) {
	        	
	        	//When pressed, the force ready option forces a transition.
				returnToHub();
	        }
	    });
		forceReadyOption.setScale(scale);
		
		table.add(readyOption).width(nameWidth);
		if (ps.isServer()) {
			table.add(forceReadyOption).colspan(4);
		}
	}
	
	/**
	 * This fills the window with stats for the designated player
	 */
	public void syncInfoTable(int connId) {
		tableInfo.clear();
		tableArtifact.clear();
		
		SavedPlayerFields field = null;
		SavedPlayerFieldsExtra fieldExtra = null;
		if (ps.isServer()) {
			field = HadalGame.server.getScores().get(connId);
			fieldExtra = HadalGame.server.getScoresExtra().get(connId);
		} else {
			field = HadalGame.client.getScores().get(connId);
			fieldExtra = HadalGame.client.getScoresExtra().get(connId);
		}
		
		if (field != null && fieldExtra != null) {
			
			infoPlayerName.setText(field.getNameAbridged(false, maxNameLen));
			
			//display player's loadout (if synced properly)
			if (fieldExtra.getLoadout() != null) {
				
				for (UnlockArtifact c: fieldExtra.getLoadout().artifacts) {
					if (!c.equals(UnlockArtifact.NOTHING)) {
						ArtifactIcon newTag = new ArtifactIcon(c, c.getInfo().getName() + "\n" + c.getInfo().getDescription(), artifactTagOffsetX, artifactTagOffsetY, artifactTagTargetWidth);
						tableArtifact.add(newTag).width(artifactTagSize).height(artifactTagSize);;
					}
				}
				
				for (int i = 0; i < Loadout.maxWeaponSlots; i++) {
					if (!fieldExtra.getLoadout().multitools[i].equals(UnlockEquip.NOTHING)) {
						Text weaponField = new Text("WEAPON" + (i + 1) + ": ", 0, 0, false);
						weaponField.setScale(infoTextScale);
						Text weapon = new Text(fieldExtra.getLoadout().multitools[i].name(), 0, 0, false);
						weapon.setScale(infoTextScale);
						tableInfo.add(weaponField).height(infoRowHeight).left().padBottom(infoPadYSmall);
						tableInfo.add(weapon).height(infoRowHeight).left().padBottom(infoPadYSmall).row();
					}
				}
				Text activeField = new Text("ACTIVE: ", 0, 0, false);
				activeField.setScale(infoTextScale);
				Text active = new Text(fieldExtra.getLoadout().activeItem.name(), 0, 0, false);
				active.setScale(infoTextScale);
				tableInfo.add(activeField).height(infoRowHeight).left().padBottom(infoPadYSmall);
				tableInfo.add(active).height(infoRowHeight).left().padBottom(infoPadYSmall).row();
			}
			
			Text damageDealtField = new Text("DAMAGE DEALT: ", 0, 0, false);
			damageDealtField.setScale(infoTextScale);
			
			Text damageAllyField = new Text("FRIENDLY FIRE: ", 0, 0, false);
			damageAllyField.setScale(infoTextScale);
			
			Text damageSelfField = new Text("SELF-DAMAGE: ", 0, 0, false);
			damageSelfField.setScale(infoTextScale);
			
			Text damageReceivedField = new Text("DAMAGE RECEIVED: ", 0, 0, false);
			damageReceivedField.setScale(infoTextScale);
			
			Text damageDealt = new Text("" + (int) fieldExtra.getDamageDealt(), 0, 0, false);
			damageDealt.setScale(infoTextScale);
			
			Text damageAlly = new Text("" + (int) fieldExtra.getDamageDealtAllies(), 0, 0, false);
			damageAlly.setScale(infoTextScale);
			
			Text damageSelf = new Text("" + (int) fieldExtra.getDamageDealtSelf(), 0, 0, false);
			damageSelf.setScale(infoTextScale);
			
			Text damageReceived = new Text("" + (int) fieldExtra.getDamageReceived(), 0, 0, false);
			damageReceived.setScale(infoTextScale);
			
			tableInfo.add(damageDealtField).height(infoRowHeight).padBottom(infoPadY);
			tableInfo.add(damageDealt).height(infoRowHeight).padBottom(infoPadY).row();
			
			tableInfo.add(damageAllyField).height(infoRowHeight).padBottom(infoPadY);
			tableInfo.add(damageAlly).height(infoRowHeight).padBottom(infoPadY).row();
			
			tableInfo.add(damageSelfField).height(infoRowHeight).padBottom(infoPadY);
			tableInfo.add(damageSelf).height(infoRowHeight).padBottom(infoPadY).row();
			
			tableInfo.add(damageReceivedField).height(infoRowHeight).padBottom(infoPadY);
			tableInfo.add(damageReceived).height(infoRowHeight).padBottom(infoPadY).row();
		} else {
			infoPlayerName.setText("");
		}
	}
	
	/**
	 * This is pressed whenever a player gets ready.
	 * @param playerId: If this is run by the server, this is the player's connID (or 0, if the host themselves).
	 * For the client, playerId is the index in scores of thte player that readies.
	 */
	public void readyPlayer(int playerId) {
		if (ps.isServer()) {
			
			//The server finds the player that readies, sets their readiness and informs all clients by sending that player's index
			SavedPlayerFields field = HadalGame.server.getScores().get(playerId);
			if (field != null) {
				ready.put(field, true);
				HadalGame.server.sendToAllTCP(new Packets.ClientReady(scores.indexOf(field)));
			}
		} else {
			
			//Clients just find the player based on that index and sets them as ready.
			ready.put(scores.get(playerId), true);
		}
		
		//When all players are ready, reddy will be true and we return to the hub
		boolean reddy = true;
		for (boolean b: ready.values()) {
			if (!b) {
				reddy = false;
			}
		}
		
		//sync score table to display new readiness
		syncScoreTable();
		
		//When the server is ready, we return to hub and tell all clients to do the same.
		if (reddy) {
			returnToHub();
		}
	}

	/**
	 * This returns us to the hub when everyone readies up
	 */
	public void returnToHub() {
		if (ps.isServer()) {
			gsm.getApp().setRunAfterTransition(new Runnable() {

				@Override
				public void run() {
					gsm.removeState(ResultsState.class);
					gsm.gotoHubState();
				}
			});
		}
		gsm.getApp().fadeOut();
	}
	
	private void savePlayerLoadout(int connId, Player player) {
		
		if (HadalGame.server.getScoresExtra().get(connId) != null) {
			
			Loadout loadoutTemp = player.getPlayerData().getLoadout();
			
			for (int i = (int) (Loadout.baseWeaponSlots + player.getPlayerData().getStat(Stats.WEAPON_SLOTS)); i < Loadout.maxWeaponSlots ; i++) {
				loadoutTemp.multitools[i] = UnlockEquip.NOTHING;
			}
			HadalGame.server.getScoresExtra().get(connId).setLoadout(loadoutTemp);
		}
	}
	
	//we update the message window to take input
	@Override
	public void update(float delta) {
		ps.getMessageWindow().tableOuter.act(delta);
		ps.getMessageWindow().tableInner.act(delta);
	}

	@Override
	public void render(float delta) {}

	@Override
	public void dispose() {	stage.dispose(); }
	
	public PlayState getPs() { return ps; }
}
