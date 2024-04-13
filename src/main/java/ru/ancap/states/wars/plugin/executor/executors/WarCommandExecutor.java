package ru.ancap.states.wars.plugin.executor.executors;

import com.mrivanplays.conversations.base.question.Question;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import ru.ancap.framework.command.api.commands.CommandTarget;
import ru.ancap.framework.command.api.commands.object.dispatched.LeveledCommand;
import ru.ancap.framework.command.api.commands.object.event.CommandDispatch;
import ru.ancap.framework.command.api.commands.object.event.CommandWrite;
import ru.ancap.framework.command.api.commands.object.executor.CommandOperator;
import ru.ancap.framework.command.api.commands.operator.delegate.Delegate;
import ru.ancap.framework.command.api.commands.operator.delegate.subcommand.Raw;
import ru.ancap.framework.command.api.commands.operator.delegate.subcommand.SubCommand;
import ru.ancap.framework.command.api.commands.operator.delegate.subcommand.rule.delegate.StringDelegatePattern;
import ru.ancap.framework.command.api.event.classic.NotEnoughArgumentsEvent;
import ru.ancap.framework.command.api.event.classic.NotEnoughPermissionsEvent;
import ru.ancap.framework.command.api.event.classic.UnexecutableCommandEvent;
import ru.ancap.framework.communicate.communicator.Communicator;
import ru.ancap.framework.communicate.modifier.Placeholder;
import ru.ancap.framework.identifier.Identifier;
import ru.ancap.framework.language.LAPI;
import ru.ancap.framework.language.additional.LAPIMessage;
import ru.ancap.states.AncapStates;
import ru.ancap.states.states.Nation.Nation;
import ru.ancap.states.states.city.City;
import ru.ancap.states.wars.AncapWars;
import ru.ancap.states.wars.api.player.Warrior;
import ru.ancap.states.wars.api.request.system.exception.AlreadySentException;
import ru.ancap.states.wars.api.request.system.exception.NoRequestException;
import ru.ancap.states.wars.api.state.WarState;
import ru.ancap.states.wars.api.war.WarData;
import ru.ancap.states.wars.connector.StateType;
import ru.ancap.states.wars.id.WarID;
import ru.ancap.states.wars.messaging.Message;
import ru.ancap.states.wars.plugin.executor.exception.*;
import ru.ancap.states.wars.utils.LAPIReceiver;

import javax.naming.NameAlreadyBoundException;
import javax.naming.NoPermissionException;
import java.util.List;
import java.util.function.Consumer;

public class WarCommandExecutor extends CommandTarget {

    public WarCommandExecutor() {
        super(new Delegate(
            new SubCommand(
                new StringDelegatePattern("declare"),
                
                /* так будет после CommandAPI 1.7 / AncapFramework 1.7 
                new Execute(){
                    @Line(provider = base, handler = handler)
                    public void run(WarriorAPI warrior, WarStateAPI warState, String name, String reason) throws Exception {
                        warrior.declareWarTo(warState, new WarData(name, reason));
                    }
                },*/
                dispatch -> {
                    Player player = (Player) dispatch.source().sender();
                    AncapWars.conversationManager().newConversationBuilder(player)
                        .withQuestion(Question.of("type", LAPI.localized(Message.Minecraft.Command.War.Declare.Conversation.TYPE, Identifier.of(dispatch.source().sender()))))
                        .withQuestion(Question.of("target", LAPI.localized(Message.Minecraft.Command.War.Declare.Conversation.TARGET, Identifier.of(dispatch.source().sender()))))
                        .withQuestion(Question.of("name", LAPI.localized(Message.Minecraft.Command.War.Declare.Conversation.NAME, Identifier.of(dispatch.source().sender()))))
                        .withQuestion(Question.of("reason", LAPI.localized(Message.Minecraft.Command.War.Declare.Conversation.REASON, Identifier.of(dispatch.source().sender()))))
                        .whenDone(context -> {
                            StateType type;
                            try {
                                type = StateType.valueOf(context.getInput("type").toUpperCase());
                            } catch (IllegalArgumentException exception) {
                                new UnexecutableCommandEvent(dispatch.source().sender(), new LAPIMessage(
                                    AncapWars.class, "messages.minecraft.errors.command.not-a-state-type",
                                    new Placeholder("input", context.getInput("type"))
                                )).callEvent();
                                return;
                            }
                            String name = context.getInput("target");
                            WarState api = WarState.ofName(type, name);
                            if (api == null) {
                                new UnexecutableCommandEvent(
                                    dispatch.source().sender(), 
                                    new LAPIMessage(
                                        AncapWars.class, "messages.minecraft.errors.command.not-a-state", 
                                        new Placeholder("input", context.getInput("target"))
                                    )
                                ).callEvent();
                                return;
                            }
                            api = api.warActor();
                            WarData data = new WarData(context.getInput("name"), context.getInput("reason"));
                            if (WarID.war().isBound(data.name())) {
                                new UnexecutableCommandEvent(
                                    dispatch.source().sender(), 
                                    new LAPIMessage(AncapWars.class, "messages.minecraft.errors.command.war-name-already-bound")
                                ).callEvent();
                                return;
                            }
                            Warrior warrior = Warrior.get(player);
                            try {
                                warrior.declareWarTo(api, data);
                            } catch (NoPermissionException exception) {
                                LAPIReceiver.send(Message.Minecraft.Error.Permission.LACK, dispatch.source().sender());
                            } catch (StateIsNeutralException exception) {
                                LAPIReceiver.send(Message.Minecraft.Error.State.NEUTRAL, dispatch.source().sender());
                            } catch (AlreadyInWarException exception) {
                                LAPIReceiver.send(Message.Minecraft.Error.State.ALREADY_IN_WAR, dispatch.source().sender());
                            } catch (IdiotException exception) {
                                LAPIReceiver.send(Message.Minecraft.Error.State.CANT_DECLARE_YOURSELF, dispatch.source().sender());
                            } catch (AllyDeclareException exception) {
                                LAPIReceiver.send(Message.Minecraft.Error.State.CANT_DECLARE_ALLY, dispatch.source().sender());
                            } catch (NameAlreadyBoundException exception) {
                                Communicator.of(dispatch.source().sender()).message(new LAPIMessage(AncapWars.class, "name-already-bound"));
                            }
                        })
                        .build()
                        .start();
                }
            ),
            new SubCommand(
                new StringDelegatePattern("peace"),
                new Delegate(
                    new Raw(dispatch -> new NotEnoughArgumentsEvent(dispatch.source().sender(), 2).callEvent()),
                    new SubCommand(
                        new StringDelegatePattern("request"),
                        new Delegate(
                            new Raw(dispatch -> new NotEnoughArgumentsEvent(dispatch.source().sender(), 2).callEvent()),
                            new SubCommand(
                                new StringDelegatePattern("send"),
                                new CommandOperator() {
                                    @Override
                                    public void on(CommandDispatch dispatch) {
                                        PeaceOperator.operate(
                                            dispatch,
                                            context -> {
                                                AncapWars.conversationManager().newConversationBuilder(context.warrior().online())
                                                    .withQuestion(Question.of("terms", LAPI.localized(Message.Minecraft.Command.War.Peace.Conversation.TERMS, Identifier.of(dispatch.source().sender()))))
                                                    .whenDone(conversationContext -> {
                                                        try {
                                                            context.warrior().requestPeaceTo(context.state(), conversationContext.getInput("terms"));
                                                        } catch (NoPermissionException exception) {
                                                            new NotEnoughPermissionsEvent(dispatch.source().sender()).callEvent();
                                                        } catch (NotInWarException exception) {
                                                            Messager.onNotInWar(dispatch.source().sender());
                                                        } catch (AlreadySentException exception) {
                                                            Messager.onAlreadySent(dispatch.source().sender());
                                                        }
                                                    })
                                                    .build()
                                                    .start();
                                            }
                                        );
                                    }
                                    
                                    @Override
                                    public void on(CommandWrite write) {
                                        WarCommandExecutor.stateTabComplete(write);
                                    }
                                }
                            ),
                            new SubCommand(
                                new StringDelegatePattern("revoke"),
                                new CommandOperator() {
                                    @Override
                                    public void on(CommandDispatch dispatch) {
                                        PeaceOperator.operate(
                                            dispatch,
                                            context -> {
                                                try {
                                                    context.warrior().revokePeaceRequestTo(context.state());
                                                } catch (NoPermissionException exception) {
                                                    new NotEnoughPermissionsEvent(dispatch.source().sender()).callEvent();
                                                } catch (NotInWarException exception) {
                                                    Messager.onNotInWar(dispatch.source().sender());
                                                } catch (NoRequestException exception) {
                                                    Messager.onNotSent(dispatch.source().sender());
                                                }
                                            }
                                        );
                                    }

                                    @Override
                                    public void on(CommandWrite write) {
                                        WarCommandExecutor.stateTabComplete(write);
                                    }
                                }
                            ),
                            new SubCommand(
                                new StringDelegatePattern("accept"),
                                new CommandOperator() {
                                    @Override
                                    public void on(CommandDispatch dispatch) {
                                        PeaceOperator.operate(
                                            dispatch,
                                            context -> {
                                                try {
                                                    context.warrior().acceptPeaceRequestFrom(context.state());
                                                } catch (NoPermissionException exception) {
                                                    new NotEnoughPermissionsEvent(dispatch.source().sender()).callEvent();
                                                } catch (NotInWarException exception) {
                                                    Messager.onNotInWar(dispatch.source().sender());
                                                } catch (
                                                    NoRequestException exception) {
                                                    Messager.onNotSent(dispatch.source().sender());
                                                }
                                            }
                                        );
                                    }

                                    @Override
                                    public void on(CommandWrite write) {
                                        WarCommandExecutor.stateTabComplete(write);
                                    }
                                }
                            ),
                            new SubCommand(
                                new StringDelegatePattern("cancel"),
                                new CommandOperator() {
                                    @Override
                                    public void on(CommandDispatch dispatch) {
                                        PeaceOperator.operate(
                                            dispatch,
                                            context -> {
                                                try {
                                                    context.warrior().cancelPeaceRequestFrom(context.state());
                                                } catch (NoPermissionException exception) {
                                                    new NotEnoughPermissionsEvent(dispatch.source().sender()).callEvent();
                                                } catch (NotInWarException exception) {
                                                    Messager.onNotInWar(dispatch.source().sender());
                                                } catch (
                                                    NoRequestException exception) {
                                                    Messager.onNotSent(dispatch.source().sender());
                                                }
                                            }
                                        );
                                    }

                                    @Override
                                    public void on(CommandWrite write) {
                                        WarCommandExecutor.stateTabComplete(write);
                                    }
                                }
                            )
                        )
                    )
                )
            )
        ));
    }

    private static void stateTabComplete(CommandWrite write) {
        if (write.line().isRaw()) write.speaker().sendTab(List.of("city", "nation"));
        else try {
            String type = write.line().nextArgument();
            List<String> tabs = switch (type) {
                case "city"   -> AncapStates.getCityMap().getCities().stream() .map(City::getName)  .toList();
                case "nation" -> AncapStates.getCityMap().getNations().stream().map(Nation::getName).toList();
                default -> throw new IllegalStateException("Unexpected value: " + type);
            };
            write.speaker().sendTab(tabs);
        } catch (Throwable ignored) {}
    }

    private interface StateContext {

        static StateContext from(CommandDispatch dispatch) {
            if (dispatch.command().isRaw()) {
                new NotEnoughArgumentsEvent(dispatch.source().sender(), 2).callEvent();
                return new ReturnStateContext();
            }
            String type = dispatch.command().nextArgument();
            StateType stateType;
            try {
                stateType = StateType.valueOf(type.toUpperCase());
            } catch (IllegalArgumentException exception) {
                new UnexecutableCommandEvent(dispatch.source().sender(), new LAPIMessage(
                    AncapWars.class, "messages.minecraft.errors.command.not-a-state-type",
                    new Placeholder("input", type)
                )).callEvent();
                return new ReturnStateContext();
            }
            LeveledCommand command = dispatch.command().withoutArgument();
            if (command.isRaw()) {
                new NotEnoughArgumentsEvent(dispatch.source().sender(), 1).callEvent();
                return new ReturnStateContext();
            }
            String name = command.nextArgument();
            WarState api = WarState.ofName(stateType, name);
            if (api == null) {
                new UnexecutableCommandEvent(dispatch.source().sender(), new LAPIMessage(
                    AncapWars.class, "messages.minecraft.errors.command.not-a-state",
                    new Placeholder("input", name)
                )).callEvent();
                return new ReturnStateContext();
            }
            Player player = (Player) dispatch.source().sender();
            Warrior warrior = Warrior.get(player);
            return new OkayStateContext(warrior, api);
        }

        boolean toReturn();
        Warrior warrior();
        WarState state();

        record OkayStateContext(Warrior warrior, WarState state) implements StateContext {

            @Override
            public boolean toReturn() {
                return false;
            }
        }

        class ReturnStateContext implements StateContext {

            @Override
            public boolean toReturn() {
                return true;
            }

            @Override
            public Warrior warrior() {
                throw new UnsupportedOperationException();
            }

            @Override
            public WarState state() {
                throw new UnsupportedOperationException();
            }
        }
    }

    private static class PeaceOperator {

        public static void operate(CommandDispatch dispatch, Consumer<StateContext> consumer) {
            StateContext context = StateContext.from(dispatch);
            if (context.toReturn()) return;
            consumer.accept(context);
        }

    }

    private static class Messager {

        public static void onNotInWar(CommandSender sender) {
            send(sender, Message.Minecraft.Error.State.NOT_IN_WAR);
        }

        public static void onAlreadySent(CommandSender sender) {
            send(sender, Message.Minecraft.Error.Request.ALREADY_SENT);
        }

        public static void onNotSent(CommandSender sender) {
            send(sender, Message.Minecraft.Error.Request.NOT_SENT);
        }

        private static void send(CommandSender sender, String s) {
            sender.sendMessage(Component.text(LAPI.localized(s, Identifier.of(sender))));
        }
    }
}