/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package daan;

import static daan.Constants.B_BISHOP;
import static daan.Constants.B_KING;
import static daan.Constants.B_KNIGHT;
import static daan.Constants.B_PAWN;
import static daan.Constants.B_QUEEN;
import static daan.Constants.B_ROOK;
import static daan.Constants.CAW_KING_SIDE;
import static daan.Constants.EMPTY_FIELD;
import static daan.Constants.INDEX_BISHOP_DIRECTION;
import static daan.Constants.INDEX_KING_DIRECTION;
import static daan.Constants.INDEX_QUEEN_DIRECTION;
import static daan.Constants.MOVE_TYPE_CAPTURE;
import static daan.Constants.MOVE_TYPE_CASTLE;
import static daan.Constants.MOVE_TYPE_NORMAL;
import static daan.Constants.MOVE_TYPE_PROMOTION;
import static daan.Constants.NE;
import static daan.Constants.NN;
import static daan.Constants.NORTH;
import static daan.Constants.NW;
import static daan.Constants.PIECE_VECTORS;
import static daan.Constants.SE;
import static daan.Constants.SOUTH;
import static daan.Constants.SS;
import static daan.Constants.SW;
import static daan.Constants.WHITE_TO_MOVE;
import static daan.Constants.W_BISHOP;
import static daan.Constants.W_KING;
import static daan.Constants.W_KNIGHT;
import static daan.Constants.W_PAWN;
import static daan.Constants.W_QUEEN;
import static daan.Constants.W_ROOK;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 *
 * @author Rafiek Mohamedjoesoef <Rafiek.Mohamedjoesoef@hva.nl> created
 * 14-feb-2013, 10:22:53
 */
public class Board implements Constants {

    //current position in 0x88 format, 0x00=A1, 0x07=H1
    private int[] position;
    
    //keeps a list of the location of the white pieces
    private HashMap<Integer, Integer> locationOfWhitePieces;
    
    //keeps a list of the location of the black pieces
    private HashMap<Integer, Integer> locationOfBlackPieces;
    
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
        locationOfWhitePieces = new HashMap<>();
        locationOfBlackPieces = new HashMap<>();                
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
                                if(Character.isUpperCase( positionInFEN.charAt(j))){
                                    locationOfWhitePieces.put(currentPositionIndex, position[currentPositionIndex]);
                                }else{
                                    locationOfBlackPieces.put(currentPositionIndex, position[currentPositionIndex]);
                                }
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

    public List generateMoves() {

        moves = new ArrayList<>();

        Set<Map.Entry<Integer, Integer>> locationOfPieces = ( sideToMove == WHITE_TO_MOVE ) ? locationOfWhitePieces.entrySet() : locationOfBlackPieces.entrySet();

        for ( Map.Entry<Integer, Integer> entry : locationOfPieces ) {

            switch ( Math.abs( entry.getValue() ) ) {//temporarily absolute value just to check type of piece  

                case W_PAWN:
                    generatePseudoPawnMoves( entry.getKey() );
                    break;
                case W_KNIGHT:
                    generatePseudoKnightMoves( entry.getKey() );
                    break;
                case W_BISHOP: 
                    generatePseudoBishopMoves( entry.getKey() );
                    break;
                case W_KING:
                    generatePseudoKingMoves( entry.getKey() );
                    break;
                case W_QUEEN: 
                    generatePseudoQueenMoves( entry.getKey() );
                    break;
                case W_ROOK: 
                    generatePseudoRookMoves( entry.getKey() );
                    break;

            }

        }

        return moves;

    }
    
    private void generatePseudoPawnMoves(int field) {
        
        if(sideToMove == WHITE_TO_MOVE){
            
                
                //pawn moves N one
                if( fieldIsEmpty( field + NORTH ) ){
                    
                    if( getRank( field ) == 6 ){ //pawn promotes to queen/rook/bishop/knight if eigth rank reached     
                        
                        moves.add( 
                            new Move( W_PAWN, field, field + NORTH, W_QUEEN, MOVE_TYPE_PROMOTION, castlingAvailability, halfMoveClock, enPassant )
                            );
                        
                        moves.add( 
                            new Move( W_PAWN, field, field + NORTH, W_ROOK, MOVE_TYPE_PROMOTION, castlingAvailability, halfMoveClock, enPassant )
                            );
                        
                        moves.add( 
                            new Move( W_PAWN, field, field + NORTH, W_BISHOP, MOVE_TYPE_PROMOTION, castlingAvailability, halfMoveClock, enPassant )
                            );
                        
                        moves.add( 
                            new Move( W_PAWN, field, field + NORTH, W_KNIGHT, MOVE_TYPE_PROMOTION, castlingAvailability, halfMoveClock, enPassant )
                            );
                        
                    } else {

                        moves.add(
                                new Move(W_PAWN, field, field + NORTH, W_PAWN, MOVE_TYPE_NORMAL, castlingAvailability, halfMoveClock, enPassant));

                    }

                    if (getRank(field) == 1) { //pawn moves N two if 2nd rank 
                        
                        if (fieldIsEmpty(field + NN)) {
                            
                            moves.add(
                                    new Move(W_PAWN, field, field + NN, W_PAWN, MOVE_TYPE_NORMAL, castlingAvailability, halfMoveClock, enPassant));
                        
                        }

                    }
                    
                }
                                
                //pawn takes NW or NE one                
                if ( isBlackPiece( position[ field + NW ] ) ) {
                    
                    if( getRank( field ) == 6 ){
                        
                         moves.add( 
                            new Move( W_PAWN, field, field + NW, W_QUEEN, (MOVE_TYPE_PROMOTION | MOVE_TYPE_CAPTURE), castlingAvailability, halfMoveClock, enPassant )
                            );
                        
                        moves.add( 
                            new Move( W_PAWN, field, field + NW, W_ROOK, (MOVE_TYPE_PROMOTION | MOVE_TYPE_CAPTURE), castlingAvailability, halfMoveClock, enPassant )
                            );
                        
                        moves.add( 
                            new Move( W_PAWN, field, field + NW, W_BISHOP, (MOVE_TYPE_PROMOTION | MOVE_TYPE_CAPTURE), castlingAvailability, halfMoveClock, enPassant )
                            );
                        
                        moves.add( 
                            new Move( W_PAWN, field, field + NW, W_KNIGHT, (MOVE_TYPE_PROMOTION | MOVE_TYPE_CAPTURE), castlingAvailability, halfMoveClock, enPassant )
                            );
                        
                    }else{
                        
                        moves.add(
                            new Move( W_PAWN, field, field + NW, W_PAWN, MOVE_TYPE_CAPTURE, castlingAvailability, halfMoveClock, enPassant )
                            );
                        
                    }

                }
                
                if ( isBlackPiece( position[ field + NE ] ) ) {
                    
                    if( getRank( field ) == 6 ){
                        
                         moves.add( 
                            new Move( W_PAWN, field, field + NE, W_QUEEN, (MOVE_TYPE_PROMOTION | MOVE_TYPE_CAPTURE), castlingAvailability, halfMoveClock, enPassant )
                            );
                        
                        moves.add( 
                            new Move( W_PAWN, field, field + NE, W_ROOK, (MOVE_TYPE_PROMOTION | MOVE_TYPE_CAPTURE), castlingAvailability, halfMoveClock, enPassant )
                            );
                        
                        moves.add( 
                            new Move( W_PAWN, field, field + NE, W_BISHOP, (MOVE_TYPE_PROMOTION | MOVE_TYPE_CAPTURE), castlingAvailability, halfMoveClock, enPassant )
                            );
                        
                        moves.add( 
                            new Move( W_PAWN, field, field + NE, W_KNIGHT, (MOVE_TYPE_PROMOTION | MOVE_TYPE_CAPTURE), castlingAvailability, halfMoveClock, enPassant )
                            );
                        
                    }else{
                        
                        moves.add(
                            new Move( W_PAWN, field, field + NE, W_PAWN, MOVE_TYPE_CAPTURE, castlingAvailability, halfMoveClock, enPassant )
                            );
                        
                    }

                }
                
                //if enpassant is available, then pawn can also take pawn by moving to enpassant field
                if( field + NW == enPassant ){
                    
                    moves.add(
                            new Move( W_PAWN, field, field + NW, W_PAWN, MOVE_TYPE_EP, castlingAvailability, halfMoveClock, enPassant )
                            );
                    
                }
                
                 if( field + NE == enPassant ){
                    
                    moves.add(
                            new Move( W_PAWN, field, field + NE, W_PAWN, MOVE_TYPE_EP, castlingAvailability, halfMoveClock, enPassant )
                            );
                    
                }
                 
            
        }else if(sideToMove == BLACK_TO_MOVE){
                            
                //pawn moves S one
                if( fieldIsEmpty( field + SOUTH ) ){
                    
                    if( getRank( field ) == 1 ){ //pawn promotes to queen/rook/bishop/knight if first rank reached     
                        
                        moves.add( 
                            new Move( B_PAWN, field, field + SOUTH, B_QUEEN, MOVE_TYPE_PROMOTION, castlingAvailability, halfMoveClock, enPassant )
                            );
                        
                        moves.add( 
                            new Move( B_PAWN, field, field + SOUTH, B_ROOK, MOVE_TYPE_PROMOTION, castlingAvailability, halfMoveClock, enPassant )
                            );
                        
                        moves.add( 
                            new Move( B_PAWN, field, field + SOUTH, B_BISHOP, MOVE_TYPE_PROMOTION, castlingAvailability, halfMoveClock, enPassant )
                            );
                        
                        moves.add( 
                            new Move( B_PAWN, field, field + SOUTH, B_KNIGHT, MOVE_TYPE_PROMOTION, castlingAvailability, halfMoveClock, enPassant )
                            );
                    
                    }else{
                        
                         moves.add( 
                            new Move( B_PAWN, field, field + SOUTH, B_PAWN, MOVE_TYPE_NORMAL, castlingAvailability, halfMoveClock, enPassant )
                            );
                         
                    }
                    
                    if ( getRank( field ) == 6 ) { //pawn moves S two if 7th rank 

                        if ( fieldIsEmpty( field + SS ) ) {

                            moves.add(
                                    new Move( B_PAWN, field, field + SS, B_PAWN, MOVE_TYPE_NORMAL, castlingAvailability, halfMoveClock, enPassant ) );

                        }

                    }
                    
                } 
                                
                //pawn takes SW or SE one                
                if ( isWhitePiece( position[ field + SW ] ) ) {

                    if( getRank( field ) == 1 ){ //pawn promotes to queen/rook/bishop/knight if first rank reached     
                        
                        moves.add( 
                            new Move( B_PAWN, field, field + SW, B_QUEEN, ( MOVE_TYPE_PROMOTION | MOVE_TYPE_CAPTURE ), castlingAvailability, halfMoveClock, enPassant )
                            );
                        
                        moves.add( 
                            new Move( B_PAWN, field, field + SW, B_ROOK, ( MOVE_TYPE_PROMOTION | MOVE_TYPE_CAPTURE ), castlingAvailability, halfMoveClock, enPassant )
                            );
                        
                        moves.add( 
                            new Move( B_PAWN, field, field + SW, B_BISHOP, ( MOVE_TYPE_PROMOTION | MOVE_TYPE_CAPTURE ), castlingAvailability, halfMoveClock, enPassant )
                            );
                        
                        moves.add( 
                            new Move( B_PAWN, field, field + SW, B_KNIGHT, ( MOVE_TYPE_PROMOTION | MOVE_TYPE_CAPTURE ), castlingAvailability, halfMoveClock, enPassant )
                            );
                    
                    }else{
                        
                         moves.add( 
                            new Move( B_PAWN, field, field + SW, B_PAWN, MOVE_TYPE_CAPTURE, castlingAvailability, halfMoveClock, enPassant )
                            );
                         
                    }

                }
                
                if ( isWhitePiece( position[ field + SE ] ) ) {
                    
                    if( getRank( field ) == 1 ){ //pawn promotes to queen/rook/bishop/knight if first rank reached     
                        
                        moves.add( 
                            new Move( B_PAWN, field, field + SE, B_QUEEN, ( MOVE_TYPE_PROMOTION | MOVE_TYPE_CAPTURE ), castlingAvailability, halfMoveClock, enPassant )
                            );
                        
                        moves.add( 
                            new Move( B_PAWN, field, field + SE, B_ROOK, ( MOVE_TYPE_PROMOTION | MOVE_TYPE_CAPTURE ), castlingAvailability, halfMoveClock, enPassant )
                            );
                        
                        moves.add( 
                            new Move( B_PAWN, field, field + SE, B_BISHOP, ( MOVE_TYPE_PROMOTION | MOVE_TYPE_CAPTURE ), castlingAvailability, halfMoveClock, enPassant )
                            );
                        
                        moves.add( 
                            new Move( B_PAWN, field, field + SE, B_KNIGHT, ( MOVE_TYPE_PROMOTION | MOVE_TYPE_CAPTURE ), castlingAvailability, halfMoveClock, enPassant )
                            );
                    
                    }else{
                        
                         moves.add( 
                            new Move( B_PAWN, field, field + SE, B_PAWN, MOVE_TYPE_CAPTURE, castlingAvailability, halfMoveClock, enPassant )
                            );
                         
                    }
                    
                }
                
                //if enpassant is available, then pawn can also take neighbour pawn by moving to enpassant field behind neighbour pawn
                if( field + SW == enPassant ){
                    
                    moves.add(
                            new Move( B_PAWN, field, field + SW, B_PAWN, MOVE_TYPE_EP, castlingAvailability, halfMoveClock, enPassant )
                            );
                    
                }
                
                 if( field + SE == enPassant ){
                    
                    moves.add(
                            new Move( B_PAWN, field, field + SE, B_PAWN, MOVE_TYPE_EP, castlingAvailability, halfMoveClock, enPassant )
                            );
                    
                }
                 
            }
        
        
    }
    
    private void generatePseudoKnightMoves( int field ) {

        int knightMove;
        int piece = isWhitePiece( position[ field ] ) ? W_KNIGHT : B_KNIGHT;

        for ( int direction = 0; direction < 8; direction++ ) {

            knightMove = field + PIECE_VECTORS[ INDEX_KNIGHT_DIRECTION ][ direction ];

            if ( !offTheBoard( knightMove ) ) {

                if ( position[ knightMove ] == EMPTY_FIELD ) {

                    moves.add(
                            new Move( piece, field, knightMove, piece, MOVE_TYPE_NORMAL, castlingAvailability, halfMoveClock, enPassant ) );

                } else if ( position[ field ] * position[ knightMove ] < 0 ) {

                    moves.add(
                            new Move( piece, field, knightMove, piece, MOVE_TYPE_CAPTURE, castlingAvailability, halfMoveClock, enPassant ) );

                }

            }

        }

    }
    
    private void generatePseudoKingMoves( int field ) {
        /*
         * castling is impossible if
         * - king in check
         * - king will be in check
         * - king moves across attacked square 
         */
        int kingMove;
        int piece = isWhitePiece( position[ field ] ) ? W_KING : B_KING ;
        
        for( int direction = 0; direction < 8; direction++ ){
            
            kingMove = field + PIECE_VECTORS[ INDEX_KING_DIRECTION ][ direction ];
            
            if( !offTheBoard( kingMove ) ) {
                
                 if( position[ kingMove ] == EMPTY_FIELD ) {
                     
                     moves.add( 
                             new Move( piece, field, kingMove, piece, MOVE_TYPE_NORMAL, castlingAvailability, halfMoveClock, enPassant ) );
                     
                 } else if ( position[ field ] * position[ kingMove ] < 0 ) {
                     
                     moves.add( 
                             new Move( piece, field, kingMove, piece, MOVE_TYPE_CAPTURE, castlingAvailability, halfMoveClock, enPassant ) );
                     
                 }
            }
            
        }
        
        if ( ( castlingAvailability & CAW_KING_SIDE ) == CAW_KING_SIDE ) {
            
            if( position[ field + 1 ] == EMPTY_FIELD && position[ field + 2 ] == EMPTY_FIELD ){
                
                moves.add(
                        new Move( piece, field, field + 2, piece, MOVE_TYPE_CASTLE, castlingAvailability, halfMoveClock, enPassant ) );                        
                
            }
            
        } 
        
        if ( ( castlingAvailability & CAW_QUEEN_SIDE ) == CAW_QUEEN_SIDE ) {
            
            if( position[ field - 1 ] == EMPTY_FIELD && position[ field - 2 ] == EMPTY_FIELD && position[ field - 3 ] == EMPTY_FIELD ){
                
                moves.add(
                        new Move( piece, field, field - 2, piece, MOVE_TYPE_CASTLE, castlingAvailability, halfMoveClock, enPassant ) );                        
                
            }            
            
        } 
        
        if ( ( castlingAvailability & CAB_KING_SIDE ) == CAB_KING_SIDE ) {
            
            if( position[ field + 1 ] == EMPTY_FIELD && position[ field + 2 ] == EMPTY_FIELD ){
                
                moves.add(
                        new Move( piece, field, field + 2, piece, MOVE_TYPE_CASTLE, castlingAvailability, halfMoveClock, enPassant ) );                        
                
            }
            
        } 
        
        if ( ( castlingAvailability & CAB_QUEEN_SIDE ) == CAB_QUEEN_SIDE ) {
            
            if( position[ field - 1 ] == EMPTY_FIELD && position[ field - 2 ] == EMPTY_FIELD && position[ field - 3 ] == EMPTY_FIELD ){
                
                moves.add(
                        new Move( piece, field, field - 2, piece, MOVE_TYPE_CASTLE, castlingAvailability, halfMoveClock, enPassant ) );                        
                
            }  
            
        }
        
    }
    
    private void generatePseudoBishopMoves( int field ){
        int bishopMove;
        int piece = isWhitePiece( position[ field ] ) ? W_BISHOP : B_BISHOP ;
        
        for( int direction = 0; direction < PIECE_VECTORS[ INDEX_BISHOP_DIRECTION ].length; direction++ ){
            
            int step = 1;
            bishopMove = field + PIECE_VECTORS[ INDEX_BISHOP_DIRECTION ][ direction ] * step;
            
            while( !offTheBoard( bishopMove ) ) {
                
                 if( position[ bishopMove ] == EMPTY_FIELD ) {
                     
                     moves.add( 
                             new Move( piece, field, bishopMove, piece, MOVE_TYPE_NORMAL, castlingAvailability, halfMoveClock, enPassant ) );
                     step++;                     
                     
                 } else if ( position[ field ] * position[ bishopMove ] < 0 ) {
                     
                     moves.add( 
                             new Move( piece, field, bishopMove, piece, MOVE_TYPE_CAPTURE, castlingAvailability, halfMoveClock, enPassant ) );
                     break;
                     
                 } else {
                     
                     break;
                     
                 }
                 
                 bishopMove = field + PIECE_VECTORS[ INDEX_BISHOP_DIRECTION ][ direction ] * step;
                 
            }
            
        }
        
    }
    
    private void generatePseudoQueenMoves( int field ){
        int queenMove;
        int piece = isWhitePiece( position[ field ] ) ? W_QUEEN : B_QUEEN ;
        
        for( int direction = 0; direction < PIECE_VECTORS[ INDEX_QUEEN_DIRECTION ].length; direction++ ){
            
            int step = 1;
            queenMove = field + PIECE_VECTORS[ INDEX_QUEEN_DIRECTION ][ direction ] * step;
            
            while( !offTheBoard( queenMove ) ) {
                
                 if( position[ queenMove ] == EMPTY_FIELD ) {
                     
                     moves.add( 
                             new Move( piece, field, queenMove, piece, MOVE_TYPE_NORMAL, castlingAvailability, halfMoveClock, enPassant ) );
                     step++;                     
                     
                 } else if ( position[ field ] * position[ queenMove ] < 0 ) {
                     
                     moves.add( 
                             new Move( piece, field, queenMove, piece, MOVE_TYPE_CAPTURE, castlingAvailability, halfMoveClock, enPassant ) );
                     break;
                     
                 } else {
                     
                     break;
                     
                 }
                 
                 queenMove = field + PIECE_VECTORS[ INDEX_QUEEN_DIRECTION ][ direction ] * step;
                 
            }
            
        }
    }
    
    private void generatePseudoRookMoves( int field ){
        int rookMove;
        int piece = isWhitePiece( position[ field ] ) ? W_ROOK : B_ROOK ;
        
        for( int direction = 0; direction < PIECE_VECTORS[ INDEX_ROOK_DIRECTION ].length; direction++ ){
            
            int step = 1;
            rookMove = field + PIECE_VECTORS[ INDEX_ROOK_DIRECTION ][ direction ] * step;
            
            while( !offTheBoard( rookMove ) ) {
                
                 if( position[ rookMove ] == EMPTY_FIELD ) {
                     
                     moves.add( 
                             new Move( piece, field, rookMove, piece, MOVE_TYPE_NORMAL, castlingAvailability, halfMoveClock, enPassant ) );
                     step++;                     
                     
                 } else if ( position[ field ] * position[ rookMove ] < 0 ) {
                     
                     moves.add( 
                             new Move( piece, field, rookMove, piece, MOVE_TYPE_CAPTURE, castlingAvailability, halfMoveClock, enPassant ) );
                     break;
                     
                 } else {
                     
                     break;
                     
                 }
                 
                 rookMove = field + PIECE_VECTORS[ INDEX_ROOK_DIRECTION ][ direction ] * step;
                 
            }
            
        }
    }
    
    private int getRank( int field ){
        return ( field >> 4 );
    }
    
    private int getFile( int field ){
        return ( field & 7 );
    }
    
    private boolean fieldIsEmpty( int field ){
        return position[ field ] == EMPTY_FIELD;
    }
    
    private boolean isWhitePiece( int piece ){
        return piece > 0;
    }
    
    private boolean isBlackPiece( int piece ){
        return piece < 0;
    }
    
    private boolean offTheBoard( int field ){
        return ( ( field & 0x88 ) != 0 );
    }
    
}
