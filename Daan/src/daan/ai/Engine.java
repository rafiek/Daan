/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package daan.ai;

import daan.representation.Board;
import daan.representation.Move;
import static daan.utils.Constants.*;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Rafiek Mohamedjoesoef <Rafiek.Mohamedjoesoef@hva.nl>
 * created 14-feb-2013, 10:22:45
 */
public class Engine{
    
    private int captures, ep, castles, promotions, checks, checkmates;
    
    public Board board;

    public int visitedNodes;

    private long maxThinkingTime;

    private long startTime;
    
    public List<Move> rootMoves;
    
    public Move bestMove;
    
    private BufferedWriter standardOutput = new BufferedWriter( new PrintWriter( System.out ) );
    
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
    
    public void setWhiteTime( long wtime ){
        
        maxThinkingTime = wtime / ( board.valueMaterialWhite / 100 + 1 );
        
    }
    
    public void setBlackTime( long btime ){
        
        maxThinkingTime = btime / ( board.valueMaterialBlack / 100 + 1 );
        
    }
    
    public void setMaxThinkingTime( long time ){
        
        maxThinkingTime = time;
        
    }
    
    public int search( int maxDepth ) {
        
        int depth = 1;
        visitedNodes = 0;
        int lowerBound = START_VALUE_ALPHA;
        int upperBound = START_VALUE_BETA;
        
        startTime = System.currentTimeMillis();
        
        rootMoves = board.generatePseudoMoves();
        //sort the moves from high to low score
        Collections.sort( rootMoves ); 
        //find the best move at depth 1
        int score = searchRoot( lowerBound, upperBound, depth );
        
        if( rootMoves.isEmpty() ){ //no legal moves, mate or stalemate
            
            return score;
            
        }
        
        for( depth = 2; depth <= maxDepth; depth++){
            
            if( !timeOver() ){
                
                break;
                
            }
            
            if( rootMoves.size() == 1 && depth == 5 ){
                
                break;
                
            }
            
            lowerBound = score - ASPIRATION;
            upperBound = score + ASPIRATION;         
            
            //find the best move with depth 'depth'
            score = searchRoot( lowerBound, upperBound, depth );
            Collections.sort( rootMoves );    
            
            if( score <= lowerBound ){ //fail low, nothing improved alpha (All-node)
                
                score = searchRoot(  START_VALUE_ALPHA, lowerBound + 1, depth );
                Collections.sort( rootMoves );   
                
            } else if( score >= upperBound ){ //fail high, (Cut-node) 
                
                score = searchRoot( upperBound - 1, START_VALUE_BETA, depth );
                Collections.sort( rootMoves );   
                
            }
            
            long time = System.currentTimeMillis() - startTime;
            long nps = ( visitedNodes / ( ( time + 1000 ) / 1000 ) );
            
            try {

                standardOutput.write( "info" );
                standardOutput.write( " time " + time );
                standardOutput.write( " depth " + depth );
                standardOutput.write( " nodes " + visitedNodes );
                standardOutput.write( " nps " + nps );

                if ( Math.abs( bestMove.score ) > 100000 ) {

                    standardOutput.write( " score mate " + ( VALUE_MATE - Math.abs( bestMove.score ) ) );

                } else {

                    standardOutput.write( " score cp " + bestMove.score );

                }

                standardOutput.write( " pv " + bestMove.getLine() );
                standardOutput.newLine();
                standardOutput.flush();

            }
            catch ( IOException ex ) {
                Logger.getLogger( Engine.class.getName() ).log( Level.SEVERE, null, ex );
            }
            
        }
        
        System.out.println( "bestmove " + bestMove );

        return score;
        
    }
    
    int searchRoot( int alpha, int beta, int ply ) {
        
        if ( !timeOver() ) {

            return alpha;

        }

        int depthLeft = ply;
        Move currMove;
        int score;
        int countFailHigh = 0;
        
        int index = rootMoves.indexOf( bestMove );
            
        if ( index >= 0 ) {

            rootMoves.remove( index );
            rootMoves.add( 0, bestMove);

        } 
        
        for ( int i = 0; i < rootMoves.size(); i++ ) {
            
            currMove = rootMoves.get( i );

            board.makeMove( currMove );

            //find position of king of the side that just moved
            int kingPosition = ( board.sideToMove == WHITE ) ? board.blackKingPosition : board.whiteKingPosition;

            //if after making a move and the king of the side that made the move is NOT in check, then it's a legal move
            if ( !board.isAttacked( board.sideToMove, kingPosition ) ) {

                visitedNodes++;

                if( i == 0 ){
                    
                    score = -alphaBeta( -beta, -alpha, depthLeft - 1, currMove );
                    
                } else {
                    
                    score = -alphaBeta( -( alpha + 1 ), -alpha, depthLeft - 1, currMove );
                    
                    if( ( score > alpha ) && ( score < beta ) ){
                        
                        countFailHigh++;
                        
                        if ( countFailHigh == 2 ) {

                            score = -alphaBeta( -beta, -alpha, depthLeft - 1, currMove );
                            countFailHigh = 0;

                        }
                        
                    }     
                    
                }
                
                board.unmakeMove( currMove );
                
                if ( !timeOver() ) {

                    break;

                }
                
                currMove.score = score;   
                
                if ( score > alpha ) {
                    
                    if ( score >= beta ) {

                        return beta;

                    }

                    bestMove = currMove;
                    alpha = score;

                }
                
                if ( ply > 7 ) {

                    try {

                        standardOutput.write( "info" );
                        standardOutput.write( " currmove " + currMove );
                        standardOutput.write( " currmovenumber " + ( i + 1 ) );
                        if ( Math.abs( score ) > 100000 ) {

                            standardOutput.write( " score mate " + ( VALUE_MATE - Math.abs( score ) ) );

                        } else {

                            standardOutput.write( " score cp " + score );

                        }
                        
                        standardOutput.newLine();
                        standardOutput.flush();

                    }
                    catch ( IOException ex ) {
                        Logger.getLogger( Engine.class.getName() ).log( Level.SEVERE, null, ex );
                    }

                }


            } else {

                board.unmakeMove( currMove );
                rootMoves.remove( i );
                i--;

            }

        }

        return alpha;

    }
    
    int alphaBeta( int alpha, int beta, int depthLeft, Move prevBestMove ){
        
        if ( !timeOver() ) {

            return alpha;

        }
        
        if( board.isAttacked( -board.sideToMove, ( ( board.sideToMove == WHITE ) ? board.whiteKingPosition : board.blackKingPosition ) ) ){
            
            depthLeft++;
            
        }
        
        if( depthLeft == 0 ){
            
            return quiescenceSearch( alpha, beta );
            
        }
        
        List<Move> moves = board.generatePseudoMoves();   
        
        Move currMove;
        int numberOfMoves = moves.size();
        int score;
        boolean noLegalMoves = true;
        int countFailHigh = 0;
        
        if( prevBestMove.next != null ){
            
            int indexPVMove = moves.indexOf( prevBestMove.next );
            
            if( indexPVMove >= 0 ){
                
                while( indexPVMove > 0 ){
                    
                    Collections.swap( moves, indexPVMove, indexPVMove - 1 );
                    indexPVMove--;
                    
                }
                   
            }
            
        } 
        
        for( int i = 0; i < numberOfMoves; i++ ){
            
            currMove = ( ( i == 0 ) && ( prevBestMove.next != null ) ) ? moves.get( 0 ) : Board.sortHighestScoringMove( numberOfMoves, moves, i );
            
            board.makeMove( currMove );
            
            //find position of king of the side that just moved
            int kingPosition = ( board.sideToMove == WHITE ) ? board.blackKingPosition : board.whiteKingPosition;
            
            //if after making a move and the king of the side that made the move is NOT in check, then it's a legal move
            if ( !board.isAttacked( board.sideToMove, kingPosition ) ) {

                visitedNodes++;

                noLegalMoves = false;

                //receive new line for evaluation
                if( i == 0 ){
                    
                     score = -alphaBeta( -beta, -alpha, depthLeft - 1, currMove );
                    
                } else {
                    
                    score = -alphaBeta( -( alpha + 1 ), -alpha, depthLeft - 1, currMove );
                    
                    if( ( score > alpha ) && ( score < beta ) ){
                        
                        countFailHigh++;
                        
                        if ( countFailHigh == 2 ) {

                            score = -alphaBeta( -beta, -alpha, depthLeft - 1, currMove );
                            countFailHigh = 0;

                        }
                        
                    }     
                    
                }
                
                board.unmakeMove( currMove );
                 
                currMove.score = score;
                
                if ( score > alpha ) {
                    
                     if ( score >= beta ) {
                         
                        return beta;                         

                    }
                     
                    prevBestMove.next = currMove;
                    alpha = score;
                                        
                } 
                
            } else {
                
                board.unmakeMove( currMove );
                
            }

        }
        
        if( noLegalMoves ){
            
            int kingPosition = ( board.sideToMove == WHITE ) ? board.whiteKingPosition : board.blackKingPosition;
            
            if( board.isAttacked( -board.sideToMove, kingPosition ) ){
                
                alpha = - ( VALUE_MATE -  ( MAX_DEPTH_SEARCH - depthLeft ) );
                
            } else {
                
                //stalemate
                alpha = VALUE_DRAW;
                
            }
            
        }

        return alpha;
    }
    
    public void start_perft(){
        
        long time = System.nanoTime();
        DecimalFormat twoDForm = new DecimalFormat("#.##");
        System.out.println( "Performance test" );
        
        for(int i = 1; i <= MAX_DEPTH_SEARCH; i++){            
            
            captures = 0;
            ep = 0;
            System.out.println( "depth: "+i+" time: "+twoDForm.format((System.nanoTime()-time) / Math.pow(10, 9))+" nodes: "+perft( i ) );
            System.out.println( "captures = "+captures );
            System.out.println( "ep = "+ep );
            
        }
        
    }
    
    int perft( int depth ){
        
        int nodes = 0;
        
        if( depth == 0 ){
            return 1;
        }
        
        List<Move> moves = board.generatePseudoMoves();          
        //System.out.println( moves );
        //System.out.println( board );
        int numberOfMoves = moves.size();
                
        for( int i = 0; i < numberOfMoves; i++ ){
            
            board.makeMove( moves.get( i ) );
            
            //System.out.println( moves );
            
            int kingPosition = ( board.sideToMove == WHITE ) ? board.blackKingPosition : board.whiteKingPosition;
            
            if( !board.isAttacked( board.sideToMove, kingPosition ) ){
                //System.out.println( "legal move!" );
                if( ( moves.get( i ).type & MOVE_TYPE_CAPTURE ) == MOVE_TYPE_CAPTURE ){
                    captures++;
                }
                if( moves.get( i ).type == MOVE_TYPE_EP ){
                    ep++;
                }
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
        visitedNodes++;

        if ( standPat >= beta ) {
            
            return beta;

        }

        if ( standPat > alpha ) {

            alpha = standPat;

        }
        
        List<Move> moves;

        int kingPosition = ( board.sideToMove == WHITE ) ? board.whiteKingPosition : board.blackKingPosition;
        
        if( !board.isAttacked( -board.sideToMove, kingPosition ) ){
                                    
            moves = board.generateQuiescence();
            
        } else { 
            
            moves = board.generatePseudoMoves();
            
        }
        
        int numberOfMoves = moves.size();

        for ( int i = 0; i < numberOfMoves; i++ ) {
            
            Board.sortHighestScoringMove( numberOfMoves, moves, i );

            board.makeMove( moves.get( i ) );

            //find position of king of the side that just moved
            kingPosition = ( board.sideToMove == WHITE ) ? board.blackKingPosition : board.whiteKingPosition;

            //if after making a move and the king of the side that made the move is NOT in check, then it's a legal move
            if ( !board.isAttacked( board.sideToMove, kingPosition ) ) {

                int score = -quiescenceSearch( -beta, -alpha );

                board.unmakeMove( moves.get( i ) );
                
                if ( score >= beta ) {

                    return beta;

                }

                if ( score > alpha ) {

                    alpha = score;                    

                }

            } else {
                
                board.unmakeMove( moves.get( i ) );
                
            }

        }

        return alpha;

    }

}
