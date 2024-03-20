package ru.ancap.states.wars.plugin.executor;

import lombok.SneakyThrows;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import ru.ancap.framework.command.api.commands.CommandTarget;
import ru.ancap.framework.command.api.commands.object.event.CommandDispatch;
import ru.ancap.framework.command.api.commands.object.executor.CommandOperator;
import ru.ancap.framework.command.api.commands.operator.communicate.Reply;
import ru.ancap.framework.command.api.commands.operator.delegate.Delegate;
import ru.ancap.framework.command.api.commands.operator.delegate.subcommand.Raw;
import ru.ancap.framework.command.api.commands.operator.delegate.subcommand.SubCommand;
import ru.ancap.framework.command.api.commands.operator.delegate.subcommand.rule.delegate.StringDelegatePattern;
import ru.ancap.framework.command.api.commands.operator.exclusive.Exclusive;
import ru.ancap.framework.command.api.commands.operator.exclusive.OP;
import ru.ancap.framework.communicate.communicator.Communicator;
import ru.ancap.framework.communicate.message.Message;
import ru.ancap.framework.communicate.modifier.Placeholder;
import ru.ancap.framework.language.additional.LAPIMessage;
import ru.ancap.hexagon.common.Point;
import ru.ancap.states.AncapStates;
import ru.ancap.states.states.State;
import ru.ancap.states.wars.AncapWars;
import ru.ancap.states.wars.api.hexagon.WarHexagon;
import ru.ancap.states.wars.api.player.Warrior;
import ru.ancap.states.wars.api.state.WarState;
import ru.ancap.states.wars.api.war.WarData;
import ru.ancap.states.wars.api.war.WarView;
import ru.ancap.states.wars.connector.StateType;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class WarsCommandExecutor extends CommandTarget {

    public WarsCommandExecutor() {
        super(new Delegate(
            new Raw(dispatch -> {
                Communicator communicator = Communicator.of(dispatch.source().sender());
                communicator.message(sender -> "Команда в разработке");
                /*List<War> wars = new ArrayList<>();
                try {
                    wars = new Warrior(dispatch.source().sender().getName()).getWars();
                } catch (NotInStateException exception) {
                    communicator.send(new LAPIMessage(AncapWars.class, "messages.minecraft.errors.state.free"));
                }
                communicator.send(ChatBook.builder(wars)
                    .header(new LAPIMessage())
                    .provider(war -> new LAPIMessage(
                            AncapWars.class, "messages.minecraft.command.wars.list.entry",
                            new Placeholder("WAR", war.name())))
                    .footer((currentPage, pageRange) -> )
                    .pageSize(sender -> 10)
                    .build()
                );
                Player player;
                player.;*/
            }),
            new SubCommand(
                new StringDelegatePattern("admin"),
                new Exclusive(
                    new OP(),
                    new Delegate(
                        new SubCommand(
                            new StringDelegatePattern("debug"),
                            new Delegate(
                                new Raw(new Reply(() -> new LAPIMessage(
                                    AncapWars.class, "messages.minecraft.command.wars.debug.status",
                                    new Placeholder("status", AncapWars.debug())
                                ))),
                                new SubCommand(
                                    new StringDelegatePattern("toggle"),
                                    dispatch -> {
                                        AncapWars.setDebug(!AncapWars.debug());
                                        Communicator.of(dispatch.source().sender()).message(new LAPIMessage(
                                            AncapWars.class, "messages.minecraft.command.wars.debug.toggle",
                                            new Placeholder("status", AncapWars.debug())
                                        ));
                                    }
                                )
                            )
                        ),
                        new SubCommand(
                            new StringDelegatePattern("initialize"),
                            new Delegate(
                                new SubCommand(
                                    new StringDelegatePattern("base"),
                                    new CommandOperator() {
                                        @Override
                                        public void on(CommandDispatch dispatch) {
                                            Bukkit.getScheduler().callSyncMethod(AncapWars.loaded(), () -> {
                                                try {
                                                    String AnusAnusov = "AnusAnusov";
                                                    String PukPukov = "PukPukov";
                                                    Player anusovPlayer = Bukkit.getPlayer(AnusAnusov);
                                                    Player pukovPlayer = Bukkit.getPlayer(PukPukov);
                                                    Warrior pukov = Warrior.get(pukovPlayer);
                                                    Warrior anusov = Warrior.get(anusovPlayer);
                                                    Location pukovHome = this.locationFor(-158, 152);
                                                    Location anusovHome = this.locationFor(318, -532);
                                                    pukov.online().teleport(pukovHome);
                                                    pukov.createTestCity();
                                                    anusov.online().teleport(anusovHome);
                                                    anusov.createTestNation();
                                                    List<Location> pukovCastles = List.of();
                                                    List<Location> anusovCastles = List.of(
                                                        this.locationFor(302, -172),
                                                        this.locationFor(448, -266),
                                                        this.locationFor(294, -690),
                                                        this.locationFor(298, -348)
                                                    );
                                                    for (Location location : pukovCastles) {
                                                        pukov.online().teleport(location);
                                                        pukov.buildCastle(PukPukov + "_castle_"+location.hashCode());
                                                    }
                                                    for (Location location : anusovCastles) {
                                                        anusov.online().teleport(location);
                                                        anusov.buildCastle(AnusAnusov + "_castle_"+location.hashCode());
                                                    }
                                                } catch (Throwable throwable) {
                                                    throwable.printStackTrace();
                                                }
                                                return Void.TYPE;
                                            });
                                        }

                                        private Location locationFor(int x, int y) {
                                            World world = Bukkit.getWorld("world");
                                            return new Location(world, x, world.getHighestBlockYAt(x, y) + 1, y);
                                        }
                                    }
                                ),
                                new SubCommand(
                                    new StringDelegatePattern("war"),
                                    new CommandOperator() {
                                        @Override
                                        @SneakyThrows
                                        public void on(CommandDispatch dispatch) {
                                            Player player = (Player) dispatch.source().sender();
                                            Warrior.get(player).declareWarTo(
                                                WarState.ofName(StateType.NATION, "AnusAnusovNation"),
                                                new WarData("example-war", "пидорасы")
                                            );
                                        }
                                    }
                                ),
                                new SubCommand(
                                    new StringDelegatePattern("small"),
                                    new CommandOperator() {
                                        @Override
                                        public void on(CommandDispatch dispatch) {
                                            Bukkit.getScheduler().callSyncMethod(AncapWars.loaded(), () -> {
                                                try {
                                                    String AnusAnusov = "AnusAnusov";
                                                    String PukPukov = "PukPukov";
                                                    Player anusovPlayer = Bukkit.getPlayer(AnusAnusov);
                                                    Player pukovPlayer = Bukkit.getPlayer(PukPukov);
                                                    Warrior pukov = Warrior.get(pukovPlayer);
                                                    Warrior anusov = Warrior.get(anusovPlayer);
                                                    Location pukovHome = this.locationFor(-158, 152);
                                                    Location anusovHome = this.locationFor(-10, -186);
                                                    pukov.online().teleport(pukovHome);
                                                    pukov.initializeTestCity(hexagon -> hexagon.neighbors(1));
                                                    anusov.online().teleport(anusovHome);
                                                    anusov.initializeTestNation(hexagon -> List.of());
                                                } catch (Throwable throwable) {
                                                    throwable.printStackTrace();
                                                }
                                                return Void.TYPE;
                                            });
                                        }

                                        private Location locationFor(int x, int y) {
                                            World world = Bukkit.getWorld("world");
                                            return new Location(world, x, world.getHighestBlockYAt(x, y) + 1, y);
                                        }
                                    }
                                )
                            )
                        ),
                        new SubCommand(
                            new StringDelegatePattern("example-message"),
                            dispatch -> {
                                List<String> collected = new ArrayList<>();
                                var command = dispatch.command();
                                while (!command.isRaw()) {
                                    collected.add(command.nextArgument());
                                    command = command.withoutArgument();
                                }
                                String result = String.join(" ", collected);
                                Communicator.of(dispatch.source().sender()).message(new Message(result));
                            }
                        ),
                        new SubCommand(
                            new StringDelegatePattern("super-error-sound"),
                            new CommandOperator() {
                                @Override
                                @SneakyThrows
                                public void on(CommandDispatch dispatch) {
                                    for (int i = 0; i < 4; i++) {
                                        for (int j = 0; j < 10; j++) {
                                            dispatch.source().sender().playSound(Sound.sound(Key.key("minecraft:block.anvil.land"), Sound.Source.AMBIENT, 2F, 0F));
                                            Thread.sleep(25);
                                        }
                                        Thread.sleep(100);
                                    }
                                }
                            }
                        ),
                        new SubCommand(
                            new StringDelegatePattern("benchmark"),
                            new Delegate(
                                new SubCommand(
                                    new StringDelegatePattern("can-be-attacked-by"),
                                    dispatch -> {
                                        WarState attacker = WarState.ofName(StateType.CITY, "PukPukovCity");
                                        WarHexagon hexagon = new WarHexagon(AncapStates.grid.hexagon(new Point(306, -180)).code());
                                        int iterations = 100000;
                                        boolean[] blackHole = new boolean[iterations+1];
                                        long start = System.nanoTime();
                                        for (int i = 0; i < iterations; i++) {
                                            blackHole[i] = hexagon.canBeAttackedBy(attacker);
                                        }
                                        long end = System.nanoTime();
                                        dispatch.source().communicator().message(new Message(blackHole[new Random().nextInt(iterations-1)]));
                                        long oneEstimated = (end - start) / iterations;
                                        dispatch.source().communicator().message(new Message("Затрачено на 1 операцию: "+oneEstimated+" нс"));
                                    }
                                ),
                                new SubCommand(
                                    new StringDelegatePattern("war-view"),
                                    dispatch -> {
                                        WarState attacker = WarState.ofName(StateType.NATION, "AnusAnusovNation");
                                        WarState defender = WarState.ofName(StateType.CITY, "PukPukovCity");
                                        int iterations = 100000;
                                        WarView[] blackHole = new WarView[iterations+1];
                                        long start = System.nanoTime();
                                        for (int i = 0; i < iterations; i++) {
                                            blackHole[i] = attacker.warWith(defender);
                                        }
                                        long end = System.nanoTime();
                                        dispatch.source().communicator().message(new Message(blackHole[new Random().nextInt(iterations-1)]));
                                        long oneEstimated = (end - start) / iterations;
                                        dispatch.source().communicator().message(new Message("Затрачено на 1 операцию: "+oneEstimated+" нс"));
                                    }
                                )
                            )
                        ),
                        new SubCommand(
                            new StringDelegatePattern("repair"),
                            dispatch -> {
                                List<State> states = new ArrayList<>();
                                states.addAll(AncapStates.getCityMap().getCities());
                                states.addAll(AncapStates.getCityMap().getNations());
                                states.forEach(state -> {
                                    StateType type = StateTypeConverter.toWars(state.name().type());
                                    if (WarState.ofName(type, state.name().name()) == null) WarState.initialize(state.id(), type);
                                });
                            }
                        )
                    )
                )
            )
        ));
    }

}
