package com.example.sudoku;

import java.util.List;
import java.util.stream.IntStream;

import org.apache.commons.collections4.CollectionUtils;

import java.util.stream.Collectors;
import java.util.HashMap;
import java.util.ArrayList;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain=true)
public class Board {

    List<Number> numbers;

    /**
     * Uses several methods to update the values of the board.
     * 
     * @return the number of unsolved positions.
     */
    public int updateBoard() {
        clearTemporaryData();
        setUsedValuesbyRow();
        setUsedValuesByColumns();
        setUsedValueInBoxes();
        findUniqueInBoxes();
        updateValues();

        return numbers.stream()
        .filter(n -> n.getValue() == 0)
        .collect(Collectors.toList()).size();
    }

    /**
     * Looks at each row and identifies what values are already used in that row.
     * This is used to set the "used values" property of each number.
     */
    private void setUsedValuesbyRow() {
    
        //Loop through each row.
        IntStream.rangeClosed(1,9).forEach(i -> {
            //Loop through each position in the row.
            IntStream.rangeClosed(1,9).forEach(j -> {
            //If this value is not set find all the other values in the row and set 
            //the possible values of this item to those that are not currently set.
            int row = i;
            Number currentNumber = numbers.stream()
                .filter(n -> n.getRow() == row)
                .filter(n -> n.getColumn() == j)
                .findAny().get();
                //If the value is 0 it is not set
                if (currentNumber.getValue() < 1) {
                    //Used numbers in this row
                    List<Integer> usedNumbers = numbers.stream()
                    .filter(n -> n.getValue() > 0)
                    .filter(n -> n.getRow() == row)
                    .filter(n -> n.getColumn() != j)
                    .map(Number::getValue)
                    .collect(Collectors.toList());

                    //If position currently doesn't have used values then set them to 
                    //the ones that were found.
                    if (currentNumber.getUsedValues() == null){
                        currentNumber.setUsedValues(usedNumbers);
                    }
                    else {
                        //Set the position's used values to the union of what it currently has and the
                        //identifed used values.
                        currentNumber.setUsedValues(
                            CollectionUtils.union(currentNumber.getUsedValues(), usedNumbers)
                            .stream().collect(Collectors.toList()));
                    }
                }
            });
        });
    }

    /**
     * Looks at each column and identifies what values are already used in that column.
     * This is used to set the "used values" property of each number.
     */
    private void setUsedValuesByColumns() {
        //Loop through each column.
        IntStream.rangeClosed(1,9).forEach(i -> {
            //Loop through each position in the row.
            IntStream.rangeClosed(1,9).forEach(j -> {
            //If this value is not set find all the other values in the row and set 
            //the possible values of this item to those that are not currently set.
            int column = i;
            Number currentNumber = numbers.stream()
                .filter(n -> n.getRow() == j)
                .filter(n -> n.getColumn() == column)
                .findAny().get();
                if (currentNumber.getValue() < 1) {
                    //Used numbers in this column
                    List<Integer> usedNumbers = numbers.stream()
                    .filter(n -> n.getValue() > 0)
                    .filter(n -> n.getRow() != j)
                    .filter(n -> n.getColumn() == column)
                    .map(Number::getValue)
                    .collect(Collectors.toList());

                    //Figure out where the numbers are different
                    if (currentNumber.getUsedValues() == null){
                        currentNumber.setUsedValues(usedNumbers);
                    }
                    else {
                        currentNumber.setUsedValues(
                            CollectionUtils.union(currentNumber.getUsedValues(), usedNumbers)
                            .stream().collect(Collectors.toList()));
                    }
                }
            });
        });

    }

    /**
     * Looks at all boxes and sets their used values. 
     */
    private void setUsedValueInBoxes() {
        
        setUsedValuesByBox(1,3,1,3);
        setUsedValuesByBox(1,3,4,6);
        setUsedValuesByBox(1,3,7,9);
        setUsedValuesByBox(4,6,1,3);
        setUsedValuesByBox(4,6,4,6);
        setUsedValuesByBox(4,6,7,9);
        setUsedValuesByBox(7,9,1,3);
        setUsedValuesByBox(7,9,4,6);
        setUsedValuesByBox(7,9,7,9);
    }

    /**
     * Used to analyze each box for unique possible values.
     */
    private void findUniqueInBoxes() {
        findUniqueInBox(1,3,1,3);
        findUniqueInBox(1,3,4,6);
        findUniqueInBox(1,3,7,9);
        findUniqueInBox(4,6,1,3);
        findUniqueInBox(4,6,4,6);
        findUniqueInBox(4,6,7,9);
        findUniqueInBox(7,9,1,3);
        findUniqueInBox(7,9,4,6);
        findUniqueInBox(7,9,7,9);

    }

    /**
     * Looks at the box specified by the row and column ranges and identifies what values are 
     * used in each box.  This is used to update the used values property.
     * @param startRow
     * @param endRow
     * @param startColumn
     * @param endColumn
     */
    private void setUsedValuesByBox(int startRow, int endRow, int startColumn, int endColumn) {

        List<Number> box = numbers.stream()
            .filter(n -> n.getRow() >= startRow)
            .filter(n -> n.getRow() <= endRow)
            .filter(n -> n.getColumn() >= startColumn)
            .filter(n -> n.getColumn() <= endColumn)
            .collect(Collectors.toList());

        box.forEach(n -> {
            if (n.getValue() == 0){
                List<Integer> usedNumbers = box.stream().filter(num -> num.getValue() > 0)
                .map(Number::getValue)
                .collect(Collectors.toList());
                    
                //Figure out where the numbers are different
                if (n.getUsedValues() == null){
                    n.setUsedValues(usedNumbers);
                }
                else {
                        n.setUsedValues(
                            CollectionUtils.union(n.getUsedValues(), usedNumbers)
                            .stream().collect(Collectors.toList()));
                }
            }
        });
    }

    /**
     * Analyzes the box to find if there are any possible values that exist in only 
     * one position.  The box is identified by the range of rows and columns.
     * @param startRow
     * @param endRow
     * @param startColumn
     * @param endColumn
     */
    private void findUniqueInBox(int startRow, int endRow, int startColumn, int endColumn) {

        //Reduce the board to consist of only the positions in the box and 
        //only include those that aren't yet assigned a value.
        List<Number> box = numbers.stream()
        .filter(n -> n.getRow() >= startRow)
        .filter(n -> n.getRow() <= endRow)
        .filter(n -> n.getColumn() >= startColumn)
        .filter(n -> n.getColumn() <= endColumn)
        .filter(n -> n.getValue() == 0)
        .collect(Collectors.toList());

        //Create a hash to count the number of occurences of each possible value.
        HashMap<Integer,Integer> possibleValues = new HashMap<Integer, Integer>();

        box.forEach(n -> {
            //Find the possible values...

            //Create a list of numbers 1-9
            List<Integer> intThrough9 = IntStream.rangeClosed(1, 9)
                .boxed()
                .collect(Collectors.toList());

            //Remove those that have been used to set the possible values.
            intThrough9.removeAll(n.getUsedValues());
            n.setPossibleValues(intThrough9);
            
            //Update the map with the count of each possible value
            intThrough9.stream().forEach(i -> {
                if (possibleValues.containsKey(i)) {
                    possibleValues.replace(i, possibleValues.get(i) + 1);
                }
                else {
                    possibleValues.put(i,1);
                }
            });
        });

        //Filter out those that only have 1.  If there are any then we know
        //that position can be set to that value.
        possibleValues.entrySet().stream().
        filter(p -> p.getValue() == 1).
        forEach(p -> {
        //Whichever block has this unique possibility is the one to set.
            box.stream()
                .filter(b -> b.getPossibleValues() != null)
                .filter(b -> b.getPossibleValues().contains(p.getKey()))
                .findFirst()
                .ifPresent(n -> n.setValue(p.getKey()));
        });
    }

    /**
     * Iterate through the board to find out if we can set any of the values.
     */
    private void updateValues() {
 
        //Find the spaces that can be solved.
        List<Number>updatableNumbers = numbers.stream()
            .filter(n -> n.getUsedValues() != null)
            .filter(n -> n.getUsedValues().size() == 8)
            .filter(n -> n.getValue() == 0)
            .collect(Collectors.toList());

        updatableNumbers.forEach(u -> {
            //Create a list of numbers 1-9
            List<Integer> intThrough9 = IntStream.rangeClosed(1, 9)
                .boxed()
                .collect(Collectors.toList());

            //Remove the used numbers from the list of 9 
            //to get expected value.
            intThrough9.removeAll(u.getUsedValues());
            u.setValue(intThrough9.get(0));
            }
        );
    }

    public boolean compareNumbers(Board compareBoard) { 

        boolean compares = true;

        compareBoard.getNumbers().stream().forEach(n -> 
        {
            Number boardNumber = this.getNumbers().stream()
                .filter(s -> s.getRow() == n.getRow())
                .filter(s -> s.getColumn() == n.getColumn())
                .findFirst().orElseThrow(() -> new IllegalArgumentException());
        });

        return compares;
    }

    /**
     * Clears out the possible and usedvalues properties.
     */
    private void clearTemporaryData() {

        numbers.stream().forEach(n -> {
            n.setPossibleValues(null);
            n.setUsedValues(null);
        });
    }

    /**
     * Converts the ImportNumbers to a Board.  ImportNumbers is a two 
     * dimensional array of the numbers.
     */
    public static Board of(ImportNumbers importNumbers) {

        List<Number> importedNumbers = new ArrayList<>();
        
        for (int i=0; i<importNumbers.getNumbers().length; i++) {
            for (int j=0; j<importNumbers.getNumbers()[i].length; j++) {
                importedNumbers.add(new Number()
                    .setColumn(j+1)
                    .setRow(i+1)
                    .setValue(importNumbers.getNumbers()[i][j]));
            }
        }
       
        return new Board().setNumbers(importedNumbers);
    }
}

