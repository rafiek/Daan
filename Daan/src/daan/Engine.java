/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package daan;

/**
 *
 * @author Rafiek Mohamedjoesoef <Rafiek.Mohamedjoesoef@hva.nl>
 * created 14-feb-2013, 10:22:45
 */
public class Engine {
    
    Board board;
    
    public Engine(){
        System.out.println(new Board().generateMoves());
        System.out.println(new Board("8/p4p1p/1r2k1pP/6P1/1P1R1P2/8/2r2PK1/7R w - -").generateMoves());
        System.out.println(new Board("8/1P6/8/8/8/8/8/8 w - - 0 1").generateMoves());
        System.out.println( new Board("8/1n1q4/2P5/8/8/8/8/8 w - - 0 1").generateMoves() );
        System.out.println( new Board("1n1b4/PPP4p/8/P2QnPpP/4PPpn/2r1r1Pp/P2P3P/8 w - g6 0 1").generateMoves() );
    }
    
    public Board getBoard(){
        return this.board;
    }

}
