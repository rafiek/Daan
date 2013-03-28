/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package daan.representation;

import static daan.utils.Constants.*;
import daan.utils.Utils;
import javax.rmi.CORBA.Util;

/**
 *
 * @author Rafiek Mohamedjoesoef <Rafiek.Mohamedjoesoef@hva.nl>
 * created 20-feb-2013, 14:18:05
 */
public class Move {
    
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

    public Move(){
        
        this.pieceFrom          = 0;
        this.from               = 0;
        this.to                 = 0;
        this.pieceTo            = 0;
        this.capture            = 0;
        this.type               = 0;
        this.castleAvailability = 0;
        this.halfMoveClock      = 0;
        this.enPassant          = 0;        
        this.score              = 0;
        
    }
    
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
        this.score              = calculateMVVLVAScore();
        
    }
    
    public Move( Move move ){
        
        this.copy( move );
        
    }
    
    public void copy( Move move ){
        
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
        return sb.toString();
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
            
            char piece = (Character)Utils.getKeyByValue( PIECE_CHARACTER_MAPPINGS, Math.abs( pieceFrom ) );
            
            sb.append( piece );
            
            if( ( type & MOVE_TYPE_CAPTURE ) == MOVE_TYPE_CAPTURE ){
                
                sb.append( 'x' );
                
            }
            
            sb.append( Utils.getKeyByValue( SQUARE_INDEX_MAPPINGS, to ) );
            
        }
        
        return sb.toString();
    }
    
    private int calculateMVVLVAScore(){
        
        int result = ( capture != EMPTY_SQUARE ) ? PIECE_VALUE_MAPPINGS.get( Math.abs( capture ) ) : 0;
        
        if( result > 0 ){
            
            result += 100 - ( PIECE_VALUE_MAPPINGS.get( Math.abs( pieceFrom ) ) / 10 );
            
        }
        
        return result;
        
    }
    
    public String getLine() {
        
        Move tmp = this;
        StringBuilder sb = new StringBuilder();
        
        do{
            
            sb.append( tmp );           
            sb.append( " " );
            tmp = tmp.next;
            
        } while( tmp.pieceFrom != 0 );
            
        
        return sb.toString();
        
    }

}
