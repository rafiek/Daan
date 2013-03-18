/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package representation;

import static daan.utils.Constants.*;

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
        this.score              = ( capture != EMPTY_SQUARE ) ? PIECE_VALUE_MAPPINGS.get( Math.abs( capture ) ) - PIECE_VALUE_MAPPINGS.get( Math.abs( pieceFrom ) ) : 0;
    }
    
    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        
        sb.append( Board.getKeyByValue( SQUARE_INDEX_MAPPINGS, from ) ); 
        sb.append( Board.getKeyByValue( SQUARE_INDEX_MAPPINGS, to ) );
        
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

}
