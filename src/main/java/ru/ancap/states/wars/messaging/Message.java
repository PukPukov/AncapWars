package ru.ancap.states.wars.messaging;

import ru.ancap.states.wars.AncapWars;

public class Message {

    public static class Minecraft {

        public static class Error {

            public static class Dev {

                public static final String COMMAND = AncapWars.MESSAGE_DOMAIN+"minecraft.errors.dev.command";

            }

            public static class Permission {

                public static final String LACK = AncapWars.MESSAGE_DOMAIN+"minecraft.errors.permission.lack";

            }

            public static class Request {

                public static final String ALREADY_SENT = AncapWars.MESSAGE_DOMAIN+"minecraft.errors.request.already-sent";
                public static final String NOT_SENT = AncapWars.MESSAGE_DOMAIN+"minecraft.errors.request.not-sent";

            }

            public static class State {

                public static final String NEUTRAL = AncapWars.MESSAGE_DOMAIN+"minecraft.errors.state.neutral";
                public static final String ALREADY_IN_WAR = AncapWars.MESSAGE_DOMAIN+"minecraft.errors.state.already-in-war";
                public static final String NOT_IN_WAR = AncapWars.MESSAGE_DOMAIN+"minecraft.errors.state.not-in-war";
                public static final String NOT_ENOUGH_MONEY = AncapWars.MESSAGE_DOMAIN+"minecraft.errors.state.not-enough-money";
                public static final String CANT_DECLARE_YOURSELF = AncapWars.MESSAGE_DOMAIN+"minecraft.errors.state.cant-declare-yourself";
                public static final String CANT_DECLARE_ALLY = AncapWars.MESSAGE_DOMAIN+"minecraft.errors.state.cant-declare-ally";

            }

            public static class Name {

                public static final String INVALID = AncapWars.MESSAGE_DOMAIN+"minecraft.errors.name.invalid";

            }

            public static class Castle {

                public static final String ATTACK_ATTACKED = AncapWars.MESSAGE_DOMAIN+"minecraft.errors.castle.attack-attacked";

                public static class Name {

                    public static final String OCCUPIED = AncapWars.MESSAGE_DOMAIN+"minecraft.errors.castle.name.occupied";
                    public static final String NOT_FOUND = AncapWars.MESSAGE_DOMAIN+"minecraft.errors.castle.name.not-found";

                }

                public static class legacy_Hexagon {

                    public static final String ALREADY_BUILT = AncapWars.MESSAGE_DOMAIN+"minecraft.errors.castle.hexagon.already-built";
                    public static final String NO_CASTLE = AncapWars.MESSAGE_DOMAIN+"minecraft.errors.castle.hexagon.no-castle";
                    public static final String PROTECTED = AncapWars.MESSAGE_DOMAIN+"minecraft.errors.castle.hexagon.protected";
                }

                public static class Attack {

                    public static class Time {

                        public static final String INVALID = AncapWars.MESSAGE_DOMAIN+"minecraft.errors.castle.attack.time.invalid";

                    }

                }

            }

            public static class War {

                public static final String BEDROCK = AncapWars.MESSAGE_DOMAIN+"minecraft.errors.war.bedrock";

            }
        }

        public static class Command {

            public static class War {

                public static class Declare {

                    public static class Conversation {

                        public static final String TYPE = AncapWars.MESSAGE_DOMAIN+"minecraft.command.war.declare.conversation.type";
                        public static final String TARGET = AncapWars.MESSAGE_DOMAIN+"minecraft.command.war.declare.conversation.target";
                        public static final String NAME = AncapWars.MESSAGE_DOMAIN+"minecraft.command.war.declare.conversation.name";
                        public static final String REASON = AncapWars.MESSAGE_DOMAIN+"minecraft.command.war.declare.conversation.reason";

                    }

                }
                
                public static class Peace {
                    
                    public static class Conversation {

                        public static final String TERMS = AncapWars.MESSAGE_DOMAIN+"minecraft.command.war.peace.conversation.terms";
                        
                    }

                }

            }

            public static class Wars {

                public static final String DEBUG_TOGGLE = AncapWars.MESSAGE_DOMAIN+"minecraft.command.wars.debug-toggle";

            }
        }

        public static class Notify {

            public static class Castle {

                public static final String FOUND = AncapWars.MESSAGE_DOMAIN+"minecraft.notify.castle.found";

            }

            public static class Assault {

                public static final String DECLARE = AncapWars.MESSAGE_DOMAIN+"minecraft.notify.assault.declare";
                public static final String YOU_DECLARED = AncapWars.MESSAGE_DOMAIN+"minecraft.notify.assault.you_declared";
                public static final String START = AncapWars.MESSAGE_DOMAIN+"minecraft.notify.assault.start";
                public static final String REPULSE = AncapWars.MESSAGE_DOMAIN+"minecraft.notify.assault.repulse";
                public static final String DESTROY = AncapWars.MESSAGE_DOMAIN+"minecraft.notify.assault.destroy";

            }

            public static class Hexagon_legacy {

                public static final String OCCUPY = AncapWars.MESSAGE_DOMAIN+"minecraft.notify.hexagon.occupy";

            }

            public static class War {

                public static class Penalty {

                    public static final String GET = AncapWars.MESSAGE_DOMAIN+"minecraft.notify.war.penalty.get";

                }

                public static class Start {

                    public static final String DECLARE = AncapWars.MESSAGE_DOMAIN+"minecraft.notify.war.start.declare";

                }

                public static class Stop {

                    public static final String DEATH = AncapWars.MESSAGE_DOMAIN+"minecraft.notify.war.stop.death";
                    public static final String PEACE = AncapWars.MESSAGE_DOMAIN+"minecraft.notify.war.stop.peace";

                }

                public static class Peace {

                    public static class Request {

                        public static class Offer {

                            public static final String YOU = AncapWars.MESSAGE_DOMAIN+"minecraft.notify.war.peace.request.offer.you";
                            public static final String TO_YOU = AncapWars.MESSAGE_DOMAIN+"minecraft.notify.war.peace.request.offer.to-you";

                        }

                        public static class Revoke {

                            public static final String YOU = AncapWars.MESSAGE_DOMAIN+"minecraft.notify.war.peace.request.revoke.you";

                        }

                        public static class Reject {

                            public static final String YOU = AncapWars.MESSAGE_DOMAIN+"minecraft.notify.war.peace.request.reject.you";
                            public static final String YOURS = AncapWars.MESSAGE_DOMAIN+"minecraft.notify.war.peace.request.reject.yours";

                        }

                    }

                }

            }

        }

        public static class Attention {

            public static class Field {

                public static final String SOON = AncapWars.MESSAGE_DOMAIN+"minecraft.attention.field.soon";
                public static final String ATTACK = AncapWars.MESSAGE_DOMAIN+"minecraft.attention.field.attack";

            }

        }

        public static class Info {

            public static class FieldConflict {

                public static class BossBar {

                    public static final String NAME = AncapWars.MESSAGE_DOMAIN+"minecraft.info.field-conflict.boss-bar.name";

                }

            }

            public static class Assault {

                public static class Core {

                    public static class BossBar {

                        public static final String DISTANCE = AncapWars.MESSAGE_DOMAIN+"minecraft.info.assault.core.boss-bar.name";

                    }

                }

            }

        }

    }



    public static class VK {

        public static class Notify {

            public static class War {

                public static final String DECLARE = AncapWars.MESSAGE_DOMAIN+"vk.notify.war.declare";

            }

            public static class Siege {

                public static final String DECLARE = AncapWars.MESSAGE_DOMAIN+"vk.notify.siege.declare";
                public static final String REPULSE = AncapWars.MESSAGE_DOMAIN+"vk.notify.siege.repulse";
                public static final String DESTROY = AncapWars.MESSAGE_DOMAIN+"vk.notify.siege.destroy";

            }

            public static class legacy_Hexagon {

                public static final String OCCUPY = AncapWars.MESSAGE_DOMAIN+"vk.notify.hexagon.occupy";

            }

        }

    }




}
