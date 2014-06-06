/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package daan.representation;

import daan.utils.Utils;

import static daan.utils.Constants.*;

/**
 *
 * @author Rafiek Mohamedjoesoef <Rafiek.Mohamedjoesoef@hva.nl>
 * created 20-feb-2013, 14:18:05
 */
public class Move implements Comparable<Move>{
    
    public int pieceFrom;
    public int from;
    public int to;
    public int pieceTo;
    public int capture;
    public int type; 
    public int castleAvailability;
    public int halfMoveClock;
    public int enPassant;
    public int score;
    public Move next;

    public Move(int pieceFrom, int from, int to, int pieceTo, int type, int capture, int castleAvailability, int halfMoveClock, int enPassant) {
        
        this.pieceFrom          = pieceFrom;
        this.from               = from;
        this.to                 = to;
        this.pieceTo            = pieceTo;
        this.capture            = capture;
        this.type               = type;
        this.castleAvailability = castleAvailability;
        this.halfMoveClock      = halfMoveClock;
        this.enPassant          = enPassant;        
        this.score              = 0;
        
    }

    void copy(Move move){
        
        this.pieceFrom          = move.pieceFrom;
        this.from               = move.from;
        this.to                 = move.to;
        this.pieceTo            = move.pieceTo;
        this.capture            = move.capture;
        this.type               = move.type;
        this.castleAvailability = move.castleAvailability;
        this.halfMoveClock      = move.halfMoveClock;
        this.enPassant          = move.enPassant;        
        this.score              = move.score;
        this.next               = move.next;
        
    }
    
    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        
        sb.append( Utils.getKeyByValue( SQUARE_INDEX_MAPPINGS, from ) ); 
        sb.append( Utils.getKeyByValue( SQUARE_INDEX_MAPPINGS, to ) );
        
        if ( ( this.type & MOVE_TYPE_PROMOTION ) == MOVE_TYPE_PROMOTION ) {

            switch ( Math.abs( pieceTo ) ) {

                case W_QUEEN:
                    sb.append( "q" );
                    break;
                case W_ROOK:
                    sb.append( "r" );
                    break;
                case W_BISHOP:
                    sb.append( "b" );
                    break;
                case W_KNIGHT:
                    sb.append( "n" );
                    break;

            }

        }
        return sb.toString()/*+" "+this.score*/;
    }
    
    @Override
    public boolean equals( Object obj ){
        
        if( obj == this ){
            return true;
        }
        
        if( obj == null || obj.getClass() != this.getClass() ){
            return false;
        }
        
        Move move = ( Move ) obj;
        
        return
                this.from               == move.from                &&
                this.to                 == move.to                  &&
                this.pieceTo            == move.pieceTo;
        
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + this.from;
        hash = 79 * hash + this.to;
        hash = 79 * hash + this.pieceTo;
        return hash;
    }
    
    public String toShortAlgebraicNotation(){
        StringBuilder sb = new StringBuilder();
        
        if( Math.abs( pieceFrom ) == W_PAWN ){
            
            if( ( type & MOVE_TYPE_CAPTURE ) == MOVE_TYPE_CAPTURE ){
                
                sb.append( 'a' + Board.getFile( from ) ) ;
                sb.append( 'x' );
                sb.append( Utils.getKeyByValue( SQUARE_INDEX_MAPPINGS, to ) );
                
            } else {
                
                sb.append( Utils.getKeyByValue( SQUARE_INDEX_MAPPINGS, to ) );
                
            }
            
            if( ( type & MOVE_TYPE_PROMOTION ) == MOVE_TYPE_PROMOTION ){
                
                sb.append( Utils.getKeyByValue( PIECE_CHARACTER_MAPPINGS, Math.abs( pieceTo ) ) );
            
            }
            
        } else {
            
            char piece = 0;
            if( pieceFrom == B_PAWN ){
                piece = (Character)Utils.getKeyByValue( PIECE_CHARACTER_MAPPINGS, pieceFrom );
            } else {
                piece = (Character)Utils.getKeyByValue( PIECE_CHARACTER_MAPPINGS, Math.abs( pieceFrom ) );
            }
            
            sb.append( piece );
            
            if( ( type & MOVE_TYPE_CAPTURE ) == MOVE_TYPE_CAPTURE ){
                
                sb.append( 'x' );
                
            }
            
            sb.append( Utils.getKeyByValue( SQUARE_INDEX_MAPPINGS, to ) );
            
        }
        
        return sb.toString();
    }
    
    public int calculateMVVLVAScore(){
        
        int result = Board.getPieceValue( pieceTo ) / 100 ;
        
        if( capture != EMPTY_SQUARE ){
            
            result += Board.getPieceValue( capture ) ;
            result += 100 - ( Board.getPieceValue( pieceFrom ) / 10 );
            
        }
        
        if( ( type & MOVE_TYPE_PROMOTION ) == MOVE_TYPE_PROMOTION ){
            
            result += Board.getPieceValue( pieceTo );
            
        }
        
        return result;
        
    }
    
    public String getLine() {
        
        return this.toString() + ( ( this.next == null ) ? " " : " " + this.next.getLine() ) ;
        
    }

    @Override
    public int compareTo( Move other ) {

//        if ( other.score == this.score ) {
//            
//            return other.calculateMVVLVAScore() - this.calculateMVVLVAScore();
//            
//        } else {
            
            return other.score - this.score;
            
//        }

    }

}
