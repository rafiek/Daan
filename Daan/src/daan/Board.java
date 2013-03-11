/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package daan;

import static daan.Constants.BLACK;
import static daan.Constants.B_BISHOP;
import static daan.Constants.B_KING;
import static daan.Constants.B_KNIGHT;
import static daan.Constants.B_PAWN;
import static daan.Constants.B_QUEEN;
import static daan.Constants.B_ROOK;
import static daan.Constants.CAB_KING_SIDE;
import static daan.Constants.CAB_QUEEN_SIDE;
import static daan.Constants.CAW_KING_SIDE;
import static daan.Constants.CAW_QUEEN_SIDE;
import static daan.Constants.EMPTY_SQUARE;
import static daan.Constants.INDEX_BISHOP_DIRECTION;
import static daan.Constants.INDEX_KING_DIRECTION;
import static daan.Constants.INDEX_KNIGHT_DIRECTION;
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
import static daan.Constants.SQUARE_INDEX_MAPPINGS;
import static daan.Constants.SS;
import static daan.Constants.SW;
import static daan.Constants.VALUE_BISHOP;
import static daan.Constants.VALUE_KING;
import static daan.Constants.VALUE_KNIGHT;
import static daan.Constants.VALUE_PAWN;
import static daan.Constants.VALUE_QUEEN;
import static daan.Constants.VALUE_ROOK;
import static daan.Constants.WHITE;
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
    
    //keeps a list of the location --> the white pieces
    public HashMap<Integer, Integer> locationOfWhitePieces;
    
    //keeps a list of the location --> the black pieces
    public HashMap<Integer, Integer> locationOfBlackPieces;
    
    //whose turn is it to move
    public int sideToMove;
    
    //castling availability 0001, 0010, 0100, 1000, KQkq
    private int castlingAvailability;
    
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
        sideToMove = WHITE;
        castlingAvailability = 0;
        enPassant = -1;
        halfMoveClock = 0;
        fullMoveNumber = 1;
        String[] fenArray = fen.split("\\s");

        for (int i = 0; i < fenArray.length; i++) {
            switch (i) {
                case 0://position

                    int currentPositionIndex = SQUARE_INDEX_MAPPINGS.get("a8");
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
                                
                        } else if (Character.isDigit(positionInFEN.charAt(j))) {//number of empty squares

                            currentPositionIndex += Character.getNumericValue(positionInFEN.charAt(j));

                        } else if (positionInFEN.charAt(j) == '/') {//go to next row

                            int rowDown = currentPositionIndex - 16;
                            currentPositionIndex = rowDown - (rowDown % 16);

                        }

                    }

                    break;

                case 1://sideToMove
                    
                    sideToMove = (fenArray[i].equals("w")) ? WHITE : BLACK;
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

                    enPassant = !(fenArray[i].equals("-")) ? SQUARE_INDEX_MAPPINGS.get(fenArray[i]) : -1;
                    
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
        int emptySquares = 0;
        for(int i=8; i>0; i--){
            for(char c='a'; c<'i'; c++){
                
                int square = position[ SQUARE_INDEX_MAPPINGS.get( Character.toString(c)+i ) ];
                
                if(square != 0){
                    
                    if (emptySquares != 0) {
                        fen.append(emptySquares);
                        emptySquares = 0;
                    }
                    
                    switch(square){
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
                }else if(square == 0){
                    emptySquares++;
                }
            }
            
            if(emptySquares != 0){
                fen.append(emptySquares);
                emptySquares = 0;
            }
                
            if( i>1 ){
                fen.append('/');
            }
            
        }

        fen.append(' ');
        
        fen.append( ( sideToMove == WHITE ) ? 'w' : 'b' );
        
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
                enPassant < 0 ? '-' : ((String)getKeyByValue(SQUARE_INDEX_MAPPINGS, enPassant))
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

        //new set of moves
        moves = new ArrayList<>();

        //use correct piece list
        Set<Map.Entry<Integer, Integer>> locationOfPieces = ( sideToMove == WHITE ) ? locationOfWhitePieces.entrySet() : locationOfBlackPieces.entrySet();

        //loop through all pieces
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
    
    private void generatePseudoPawnMoves( int square ) {
        
        int capture = EMPTY_SQUARE;
        
        if( sideToMove == WHITE ){            
                
                //pawn moves N one
                if( squareIsEmpty( square + NORTH ) ){
                    
                    if( getRank( square ) == 6 ){ //pawn promotes to queen/rook/bishop/knight if eigth rank reached     
                        
                        moves.add( createMove( W_PAWN, square, square + NORTH, W_QUEEN, MOVE_TYPE_PROMOTION, capture ) );
                        
                        moves.add( createMove( W_PAWN, square, square + NORTH, W_ROOK, MOVE_TYPE_PROMOTION, capture ) );
                        
                        moves.add( createMove( W_PAWN, square, square + NORTH, W_BISHOP, MOVE_TYPE_PROMOTION, capture ) );
                        
                        moves.add( createMove( W_PAWN, square, square + NORTH, W_KNIGHT, MOVE_TYPE_PROMOTION, capture ) );
                        
                    } else {

                        moves.add( createMove( W_PAWN, square, square + NORTH, W_PAWN, MOVE_TYPE_NORMAL, capture ) );

                    }

                    if ( getRank( square ) == 1 ) { //pawn moves N two if 2nd rank 
                        
                        if ( squareIsEmpty( square + NN ) ) {
                            
                            moves.add( createMove( W_PAWN, square, square + NN, W_PAWN, MOVE_TYPE_NORMAL, capture ) );
                        
                        }

                    }
                    
                }
                
                capture = position[ square + NW ];
                
                //pawn takes NW or NE one                
                if ( isBlackPiece( capture ) ) {
                    
                    if( getRank( square ) == 6 ){
                        
                         moves.add( createMove( W_PAWN, square, square + NW, W_QUEEN, (MOVE_TYPE_PROMOTION | MOVE_TYPE_CAPTURE), capture ) );
                        
                        moves.add( createMove( W_PAWN, square, square + NW, W_ROOK, (MOVE_TYPE_PROMOTION | MOVE_TYPE_CAPTURE), capture ) );
                        
                        moves.add( createMove( W_PAWN, square, square + NW, W_BISHOP, (MOVE_TYPE_PROMOTION | MOVE_TYPE_CAPTURE), capture ) );
                        
                        moves.add( createMove( W_PAWN, square, square + NW, W_KNIGHT, (MOVE_TYPE_PROMOTION | MOVE_TYPE_CAPTURE), capture ) );
                        
                    }else{
                        
                        moves.add( createMove( W_PAWN, square, square + NW, W_PAWN, MOVE_TYPE_CAPTURE, capture ) );
                        
                    }

                }
                
                capture = position[ square + NE ];
                
                if ( isBlackPiece( capture ) ) {
                    
                    if( getRank( square ) == 6 ){
                        
                        moves.add( createMove( W_PAWN, square, square + NE, W_QUEEN, (MOVE_TYPE_PROMOTION | MOVE_TYPE_CAPTURE), capture ) );
                        
                        moves.add( createMove( W_PAWN, square, square + NE, W_ROOK, (MOVE_TYPE_PROMOTION | MOVE_TYPE_CAPTURE), capture ) );
                        
                        moves.add( createMove( W_PAWN, square, square + NE, W_BISHOP, (MOVE_TYPE_PROMOTION | MOVE_TYPE_CAPTURE), capture ) );
                        
                        moves.add( createMove( W_PAWN, square, square + NE, W_KNIGHT, (MOVE_TYPE_PROMOTION | MOVE_TYPE_CAPTURE), capture ) );
                        
                    }else{
                        
                        moves.add( createMove( W_PAWN, square, square + NE, W_PAWN, MOVE_TYPE_CAPTURE, capture ) );
                        
                    }

                }
                
                //if enpassant is available, then pawn can also take pawn by moving to enpassant square
                if( square + NW == enPassant ){
                    
                    moves.add( createMove( W_PAWN, square, square + NW, W_PAWN, MOVE_TYPE_EP, B_PAWN ) );
                    
                }
                
                 if( square + NE == enPassant ){
                    
                    moves.add( createMove( W_PAWN, square, square + NE, W_PAWN, MOVE_TYPE_EP, B_PAWN ) );
                    
                }
                 
            
        }else if( sideToMove == BLACK ){
                            
                //pawn moves S one
                if( squareIsEmpty( square + SOUTH ) ){
                    
                    if( getRank( square ) == 1 ){ //pawn promotes to queen/rook/bishop/knight if first rank reached     
                        
                        moves.add( createMove( B_PAWN, square, square + SOUTH, B_QUEEN, MOVE_TYPE_PROMOTION, capture ) );
                        
                        moves.add( createMove( B_PAWN, square, square + SOUTH, B_ROOK, MOVE_TYPE_PROMOTION, capture ) );
                        
                        moves.add( createMove( B_PAWN, square, square + SOUTH, B_BISHOP, MOVE_TYPE_PROMOTION, capture ) );
                        
                        moves.add( createMove( B_PAWN, square, square + SOUTH, B_KNIGHT, MOVE_TYPE_PROMOTION, capture ) );
                    
                    }else{
                        
                         moves.add( createMove( B_PAWN, square, square + SOUTH, B_PAWN, MOVE_TYPE_NORMAL, capture ) );
                         
                    }
                    
                    if ( getRank( square ) == 6 ) { //pawn moves S two if 7th rank 

                        if ( squareIsEmpty( square + SS ) ) {

                            moves.add( createMove( B_PAWN, square, square + SS, B_PAWN, MOVE_TYPE_NORMAL, capture ) );

                        }

                    }
                    
                } 
                
                capture = position[ square + SW ];
                
                //pawn takes SW or SE one                     
                if ( isWhitePiece( capture ) ) {

                    if( getRank( square ) == 1 ){ //pawn promotes to queen/rook/bishop/knight if first rank reached     
                        
                        moves.add( createMove( B_PAWN, square, square + SW, B_QUEEN, ( MOVE_TYPE_PROMOTION | MOVE_TYPE_CAPTURE ), capture ) );
                        
                        moves.add( createMove( B_PAWN, square, square + SW, B_ROOK, ( MOVE_TYPE_PROMOTION | MOVE_TYPE_CAPTURE ), capture ) );
                        
                        moves.add( createMove( B_PAWN, square, square + SW, B_BISHOP, ( MOVE_TYPE_PROMOTION | MOVE_TYPE_CAPTURE ), capture ) );
                        
                        moves.add( createMove( B_PAWN, square, square + SW, B_KNIGHT, ( MOVE_TYPE_PROMOTION | MOVE_TYPE_CAPTURE ), capture ) );
                    
                    }else{
                        
                         moves.add( createMove( B_PAWN, square, square + SW, B_PAWN, MOVE_TYPE_CAPTURE, capture ) );
                         
                    }

                }                
                
                capture = position[ square + SE ];
                
                if ( isWhitePiece( capture ) ) {
                    
                    if( getRank( square ) == 1 ){ //pawn promotes to queen/rook/bishop/knight if first rank reached     
                        
                        moves.add( createMove( B_PAWN, square, square + SE, B_QUEEN, ( MOVE_TYPE_PROMOTION | MOVE_TYPE_CAPTURE ), capture ) );
                        
                        moves.add( createMove( B_PAWN, square, square + SE, B_ROOK, ( MOVE_TYPE_PROMOTION | MOVE_TYPE_CAPTURE ), capture ) );
                        
                        moves.add( createMove( B_PAWN, square, square + SE, B_BISHOP, ( MOVE_TYPE_PROMOTION | MOVE_TYPE_CAPTURE ), capture ) );
                        
                        moves.add( createMove( B_PAWN, square, square + SE, B_KNIGHT, ( MOVE_TYPE_PROMOTION | MOVE_TYPE_CAPTURE ), capture ) );
                    
                    }else{
                        
                         moves.add( createMove( B_PAWN, square, square + SE, B_PAWN, MOVE_TYPE_CAPTURE, capture ) );
                         
                    }
                    
                }
                
                //if enpassant is available, then pawn can also take neighbour pawn by moving to enpassant square behind neighbour pawn
                if( square + SW == enPassant ){
                    
                    moves.add( createMove( B_PAWN, square, square + SW, B_PAWN, MOVE_TYPE_EP, W_PAWN ) );
                    
                }
                
                 if( square + SE == enPassant ){
                    
                    moves.add( createMove( B_PAWN, square, square + SE, B_PAWN, MOVE_TYPE_EP, W_PAWN ) );
                    
                }
                 
            }
        
        
    }
    
    private void generatePseudoKnightMoves( int square ) {

        int knightMove;
        int piece = isWhitePiece( position[ square ] ) ? W_KNIGHT : B_KNIGHT;

        for ( int direction = 0; direction < 8; direction++ ) {

            knightMove = square + PIECE_VECTORS[ INDEX_KNIGHT_DIRECTION ][ direction ];

            if ( !offTheBoard( knightMove ) ) {

                if ( position[ knightMove ] == EMPTY_SQUARE ) {

                    moves.add( createMove( piece, square, knightMove, piece, MOVE_TYPE_NORMAL, EMPTY_SQUARE ) );

                } else if ( position[ square ] * position[ knightMove ] < 0 ) {

                    moves.add( createMove( piece, square, knightMove, piece, MOVE_TYPE_CAPTURE, position[ knightMove ] ) );

                }

            }

        }

    }
    
    private void generatePseudoKingMoves( int square ) {
        /*
         * castling is impossible if
         * - king in check
         * - king will be in check
         * - king moves across attacked square 
         */
        int kingMove;
        int piece = isWhitePiece( position[ square ] ) ? W_KING : B_KING ;
        
        for( int direction = 0; direction < 8; direction++ ){
            
            kingMove = square + PIECE_VECTORS[ INDEX_KING_DIRECTION ][ direction ];
            
            if( !offTheBoard( kingMove ) ) {
                
                 if( position[ kingMove ] == EMPTY_SQUARE ) {
                     
                     moves.add( createMove( piece, square, kingMove, piece, MOVE_TYPE_NORMAL, EMPTY_SQUARE ) );
                     
                 } else if ( position[ square ] * position[ kingMove ] < 0 ) {
                     
                     moves.add( createMove( piece, square, kingMove, piece, MOVE_TYPE_CAPTURE, position[ kingMove ] ) );
                     
                 }
            }
            
        }
        
        if ( sideToMove == WHITE ) {

            if ( ( castlingAvailability & CAW_KING_SIDE ) == CAW_KING_SIDE ) {

                if ( ( position[ square + 1] == EMPTY_SQUARE ) && ( position[ square + 2] == EMPTY_SQUARE )
                        && ( !isAttacked( BLACK, square ) && !isAttacked( BLACK, square + 1 ) && !isAttacked( BLACK, square + 2 ) ) ) {

                    moves.add( createMove( piece, square, square + 2, piece, MOVE_TYPE_CASTLE, EMPTY_SQUARE ) );

                }

            }

            if ( ( castlingAvailability & CAW_QUEEN_SIDE ) == CAW_QUEEN_SIDE ) {

                if ( ( position[ square - 1] == EMPTY_SQUARE ) && ( position[ square - 2] == EMPTY_SQUARE ) && ( position[ square - 3] == EMPTY_SQUARE )
                        && ( !isAttacked( BLACK, square ) && !isAttacked( BLACK, square - 1 ) && !isAttacked( BLACK, square - 2 ) ) ) {

                    moves.add( createMove( piece, square, square - 2, piece, MOVE_TYPE_CASTLE, EMPTY_SQUARE ) );

                }

            }

        } else if ( sideToMove == BLACK ) {

            if ( ( castlingAvailability & CAB_KING_SIDE ) == CAB_KING_SIDE ) {

                if ( ( position[ square + 1] == EMPTY_SQUARE ) && ( position[ square + 2] == EMPTY_SQUARE )
                        && ( !isAttacked( WHITE, square ) && !isAttacked( WHITE, square + 1 ) && !isAttacked( WHITE, square + 2 ) ) ) {

                    moves.add( createMove( piece, square, square + 2, piece, MOVE_TYPE_CASTLE, EMPTY_SQUARE ) );

                }

            }

            if ( ( castlingAvailability & CAB_QUEEN_SIDE ) == CAB_QUEEN_SIDE ) {

                try {
                    if ( ( position[ square - 1] == EMPTY_SQUARE ) && ( position[ square - 2] == EMPTY_SQUARE ) && ( position[ square - 3] == EMPTY_SQUARE )
                            && ( !isAttacked( WHITE, square ) && !isAttacked( WHITE, square - 1 ) && !isAttacked( WHITE, square - 2 ) ) ) {

                        moves.add( createMove( piece, square, square - 2, piece, MOVE_TYPE_CASTLE, EMPTY_SQUARE ) );

                    }
                }
                catch ( ArrayIndexOutOfBoundsException e ) {
                    System.out.println( e + " square: " + square );
                    System.out.println( "castle: " + castlingAvailability );
                }

            }

        }
        
    }
    
    private void generatePseudoBishopMoves( int square ){
        int bishopMove;
        int piece = isWhitePiece( position[ square ] ) ? W_BISHOP : B_BISHOP ;
        
        for( int direction = 0; direction < PIECE_VECTORS[ INDEX_BISHOP_DIRECTION ].length; direction++ ){
            
            int step = 1;
            bishopMove = square + PIECE_VECTORS[ INDEX_BISHOP_DIRECTION ][ direction ] * step;
            
            while( !offTheBoard( bishopMove ) ) {
                
                 if( position[ bishopMove ] == EMPTY_SQUARE ) {
                     
                     moves.add( createMove(  piece, square, bishopMove, piece, MOVE_TYPE_NORMAL, EMPTY_SQUARE ) );
                     step++;                     
                     
                 } else if ( position[ square ] * position[ bishopMove ] < 0 ) {
                     
                     moves.add( createMove( piece, square, bishopMove, piece, MOVE_TYPE_CAPTURE, position[ bishopMove ] ) );
                     break;
                     
                 } else {
                     
                     break;
                     
                 }
                 
                 bishopMove = square + PIECE_VECTORS[ INDEX_BISHOP_DIRECTION ][ direction ] * step;
                 
            }
            
        }
        
    }
    
    private void generatePseudoQueenMoves( int square ){
        int queenMove;
        int piece = isWhitePiece( position[ square ] ) ? W_QUEEN : B_QUEEN ;
        
        for( int direction = 0; direction < PIECE_VECTORS[ INDEX_QUEEN_DIRECTION ].length; direction++ ){
            
            int step = 1;
            queenMove = square + PIECE_VECTORS[ INDEX_QUEEN_DIRECTION ][ direction ] * step;
            
            while( !offTheBoard( queenMove ) ) {
                
                 if( position[ queenMove ] == EMPTY_SQUARE ) {
                     
                     moves.add( createMove( piece, square, queenMove, piece, MOVE_TYPE_NORMAL, EMPTY_SQUARE ) );
                     step++;                     
                     
                 } else if ( position[ square ] * position[ queenMove ] < 0 ) {
                     
                     moves.add( createMove( piece, square, queenMove, piece, MOVE_TYPE_CAPTURE, position[ queenMove ] ) );
                     break;
                     
                 } else {
                     
                     break;
                     
                 }
                 
                 queenMove = square + PIECE_VECTORS[ INDEX_QUEEN_DIRECTION ][ direction ] * step;
                 
            }
            
        }
    }
    
    private void generatePseudoRookMoves( int square ){
        int rookMove;
        int piece = isWhitePiece( position[ square ] ) ? W_ROOK : B_ROOK ;
        
        for( int direction = 0; direction < PIECE_VECTORS[ INDEX_ROOK_DIRECTION ].length; direction++ ){
            
            int step = 1;
            rookMove = square + PIECE_VECTORS[ INDEX_ROOK_DIRECTION ][ direction ] * step;
            
            while( !offTheBoard( rookMove ) ) {
                
                 if( position[ rookMove ] == EMPTY_SQUARE ) {
                     
                     moves.add( createMove( piece, square, rookMove, piece, MOVE_TYPE_NORMAL, EMPTY_SQUARE ) );
                     step++;                     
                     
                 } else if ( position[ square ] * position[ rookMove ] < 0 ) {
                     
                     moves.add( createMove( piece, square, rookMove, piece, MOVE_TYPE_CAPTURE, position[ rookMove ] ) );
                     break;
                     
                 } else {
                     
                     break;
                     
                 }
                 
                 rookMove = square + PIECE_VECTORS[ INDEX_ROOK_DIRECTION ][ direction ] * step;
                 
            }
            
        }
    }
    
    public void makeMove( Move move ){
        
        if ( getKeyByValue( SQUARE_INDEX_MAPPINGS, move.to ) == null ) {
            System.out.println( move.pieceFrom );
            System.out.println( move.from );
            System.out.println( move.to );
            System.out.println( move.castleAvailability );
            System.out.println( this );
        }
                
        //switch side to move
        sideToMove *= -1;
        
        //check reset of halfMoveClock
        if( ( Math.abs( move.pieceFrom ) == W_PAWN ) || ( ( move.type & MOVE_TYPE_CAPTURE ) == MOVE_TYPE_CAPTURE ) ){
            halfMoveClock = 0;
        }else{
            halfMoveClock++;            
        }
        
        if( move.pieceFrom < 0 ){
            fullMoveNumber++;
        }
        
        //piece leaves square
        clearSquare( move.from );
        
        if( ( move.type & MOVE_TYPE_CAPTURE ) == MOVE_TYPE_CAPTURE){
            clearSquare( move.to );
        }
        
        //also covers promotion       
        fillSquare( move.pieceTo, move.to );
        
        //update castle flags if king or rook has moved
        switch( getKeyByValue( SQUARE_INDEX_MAPPINGS, move.from ) ){
            case "h1": castlingAvailability &= ~CAW_KING_SIDE;                  break;
            case "e1": castlingAvailability &= ~(CAW_KING_SIDE|CAW_QUEEN_SIDE); break;
            case "a1": castlingAvailability &= ~CAW_QUEEN_SIDE;                 break;
            case "h8": castlingAvailability &= ~CAB_KING_SIDE;                  break;                                                         
            case "e8": castlingAvailability &= ~(CAB_KING_SIDE|CAB_QUEEN_SIDE); break;
            case "a8": castlingAvailability &= ~CAB_QUEEN_SIDE;                 break;
        }
        
        switch( getKeyByValue( SQUARE_INDEX_MAPPINGS, move.to ) ){
            case "h1": castlingAvailability &= ~CAW_KING_SIDE;                  break;
            case "e1": castlingAvailability &= ~(CAW_KING_SIDE|CAW_QUEEN_SIDE); break;
            case "a1": castlingAvailability &= ~CAW_QUEEN_SIDE;                 break;
            case "h8": castlingAvailability &= ~CAB_KING_SIDE;                  break;                                                         
            case "e8": castlingAvailability &= ~(CAB_KING_SIDE|CAB_QUEEN_SIDE); break;
            case "a8": castlingAvailability &= ~CAB_QUEEN_SIDE;                 break;
        }
        
        //when move is castle, then also move rook 
        if ( ( move.type & MOVE_TYPE_CASTLE ) == MOVE_TYPE_CASTLE ) {
            
            switch ( getKeyByValue( SQUARE_INDEX_MAPPINGS, move.to ) ) {
                
                case "g1":
                    clearSquare( SQUARE_INDEX_MAPPINGS.get( "h1" ) );
                    fillSquare( W_ROOK, SQUARE_INDEX_MAPPINGS.get( "f1" ) );
                    //castlingAvailability &= ~(CAW_KING_SIDE|CAW_QUEEN_SIDE);
                    break;
                    
                case "c1":
                    clearSquare( SQUARE_INDEX_MAPPINGS.get( "a1" ) );
                    fillSquare( W_ROOK, SQUARE_INDEX_MAPPINGS.get( "d1" ) );
                    //castlingAvailability &= ~(CAW_KING_SIDE|CAW_QUEEN_SIDE);
                    break;
                    
                case "g8":
                    clearSquare( SQUARE_INDEX_MAPPINGS.get( "h8" ) );
                    fillSquare( B_ROOK, SQUARE_INDEX_MAPPINGS.get( "f8" ) );
                    //castlingAvailability &= ~(CAB_KING_SIDE|CAB_QUEEN_SIDE);
                    break;
                    
                case "c8":
                    clearSquare( SQUARE_INDEX_MAPPINGS.get( "a8" ) );
                    fillSquare( B_ROOK, SQUARE_INDEX_MAPPINGS.get( "d8" ) );
                    //castlingAvailability &= ~(CAB_KING_SIDE|CAB_QUEEN_SIDE);
                    break;
                    
            }
            
        }
        
        //set enpassant to -1
        enPassant = -1;        
        
        if( Math.abs( move.pieceFrom ) == W_PAWN && Math.abs( move.from - move.to ) == 32 ){
            enPassant = ( move.from + move.to ) / 2;
        }
        
        if( move.type == MOVE_TYPE_EP ){
            
            if( isWhitePiece( move.pieceFrom ) ){
                
                clearSquare( move.to - 16 );
                
            } else {
                
                clearSquare( move.to + 16 );
                
            }
            
        }
                
    }
    
    public void unmakeMove( Move move ){
        
        sideToMove *= -1;
        
        halfMoveClock = move.halfMoveClock;
        
        enPassant = move.enPassant;
        
        if( move.pieceFrom < 0 ){
            
            fullMoveNumber--;
            
        }
        
        clearSquare( move.to );
        
        fillSquare( move.pieceFrom, move.from );
        
        if( ( move.type & MOVE_TYPE_CAPTURE ) == MOVE_TYPE_CAPTURE ){
            fillSquare( move.capture, move.to );                        
        }
                
        if( move.type == MOVE_TYPE_CASTLE ){
            
            switch( getKeyByValue( SQUARE_INDEX_MAPPINGS, move.to ) ){
                
                case "g1":
                    clearSquare( SQUARE_INDEX_MAPPINGS.get( "f1" ) );
                    fillSquare( W_ROOK, SQUARE_INDEX_MAPPINGS.get( "h1" ) );
                    break;
                    
                case "c1":
                    clearSquare( SQUARE_INDEX_MAPPINGS.get( "d1" ) );
                    fillSquare( W_ROOK, SQUARE_INDEX_MAPPINGS.get( "a1" ) );
                    break;
                    
                case "g8":
                    clearSquare( SQUARE_INDEX_MAPPINGS.get( "f8" ) );
                    fillSquare( B_ROOK, SQUARE_INDEX_MAPPINGS.get( "h8" ) );
                    break;
                    
                case "c8":
                    clearSquare( SQUARE_INDEX_MAPPINGS.get( "d8" ) );
                    fillSquare( B_ROOK, SQUARE_INDEX_MAPPINGS.get( "a8" ) );
                    break;
                    
            }                
            
        }
        
        castlingAvailability = move.castleAvailability;
        
        if( move.type == MOVE_TYPE_EP ){
            
            if( sideToMove == WHITE ){
                
                fillSquare( B_PAWN, move.to - 16 );
                
            } else {
                
                fillSquare( W_PAWN, move.to + 16 );
                
            }
            
        }        
        
    }
    
    public boolean isAttacked( int byColor, int square ){
        
        //pawns
        if( byColor == WHITE ){
            
            if( !offTheBoard( square + SW ) ){
                
                if( position[ square + SW ] == W_PAWN ){
                    //System.out.println( "white pawn attack" );
                    return true;
                    
                }
                
            }
            
            if( !offTheBoard( square + SE ) ){
                
                if( position[ square + SE ] == W_PAWN ){
                    //System.out.println( "white pawn attack" );
                    return true;
                    
                }
                
            }
            
        } else {
            
            if( !offTheBoard( square + NW ) ){
                
                if( position[ square + NW ] == B_PAWN ){
                    //System.out.println( "black pawn attack" );
                    //System.out.println( this );
                    return true;
                    
                }
                
            }
            
            if( !offTheBoard( square + NE ) ){
                
                if( position[ square + NE ] == B_PAWN ){
                    //System.out.println( "black pawn attack" );
                    //System.out.println( this );
                    return true;
                    
                }
                
            }
            
        }
        
        //knights
        if( knightAttack( byColor, square ) ){
            //System.out.println( "knight attack" );
            return true;
            
        }
        
        //kings
        if( kingAttack( byColor, square ) ){
            //System.out.println( "king attack" );
            return true;
            
        }
        
        //straight
        if( straightAttack( byColor, square ) ){
            //System.out.println( "straightattack" );
            return true;
            
        }
        
        //diagonal
        if( diagonalAttack( byColor, square ) ){
            //System.out.println( "diagonalattack" );
            return true;
            
        }
        
        
        return false;
    }
    
    private int getRank( int square ){
        return ( square >> 4 );
    }
    
    private int getFile( int square ){
        return ( square & 7 );
    }
    
    private boolean squareIsEmpty( int square ){
        return position[ square ] == EMPTY_SQUARE;
    }
    
    private void clearSquare( int square ){
        
        if( isWhitePiece( position[square ] ) ){
            
            locationOfWhitePieces.remove( square );
            
        } else {
            
            locationOfBlackPieces.remove( square );
            
        }
        
        position[ square ] = EMPTY_SQUARE;
        
    }
    
    private void fillSquare( int piece, int square ){
        
        if( piece > 0 ){
            
            locationOfWhitePieces.put( square, piece );
            
        }else if( piece < 0){
            
            locationOfBlackPieces.put( square, piece );
            
        }
        
        position[ square ] = piece;
        
    }
    
    private boolean isWhitePiece( int piece ){
        return piece > 0;
    }
    
    private boolean isBlackPiece( int piece ){
        return piece < 0;
    }
    
    private boolean offTheBoard( int square ){
        return ( ( square & 0x88 ) != 0 );
    }

    private boolean knightAttack( int byColor, int square ) {
        
        int knightMove;
        int knight = ( byColor == WHITE ) ? W_KNIGHT : B_KNIGHT;

        for ( int direction = 0; direction < 8; direction++ ) {

            knightMove = square + PIECE_VECTORS[ INDEX_KNIGHT_DIRECTION ][ direction ];

            if ( !offTheBoard( knightMove ) ) {

                if ( position[ knightMove ] == knight ) {

                    return true;

                } 

            }

        }
        
        return false;
        
    }

    private boolean kingAttack( int byColor, int square ) {
        
        int kingMove;
        int king = ( byColor == WHITE ) ? W_KING : B_KING ;
        
        for( int direction = 0; direction < 8; direction++ ){
            
            kingMove = square + PIECE_VECTORS[ INDEX_KING_DIRECTION ][ direction ];
            
            if( !offTheBoard( kingMove ) ) {
                
                 if( position[ kingMove ] == king ) {
                     
                     return true;
                     
                 } 
                 
            }
            
        }
        
        return false;
        
    }
    
    private boolean straightAttack( int byColor, int square ){
        int sliderMove;
        int queen = ( byColor == WHITE ) ? W_QUEEN : B_QUEEN;
        int rook = ( byColor == WHITE ) ? W_ROOK : B_ROOK;

        for ( int direction = 0; direction < PIECE_VECTORS[ INDEX_ROOK_DIRECTION].length; direction++ ) {

            int step = 1;
            sliderMove = square + PIECE_VECTORS[ INDEX_ROOK_DIRECTION][ direction] * step;

            while ( !offTheBoard( sliderMove ) ) {

                if ( position[ sliderMove] != EMPTY_SQUARE ) {
                    
                    if( ( position[ sliderMove] == queen )  || ( position[ sliderMove] == rook ) ){
//                        System.out.println( sliderMove );
//                        System.out.println( position[ sliderMove ] );
                        return true;
                        
                    } else {
                        
                        break;
                        
                    }
                    
                } else {
                    
                    step++;
                    
                }
                
                sliderMove = square + PIECE_VECTORS[ INDEX_ROOK_DIRECTION][ direction] * step;

            }

        }

        return false;
        
    }

    private boolean diagonalAttack( int byColor, int square ) {
        
        int sliderMove;
        int queen = ( byColor == WHITE ) ? W_QUEEN : B_QUEEN;
        int bishop = ( byColor == WHITE ) ? W_BISHOP : B_BISHOP;

        for ( int direction = 0; direction < PIECE_VECTORS[ INDEX_BISHOP_DIRECTION].length; direction++ ) {

            int step = 1;
            sliderMove = square + PIECE_VECTORS[ INDEX_BISHOP_DIRECTION][ direction] * step;

            while ( !offTheBoard( sliderMove ) ) {

                if ( position[ sliderMove] != EMPTY_SQUARE ) {
                    
                    if( ( position[ sliderMove] == queen )  || ( position[ sliderMove] == bishop ) ){
//                        System.out.println( sliderMove );
//                        System.out.println( position[ sliderMove ] );
                        return true;
                        
                    } else {
                        
                        break;
                        
                    }
                    
                } else {
                    
                    step++;
                    
                }
                
                sliderMove = square + PIECE_VECTORS[ INDEX_BISHOP_DIRECTION][ direction] * step;

            }

        }

        return false;

    }
    
    Move createMove( int pieceFrom, int from, int to, int pieceTo, int type, int capture ){
        
        return new Move( pieceFrom, from, to, pieceTo, type, capture, castlingAvailability, halfMoveClock, enPassant );
        
    }
    
    double evaluate(){
        double valueWhite = 0;
        double valueBlack = 0;
        
        Set<Map.Entry<Integer, Integer>> entrySetWhite = locationOfWhitePieces.entrySet();
        Set<Map.Entry<Integer, Integer>> entrySetBlack = locationOfBlackPieces.entrySet();
        
        for(Map.Entry<Integer, Integer> entry : entrySetWhite ){
            
            switch( entry.getValue() ){
                case W_KING:    valueWhite += VALUE_KING;   break;
                case W_QUEEN:   valueWhite += VALUE_QUEEN;  break;
                case W_ROOK:    valueWhite += VALUE_ROOK;   break;
                case W_BISHOP:  valueWhite += VALUE_BISHOP; break;                        
                case W_KNIGHT:  valueWhite += VALUE_KNIGHT; break;
                case W_PAWN:    valueWhite += VALUE_PAWN;   break;
            }
            
        }
        
        for(Map.Entry<Integer, Integer> entry : entrySetBlack ){
            
            switch( entry.getValue() ){
                case B_KING:    valueBlack += VALUE_KING;   break;
                case B_QUEEN:   valueBlack += VALUE_QUEEN;  break;
                case B_ROOK:    valueBlack += VALUE_ROOK;   break;
                case B_BISHOP:  valueBlack += VALUE_BISHOP; break;                        
                case B_KNIGHT:  valueBlack += VALUE_KNIGHT; break;
                case B_PAWN:    valueBlack += VALUE_PAWN;   break;
            }
            
        }
        
        return ( valueWhite - valueBlack ) * sideToMove;
        
    }
    
}
