/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package daan;

import static daan.Constants.FIELD_INDEX_MAPPINGS;
import daan.Constants.MoveTypes;

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
    public MoveTypes type; //OR-ing of MoveTypes    
    public int castleAvailability;
    public int halfMoveClock;
    public int enPassant;

    public Move(int pieceFrom, int from, int to, int pieceTo, MoveTypes type, int castleAvailability, int halfMoveClock, int enPassant) {
        this.pieceFrom = pieceFrom;
        this.from = from;
        this.to = to;
        this.pieceTo = pieceTo;
        this.type = type;
        this.castleAvailability = castleAvailability;
        this.halfMoveClock = halfMoveClock;
        this.enPassant = enPassant;
    }
    
    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        
        sb.append( Board.getKeyByValue( FIELD_INDEX_MAPPINGS, from ) ); 
        sb.append( Board.getKeyByValue( FIELD_INDEX_MAPPINGS, to ) );
        return sb.toString();
    }

}
