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
    public Move search( int depth ) {

        
        visitedNodes = 0;
        Move bestMove = new Move();
        alphaBeta( START_VALUE_ALPHA, START_VALUE_BETA, depth, bestMove );

        //if ( bestMove != null ) {

            System.out.print( "info" );
            System.out.print( " nodes " + visitedNodes );

            if ( Math.abs( bestMove.score ) > 100000 ) {

                int mateDistance = VALUE_MATE - Math.abs( bestMove.score );
                
                //using a hack to print the correct amount of moves, should fix this in the search
                System.out.print( " score mate " + mateDistance );                

            } else {

                System.out.print( " score cp " + bestMove.score );

            }
            
            System.out.println( " pv " + bestMove.getLine() );
            
            System.out.println( "bestmove " + bestMove );

        //} else {

        //    System.out.println( "pv is empty" );

        //}

        return bestMove;

    }
    
    Move alphaBeta( int alpha, int beta, int depthLeft, Move bestMove ){        
        
        if( depthLeft == 0 ){
            
            Move quiesceMove = new Move();
            quiesceMove.score = quiescenceSearch( alpha, beta );
            return quiesceMove;
            
        }
        
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

                Move bestLine = bestMove.next;
                
                bestMove.next = alphaBeta( -beta, -alpha, depthLeft - 1, new Move() );

                int score = -bestMove.next.score;

                if ( score >= beta ) {

                    board.unmakeMove( moves.get( i ) );                    
                    bestMove.next = bestLine;
                    Move betaMove = new Move();
                    betaMove.score = beta;
                    return betaMove;

                }

                if ( score > alpha ) {

                    alpha = score;
                    moves.get( i ).next = bestMove.next;
                    bestMove.copy( moves.get( i ) );
                    bestMove.score = alpha;
                                        
                } else {
                    
                    bestMove.next = bestLine;
                    
                }
                
                if ( depthLeft == MAX_DEPTH_SEARCH ) {

                    System.out.print( "info" );
                    System.out.print( " nodes " + visitedNodes );
                    
                    if( Math.abs( alpha ) > 100000 ){
                        
                        System.out.print( " score mate " + ( VALUE_MATE - Math.abs( alpha ) ) );  
                        
                    } else {
                        
                        System.out.print( " score cp " + alpha );
                        
                    }
                    
                    System.out.println( " pv " + bestMove.getLine() );

                }

            }

            board.unmakeMove( moves.get( i ) );

        }
        
        if( noLegalMoves ){
            
            int kingPosition = ( board.sideToMove == WHITE ) ? board.whiteKingPosition : board.blackKingPosition;
            
            if( board.isAttacked( -board.sideToMove, kingPosition ) ){
                
                bestMove.score = - ( VALUE_MATE -  ( MAX_DEPTH_SEARCH - depthLeft ) );
                
            } else {
                
                //stalemate
                bestMove.score = VALUE_DRAW;
                
            }
            
        }

        return bestMove;
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
