/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package daan;

/**
 *
 * @author Rafiek Mohamedjoesoef <Rafiek.Mohamedjoesoef@hva.nl>
 * created 20-feb-2013, 14:18:05
 */
class Move {
    
    public int pieceFrom;
    public int from;
    public int to;
    public int pieceTo;
    public int type; //OR-ing of MoveTypes    
    public int castleAvailability;
    public int halfMoveClock;
    public int enPassant;
    public int score;

}
