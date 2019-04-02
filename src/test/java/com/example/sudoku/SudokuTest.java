package com.example.sudoku;

import static org.assertj.core.api.Assertions.*;
import org.junit.Test;

import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;
import com.fasterxml.jackson.databind.*;
import java.io.File;
import java.util.stream.Collectors;

public class SudokuTest {

    public void produceTestData() throws Exception{

        Board board = new Board();

        List<Number> numbers = new ArrayList<Number>();

        IntStream.rangeClosed(1,9).forEach(i -> {
            IntStream.rangeClosed(1,9).forEach(j -> {
                numbers.add(new Number()
                    .setRow(i)
                    .setColumn(j)
                    .setValue(1));
            });
        });

        board.setNumbers(numbers);
        ObjectMapper mapper = new ObjectMapper();
        System.out.println(mapper.writeValueAsString(board));

        File file = new File("C:/Users/reidk/Documents/sudoku/sudoku/testOutput.txt");
        FileOutputStream fos = new FileOutputStream(file);
        fos.write(mapper.writeValueAsString(board).getBytes());
        fos.close();
    }

    @Test
    public void readTestData() throws Exception {

        ObjectMapper mapper = new ObjectMapper();
        Board board = mapper.readValue(this.getClass().getResourceAsStream("/board.json"), Board.class);
        assertThat(board.getNumbers().size()).isEqualTo(81);
    }

    @Test
    public void boardTest() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        Board board = mapper.readValue(this.getClass().getResourceAsStream("/board.json"), Board.class);

        System.out.println("------total values starting are: " + board.numbers.stream().filter(n->n.getValue() > 0).collect(Collectors.toList()).size());

        IntStream.rangeClosed(1, 10).forEach(i -> {
            if (board.updateBoard() == 0) {
                System.out.println("****Finished with " + i + " iterations.****");
            }
        });
        
        List<Number> numbers = board.numbers.stream().filter(n->n.getValue() > 0).collect(Collectors.toList());
        System.out.println("------total values are: " + numbers.size());

        printBoard(board);
    }

    @Test
    public void importNumberTest() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        ImportNumbers importNumbers = mapper.readValue(this.getClass().getResourceAsStream("/numbers.json"), ImportNumbers.class);

        assertThat(importNumbers.getNumbers().length).isEqualTo(9);

    }

    @Test
    public void boardOfTest() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        ImportNumbers importNumbers = mapper.readValue(this.getClass().getResourceAsStream("/numbers.json"), ImportNumbers.class);

        assertThat(Board.of(importNumbers).getNumbers().size()).isEqualTo(81);
    }

    @Test
    public void testBoardThatIsLoadedFromImportNumbers() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        ImportNumbers importNumbers = mapper
            .readValue(this.getClass().getResourceAsStream("/numbers.json"), ImportNumbers.class);
        System.out.println(importNumbers.getNumbers().toString());
        Board board = Board.of(importNumbers);

        IntStream.rangeClosed(1, 10).forEach(i -> {
            if (board.updateBoard() == 0) {
                System.out.println("****Finished with " + i + " iterations.****");
            }
        });
        
        printBoard(board);
    }

    /**
     * Helper method that prints the provided board.
     */
    public void printBoard(Board board) throws Exception {
        
        PrintWriter printWriter = new PrintWriter(new File("C:/Users/reidk/Documents/sudoku/sudoku/sudokuNumbersResults.txt"));

        IntStream.rangeClosed(1, 9).forEach(
            i -> {
                printWriter.println(boardRow(i, board)); 
            }
        );
        printWriter.close();
    }

    /**
     * Helper method that returns a comma separted value for a board row.
     */
    public String boardRow(int row, Board board) {
        return 
        board.numbers.stream()
            .filter(n -> n.getRow() == row)
            .map(Number::getValue)
            .map(String::valueOf)
            .collect(Collectors.joining(","));

    }
}