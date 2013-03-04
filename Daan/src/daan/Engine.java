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
        System.out.println( new Board("8/8/2N2n2/3Nn3/3Nn3/2N2n2/8/8 w - - 0 1").generateMoves() );
        System.out.println( new Board("8/8/8/8/8/8/8/RN2K2R w KQ - 0 1").generateMoves() );
        System.out.println( new Board("8/8/8/4b3/3BB3/8/8/8 w - - 0 1").generateMoves() );
        System.out.println( new Board("8/8/5n2/4K3/3Q4/8/1R1P1q2/8 w - - 0 1").generateMoves() );
        System.out.println( new Board("8/8/4k3/3R4/1Q1RK3/8/3r4/8 w - - 0 1").generateMoves() );
    }
    
    public Board getBoard(){
        return this.board;
    }

}
