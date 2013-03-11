/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package daan;

import static daan.Constants.B_KING;
import static daan.Constants.W_KING;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Rafiek Mohamedjoesoef <Rafiek.Mohamedjoesoef@hva.nl>
 * created 14-feb-2013, 10:22:45
 */
public class Engine implements Constants{
    
    Board board;
    int kingLocation;    
    
    public Engine(){
        
        init( FEN_START_POSITION );
        
    }
    
    public Engine( String fen ){
        
        init( fen );
        
    }
    
    private void init( String fen ){
        
        this.board = new Board(fen);
        
    }
    
    public Board getBoard(){
        return this.board;
    }
    
    /*
     * root negaMax
     */
    public List<Move> search(int depth){
        ArrayList<Move> principalVariation = new ArrayList<>(depth);
        double eval = negaMax( depth, principalVariation);
        System.out.println( "PV: " + eval + " " + principalVariation );
        
        return principalVariation;
        
    }
    
    
    
    double negaMax( int depth, ArrayList<Move> parentPV ){
        
        if( depth == 0 ){
            
            return board.evaluate();
            
        }
        
        double max = Double.NEGATIVE_INFINITY;
        double score = 0;
        
        ArrayList<Move> childPV = new ArrayList<>();
        List<Move> moves = board.generateMoves();        
        int numberOfMoves = moves.size();
                
        for( int i = 0; i < numberOfMoves; i++ ){
            
            board.makeMove( moves.get( i ) );
            
            if( depth == 5 ){
                
                System.out.println( moves.get( i ) + " " );
                
            }
            
            if( board.sideToMove < 0 ){
                kingLocation = Board.getKeyByValue( board.locationOfWhitePieces, W_KING );            
            }else{
                kingLocation = Board.getKeyByValue( board.locationOfBlackPieces, B_KING );   
            }
            
            if ( !board.isAttacked( board.sideToMove, kingLocation ) ) {

                score = -negaMax( depth - 1, childPV );               
                
                if ( score > max ) {
                    
                    max = score;
                    
                    if( parentPV.isEmpty() ){
                        
                        parentPV.add( moves.get( i ) );
                        
                    } else {
                        
                        parentPV.set( 0, moves.get( i ) );
                                
                    }
                     
                    if ( parentPV.size() > 1 ) {

                        for ( int j = 0; j < childPV.size(); j++ ) {

                            parentPV.set( j + 1, childPV.get( j ) );

                        }

                    } else {

                        parentPV.addAll( 1, childPV );

                    }
                   
                    if ( depth == 5 ) {

                        System.out.println( max + ": " + parentPV );

                    }

                }
                
            }
            
            board.unmakeMove( moves.get( i ) );
            
        }
        
        return max;
    }
    
    public void start_perft(){
        
        long time = System.nanoTime();
        DecimalFormat twoDForm = new DecimalFormat("#.##");
        System.out.println( "Performance test" );
        
        for(int i = 1; i <= 6; i++){            
            
            System.out.println( "depth: "+i+" time: "+twoDForm.format((System.nanoTime()-time) / Math.pow(10, 9))+" nodes: "+perft( i ) );
            
        }
        
    }
    
    int perft( int depth ){
        
        int nodes = 0;
        
        if( depth == 0 ){
            return 1;
        }
        
        List<Move> moves = board.generateMoves();        
        int numberOfMoves = moves.size();
                
        for( int i = 0; i < numberOfMoves; i++ ){
            
            board.makeMove( moves.get( i ) );
            //System.out.println( moves );
            if( board.sideToMove < 0 ){
                kingLocation = Board.getKeyByValue( board.locationOfWhitePieces, W_KING );            
            }else{
                kingLocation = Board.getKeyByValue( board.locationOfBlackPieces, B_KING );   
            }
            
            if( !board.isAttacked( board.sideToMove, kingLocation ) ){
                
                nodes += perft( depth - 1 );                
               
            }
            
            board.unmakeMove( moves.get( i ) );
           
        }
                
        return nodes;
    }

}
