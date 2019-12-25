package com.mygdx.hadal.server;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map.Entry;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.KryoSerialization;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.client.KryoClient;
import com.mygdx.hadal.equip.Loadout;
import com.mygdx.hadal.managers.GameStateManager;
import com.mygdx.hadal.schmucks.SavePoint;
import com.mygdx.hadal.schmucks.bodies.MouseTracker;
import com.mygdx.hadal.schmucks.bodies.Player;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.states.GameState;
import com.mygdx.hadal.states.PauseState;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.states.ResultsState;

/**
 * This is the server of the game.
 * @author Zachary Tu
 *
 */
public class KryoServer {
	
	//Me server
	private Server server;
	
	//This is the gsm of the server
	public GameStateManager gsm;
	
	//These keep track of all connected players, their mice and scores. Scores is the only one that contains the host.
	private HashMap<Integer, Player> players;
	private HashMap<Integer, MouseTracker> mice;
	private HashMap<Integer, SavedPlayerFields> scores;
	
	public KryoServer(GameStateManager gameStateManager) {
		this.gsm = gameStateManager;
	}
	
	/**
	 * This is called upon starting a new server. initialize server and tracked client data 
	 */
	public void init(boolean start) {
		Kryo kryo = new Kryo();
		kryo.setReferences(true);
		KryoSerialization serialization = new KryoSerialization(kryo);
		this.server = new Server(16384, 2048, serialization);
		this.players = new HashMap<Integer, Player>();
		this.mice = new HashMap<Integer, MouseTracker>();
		this.scores = new HashMap<Integer, SavedPlayerFields>();
		
		scores.put(0, new SavedPlayerFields(gsm.getRecord().getName()));
		
		if (!start) {
			return;
		}
		
		server.addListener(new Listener() {
			
			@Override
			public void disconnected(final Connection c) {
				final PlayState ps = getPlayState();
				
				if (ps != null) {
					
					//Identify the player that disconnected
					final Player p = players.get(c.getID());
					ps.addPacketEffect(new PacketEffect() {

						@Override
						public void execute() {
							if (p != null) {
								
								//Inform all that the player disconnected and kill the player
								addNotificationToAllExcept(ps, c.getID(), p.getName(), "PLAYER DISCONNECTED!");
								p.getPlayerData().die(ps.getWorldDummy().getBodyData(), null);
								
								//remove disconnecting player from all tracked lists
								players.remove(c.getID());
								mice.remove(c.getID());
								scores.remove(c.getID());
								ps.getScoreWindow().syncTable();
							}
						}
					});
				}
				
				/**
				 * If in a victory state, count a disconnect as ready so disconnected players don't prevent return to hub.
				 */
				if (!gsm.getStates().empty() && gsm.getStates().peek() instanceof ResultsState) {
					final ResultsState vs =  (ResultsState) gsm.getStates().peek();
					Gdx.app.postRunnable(new Runnable() {
        				
                        @Override
                        public void run() {
                        	vs.readyPlayer(c.getID());
                        }
					});
				}
			}
			
			public void received(final Connection c, Object o) {

				/*
				 * The Client has connected.
				 * Notify clients and create a new player for the client. Also, tell the new client what level to load
				 */
				if (o instanceof Packets.PlayerConnect) {
					final Packets.PlayerConnect p = (Packets.PlayerConnect) o;
					final PlayState ps = getPlayState();
					
					if (ps != null) {
						if (p.firstTime) {
							addNotificationToAllExcept(ps, c.getID(), p.name, "PLAYER CONNECTED!");
						}
                        sendToTCP(c.getID(), new Packets.LoadLevel(ps.getLevel(), p.firstTime));
					}
				}
				
				/*
				 * The Client has loaded the level.
				 * Announce the new player joining and catchup the new client.
				 */
				if (o instanceof Packets.ClientLoaded) {
					final Packets.ClientLoaded p = (Packets.ClientLoaded) o;
					final PlayState ps = getPlayState();
					
					//If we are paused, inform the connected client we are paused.
					if (!gsm.getStates().empty() && gsm.getStates().peek() instanceof PauseState) {
						PauseState pss = ((PauseState)gsm.getStates().peek());
						HadalGame.server.sendToTCP(c.getID(), new Packets.Paused(pss.getPauser()));
					}
					
					//notify players of new joiners
					if (p.firstTime) {
						sendNotification(ps, c.getID(), ps.getPlayer().getName(), "JOINED SERVER!");
					}
					
					//If the client has already been created, we create a new player, otherwise we reuse their old data.
					final Player player = players.get(c.getID());
					if (player != null) {
						if (ps.isReset()) {
							createNewClientPlayer(ps, c.getID(), p.name, p.loadout, player.getPlayerData(), ps.isReset()); 
						} else {
							createNewClientPlayer(ps, c.getID(), p.name, player.getPlayerData().getLoadout(), player.getPlayerData(), ps.isReset()); 
						}
					} else {
						createNewClientPlayer(ps, c.getID(), p.name, p.loadout, null, true);                        
					}
					
					//catch up client
					if (ps != null) {
						ps.addPacketEffect(new PacketEffect() {

							@Override
							public void execute() {
		                        ps.catchUpClient(c.getID());
							}
						});
					}
				}
				
				/*
				 * The Client has loaded the level.
				 * Announce the new player joining and catchup the new client.
				 */
				if (o instanceof Packets.ClientPlayerCreated) {
					final Player player = players.get(c.getID());
					if (player != null) {
						HadalGame.server.sendToAllTCP(new Packets.SyncServerLoadout(player.getEntityID().toString(), player.getPlayerData().getLoadout()));
					}
				}
				
				/*
				 * The Client has changed their loadout (in the hub because anywhere else is handled server side)
				 * Find the client changing and update their player loadout
				 */
				if (o instanceof Packets.SyncClientLoadout) {
        			final Packets.SyncClientLoadout p = (Packets.SyncClientLoadout) o;
        			final Player player = players.get(c.getID());
    				final PlayState ps = getPlayState();
    				
    				if (ps != null && player != null) {
    					ps.addPacketEffect(new PacketEffect() {
    						
    						@Override
							public void execute() {
    							player.getPlayerData().syncLoadoutFromClient(p.equip, p.artifactAdd, p.artifactRemove, p.active, p.character);
                				player.getPlayerData().syncServerLoadoutChange();
    						}
        				});
    				}
        		}
				
				/*
				 * The client has finished respawning (after transitioning to black.
				 * We spawn a new player for them
				 */
				if (o instanceof Packets.ClientFinishRespawn) {
					final PlayState ps = getPlayState();
					
					//acquire the client's name and data
					String playerName = "";
					Player player = players.get(c.getID());
					if (player != null) {
						playerName = player.getName();
					}
					createNewClientPlayer(ps, c.getID(), playerName, player.getPlayerData().getLoadout(), null, true);
				}
				
				/*
				 * A Client has unpaused the game
				 * Return to the PlayState and inform everyone who unpaused.
				 */
				if (o instanceof Packets.Unpaused) {
        			if (!gsm.getStates().empty() && gsm.getStates().peek() instanceof PauseState) {
        				
        				Player p = players.get(c.getID());
        				if (p != null) {
        					final PauseState cs = (PauseState) gsm.getStates().peek();
            				cs.setToRemove(true);
            				HadalGame.server.sendToAllExceptTCP(c.getID(), new Packets.Unpaused(p.getName()));
        				}
        			}
				}
				
				/*
				 * A Client has sent the server a notification.
				 * Display the notification and echo it to all clients  
				 */
				if (o instanceof Packets.Notification) {
					final Packets.Notification p = (Packets.Notification) o;
					final PlayState ps = getPlayState();
					if (ps != null) {
						addNotificationToAll(ps, p.name, p.text);
					}
				}
				
				/*
				 * A Client has said they are ready to return to hub state from results state
				 * Ready that player in the results state.
				 */
				if (o instanceof Packets.ClientReady) {
					if (!gsm.getStates().empty() && gsm.getStates().peek() instanceof ResultsState) {
						final ResultsState vs =  (ResultsState) gsm.getStates().peek();
						Gdx.app.postRunnable(new Runnable() {
	        				
	                        @Override
	                        public void run() {
	                        	vs.readyPlayer(c.getID());
	                        }
						});
					}
				}
				
				/*
				 * A Client has performed an action involving pressing a key down
				 * Register the keystroke for that client's player.
				 */
				if (o instanceof Packets.KeyDown) {
					final Packets.KeyDown p = (Packets.KeyDown) o;
					final PlayState ps = getPlayState();
					final Player player = players.get(c.getID());
					if (ps != null && player != null) {
						
						ps.addPacketEffect(new PacketEffect() {
    						
    						@Override
							public void execute() {
    							if (player.getController() != null) {
    								player.getController().keyDown(p.action);
    							}
    						}
        				});
					}	
				}
				
				/*
				 * A Client has performed an action involving pressing a key up
				 * Register the keystroke for that client's player.
				 */
				if (o instanceof Packets.KeyUp) {
					final Packets.KeyUp p = (Packets.KeyUp) o;
					final PlayState ps = getPlayState();
					final Player player = players.get(c.getID());
					if (ps != null && player != null) {
						ps.addPacketEffect(new PacketEffect() {
    						
    						@Override
							public void execute() {
    							if (player.getController() != null) {
    								player.getController().keyUp(p.action);
    							}
    						}
        				});
					}
				}
				
				/*
				 * A Client sends this every engine tick to send their mouse location.
				 * Update the client's player's mouse pointer.
				 */
				if (o instanceof Packets.MouseMove) {
					final Packets.MouseMove p = (Packets.MouseMove) o;
					final PlayState ps = getPlayState();
					final MouseTracker mouse = mice.get(c.getID());
					if (ps != null && mouse != null) {
						mouse.setDesiredLocation(p.x, p.y);
					}
				}
			}
		});
		
		try {
			server.bind(KryoClient.tcpPortSocket, KryoClient.udpPortSocket);
		} catch (IOException e) {
			e.printStackTrace();
		}

		registerPackets();
		server.start();
	}
	
	/**
	 * This is called whenever the server creates a new player for a client
	 * @param ps: This is the server's current play state
	 * @param connId: This is the connId of the client requesting a new player
	 * @param name: The name of the new player
	 * @param loadout: The loadout of the new player
	 * @param data: The player data of the new player.
	 * @param reset: Do we want to reset the new player's hp/fuel/ammo etc?
	 * @param firstTime: Is this the first time we are spawning this player?
	 */
	public void createNewClientPlayer(final PlayState ps, final int connId, final String name, final Loadout loadout, final PlayerBodyData data, final boolean reset) {

		ps.addPacketEffect(new PacketEffect() {

			@Override
			public void execute() {
				SavePoint newSave = ps.getSavePoint();
				
				//Create a new player with the designated fields and give them a mouse pointer.
				Player newPlayer = ps.createPlayer(newSave.getLocation(), name, loadout, data, connId, reset);
		        MouseTracker newMouse = new MouseTracker(ps, false);
		        newPlayer.setMouse(newMouse);
		        players.put(connId, newPlayer);
		        mice.put(connId, newMouse);
		        
		        //Update that player's scores or give them a new one if they are a new client
		        if (scores.containsKey(connId)) {
			        scores.put(connId, scores.get(connId));
		        } else {
			        scores.put(connId, new SavedPlayerFields(name));
		        }
		        
		        //sync score window to display new player
		        ps.getScoreWindow().syncTable();
		        
		        //Inform the client that their new player has been created and give them their new id
		        server.sendToTCP(connId, new Packets.NewClientPlayer(newPlayer.getEntityID().toString()));
		        
		        Vector2 zoomPos = newSave.getZoomLocation();
		        
		        server.sendToTCP(connId, new Packets.SyncCamera(zoomPos, newSave.getZoom(), ps.getCameraBounds(), ps.getCameraBounded()));
		        server.sendToTCP(connId, new Packets.SyncUI(ps.getUiExtra().getCurrentTags(), ps.getUiExtra().getTimer(), ps.getUiExtra().getTimerIncr()));
			}
		});
	}
	
	/**
	 * This is called when a layer is killed to update score information
	 * @param perp: player that kills
	 * @param vic: player that gets killed
	 * @param lives: Do we care about decrementing lives?
	 * @return: whether this death was the victim's last life
	 */
	public boolean registerKill(Player perp, Player vic, boolean lives) {
		
		PlayState ps = getPlayState();
		boolean outOfLives = false;
		
		if (ps != null) {
			
			//If the host is the perp or vic, update score with id 0
			if (perp != null) {
				if (perp.equals(ps.getPlayer())) {
					scores.get(0).registerKill();
				}
			}
			
			if (vic.equals(ps.getPlayer())) {
				scores.get(0).registerDeath();
				if (lives && scores.get(0).getLives() <= 0) {
					outOfLives = true;
				}
			}
			
			//Otherwise, update score of client matching the players involved
			for (Entry<Integer, Player> conn: players.entrySet()) {
				if (conn.getValue().equals(vic)) {
					if (scores.containsKey(conn.getKey())) {
						scores.get(conn.getKey()).registerDeath();
						
						if (lives && scores.get(conn.getKey()).getLives() <= 0) {
							outOfLives = true;
						}
					}
					break;
				}
			}
			
			if (perp != null) {
				for (Entry<Integer, Player> conn: players.entrySet()) {
					if (conn.getValue().equals(perp)) {
						if (scores.containsKey(conn.getKey())) {
							scores.get(conn.getKey()).registerKill();
						}
						break;
					}
				}
			}
			
			//Sync score window to show updated kda and score
			ps.getScoreWindow().syncTable();
		}
		return outOfLives;
	}
	
	/**
	 * This sends a specific packet to a specific client based on their player 
	 * @param p: player to send a packet to
	 * @param o: packet to send
	 */
	public void sendPacketToPlayer(Player p, Object o) {
		
		if (server == null) {
			return;
		}
		
		for (Entry<Integer, Player> conn: players.entrySet()) {
			if (conn.getValue().equals(p)) {
				server.sendToTCP(conn.getKey(), o);
				break;
			}
		}
	}
	
	/**
	 * This gets the server's playstate. This allows the server to make changes to a playstate underneath a pausestate.
	 * @return
	 */
	public PlayState getPlayState() {
		if (!gsm.getStates().empty()) {
			GameState currentState = gsm.getStates().peek();
			if (currentState instanceof PlayState) {
				return (PlayState) currentState;
			}
			if (currentState instanceof PauseState) {
				return ((PauseState) currentState).getPs();
			}
		}
		return null;
	}
	
	/**
	 * This makes a client display a notification
	 * @param ps: server's current playstate
	 * @param connId: id of the client to send the notification to
	 * @param name: name giving the notification
	 * @param text: notification text
	 */
	public void sendNotification(PlayState ps, int connId, String name, String text) {
		if (server != null) {
			server.sendToTCP(connId, new Packets.Notification(name, text));	
		}
	}
	
	/**
	 * This adds a notification all clients and also the server themselves
	 * @param ps: server's current playstate
	 * @param name: name giving the notification
	 * @param text: notification text
	 */
	public void addNotificationToAll(PlayState ps, String name, String text) {
		if (ps.getPlayStateStage() != null && server != null) {
			ps.getPlayStateStage().addDialogue(name, text, "", true, true, true, 3.0f, null, null);
	        server.sendToAllTCP(new Packets.Notification(name, text));	
		}
	}
	
	/**
	 * This makes all client display a notification and also self, excluding one client
	 * @param ps: server's current playstate
	 * @param connId: id of the client to exclude
	 * @param name: name giving the notification
	 * @param text: notification text
	 */
	public void addNotificationToAllExcept(PlayState ps, int connId, String name, String text) {
		if (ps.getPlayStateStage() != null && server != null) {
			ps.getPlayStateStage().addDialogue(name, text, "", true, true, true, 3.0f, null, null);
	        server.sendToAllExceptTCP(connId, new Packets.Notification(name, text));
		}
	}
	
	private void registerPackets() {
		Kryo kryo = server.getKryo();
		Packets.allPackets(kryo);
	}
	
	public void sendToAllTCP(Object p) {
		if (server != null) {
			server.sendToAllTCP(p);
		}
	}
	
	public void sendToAllExceptTCP(int connId, Object p) {
		if (server != null) {
			server.sendToAllExceptTCP(connId, p);
		}
	}
	
	public void sendToTCP(int connId, Object p) {
		if (server != null) {
			server.sendToTCP(connId, p);
		}
	}
	
	public void sendToAllUDP(Object p) {
		if (server != null) {
			server.sendToAllUDP(p);
		}
	}
	
	public Server getServer() {	return server; }

	public void setServer(Server server) { this.server = server; }

	public HashMap<Integer, Player> getPlayers() { return players; }

	public HashMap<Integer, MouseTracker> getMice() { return mice; }

	public HashMap<Integer, SavedPlayerFields> getScores() { return scores; }
}
