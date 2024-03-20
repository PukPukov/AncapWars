package ru.ancap.states.wars.fees;

import ru.ancap.library.Balance;

import java.util.Map;

public class WarFees {

    public static final Balance CASTLE_CREATION = new Balance(Map.of("netherite", 3.0));

    public static final Balance CASTLE_ATTACK = new Balance(Map.of("iron", 192.0));

    public static final Balance DEVASTATION_REPAYMENT = new Balance(Map.of("netherite", 7.0));
    
    public static final Balance CORE_BARRIER_ATTACK = new Balance(Map.of("iron", 32.0));

}
