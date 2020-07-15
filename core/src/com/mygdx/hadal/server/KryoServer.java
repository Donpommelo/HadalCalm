package com.mygdx.hadal.server;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map.Entry;

import com.badlogic.gdx.Gdx;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.KryoSerialization;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.equip.Loadout;
import com.mygdx.hadal.event.StartPoint;
import com.mygdx.hadal.managers.GameStateManager;
import com.mygdx.hadal.schmucks.bodies.HadalEntity;
import com.mygdx.hadal.schmucks.bodies.MouseTracker;
import com.mygdx.hadal.schmucks.bodies.Player;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.server.Packets.EndSpectate;
import com.mygdx.hadal.states.SettingState;
import com.mygdx.hadal.states.TitleState;
import com.mygdx.hadal.statuses.DamageTypes;
import com.mygdx.hadal.states.GameState;
import com.mygdx.hadal.states.PauseState;
import com.mygdx.hadal.states.PlayState;
import com.mygdx.hadal.states.ResultsState;

/**
 * This is the server of the game.
 * @author Zachary Tu
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
		this.server = new Server(5000, 5000, serialization);
		this.players = new HashMap<Integer, Player>();
		this.mice = new HashMap<Integer, MouseTracker>();
		this.scores = new HashMap<Integer, SavedPlayerFields>();
		
		scores.put(0, new SavedPlayerFields(gsm.getLoadout().getName(), true));
		
		if (!start) { return; }
		
		Listener packetListener = new Listener() {
			
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
								p.getPlayerData().die(ps.getWorldDummy().getBodyData(), DamageTypes.DISCONNECT);
								addNotificationToAll(ps, p.getName(), " DISCONNECTED!");
								
								//remove disconnecting player from all tracked lists
								players.remove(c.getID());
								mice.remove(c.getID());
								scores.remove(c.getID());
								ps.getScoreWindow().syncScoreTable();
							}
						}
					});
				}
				
				//If in a victory state, count a disconnect as ready so disconnected players don't prevent return to hub.
				if (!gsm.getStates().empty() && gsm.getStates().peek() instanceof ResultsState) {
					final ResultsState vs =  (ResultsState) gsm.getStates().peek();
					Gdx.app.postRunnable(new Runnable() {
        				
                        @Override
                        public void run() {
                        	vs.readyPlayer(c.getID());
                        	//remove disconnecting player from all tracked lists
							players.remove(c.getID());
							mice.remove(c.getID());
							scores.remove(c.getID());
                        }
					});
				}
			}
			
			/**
        	 * Note that the order of these if/elses is according to approximate frequency of packets.
        	 * This might have a (very minor) effect on performance or something idk
        	 */
			@Override
			public void received(final Connection c, Object o) {

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
				else if (o instanceof Packets.KeyUp) {
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
				else if (o instanceof Packets.MouseMove) {
					final Packets.MouseMove p = (Packets.MouseMove) o;
					final PlayState ps = getPlayState();
					final MouseTracker mouse = mice.get(c.getID());
					if (ps != null && mouse != null) {
						mouse.setDesiredLocation(p.x, p.y);
					}
				}
				
				/*
				 * The Client has connected.
				 * Notify clients and create a new player for the client. Also, tell the new client what level to load
				 */
				else if (o instanceof Packets.PlayerConnect) {
					final Packets.PlayerConnect p = (Packets.PlayerConnect) o;
					final PlayState ps = getPlayState();
					if (ps != null) {
						if (p.firstTime) {
							
							//reject clients with wrong version
							if (!p.version.equals(HadalGame.Version)) {
								sendToTCP(c.getID(), new Packets.ConnectReject("INCOMPATIBLE VERSION. HOST ON VER: " + HadalGame.Version));
								return;
							}

							addNotificationToAllExcept(ps, c.getID(), p.name, "PLAYER CONNECTED!");
							
							//clients joining full servers or in the middle of matches join as spectators
							if (getNumPlayers() >= ps.getGsm().getSetting().getMaxPlayers()) {
								sendToTCP(c.getID(), new Packets.LoadLevel(ps.getLevel(), p.firstTime, true));
								return;
							}

							if (!ps.isHub()) {
								sendToTCP(c.getID(), new Packets.LoadLevel(ps.getLevel(), p.firstTime, true));
								return;
							}
						}
                        sendToTCP(c.getID(), new Packets.LoadLevel(ps.getLevel(), p.firstTime, false));
					}
				}
				
				/*
				 * The Client has loaded the level.
				 * Announce the new player joining and catchup the new client.
				 */
				else if (o instanceof Packets.ClientLoaded) {
					final Packets.ClientLoaded p = (Packets.ClientLoaded) o;
					final PlayState ps = getPlayState();
					//notify players of new joiners
					if (p.firstTime) {
						sendNotification(ps, c.getID(), ps.getPlayer().getName(), "JOINED SERVER!");
					}

					//catch up client
					if (ps != null) {
						ps.addPacketEffect(new PacketEffect() {

							@Override
							public void execute() {
								ps.catchUpClient(c.getID());
		                        
								boolean spectator = p.spectator || (p.lastSpectator && !ps.isHub());
								
		                        //If the client has already been created, we create a new player, otherwise we reuse their old data.
		    					final Player player = players.get(c.getID());
		    					
		    					if (player != null) {
		    						
		    						//if the player is told to start as a spectator or was a spectator prior to the match, they join as a spectator
		    						if (player.isStartSpectator()) {
		    							spectator = true;
		    						}
		    						
		    						if (ps.isReset()) {
		    							createNewClientPlayer(ps, c.getID(), p.name, p.loadout, player.getPlayerData(), ps.isReset(), spectator); 
		    						} else {
		    							createNewClientPlayer(ps, c.getID(), p.name, player.getPlayerData().getLoadout(), player.getPlayerData(), ps.isReset(), spectator); 
		    						}
		    					} else {
		    						createNewClientPlayer(ps, c.getID(), p.name, p.loadout, null, true, spectator);                        
		    					}
							}
						});
					}
				}
				
				/*
				 * The Client has loaded the level.
				 * Announce the new player joining and catchup the new client.
				 */
				else if (o instanceof Packets.ClientPlayerCreated) {
					final Player player = players.get(c.getID());
					if (player != null) {
						HadalGame.server.sendToAllTCP(new Packets.SyncServerLoadout(player.getEntityID().toString(), player.getPlayerData().getLoadout()));
						if (player.getStart() != null) {
							player.getStart().playerStart(player);
						}
					}
				}
				
				/*
				 * The Client has changed their loadout (in the hub because anywhere else is handled server side)
				 * Find the client changing and update their player loadout
				 */
				else if (o instanceof Packets.SyncClientLoadout) {
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
				 * The Client tells us they might have missed a create packet.
				 * Check if the entity exists and send a catchup create packet if so. 
				 */
				else if (o instanceof Packets.MissedCreate) {
					
					final Packets.MissedCreate p = (Packets.MissedCreate) o;
					final PlayState ps = getPlayState();
					if (ps != null) {
						
						ps.addPacketEffect(new PacketEffect() {
    						
    						@Override
							public void execute() {
    							HadalEntity entity = ps.findEntity(p.entityID);
    							if (entity != null) {
    								Object packet = entity.onServerCreate();
    								if (packet != null) {
    									sendToUDP(c.getID(), packet);
    								}
    							}
    						}
						});
					}
				}
				
				/*
				 * The Client tells us they might have missed a delete packet.
				 * Check if the entity exists and send a catchup delete packet if not. 
				 */
				else if (o instanceof Packets.MissedDelete) {
					final Packets.MissedDelete p = (Packets.MissedDelete) o;
					final PlayState ps = getPlayState();
					
					if (ps != null) {
						
						ps.addPacketEffect(new PacketEffect() {
    						
    						@Override
							public void execute() {
    							HadalEntity entity = ps.findEntity(p.entityID);
    							if (entity == null) {
    								sendToUDP(c.getID(), new Packets.DeleteEntity(p.entityID, ps.getTimer()));	
    							}
    						}
						});
					}
				}
				
				/*
				 * The client has finished respawning (after transitioning to black.)
				 * We spawn a new player for them
				 */
				else if (o instanceof Packets.ClientFinishRespawn) {
					final PlayState ps = getPlayState();
					
					//acquire the client's name and data
					String playerName = "";
					Player player = players.get(c.getID());
					if (player != null) {
						playerName = player.getName();
						createNewClientPlayer(ps, c.getID(), playerName, player.getPlayerData().getLoadout(), player.getPlayerData(), true, false);
					}
				}
				
				/*
				 * A Client has unpaused the game
				 * Return to the PlayState and inform everyone who unpaused.
				 */
				else if (o instanceof Packets.Unpaused) {
        			if (!gsm.getStates().empty()) {
        				
        				Player p = players.get(c.getID());
        				if (p != null && gsm.getSetting().isMultiplayerPause()) {
        					if (gsm.getStates().peek() instanceof PauseState) {
        						final PauseState ps = (PauseState) gsm.getStates().peek();
                				addNotificationToAll(ps.getPs(), p.getName(), "UNPAUSED THE GAME!");
                				ps.setToRemove(true);
        					}
        					if (gsm.getStates().peek() instanceof SettingState) {
        						final SettingState ss = (SettingState) gsm.getStates().peek();
                				addNotificationToAll(ss.getPs(), p.getName(), "UNPAUSED THE GAME!");
                				ss.setToRemove(true);
        					}
            				HadalGame.server.sendToAllTCP(new Packets.Unpaused(p.getName()));
        				}
        			}
				}
				
				/*
				 * A Client has sent the server a notification.
				 * Display the notification and echo it to all clients  
				 */
				else if (o instanceof Packets.Notification) {
					final Packets.Notification p = (Packets.Notification) o;
					final PlayState ps = getPlayState();
					if (ps != null) {
						addNotificationToAll(ps, p.name, p.text);
					}
				}
				
				/*
				 * A Client has said they want to enter spectator mode
				 */
				else if (o instanceof Packets.StartSpectate) {
					
					final PlayState ps = getPlayState();
					if (ps != null) {
						Player p = players.get(c.getID());
						if (p != null) {
							ps.becomeSpectator(p);
						}
					}
				}
				
				/*
				 * A Client has said they want to exit spectator mode
				 */
				else if (o instanceof EndSpectate) {
					
					final PlayState ps = getPlayState();
					if (ps != null) {
						Player p = players.get(c.getID());
						if (p != null) {
							ps.exitSpectator(p);
						}
					}
				}
				
				/*
				 * A Client has said they are ready to return to hub state from results state
				 * Ready that player in the results state.
				 */
				else if (o instanceof Packets.ClientReady) {
					if (!gsm.getStates().empty() && gsm.getStates().peek() instanceof ResultsState) {
						final ResultsState vs = (ResultsState) gsm.getStates().peek();
						Gdx.app.postRunnable(new Runnable() {
	        				
	                        @Override
	                        public void run() {
	                        	vs.readyPlayer(c.getID());
	                        }
						});
					}
				}
			}
		};
		
        server.addListener(new Listener.LagListener(100, 100, packetListener));
//		server.addListener(packetListener);
		
		try {
			server.bind(gsm.getSetting().getPortNumber(), gsm.getSetting().getPortNumber());
		} catch (IOException e) {
			if (gsm.getStates().peek() instanceof TitleState) {
				((TitleState) gsm.getStates().peek()).setNotification("COULD NOT OPEN SERVER AT PORT: " + gsm.getSetting().getPortNumber());
			}
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
	 * @param spectator: is this player created as a spectator?
	 */
	public void createNewClientPlayer(final PlayState ps, final int connId, final String name, final Loadout loadout, final PlayerBodyData data, final boolean reset, final boolean spectator) {

		ps.addPacketEffect(new PacketEffect() {

			@Override
			public void execute() {
				StartPoint newSave = ps.getSavePoint();
				
				//Create a new player with the designated fields and give them a mouse pointer.
				Player newPlayer = ps.createPlayer(newSave, name, loadout, data, connId, reset);
		        MouseTracker newMouse = new MouseTracker(ps, false);
		        newPlayer.setMouse(newMouse);
		        players.put(connId, newPlayer);
		        mice.put(connId, newMouse);
		        
		        //Update that player's scores or give them a new one if they are a new client
		        if (scores.containsKey(connId)) {
			        scores.put(connId, scores.get(connId));
		        } else {
			        scores.put(connId, new SavedPlayerFields(name, false));
		        }
		        
		        //sync score window to display new player
		        ps.getScoreWindow().syncScoreTable();
		        ps.getScoreWindow().syncSettingTable();
		        
		        //sync client ui elements
		        sendToTCP(connId, new Packets.SyncUI(ps.getUiExtra().getCurrentTags(), ps.getUiExtra().getTimer(), ps.getUiExtra().getTimerIncr()));
		        
		        //set the client as a spectator if requested
		        newPlayer.setStartSpectator(spectator);
			}
		});
	}
	
	/**
	 * This is called when a layer is killed to update score information
	 * @param perp: player that kills
	 * @param vic: player that gets killed
	 * @return: whether this death was the victim's last life
	 */
	public void registerKill(Player perp, Player vic) {
		
		PlayState ps = getPlayState();
		
		if (ps != null) {
			
			//If the host is the perp or vic, update score with id 0
			if (perp != null) {
				if (perp.equals(ps.getPlayer())) {
					scores.get(0).registerKill();
				}
			}
			if (vic != null) {
				if (vic.equals(ps.getPlayer())) {
					scores.get(0).registerDeath();
				}
			}
			
			//Otherwise, update score of client matching the players involved
			if (vic != null) {
				for (Entry<Integer, Player> conn: players.entrySet()) {
					if (conn.getKey().equals(vic.getConnID())) {
						if (scores.containsKey(conn.getKey())) {
							scores.get(conn.getKey()).registerDeath();
						}
						break;
					}
				}
			}
			
			if (perp != null) {
				for (Entry<Integer, Player> conn: players.entrySet()) {
					if (conn.getKey().equals(perp.getConnID())) {
						if (scores.containsKey(conn.getKey())) {
							scores.get(conn.getKey()).registerKill();
						}
						break;
					}
				}
			}
			
			//Sync score window to show updated kda and score
			ps.getScoreWindow().syncScoreTable();
		}
	}
	
	/**
	 * This sends a specific packet to a specific client based on their player 
	 * @param p: player to send a packet to
	 * @param o: packet to send
	 */
	public void sendPacketToPlayer(Player p, Object o) {
		
		if (server == null) { return; }
		
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
			} else if (currentState instanceof PauseState) {
				return ((PauseState) currentState).getPs();
			} else if (currentState instanceof SettingState) {
				return ((SettingState) currentState).getPs();
			} else if (currentState instanceof ResultsState) {
				return ((ResultsState) currentState).getPs();
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
		sendToTCP(connId, new Packets.Notification(name, text));	
	}
	
	/**
	 * This adds a notification all clients and also the server themselves
	 * @param ps: server's current playstate
	 * @param name: name giving the notification
	 * @param text: notification text
	 */
	public void addNotificationToAll(final PlayState ps, final String name, final String text) {
		if (ps.getDialogBox() != null && server != null) {
			server.sendToAllTCP(new Packets.Notification(name, text));	
			Gdx.app.postRunnable(new Runnable() {
				
				@Override
                public void run() {
					ps.getDialogBox().addDialogue(name, text, "", true, true, true, 3.0f, null, null);
                }
			});
		}
	}
	
	/**
	 * This makes all client display a notification and also self, excluding one client
	 * @param ps: server's current playstate
	 * @param connId: id of the client to exclude
	 * @param name: name giving the notification
	 * @param text: notification text
	 */
	public void addNotificationToAllExcept(final PlayState ps, int connId, final String name, final String text) {
		if (ps.getDialogBox() != null && server != null) {
			server.sendToAllExceptTCP(connId, new Packets.Notification(name, text));
			Gdx.app.postRunnable(new Runnable() {
				
				@Override
                public void run() {
					ps.getDialogBox().addDialogue(name, text, "", true, true, true, 3.0f, null, null);
                }
			});
		}
	}
	
	/**
	 * This returns the number of non-spectator players. used to determine whether the server is full or not.
	 */
	public int getNumPlayers() {
		int playerNum = 0;
		
		for (Entry<Integer, Player> conn: players.entrySet()) {		
			if (!conn.getValue().isSpectator()) {
				playerNum++;
			}
		}
		return playerNum;
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
	
	public void sendToUDP(int connId, Object p) {
		if (server != null) {
			server.sendToUDP(connId, p);
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

	public HashMap<Integer, SavedPlayerFields> getScores() { return scores; }
}
