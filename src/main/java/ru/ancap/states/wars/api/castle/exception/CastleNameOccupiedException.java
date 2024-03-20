package ru.ancap.states.wars.api.castle.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class CastleNameOccupiedException extends Exception {

    private final String name;
}
