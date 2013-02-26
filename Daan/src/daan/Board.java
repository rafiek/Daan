/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package daan;

import static daan.Constants.NN;
import static daan.Constants.NORTH;
import static daan.Constants.W_PAWN;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 *
 * @author Rafiek Mohamedjoesoef <Rafiek.Mohamedjoesoef@hva.nl> created
 * 14-feb-2013, 10:22:53
 */
public class Board implements Constants {

    //current position in 0x88 format, 0x00=A1, 0x07=H1
    private int[] position;
    
    //keeps a list of the location of the pieces
    private HashMap<Integer, Integer> locationOfPieces;
    
    //whose turn is it to move
    private int sideToMove;
    
    //castling availability 0001, 0010, 0100, 1000, KQkq
    private short castlingAvailability;
    
    //targetsquare, -1 if no targetsquare
    private int enPassant;
    
    //number of halfmoves after last pawn move or a capture
    private int halfMoveClock;
    
    //incremented after black move, starts at 1
    private int fullMoveNumber;
    
    private List<Move> history;
    
    private List<Move> moves;

    public Board() {
        initPosition(FEN_START_POSITION);
    }
    
    public Board(String FEN){
        initPosition(FEN);
    }
    
    private void initPosition(String fen) {
        position = new int[128];
        locationOfPieces = new HashMap<>();
        history = new ArrayList<>();
        sideToMove = WHITE_TO_MOVE;
        castlingAvailability = 0;
        enPassant = -1;
        halfMoveClock = 0;
        fullMoveNumber = 1;
        String[] fenArray = fen.split("\\s");

        for (int i = 0; i < fenArray.length; i++) {
            switch (i) {
                case 0://position

                    int currentPositionIndex = FIELD_INDEX_MAPPINGS.get("a8");
                    String positionInFEN = fenArray[i];

                    for (int j=0; j < positionInFEN.length(); j++) {

                        if (Character.isLetter(positionInFEN.charAt(j))) {//piece

                                position[currentPositionIndex] = PIECE_CHARACTER_MAPPINGS.get(positionInFEN.charAt(j));
                                locationOfPieces.put(currentPositionIndex, position[currentPositionIndex]);
                                currentPositionIndex++;
                                
                        } else if (Character.isDigit(positionInFEN.charAt(j))) {//number of empty fields

                            currentPositionIndex += Character.getNumericValue(positionInFEN.charAt(j));

                        } else if (positionInFEN.charAt(j) == '/') {//go to next row

                            int rowDown = currentPositionIndex - 16;
                            currentPositionIndex = rowDown - (rowDown % 16);

                        }

                    }

                    break;

                case 1://sideToMove
                    
                    sideToMove = (fenArray[i].equals("w")) ? WHITE_TO_MOVE : BLACK_TO_MOVE;
                    break;
                    
                case 2://castlingAvailability
                    String castling = fenArray[i];
                    
                    for (int j=0; j<castling.length(); j++) {
                        switch (castling.charAt(j)) {
                            case 'K':
                                castlingAvailability |= CAW_KING_SIDE;
                                break;
                            case 'Q':
                                castlingAvailability |= CAW_QUEEN_SIDE;
                                break;
                            case 'k':
                                castlingAvailability |= CAB_KING_SIDE;                                
                                break;
                            case 'q':
                                castlingAvailability |= CAB_QUEEN_SIDE;
                                break;
                            default:
                                castlingAvailability = 0;
                        }
                    }
                    
                    break;
                    
                case 3://enPassant targetSquare

                    enPassant = !(fenArray[i].equals("-")) ? FIELD_INDEX_MAPPINGS.get(fenArray[i]) : -1;
                    
                    break;
                    
                case 4://halfMoveClock
                    
                    halfMoveClock = Integer.parseInt(fenArray[i]);
                    
                    break;
                case 5://fullMoveNumber
                    
                    fullMoveNumber = Integer.parseInt(fenArray[i]);
                    
                    break;
                    
                default://FEN-string has wrong syntax
                    break;
            }
        }

    }

    @Override
    public String toString() {
        StringBuilder fen = new StringBuilder();
        int emptyFields = 0;
        for(int i=8; i>0; i--){
            for(char c='a'; c<'i'; c++){
                
                int field = position[ FIELD_INDEX_MAPPINGS.get( Character.toString(c)+i ) ];
                
                if(field != 0){
                    
                    if (emptyFields != 0) {
                        fen.append(emptyFields);
                        emptyFields = 0;
                    }
                    
                    switch(field){
                        case B_ROOK     : fen.append('r'); break;
                        case B_KNIGHT   : fen.append('n'); break;
                        case B_BISHOP   : fen.append('b'); break;
                        case B_QUEEN    : fen.append('q'); break;
                        case B_KING     : fen.append('k'); break;
                        case B_PAWN     : fen.append('p'); break;
                        case W_PAWN     : fen.append('P'); break;
                        case W_ROOK     : fen.append('R'); break;
                        case W_KNIGHT   : fen.append('N'); break;
                        case W_BISHOP   : fen.append('B'); break;
                        case W_QUEEN    : fen.append('Q'); break;
                        case W_KING     : fen.append('K'); break;  
                        default         : break;    
                    }
                }else if(field == 0){
                    emptyFields++;
                }
            }
            
            if(emptyFields != 0){
                fen.append(emptyFields);
                emptyFields = 0;
            }
                
            if( i>1 ){
                fen.append('/');
            }
            
        }

        fen.append(' ');
        
        fen.append( ( sideToMove == WHITE_TO_MOVE ) ? 'w' : 'b' );
        
        fen.append(' ');
        
        if ((castlingAvailability & CAW_KING_SIDE) > 0) {
            fen.append('K');
        }
        if ((castlingAvailability & CAW_QUEEN_SIDE) > 0) {
            fen.append('Q');
        }
        if ((castlingAvailability & CAB_KING_SIDE) > 0) {
            fen.append('k');
        }
        if ((castlingAvailability & CAB_QUEEN_SIDE) > 0) {
            fen.append('q');
        }
        if ( castlingAvailability == 0 ) {
            fen.append('-');
        }
        
        fen.append(' ');
        
        fen.append( 
                enPassant < 0 ? '-' : ((String)getKeyByValue(FIELD_INDEX_MAPPINGS, enPassant))
                );
        
        return fen.toString();
    }
    
    public static <T, E> T getKeyByValue(Map<T, E> map, E value) {
        for (Entry<T, E> entry : map.entrySet()) {
            if (value.equals(entry.getValue())) {
                return entry.getKey();
            }
        }
        return null;
    }

    public List generateMoves(){
        
        moves = new ArrayList<>();
        
        for( Map.Entry<Integer,Integer> entry : locationOfPieces.entrySet() ){
            
            switch(Math.abs(entry.getValue())){//temporarily absolute value just to check type of piece  
                
                case W_PAWN     : generatePawnMoves( entry.getKey() );
                    break;
                case W_KNIGHT   : //System.out.println(entry);
                    break;
                case W_BISHOP   : //System.out.println(entry);
                    break;    
                case W_KING     : //System.out.println(entry);
                    break;
                case W_QUEEN    : //System.out.println(entry);
                    break;
                case W_ROOK     : //System.out.println(entry);
                    break;
                    
            }
            
        }
        
        return moves;
    }
    
    private void generatePawnMoves(int field) {
        
        if(sideToMove == WHITE_TO_MOVE){
            
            if( position[ field ] > 0 ){//Look at white pawns only
                
                //pawn moves N one
                if( position[ field + NORTH ] == 0 ){
                    
                    moves.add( 
                            new Move( W_PAWN, field, field + NORTH, W_PAWN, MoveTypes.NORMAL, castlingAvailability, halfMoveClock, enPassant )
                            );
                    
                    //pawn moves N two if 2nd rank 
                    if( position[ field + NN ] == 0 && getRow( field ) == 1 ){
                        
                        moves.add( 
                            new Move( W_PAWN, field, field + NN, W_PAWN, MoveTypes.NORMAL, castlingAvailability, halfMoveClock, enPassant )
                                );
                                                
                    }
                    
                } 
                                
                //pawn takes NW or NE one 
                
                //if enpassant is available, then pawn can also take pawn by moving to enpassant field
                
                //pawn promotes to queen/rook/bishop/knight if eigth rank reached
            }
            
        }else if(sideToMove == BLACK_TO_MOVE){
            //System.out.println( position[ field ] );
            //pawn moves S two if 7th rank 
            //pawn moves S one  
            //pawn takes SW or SE one 
            //if enpassant is available, then pawn can also take pawn by moving to enpassant field
            //pawn promotes to queen/rook/bishop/knight if 1st rank reached
        }
        
        
    }
    
    private int getRow( int field ){
        return ( field - ( field % 16 ) ) / 16;
    }
}
