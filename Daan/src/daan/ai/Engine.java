/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package daan.ai;

import daan.representation.Board;
import daan.representation.Move;
import daan.utils.Constants.*;
import static daan.utils.Constants.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Rafiek Mohamedjoesoef <Rafiek.Mohamedjoesoef@hva.nl>
 * created 14-feb-2013, 10:22:45
 */
public class Engine{
    
    public Board board; 

    private int visitedNodes;
    
    private int currentMoveNumber;
    
     ArrayList<Move> principalVariation;
    
    public Engine(){
        
        init( FEN_START_POSITION );
        
    }
    
    public Engine( String fen ){
        
        init( fen );
        
    }
    
    private void init( String fen ){
        
        this.board = new Board(fen);
        currentMoveNumber = 1;
        visitedNodes = 0;
        
    }
    
    public Board getBoard(){
        return this.board;
    }
    
    
    
    /*
     * root alphaBeta
     */
    public List<Move> search( int depth ) {

        principalVariation = new ArrayList<>();
        visitedNodes = 0;
        int eval = alphaBeta( START_VALUE_ALPHA, START_VALUE_BETA, depth, principalVariation );

        if ( !principalVariation.isEmpty() ) {

            System.out.print( "info" );
            System.out.print( " nodes " + visitedNodes );

            if ( Math.abs( eval ) > 100000 ) {

                int mateDistance = VALUE_MATE - Math.abs( eval );
                
                //using a hack to print the correct amount of moves, should fix this in the search
                System.out.print( " score mate " + mateDistance );
                List<Move> subList = principalVariation.subList( 0, mateDistance );
                System.out.println( " pv " + subList.toString().substring( 1, subList.toString().length() - 1 ) );

            } else {

                System.out.print( " score cp " + eval );
                System.out.println( " pv " + principalVariation.toString().substring( 1, principalVariation.toString().length() - 1 ) );

            }

            
            System.out.println( "bestmove " + principalVariation.get( 0 ) );

        } else {

            System.out.println( "pv is empty" );

        }

        return principalVariation;

    }
    
    int alphaBeta( int alpha, int beta, int depthLeft, ArrayList<Move> parentPV ){        
        
        if( depthLeft == 0 ){
            
            return quiescenceSearch( alpha, beta );
            
        }
        
        ArrayList<Move> childPV = new ArrayList<>();
        List<Move> moves = board.generateMoves();        
        int numberOfMoves = moves.size();
        boolean noLegalMoves = true;
        
        for( int i = 0; i < numberOfMoves; i++ ){    
            
            board.makeMove( moves.get( i ) );
            
            //find position of king of the side that just moved
            int kingPosition = ( board.sideToMove == WHITE ) ? board.blackKingPosition : board.whiteKingPosition;
            
            //if after making a move and the king of the side that made the move is NOT in check, then it's a legal move
            if ( !board.isAttacked( board.sideToMove, kingPosition ) ) {
                
                if ( depthLeft == MAX_DEPTH_SEARCH ) {

                    System.out.print( "info" );
                    System.out.print( " nodes " + visitedNodes );
                    System.out.print( " currmove " + moves.get( i ) );
                    System.out.println( " currmovenumber " + currentMoveNumber );
                    
                    currentMoveNumber++;

                }
                
                visitedNodes++;
                
                noLegalMoves = false;

                int score = -alphaBeta( -beta, -alpha, depthLeft - 1, childPV );  
                
                if ( score >= beta ) {
                    
                    board.unmakeMove( moves.get( i ) );
                    return beta;
                    
                }
                
                if( score > alpha ){

                    alpha = score;

                    if( parentPV.isEmpty() ){
                        
                        parentPV.trimToSize();
                        parentPV.add( moves.get( i ) );    
                        
                    } else {
                        
                        parentPV.set( 0, moves.get( i ) );
                        
                    }
                    
                    for ( int j = 0; j < childPV.size(); j++ ) {

                        if ( parentPV.size() - 2 < j ) {

                            parentPV.addAll( j + 1, childPV );
                            break;

                        } else {

                            parentPV.set( j + 1, childPV.get( j ) );

                        }

                    }

                }
                
                if ( depthLeft == MAX_DEPTH_SEARCH ) {

                    System.out.print( "info" );
                    System.out.print( " nodes " + visitedNodes );
                    
                    if( Math.abs( alpha ) > 100000 ){
                        
                        System.out.print( " score mate " + ( VALUE_MATE - Math.abs( alpha ) ) );                        
                        
                    } else {
                        
                        System.out.print( " score cp " + alpha );
                        
                    }
                    
                    System.out.println( " pv " + parentPV.toString().substring( 1, parentPV.toString().length() - 1 ) );

                }

            }

            board.unmakeMove( moves.get( i ) );

        }
        
        if( noLegalMoves ){
            
            int kingPosition = ( board.sideToMove == WHITE ) ? board.whiteKingPosition : board.blackKingPosition;
            
            if( board.isAttacked( -board.sideToMove, kingPosition ) ){
                
                return ( VALUE_MATE -  ( MAX_DEPTH_SEARCH - depthLeft ) ) * board.sideToMove;
                
            } else {
                
                //stalemate
                return VALUE_DRAW;
                
            }
            
        }

        return alpha;
    }
    
    public void start_perft(){
        
        long time = System.nanoTime();
        DecimalFormat twoDForm = new DecimalFormat("#.##");
        System.out.println( "Performance test" );
        
        for(int i = 1; i <= MAX_DEPTH_SEARCH; i++){            
            
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
            
            int kingPosition = ( board.sideToMove == WHITE ) ? board.blackKingPosition : board.whiteKingPosition;
            
            if( !board.isAttacked( board.sideToMove, kingPosition ) ){
                //System.out.println( "legal move!" );
                nodes += perft( depth - 1 );                
               
            }
            
            board.unmakeMove( moves.get( i ) );
           
        }
                
        return nodes;
    }

    public void makeMoves( String[] moves ) {
        
        board.makeMoves( moves );
        
    }

    private int quiescenceSearch( int alpha, int beta ) {

        int standPat = board.evaluate( 0 );

        if ( standPat >= beta ) {

            return beta;

        }

        if ( standPat > alpha ) {

            alpha = standPat;

        }

        List<Move> moves = board.filterCaptureMoves( board.generateMoves() );
        int numberOfMoves = moves.size();

        for ( int i = 0; i < numberOfMoves; i++ ) {

            board.makeMove( moves.get( i ) );

            //find position of king of the side that just moved
            int kingPosition = ( board.sideToMove == WHITE ) ? board.blackKingPosition : board.whiteKingPosition;

            //if after making a move and the king of the side that made the move is NOT in check, then it's a legal move
            if ( !board.isAttacked( board.sideToMove, kingPosition ) ) {

                visitedNodes++;

                int score = -quiescenceSearch( -beta, -alpha );

                if ( score >= beta ) {

                    board.unmakeMove( moves.get( i ) );
                    return beta;

                }

                if ( score > alpha ) {

                    alpha = score;

                }

            }

            board.unmakeMove( moves.get( i ) );

        }

        return alpha;

    }

}
