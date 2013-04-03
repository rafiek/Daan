/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package daan.ai;

import daan.representation.Board;
import daan.representation.Move;
import static daan.utils.Constants.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author Rafiek Mohamedjoesoef <Rafiek.Mohamedjoesoef@hva.nl>
 * created 14-feb-2013, 10:22:45
 */
public class Engine{
    
    public Board board;

    public int visitedNodes;

    ArrayList<Move> principalVariation;

    private long maxThinkingTime;

    private long startTime;
    
    List<Move> rootMoves;
    
    public Engine(){
        
        init( FEN_START_POSITION );
        
    }
    
    public Engine( String fen ){
        
        init( fen );
        
    }
    
    private void init( String fen ){
        
        this.board = new Board(fen);
        visitedNodes = 0;
        
    }
    
    public Board getBoard(){
        return this.board;
    }
    
    boolean timeOver() {
        
        if ( System.currentTimeMillis() - startTime >= maxThinkingTime ) {

            return false;

        } else {
            
            return true;
            
        }
        
    }
    
    /*
     * root alphaBeta
     */
    public Move search( int maxDepth, long wtime, long btime ) {
        
        if( board.sideToMove == WHITE ){
          
            maxThinkingTime = wtime / ( board.valueMaterialWhite / 100 + 1 );
            
        } else {
            
            maxThinkingTime = btime / ( board.valueMaterialBlack / 100 + 1 );
             
        }
        
        int depth = 1;
        int lowerBound;
        int upperBound;
        
        startTime = System.currentTimeMillis();
        
        rootMoves = board.generateMoves();
        
        Move bestMove = searchRoot( START_VALUE_ALPHA, START_VALUE_BETA, depth );
        
        if( rootMoves.isEmpty() ){ //no legal moves, mate or stalemate
            
            return bestMove;
            
        }
        
        Collections.sort( rootMoves, HIGH_LOW_SCORE );                
        
        for( depth = 2; depth <= maxDepth; depth++){
            
            lowerBound = bestMove.score - ASPIRATION;
            upperBound = bestMove.score + ASPIRATION;            
            
            bestMove = searchRoot( lowerBound, upperBound, depth );
            
            if( bestMove.score <= lowerBound ){ //fail low, nothing improved alpha (All-node)
                
                bestMove = searchRoot( bestMove.score - ( 2 * ASPIRATION ), upperBound, depth );
                
            } else if( bestMove.score >= upperBound ){ //fail high, (Cut-node) 
                
                bestMove = searchRoot( lowerBound, bestMove.score + ( 2 * ASPIRATION ), depth );
                
            }
            
             if( !timeOver() ){
                
                break;
                
            }
            
            Collections.sort( rootMoves, HIGH_LOW_SCORE );    
            
        }

        System.out.println( "bestmove " + bestMove );

        return bestMove;
        
    }
    
    Move searchRoot( int alpha, int beta, int depthLeft ) {

        visitedNodes = 0;
        Move bestMove = rootMoves.get( 0 );
        
        if( board.isAttacked( -board.sideToMove, ( board.sideToMove == WHITE ) ? board.whiteKingPosition : board.blackKingPosition ) ){
            
            depthLeft++;
            
        }
        
        for ( int i = 0; i < rootMoves.size(); i++ ) {

            board.makeMove( rootMoves.get( i ) );

            //find position of king of the side that just moved
            int kingPosition = ( board.sideToMove == WHITE ) ? board.blackKingPosition : board.whiteKingPosition;

            //if after making a move and the king of the side that made the move is NOT in check, then it's a legal move
            if ( !board.isAttacked( board.sideToMove, kingPosition ) ) {

                visitedNodes++;
                
                rootMoves.get( i ).next = alphaBeta( -beta, -alpha, depthLeft - 1, rootMoves.get( i ).next );
                
                if ( !timeOver() ) {

                    break;

                }

                //take -score of new currline
                int score = -rootMoves.get( i ).next.score;
                rootMoves.get( i ).score = score;
                
                if ( score > alpha ) {

                    bestMove = rootMoves.get( i );
                    
                    if( score >= beta ){
                        
                        //save move??
                        
                    } else {
                        
                        alpha = score;
                        
                    }

                }
                
                System.out.print( "info" );
                System.out.print( " currmove " + rootMoves.get( i ) );
                System.out.print( " currmovenumber " + ( i + 1 ) );

                System.out.print( " nodes " + visitedNodes );

                if ( Math.abs( alpha ) > 100000 ) {

                    System.out.print( " score mate " + ( VALUE_MATE - Math.abs( alpha ) ) );

                } else {

                    System.out.print( " score cp " + alpha );

                }

                System.out.println( " pv " + bestMove.getLine() );

                board.unmakeMove( rootMoves.get( i ) );

            } else {

                board.unmakeMove( rootMoves.get( i ) );
                rootMoves.remove( i );
                i--;
                
                if( !timeOver() ){
                    
                    break;
                    
                }
                
            }
            
        }

        return bestMove;

    }
    
    Move alphaBeta( int alpha, int beta, int depthLeft, Move pvMove ){        
        
        Move bestMove = new Move();
        
        if( board.isAttacked( -board.sideToMove, ( board.sideToMove == WHITE ) ? board.whiteKingPosition : board.blackKingPosition ) ){
            
            depthLeft++;
            
        }
        
        if( depthLeft == 0 ){
            
            bestMove.score = quiescenceSearch( alpha, beta );
            return bestMove;
            
        }
        
        List<Move> moves = board.generateMoves();  
        
        if( pvMove != null ){            
            
            int swapIndex = moves.indexOf( pvMove );
            
            if( swapIndex > 0 ){
                
                moves.set( swapIndex, moves.get( 0 ) );
                moves.set( 0, pvMove );
                
            }
            
        } 
        
        int numberOfMoves = moves.size();
        boolean noLegalMoves = true;
        Move prevLine;
        
        for( int i = 0; i < numberOfMoves; i++ ){ 
            
            board.makeMove( moves.get( i ) );
            
            //find position of king of the side that just moved
            int kingPosition = ( board.sideToMove == WHITE ) ? board.blackKingPosition : board.whiteKingPosition;
            
            //if after making a move and the king of the side that made the move is NOT in check, then it's a legal move
            if ( !board.isAttacked( board.sideToMove, kingPosition ) ) {

                visitedNodes++;

                noLegalMoves = false;
                
                //backup previous line
                prevLine = bestMove.next;

                //receive new line for evaluation
                moves.get( i ).next = alphaBeta( -beta, -alpha, depthLeft - 1, moves.get( i ).next );
                
                if ( !timeOver() ) {

                    return bestMove;

                }

                //take -score of new line
                int score = -moves.get( i ).next.score;
                moves.get( i ).score = score;
                
                if ( score >= beta ) {

                    board.unmakeMove( moves.get( i ) );
                    moves.get( i ).score = beta;
                    
                    return moves.get( i );

                }

                if ( score > alpha ) {

                    alpha = score;
                    bestMove = moves.get( i );
                                        
                } else {
                    
                    bestMove.next = prevLine;
                    bestMove.score = score;
                    
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
        
        List<Move> moves = board.generateMoves();

        int kingPosition = ( board.sideToMove == WHITE ) ? board.whiteKingPosition : board.blackKingPosition;
        
        if( !board.isAttacked( -board.sideToMove, kingPosition ) ){
                                    
            moves = board.filterQuiescenceMoves( moves );
            
        }
        
        int numberOfMoves = moves.size();

        for ( int i = 0; i < numberOfMoves; i++ ) {

            board.makeMove( moves.get( i ) );

            //find position of king of the side that just moved
            kingPosition = ( board.sideToMove == WHITE ) ? board.blackKingPosition : board.whiteKingPosition;

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
