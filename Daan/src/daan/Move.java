/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package daan;

import static daan.Constants.SQUARE_INDEX_MAPPINGS;

/**
 *
 * @author Rafiek Mohamedjoesoef <Rafiek.Mohamedjoesoef@hva.nl>
 * created 20-feb-2013, 14:18:05
 */
public class Move implements Constants{
    
    public int pieceFrom;
    public int from;
    public int to;
    public int pieceTo;
    public int capture;
    public int type; //OR-ing of MoveTypes    
    public int castleAvailability;
    public int halfMoveClock;
    public int enPassant;

    public Move(int pieceFrom, int from, int to, int pieceTo, int capture, int type, int castleAvailability, int halfMoveClock, int enPassant) {
        this.pieceFrom          = pieceFrom;
        this.from               = from;
        this.to                 = to;
        this.pieceTo            = pieceTo;
        this.capture            = capture;
        this.type               = type;
        this.castleAvailability = castleAvailability;
        this.halfMoveClock      = halfMoveClock;
        this.enPassant          = enPassant;
    }
    
    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        
        sb.append( Board.getKeyByValue( SQUARE_INDEX_MAPPINGS, from ) ); 
        sb.append( Board.getKeyByValue( SQUARE_INDEX_MAPPINGS, to ) );
        
        if ( ( this.type & MOVE_TYPE_PROMOTION ) == MOVE_TYPE_PROMOTION ) {

            sb.append( "=" );

            switch ( Math.abs( pieceTo ) ) {

                case W_QUEEN:
                    sb.append( "Q" );
                    break;
                case W_ROOK:
                    sb.append( "R" );
                    break;
                case W_BISHOP:
                    sb.append( "B" );
                    break;
                case W_KNIGHT:
                    sb.append( "N" );
                    break;

            }

        }
        return sb.toString();
    }

}
