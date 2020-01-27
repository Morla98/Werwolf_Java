package exercises.JohannsLogikBude.StateMachine;

/**
 * @author Johann Hein
 */

public enum State {

    CUPID_SEND {
        @Override
        State simpleFunction() {
            System.out.println("Sending package to cupid...");
            return CUPID_GET;
        }
    },
    CUPID_GET {
        @Override
        State simpleFunction() {
            System.out.println("Waiting for cupid's answer...");
           // if(GameCycle.getTag().equals("Cupidmove")) {
            if(GameCycle.testTag.equals("Cupidmove")) {
                return SEER_SEND;
            }
            else {
                System.out.println("Returning current state!");
                return this;
            }
        }
    },
    SEER_SEND {
        @Override
        State simpleFunction() {
            System.out.println("Sending package to seer...");

            return SEER_GET;
        }
    },
    SEER_GET {
        @Override
        State simpleFunction() {
            System.out.println("Waiting for seer's answer...");
            if(GameCycle.getTag().equals("Seermove")) {
                return WEREWOLF_SEND;
            }
            else {
                return this;
            }
        }
    },
    WEREWOLF_SEND {
        @Override
        State simpleFunction() {
            System.out.println("Sending package to werewolves...");

            return WEREWOLF_GET;
        }
    },
    WEREWOLF_GET {
        @Override
        State simpleFunction() {
            System.out.println("Werewolves' move!");
            if(GameCycle.getTag().equals("")) {
                return WITCH_SEND_HEAL;
            }
            else {
                return this;
            }
        }
    },
    WITCH_SEND_HEAL {
        @Override
        State simpleFunction() {
            System.out.println("Sending package to seer...");
            return WITCH_GET_HEAL;
        }
    },
    WITCH_GET_HEAL {
        @Override
        State simpleFunction() {
            System.out.println("Witch's heal move!");
            if(GameCycle.getTag().equals("witchHeal")) {
                return VOTE_MAYOR;
            }
            else {
                return this;
            }
        }
    },
    WITCH_SEND_KILL {
        @Override
        State simpleFunction() {
            System.out.println("Sending package to seer...");
            return WITCH_GET_KILL;
        }
    },
    WITCH_GET_KILL {
        @Override
        State simpleFunction() {
            System.out.println("Witch's kill move!");
            if(GameCycle.getTag().equals("witchKill")) {
                return VOTE_MAYOR;
            }
            else {
                return this;
            }
        }

    },
    VOTE_MAYOR {
        @Override
        State simpleFunction() {
            System.out.println("Voting the mayor!");
            return VOTE;
        }
    },
    VOTE {
        @Override
        State simpleFunction() {
            System.out.println("Voting!");
            return SEER_SEND;
        }
    };
    abstract State simpleFunction();
}
