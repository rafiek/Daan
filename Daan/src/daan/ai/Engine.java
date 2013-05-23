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
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Rafiek Mohamedjoesoef <Rafiek.Mohamedjoesoef@hva.nl>
 * created 14-feb-2013, 10:22:45
 */
public class Engine{
    
    private int captures, ep;
    
    private Board board;

    private int visitedNodes;

    private long maxThinkingTime;

    private long startTime;
    
    private List<Move> rootMoves;
    
    private Move bestMove;
    
    private TreeMap<Move,Integer>[] killerMoves = new TreeMap[ MAX_DEPTH_SEARCH ];
    
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
    
    private boolean timeOver() {
        
        if ( ( System.currentTimeMillis() - startTime ) >= maxThinkingTime ) {

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
        
        int depth = 4;
        visitedNodes = 0;
        int lowerBound = START_VALUE_ALPHA;
        int upperBound = START_VALUE_BETA;
        
        startTime = System.currentTimeMillis();
        
        rootMoves = board.generatePseudoMoves();
        
        //find the best move at depth 1
        bestMove = Board.sortHighestScoringMove( rootMoves.size(), rootMoves, 0 );
        int score = searchRoot( lowerBound, upperBound, depth );
        
        if( rootMoves.isEmpty() ){ //no legal moves, mate or stalemate
            
            return score;
            
        }
        
        for( depth = 2; depth <= maxDepth; depth++){
            
            Collections.sort( rootMoves );
            
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
            int widening = 4;
            
            if( score <= lowerBound ){ //fail low, nothing improved alpha (All-node)
                
                while( score <= lowerBound ){
                    
                    score = searchRoot( score - ( ASPIRATION * widening ), lowerBound + 1, depth );
                    lowerBound = score - ( ASPIRATION * widening );
                    widening *= widening;
                    
                }
                
            } else if( score >= upperBound ){ //fail high, (Cut-node) 
                
                while( score >= upperBound ){
                    
                    score = searchRoot( upperBound - 1, score + ( ASPIRATION * widening ), depth );
                    upperBound = score + ( ASPIRATION * widening );
                    widening *= widening;
                    
                }
                
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
    
    private int searchRoot( int alpha, int beta, int ply ) {

        int depthLeft = ply;
        Move currMove;
        int score = 0;
        
        int indexPVMove = rootMoves.indexOf( bestMove );
            
        while ( indexPVMove > 0 ) {
            
            depthLeft++;

            Collections.swap( rootMoves, indexPVMove, indexPVMove - 1 );
            indexPVMove--;

        }
                
        for ( int i = 0; i < rootMoves.size(); i++ ) {

            currMove = ( i == 0 ) && ( bestMove != null ) ? rootMoves.get( 0 ) : Board.sortHighestScoringMove( rootMoves.size(), rootMoves, i );

            board.makeMove( currMove );

            //find position of king of the side that just moved
            int kingPosition = ( board.sideToMove == WHITE ) ? board.blackKingPosition : board.whiteKingPosition;

            //if after making a move and the king of the side that made the move is NOT in check, then it's a legal move
            if ( !board.isAttacked( board.sideToMove, kingPosition ) ) {

                visitedNodes++;
                                
                if( ( ( i==0 ) && ( bestMove != null ) ) || ( -alphaBeta( -( alpha + 1 ), -alpha, depthLeft - 1, currMove, 1, false ) > alpha ) ){
                    
                    score = -alphaBeta( -beta, -alpha, depthLeft - 1, currMove, 1, true );
                    
                }
                
                board.unmakeMove( currMove );
                
                if ( !timeOver() ) {

                    return alpha;

                }
                
                currMove.score = score;
                
                if ( score > alpha ) {                    
                    
                    
                    if ( score >= beta ) {
                        
                        maxThinkingTime += maxThinkingTime / 100;
                        
                        return beta;

                    } 
                    
                    bestMove = currMove;
                    alpha = score;

                } 
                
                if ( ply > 5 ) {

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
    
    private int alphaBeta( int alpha, int beta, int depthLeft, Move prevBestMove, int ply, boolean isPV ){
        
        if( board.isAttacked( -board.sideToMove, ( ( board.sideToMove == WHITE ) ? board.whiteKingPosition : board.blackKingPosition ) ) ){
            
            depthLeft++;
            
        }
        
        if( depthLeft == 0 ){
            
            return quiescenceSearch( alpha, beta, prevBestMove, isPV );
            
        }
        
        List<Move> moves = board.generatePseudoMoves();   
        
        Move currMove;
        int score = 0;
        boolean noLegalMoves = true;
        boolean raisedAlpha = false;
        
        if ( prevBestMove.next != null ) {

            depthLeft++;
            
            int indexMove = moves.indexOf( prevBestMove.next );

            while ( indexMove > 0 ) {

                Collections.swap( moves, indexMove, indexMove - 1 );
                indexMove--;

            }

        } 
        
        for( int i = 0; i < moves.size(); i++ ){
            
            currMove = ( i == 0 ) && ( prevBestMove.next != null ) ? moves.get( 0 ) : Board.sortHighestScoringMove( moves.size(), moves, i );
                        
            board.makeMove( currMove );
            
            //find position of king of the side that just moved
            int kingPosition = ( board.sideToMove == WHITE ) ? board.blackKingPosition : board.whiteKingPosition;
            
            //if after making a move and the king of the side that made the move is NOT in check, then it's a legal move
            if ( !board.isAttacked( board.sideToMove, kingPosition ) ) {

                visitedNodes++;

                noLegalMoves = false;

                if ( !isPV || ( depthLeft < 3 ) ) {

                    score = -alphaBeta( -beta, -alpha, depthLeft - 1, currMove, ply + 1, false );

                } else {
                    
                    if( !raisedAlpha ){
                        
                        score = -alphaBeta( -beta, -alpha, depthLeft - 1, currMove, ply + 1, true );
                        
                    } else {
                        
                        if( -alphaBeta( -( alpha + 1 ), -alpha, depthLeft - 1, currMove, ply + 1, false ) > alpha ){
                            
                            score = -alphaBeta( -beta, -alpha, depthLeft - 1, currMove, ply + 1, true );
                            
                        }
                        
                    }
                    
                }
                
                board.unmakeMove( currMove ); 
                
                if ( !timeOver() ) {

                    return alpha;

                }                
                
                if ( score > alpha ) {
                                    
                    if ( score >= beta ) {
                        
                        currMove.next = null;
                        return beta;

                    }
                    
                    prevBestMove.next = currMove;
                    raisedAlpha = true;
                    alpha = score;

                } else {
                    
                    raisedAlpha = false;
                    currMove.next = null;
                    
                }
                
            } else {
                
                board.unmakeMove( currMove );
                moves.remove( i );
                i--;
                
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
    
    private int quiescenceSearch( int alpha, int beta, Move prevBestMove, boolean isPV ){
        
        visitedNodes++;

        List<Move> moves;

        int standPat = board.evaluate( 0 );

        if ( standPat > alpha ) {

            if ( standPat >= beta ) {

                return beta;

            }

            alpha = standPat;

        }

        moves = board.generateQuiescence();

        Move currMove;
        int score = 0;
        boolean raisedAlpha = false;

        if ( prevBestMove.next != null ) {
            
            int indexMove = moves.indexOf( prevBestMove.next );

            while ( indexMove > 0 ) {

                Collections.swap( moves, indexMove, indexMove - 1 );
                indexMove--;

            }

        } 
        
        for( int i = 0; i < moves.size(); i++ ){
            
            currMove = ( i == 0 ) && ( prevBestMove.next != null ) ? moves.get( 0 ) : Board.sortHighestScoringMove( moves.size(), moves, i );
                        
            board.makeMove( currMove );
            
            //find position of king of the side that just moved
            int kingPosition = ( board.sideToMove == WHITE ) ? board.blackKingPosition : board.whiteKingPosition;
            
            //if after making a move and the king of the side that made the move is NOT in check, then it's a legal move
            if ( !board.isAttacked( board.sideToMove, kingPosition ) ) {

                visitedNodes++;

                if ( !isPV ) {

                    score = -quiescenceSearch( -beta, -alpha, currMove, false );

                } else {
                    
                    if( !raisedAlpha ){
                        
                        score = -quiescenceSearch( -beta, -alpha, currMove, true );
                        
                    } else {
                        
                        if( -quiescenceSearch( -( alpha + 1 ), -alpha, currMove, false ) > alpha ){
                            
                            score = -quiescenceSearch( -beta, -alpha, currMove, true );
                            
                        }
                        
                    }
                    
                }
                
                board.unmakeMove( currMove ); 
                
                if ( !timeOver() ) {

                    return alpha;

                }                
                
                if ( score > alpha ) {
                                    
                    if ( score >= beta ) {
                        
                        currMove.next = null;
                        return beta;

                    }
                    
                    prevBestMove.next = currMove;
                    raisedAlpha = true;
                    alpha = score;

                } else {
                    
                    raisedAlpha = false;
                    currMove.next = null;
                    
                }
                
            } else {
                
                board.unmakeMove( currMove );
                moves.remove( i );
                i--;
                
            }

        }

        return alpha;
        
    }
    
    public void runPerft(){
        
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
    
    private int perft( int depth ){
        
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

//    private int quiescenceSearch( int alpha, int beta, Move prevBestMove ) {
//        
//        visitedNodes++;
//        
//        List<Move> moves;
//
//        int kingPosition = ( board.sideToMove == WHITE ) ? board.whiteKingPosition : board.blackKingPosition;
//        
//        int standPat = board.evaluate( 0 );
//                
////        if ( !board.isAttacked( -board.sideToMove, kingPosition ) ) {
//
//            if ( standPat > alpha ) {
//
//                if ( standPat >= beta ) {
//
//                    return beta;
//
//                }
//
//                alpha = standPat;
//
//            }
//
//            moves = board.generateQuiescence();
//
////        } else {
////            
////            if ( standPat > alpha ) {
////
////                alpha = standPat;
////
////            }
////
////            moves = board.generatePseudoMoves();
////
////        }
//      
//            
//        Move currMove;
//        
//        if ( prevBestMove.next != null ) {
//
//            int indexMove = moves.indexOf( prevBestMove.next );
//
//            while ( indexMove > 0 ) {
//
//                Collections.swap( moves, indexMove, indexMove - 1 );
//                indexMove--;
//
//            }
//
//        }
//
//        for ( int i = 0; i < moves.size(); i++ ) {
//            
//            currMove = ( ( i == 0 ) && ( prevBestMove.next != null ) ) ? moves.get( 0 ) : Board.sortHighestScoringMove( moves.size(), moves, i );
//
//            board.makeMove( moves.get( i ) );
//
//            //find position of king of the side that just moved
//            kingPosition = ( board.sideToMove == WHITE ) ? board.blackKingPosition : board.whiteKingPosition;
//
//            //if after making a move and the king of the side that made the move is NOT in check, then it's a legal move
//            if ( !board.isAttacked( board.sideToMove, kingPosition ) ) {
//
//                int score = -quiescenceSearch( -beta, -alpha, currMove );
//
//                board.unmakeMove( currMove );  
//               
//                if ( score > alpha ) {
//                    
//                    if ( score >= beta ) {
//
//                        currMove.next = null;
//                        return beta;
//
//                    }
//
//                    prevBestMove.next = currMove;
//                    alpha = score;
//
//                } else {
//                    
//                    currMove.next = null;
//                    
//                }
//
//            } else {
//                
//                board.unmakeMove( currMove );
//                moves.remove( i );
//                i--;
//                
//            }
//
//        }
//
//        return alpha;
//
//    }

    private void addKillerMove( int ply, Move currMove ) {
        
        if( killerMoves[ ply ] == null ){
            
            killerMoves[ ply ] = new TreeMap<>();
            
        }
               
                
        
        if( killerMoves[ ply ].containsKey( currMove ) ){
            
            int occurences = killerMoves[ ply ].remove( currMove );            
            occurences++;
            killerMoves[ ply ].put( currMove, occurences );
            
        } else {
            
            killerMoves[ ply ].put( currMove, 1 );
            
        }
        
    }
    
    public int getVisitedNodes() {
        
        return visitedNodes;
        
    }
    
    public Move getBestMove() {
        
        return bestMove;
        
    }

}
