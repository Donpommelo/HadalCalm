package com.mygdx.hadal.server;

import com.badlogic.gdx.Gdx;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import com.esotericsoftware.kryonet.serialization.KryoSerialization;
import com.mygdx.hadal.HadalGame;
import com.mygdx.hadal.actors.DialogBox.DialogType;
import com.mygdx.hadal.equip.Loadout;
import com.mygdx.hadal.event.StartPoint;
import com.mygdx.hadal.managers.GameStateManager;
import com.mygdx.hadal.schmucks.bodies.HadalEntity;
import com.mygdx.hadal.schmucks.bodies.MouseTracker;
import com.mygdx.hadal.schmucks.bodies.Player;
import com.mygdx.hadal.schmucks.userdata.PlayerBodyData;
import com.mygdx.hadal.server.Packets.EndSpectate;
import com.mygdx.hadal.states.*;
import com.mygdx.hadal.statuses.DamageTypes;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map.Entry;

/**
 * This is the server of the game.
 * @author Nurgarita Nelfram
 */
public class KryoServer {
	
	//Me server
	private Server server;
	
	//This is the gsm of the server
	public GameStateManager gsm;
	
	//These keep track of all connected players, their mice and scores.
	private  HashMap<Integer, User> users;

	public KryoServer(GameStateManager gameStateManager) {
		this.gsm = gameStateManager;
	}
	
	/**
	 * This is called upon starting a new server. initialize server and tracked client data 
	 * start is false if we are loading singleplayer and don't actually want the server to start
	 */
	public void init(boolean start) {
		Kryo kryo = new Kryo();
		kryo.setReferences(true);

		KryoSerialization serialization = new KryoSerialization(kryo);
		this.server = new Server(16384, 8192, serialization);
		this.users = new HashMap<>();

		users.put(0, new User(null, null, new SavedPlayerFields(gsm.getLoadout().getName(), 0), new SavedPlayerFieldsExtra()));

		if (!start) { return; }

		Listener packetListener = new Listener() {
			
			@Override
			public void disconnected(final Connection c) {
				final PlayState ps = getPlayState();

				//Identify the player that disconnected
				User user = users.get(c.getID());
				if (user != null && ps != null) {

					//free up the disconnected user's player slot
					user.getHitBoxFilter().setUsed(false);

					Player player = user.getPlayer();
					if (player != null) {
						ps.addPacketEffect(() -> {

							//Inform all that the player disconnected and kill the player
							player.getPlayerData().die(ps.getWorldDummy().getBodyData(), DamageTypes.DISCONNECT);
							addNotificationToAll(ps, player.getName(), " DISCONNECTED!", DialogType.SYSTEM);

							//remove disconnecting player from users
							users.remove(c.getID());
							ps.getScoreWindow().syncScoreTable();
							sendToAllTCP(new Packets.RemoveScore(c.getID()));
						});
					}
				}

				//If in a victory state, count a disconnect as ready so disconnected players don't prevent return to hub.
				if (!gsm.getStates().empty() && gsm.getStates().peek() instanceof ResultsState) {
					final ResultsState vs =  (ResultsState) gsm.getStates().peek();
					Gdx.app.postRunnable(() -> {
						vs.readyPlayer(c.getID());

						//remove disconnecting player from users
						users.remove(c.getID());
					});
				}
			}
			
			/**
        	 * Note that the order of these if/else is according to approximate frequency of packets.
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
					User user = users.get(c.getID());
					if (user != null && ps != null) {
						Player player = user.getPlayer();
						if (player != null) {
							ps.addPacketEffect(() -> {
								if (player.getController() != null) {
									player.getController().keyDown(p.action);
								}
							});
						}
					}
				}
				
				/*
				 * A Client has performed an action involving pressing a key up
				 * Register the keystroke for that client's player.
				 */
				else if (o instanceof Packets.KeyUp) {
					final Packets.KeyUp p = (Packets.KeyUp) o;
					final PlayState ps = getPlayState();

					User user = users.get(c.getID());
					if (user != null && ps != null) {
						Player player = user.getPlayer();
						if (player != null) {
							ps.addPacketEffect(() -> {
								if (player.getController() != null) {
									player.getController().keyUp(p.action);
								}
							});
						}
					}
				}
				
				/*
				 * A Client sends this every engine tick to send their mouse location.
				 * Update the client's player's mouse pointer.
				 */
				else if (o instanceof Packets.MouseMove) {
					final Packets.MouseMove p = (Packets.MouseMove) o;
					final PlayState ps = getPlayState();
					User user = users.get(c.getID());
					if (user != null) {
						final MouseTracker mouse = user.getMouse();
						if (ps != null && mouse != null) {
							mouse.setDesiredLocation(p.x, p.y);
						}
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

							//if no server password, the client connects.
							if (!gsm.getSetting().getServerPassword().equals("")) {
								//password being null indicates the client just attempted to connect.
								//otherwise, we check whether the password entered matches
								if (p.password == null) {
									sendToTCP(c.getID(), new Packets.PasswordRequest());
									return;
								} else if (!gsm.getSetting().getServerPassword().equals(p.password)){
									sendToTCP(c.getID(), new Packets.ConnectReject("INCORRECT PASSWORD"));
									return;
								}
							}
							addNotificationToAllExcept(ps, c.getID(), p.name, "PLAYER CONNECTED!", DialogType.SYSTEM);

							//clients joining full servers or in the middle of matches join as spectators
							if (getNumPlayers() >= ps.getGsm().getSetting().getMaxPlayers() + 1) {
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
				 * this is also where we determine if the client is a spectator or not
				 */
				else if (o instanceof Packets.ClientLoaded) {
					final Packets.ClientLoaded p = (Packets.ClientLoaded) o;
					final PlayState ps = getPlayState();
					//notify players of new joiners
					if (p.firstTime) {
						sendNotification(c.getID(), ps.getPlayer().getName(), "JOINED SERVER!", DialogType.SYSTEM);
					}

					//catch up client
					if (ps != null) {
						ps.addPacketEffect(() -> {
							ps.catchUpClient(c.getID());

							//client joins as a spectator if their packet specifies so, or if there were previously a spectator and are joining a non-hub level
							boolean spectator = p.spectator || (p.lastSpectator && !ps.isHub());

							//If the client has already been created, we create a new player, otherwise we reuse their old data.
							User user = users.get(c.getID());
							if (user != null) {
								Player player = user.getPlayer();
								if (player != null) {

									//if the player is told to start as a spectator or was a spectator prior to the match, they join as a spectator
									if (player.isStartSpectator()) {
										spectator = true;
									}

									//alive check prevents duplicate players if entering/respawning simultaneously
									if (!player.isAlive()) {
										if (ps.isReset()) {
											createNewClientPlayer(ps, c.getID(), p.name, p.loadout, player.getPlayerData(), ps.isReset(), spectator);
										} else {
											createNewClientPlayer(ps, c.getID(), p.name, player.getPlayerData().getLoadout(), player.getPlayerData(), ps.isReset(), spectator);
										}
									}
								} else {
									createNewClientPlayer(ps, c.getID(), p.name, p.loadout, null, true, spectator);
								}
							} else {
								createNewClientPlayer(ps, c.getID(), p.name, p.loadout, null, true, spectator);
							}

							//sync client ui elements
							sendToTCP(c.getID(), new Packets.SyncUI(ps.getUiExtra().getCurrentTags(), ps.getUiExtra().getTimer(), ps.getUiExtra().getTimerIncr()));
							sendToTCP(c.getID(), new Packets.SyncSharedSettings(ps.getGsm().getSharedSetting()));
						});
					}
				}
				
				/*
				 * The Client has loaded the level.
				 * sync the client's loadout and activate the event connected to the start point.
				 */
				else if (o instanceof Packets.ClientPlayerCreated) {
					User user = users.get(c.getID());
					if (user != null) {
						Player player = user.getPlayer();
						if (player != null) {
							HadalGame.server.sendToAllTCP(new Packets.SyncServerLoadout(player.getEntityID().toString(), player.getPlayerData().getLoadout()));
							if (player.getStart() != null) {
								player.getStart().playerStart(player);
							}
						}
					}
				}
				
				/*
				 * The Client has changed their loadout (in the hub because anywhere else is handled server side)
				 * Find the client changing and update their player loadout
				 */
				else if (o instanceof Packets.SyncClientLoadout) {
        			final Packets.SyncClientLoadout p = (Packets.SyncClientLoadout) o;
    				final PlayState ps = getPlayState();

    				User user = users.get(c.getID());
					if (user != null && ps != null) {
						Player player = user.getPlayer();
						if (player != null) {
							ps.addPacketEffect(() -> {
								player.getPlayerData().syncLoadoutFromClient(p.equip, p.artifactAdd, p.artifactRemove, p.active, p.character, p.team);
								player.getPlayerData().syncServerLoadoutChange();
							});
						}
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
						ps.addPacketEffect(() -> {
							HadalEntity entity = ps.findEntity(p.entityID);
							if (entity != null) {
								Object packet = entity.onServerCreate();
								if (packet != null) {
									sendToUDP(c.getID(), packet);
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
						ps.addPacketEffect(() -> {
							HadalEntity entity = ps.findEntity(p.entityID);
							if (entity == null) {
								sendToUDP(c.getID(), new Packets.DeleteEntity(p.entityID, ps.getTimer()));
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
					User user = users.get(c.getID());
					if (user != null && ps != null) {
						Player player = user.getPlayer();
						if (player != null) {
							//alive check prevents duplicate players if entering/respawning simultaneously
							if (!player.isAlive()) {
								String playerName = player.getName();
								createNewClientPlayer(ps, c.getID(), playerName, player.getPlayerData().getLoadout(), player.getPlayerData(), true, false);
							}
						}
					}
				}
				
				/*
				 * A Client has unpaused the game
				 * Return to the PlayState and inform everyone who unpaused.
				 */
				else if (o instanceof Packets.Unpaused) {
        			if (!gsm.getStates().empty()) {
						User user = users.get(c.getID());
						if (user != null) {
							Player player = user.getPlayer();

							//if pauses are enabled, unpause and remove pause state (and setting state)
							if (player != null && gsm.getSetting().isMultiplayerPause()) {
								if (gsm.getStates().peek() instanceof PauseState) {
									final PauseState ps = (PauseState) gsm.getStates().peek();
									addNotificationToAll(ps.getPs(), player.getName(), "UNPAUSED THE GAME!", DialogType.SYSTEM);
									ps.setToRemove(true);
								}
								if (gsm.getStates().peek() instanceof SettingState) {
									final SettingState ss = (SettingState) gsm.getStates().peek();
									addNotificationToAll(ss.getPs(), player.getName(), "UNPAUSED THE GAME!", DialogType.SYSTEM);
									ss.setToRemove(true);
								}
								HadalGame.server.sendToAllTCP(new Packets.Unpaused(player.getName()));
							}
						}
        			}
				}
				
				/*
				 * A Client has sent the server a message.
				 * Display the notification and echo it to all clients  
				 */
				else if (o instanceof Packets.ClientChat) {
					final Packets.ClientChat p = (Packets.ClientChat) o;
					final PlayState ps = getPlayState();
					if (ps != null) {
						addChatToAll(ps, p.text, p.type, c.getID());
					}
				}
				
				/*
				 * Respond to this packet sent from the client periodically so the client knows their latency.
				 */
				else if (o instanceof Packets.LatencySyn) {
					final Packets.LatencySyn p = (Packets.LatencySyn) o;
					final PlayState ps = getPlayState();

					User user = users.get(c.getID());
					if (user != null && ps != null) {
						SavedPlayerFields score = user.getScores();
						if (score != null) {
							if (score.getPing() != p.latency) {
								score.setPing(p.latency);
								user.setScoreUpdated(true);
							}
						}
					}
					server.sendToTCP(c.getID(), new Packets.LatencyAck());
				}
				
				/*
				 * This packet indicates the client is typing, so make a bubble appear above their head.
				 */
				else if (o instanceof Packets.SyncTyping) {
					final PlayState ps = getPlayState();
					User user = users.get(c.getID());
					if (user != null && ps != null) {
						Player player = user.getPlayer();
						if (player != null) {
							player.startTyping();
							sendToAllExceptUDP(c.getID(), new Packets.SyncTyping(player.getEntityID().toString()));
						}
					}
				}

				/*
				 * The client tried to emote. make them emote, if possible
				 */
				else if (o instanceof Packets.SyncEmote) {
					final Packets.SyncEmote p = (Packets.SyncEmote) o;

					final PlayState ps = getPlayState();
					User user = users.get(c.getID());
					if (user != null && ps != null) {
						Player player = user.getPlayer();
						if (player != null) {
							ps.getChatWheel().emote(player, p.emoteIndex);
						}
					}
				}

				/*
				 * A Client has said they want to enter spectator mode
				 */
				else if (o instanceof Packets.StartSpectate) {
					final PlayState ps = getPlayState();
					if (ps != null) {
						ps.addPacketEffect(() -> {
							User user = users.get(c.getID());
							if (user != null) {
								Player player = user.getPlayer();
								if (player != null) {
									ps.becomeSpectator(player);
								}
							}
						});
					}
				}
				
				/*
				 * A Client has said they want to exit spectator mode
				 */
				else if (o instanceof EndSpectate) {
					final PlayState ps = getPlayState();
					if (ps != null) {
						ps.addPacketEffect(() -> {
							User user = users.get(c.getID());
							if (user != null) {
								Player player = user.getPlayer();
								if (player != null) {
									ps.exitSpectator(player);
								}
							}
						});
					}
				}
				
				/*
				 * A Client has said they are ready to return to hub state from results state
				 * Ready that player in the results state.
				 */
				else if (o instanceof Packets.ClientReady) {
					if (!gsm.getStates().empty() && gsm.getStates().peek() instanceof ResultsState) {
						final ResultsState vs = (ResultsState) gsm.getStates().peek();
						Gdx.app.postRunnable(() -> vs.readyPlayer(c.getID()));
					}
				}
			}
		};
		
//        server.addListener(new Listener.LagListener(100, 100, packetListener));
		server.addListener(packetListener);
		
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
	 * @param spectator: is this player created as a spectator?
	 */
	public void createNewClientPlayer(final PlayState ps, final int connId, final String name, final Loadout loadout, final PlayerBodyData data, final boolean reset, final boolean spectator) {

		ps.addPacketEffect(() -> {
			StartPoint newSave = ps.getSavePoint();

			//Update that player's fields or give them new ones if they are a new client
			User user;
			if (users.containsKey(connId)) {
				user = users.get(connId);
			} else {
				user = new User(null, null, new SavedPlayerFields(name, connId), new SavedPlayerFieldsExtra());
				users.put(connId, user);
			}

			//Create a new player with the designated fields and give them a mouse pointer.
			Player newPlayer = ps.createPlayer(newSave, name, loadout, data, connId, reset, false, user.getHitBoxFilter().getFilter());
			MouseTracker newMouse = new MouseTracker(ps, false);
			newPlayer.setMouse(newMouse);

			user.setPlayer(newPlayer);
			user.setMouse(newMouse);
			user.setTeamFilter(loadout.team);

			//set the client as a spectator if requested
			newPlayer.setStartSpectator(spectator);
		});
	}
	
	/**
	 * This is called when a player is killed to update score information
	 * @param perp: player that kills
	 * @param vic: player that gets killed
	 */
	public void registerKill(Player perp, Player vic) {
		
		PlayState ps = getPlayState();
		
		if (ps != null) {
						
			//update score of client matching the players involved
			if (vic != null) {
				if (users.containsKey(vic.getConnID())) {
					User user = users.get(vic.getConnID());
					user.getScores().registerDeath();
					user.setScoreUpdated(true);
				}
			}
			
			if (perp != null) {
				if (users.containsKey(perp.getConnID())) {
					User user = users.get(perp.getConnID());
					user.getScores().registerKill();
					user.setScoreUpdated(true);
				}
			}
		}
	}
	
	/**
	 * This sends a specific packet to a specific client based on their player 
	 * @param p: player to send a packet to
	 * @param o: packet to send
	 */
	public void sendPacketToPlayer(Player p, Object o) {
		
		if (server == null) { return; }
		
		for (Entry<Integer, User> conn: users.entrySet()) {
			if (conn.getValue().getPlayer() != null) {
				if (conn.getValue().getPlayer().equals(p)) {
					server.sendToTCP(conn.getKey(), o);
					break;
				}
			}
		}
	}
	
	/**
	 * This gets the server's playstate. This allows the server to make changes to a playstate underneath a pausestate.
	 * @return server's playstate and null if there isn't one
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
	 * @param connId: id of the client to send the notification to
	 * @param name: name giving the notification
	 * @param text: notification text
	 * @param type: type of dialog (dialog, system msg, etc)
	 */
	public void sendNotification(int connId, String name, String text, final DialogType type) {
		sendToTCP(connId, new Packets.ServerNotification(name, text, type));
	}
	
	/**
	 * This adds a notification all clients and also the server themselves
	 * @param ps: server's current playstate
	 * @param name: name giving the notification
	 * @param text: notification text
	 * @param type: type of dialog (dialog, system msg, etc)
	 */
	public void addNotificationToAll(final PlayState ps, final String name, final String text, final DialogType type) {
		if (ps.getDialogBox() != null && server != null) {
			server.sendToAllTCP(new Packets.ServerNotification(name, text, type));
			Gdx.app.postRunnable(() -> ps.getDialogBox().addDialogue(name, text, "", true, true, true, 3.0f, null, null, type));
		}
	}

	/**
	 * This sends a chat message to all clients and also the server themselves
	 * @param ps: server's current playstate
	 * @param text: the content of the chat message
	 * @param type: type of chat (system, normal chat)
	 * @param connID: connID of the player sending the chat message
	 */
	public void addChatToAll(final PlayState ps, final String text, final DialogType type, final int connID) {
		if (ps.getMessageWindow() != null && server != null) {
			server.sendToAllTCP(new Packets.ServerChat(text, type, connID));
			Gdx.app.postRunnable(() -> ps.getMessageWindow().addText(text, type, connID));
		}
	}
	
	/**
	 * This makes all client display a notification and also self, excluding one client
	 * @param ps: server's current playstate
	 * @param connId: id of the client to exclude
	 * @param name: name giving the notification
	 * @param text: notification text
	 * @param type: type of dialog (dialog, system msg, etc)
	 */
	public void addNotificationToAllExcept(final PlayState ps, int connId, final String name, final String text, final DialogType type) {
		if (ps.getDialogBox() != null && server != null) {
			server.sendToAllExceptTCP(connId, new Packets.ServerNotification(name, text, type));
			Gdx.app.postRunnable(() -> ps.getDialogBox().addDialogue(name, text, "", true, true, true, 3.0f, null, null, type));
		}
	}
	
	/**
	 * This returns the number of non-spectator players. used to determine whether the server is full or not.
	 */
	public int getNumPlayers() {
		int playerNum = 0;
		
		for (Entry<Integer, User> conn: users.entrySet()) {
			if (conn.getValue().getPlayer() != null) {
				if (!conn.getValue().getPlayer().isSpectator()) {
					playerNum++;
				}
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

	public void sendToAllExceptUDP(int connId, Object p) {
		if (server != null) {
			server.sendToAllExceptUDP(connId, p);
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

	/**
	 * This boots the designated player from the game
	 */
	public void kickPlayer(PlayState ps, User user, int connID) {
		if (server != null) {
			addNotificationToAll(ps, user.getPlayer().getName(), " WAS KICKED!", DialogType.SYSTEM);
			sendToTCP(connID, new Packets.ClientYeet());
		}
	}

	/**
	 * @param p: the player
	 * @return the user corresponding to player p
	 */
	public User playerToUser(Player p) {
		for (User user: users.values()) {
			if (user.getPlayer() != null) {
				if (user.getPlayer().equals(p)) {
					return user;
				}
			}
		}
		return null;
	}

	public Server getServer() {	return server; }

	public void setServer(Server server) { this.server = server; }

	public HashMap<Integer, User> getUsers() { return users; }
}
