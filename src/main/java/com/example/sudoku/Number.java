package com.example.sudoku;

import java.util.List;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Defines the attributes of a specific position of a sodoku board.
 */
@Data
@Accessors(chain=true)
public class Number {

    /**
     * The row for this position.
     */
    int row;
    /**
     * The column for this position.
     */
    int column;
    /**
     * The value that this position contains. 
     * If no value is assigned it is set to 0.
     */
    int value;
    /**
     * This identifies values that are not available to be used
     * for this position.
     */
    List<Integer> usedValues;
    /**
     * This identifies the values that can possibly be used
     * for this position. The idea is that if only one is
     * possible then it must be the correct one.
     */
    List<Integer> possibleValues;
}