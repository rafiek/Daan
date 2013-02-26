/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package daan;

import java.util.List;

/**
 *
 * @author Rafiek Mohamedjoesoef <Rafiek.Mohamedjoesoef@hva.nl>
 * created 14-feb-2013, 10:22:45
 */
public class Engine {
    
    Board board;
    
    public Engine(){
        this.board = new Board();
        List<Move> moves = board.generateMoves();
        System.out.println(moves);
        this.board = new Board("8/p4p1p/1r2k1pP/6P1/1P1R1P2/8/2r2PK1/7R b - -");
        //board.generateMoves();
    }
    
    public Board getBoard(){
        return this.board;
    }

}
