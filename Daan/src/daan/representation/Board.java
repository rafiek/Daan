/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package daan.representation;

import static daan.utils.Constants.*;
import daan.utils.Utils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author Rafiek Mohamedjoesoef <Rafiek.Mohamedjoesoef@hva.nl> created
 * 14-feb-2013, 10:22:53
 */
public class Board {

    //current position in 0x88 format, 0x00=A1, 0x07=H1
    public int[] position;

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

    //possible pseudo moves for current board
    private List<Move> moves;

    public int whiteKingPosition;

    public int blackKingPosition;

    public int valueMaterialWhite;

    public int valueMaterialBlack;

    private int valuePieceSquareWhite;

    private int valuePieceSquareBlack;

    public Board() {
        initPosition( FEN_START_POSITION );
    }

    public Board( String FEN ) {
        initPosition( FEN );
    }

    private void initPosition( String fen ) {
        position = new int[ 128 ];
        locationOfWhitePieces = new HashMap<>();
        locationOfBlackPieces = new HashMap<>();
        sideToMove = WHITE;
        castlingAvailability = 0;
        enPassant = -1;
        halfMoveClock = 0;
        fullMoveNumber = 1;
        valueMaterialWhite = 0;
        valueMaterialBlack = 0;
        valuePieceSquareWhite = 0;
        valuePieceSquareBlack = 0;

        String[] fenArray = fen.split( "\\s" );

        for ( int i = 0; i < fenArray.length; i++ ) {
            switch ( i ) {
                case 0://position

                    int currentPositionIndex = SQUARE_INDEX_MAPPINGS.get( "a8" );
                    String positionInFEN = fenArray[i];

                    for ( int j = 0; j < positionInFEN.length(); j++ ) {

                        if ( Character.isLetter( positionInFEN.charAt( j ) ) ) {//piece

                            position[currentPositionIndex] = PIECE_CHARACTER_MAPPINGS.get( positionInFEN.charAt( j ) );

                            if ( isWhitePiece( position[currentPositionIndex] ) ) {

                                valueMaterialWhite += PIECE_VALUE_MAPPINGS.get( Math.abs( position[currentPositionIndex] ) );

                                switch ( position[currentPositionIndex] ) {
                                    case W_PAWN:
                                        valuePieceSquareWhite += PIECE_SQUARE_WPAWN[ currentPositionIndex];
                                        break;
                                    case W_KNIGHT:
                                        valuePieceSquareWhite += PIECE_SQUARE_WKNIGHT[ currentPositionIndex];
                                        break;
                                    case W_BISHOP:
                                        valuePieceSquareWhite += PIECE_SQUARE_WBISHOP[ currentPositionIndex];
                                        break;
                                    case W_ROOK:
                                        valuePieceSquareWhite += PIECE_SQUARE_WROOK[ currentPositionIndex];
                                        break;
                                }

                            } else {

                                valueMaterialBlack += PIECE_VALUE_MAPPINGS.get( Math.abs( position[currentPositionIndex] ) );

                                switch ( position[currentPositionIndex] ) {
                                    case B_PAWN:
                                        valuePieceSquareBlack += PIECE_SQUARE_BPAWN[ currentPositionIndex];
                                        break;
                                    case B_KNIGHT:
                                        valuePieceSquareBlack += PIECE_SQUARE_BKNIGHT[ currentPositionIndex];
                                        break;
                                    case B_BISHOP:
                                        valuePieceSquareBlack += PIECE_SQUARE_BBISHOP[ currentPositionIndex];
                                        break;
                                    case B_ROOK:
                                        valuePieceSquareBlack += PIECE_SQUARE_BROOK[ currentPositionIndex];
                                        break;
                                }

                            }

                            if ( position[currentPositionIndex] == W_KING ) {

                                whiteKingPosition = currentPositionIndex;

                            } else if ( position[currentPositionIndex] == B_KING ) {

                                blackKingPosition = currentPositionIndex;

                            }

                            if ( Character.isUpperCase( positionInFEN.charAt( j ) ) ) {

                                locationOfWhitePieces.put( currentPositionIndex, position[currentPositionIndex] );

                            } else {

                                locationOfBlackPieces.put( currentPositionIndex, position[currentPositionIndex] );

                            }

                            currentPositionIndex++;

                        } else if ( Character.isDigit( positionInFEN.charAt( j ) ) ) {//number of empty squares

                            currentPositionIndex += Character.getNumericValue( positionInFEN.charAt( j ) );

                        } else if ( positionInFEN.charAt( j ) == '/' ) {//go to next row

                            int rowDown = currentPositionIndex - 16;
                            currentPositionIndex = rowDown - ( rowDown % 16 );

                        }

                    }

                    break;

                case 1://sideToMove

                    sideToMove = ( fenArray[ i].equals( "w" ) ) ? WHITE : BLACK;

                    break;

                case 2://castlingAvailability
                    String castling = fenArray[i];

                    for ( int j = 0; j < castling.length(); j++ ) {
                        switch ( castling.charAt( j ) ) {
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

                    enPassant = !( fenArray[i].equals( "-" ) ) ? SQUARE_INDEX_MAPPINGS.get( fenArray[i] ) : -1;

                    break;

                case 4://halfMoveClock

                    halfMoveClock = Integer.parseInt( fenArray[i] );

                    break;
                case 5://fullMoveNumber

                    fullMoveNumber = Integer.parseInt( fenArray[i] );

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
        for ( int i = 8; i > 0; i-- ) {
            for ( char c = 'a'; c < 'i'; c++ ) {

                int square = position[ SQUARE_INDEX_MAPPINGS.get( Character.toString( c ) + i )];

                if ( square != 0 ) {

                    if ( emptySquares != 0 ) {
                        fen.append( emptySquares );
                        emptySquares = 0;
                    }

                    switch ( square ) {
                        case B_ROOK:
                            fen.append( 'r' );
                            break;
                        case B_KNIGHT:
                            fen.append( 'n' );
                            break;
                        case B_BISHOP:
                            fen.append( 'b' );
                            break;
                        case B_QUEEN:
                            fen.append( 'q' );
                            break;
                        case B_KING:
                            fen.append( 'k' );
                            break;
                        case B_PAWN:
                            fen.append( 'p' );
                            break;
                        case W_PAWN:
                            fen.append( 'P' );
                            break;
                        case W_ROOK:
                            fen.append( 'R' );
                            break;
                        case W_KNIGHT:
                            fen.append( 'N' );
                            break;
                        case W_BISHOP:
                            fen.append( 'B' );
                            break;
                        case W_QUEEN:
                            fen.append( 'Q' );
                            break;
                        case W_KING:
                            fen.append( 'K' );
                            break;
                        default:
                            break;
                    }
                } else if ( square == 0 ) {
                    emptySquares++;
                }
            }

            if ( emptySquares != 0 ) {
                fen.append( emptySquares );
                emptySquares = 0;
            }

            if ( i > 1 ) {
                fen.append( '/' );
            }

        }

        fen.append( ' ' );

        fen.append( ( sideToMove == WHITE ) ? 'w' : 'b' );

        fen.append( ' ' );

        if ( ( castlingAvailability & CAW_KING_SIDE ) > 0 ) {
            fen.append( 'K' );
        }
        if ( ( castlingAvailability & CAW_QUEEN_SIDE ) > 0 ) {
            fen.append( 'Q' );
        }
        if ( ( castlingAvailability & CAB_KING_SIDE ) > 0 ) {
            fen.append( 'k' );
        }
        if ( ( castlingAvailability & CAB_QUEEN_SIDE ) > 0 ) {
            fen.append( 'q' );
        }
        if ( castlingAvailability == 0 ) {
            fen.append( '-' );
        }

        fen.append( ' ' );

        fen.append(
                enPassant < 0 ? '-' : ( ( String ) Utils.getKeyByValue( SQUARE_INDEX_MAPPINGS, enPassant ) ) );

        return fen.toString();
    }

    public List filterQuiescenceMoves( List<Move> moves ) {

        List<Move> quiescenceMoves = new ArrayList<>();

        for ( Move move : moves ) {

// CHECKING THIS IS TERRIBLE FOR PERFORMANCE
//            makeMove( move );
//
//            int kingPosition = ( sideToMove == WHITE ) ? whiteKingPosition : blackKingPosition;
//
//            if ( isAttacked( -sideToMove, kingPosition ) ) {
//
//                quiescenceMoves.add( move );
//                continue;
//
//            }
//
//            unmakeMove( move );

            if ( ( move.type & MOVE_TYPE_CAPTURE ) == MOVE_TYPE_CAPTURE ) {

                quiescenceMoves.add( move );

            } else if ( ( move.type & MOVE_TYPE_PROMOTION ) == MOVE_TYPE_PROMOTION ) {

                quiescenceMoves.add( move );

            }

        }

        return quiescenceMoves;

    }

    public List generateMoves( boolean normal ) {

        //new set of moves
        moves = new ArrayList<>();

        //use correct piece list
        Set<Map.Entry<Integer, Integer>> locationOfPieces = ( sideToMove == WHITE ) ? locationOfWhitePieces.entrySet() : locationOfBlackPieces.entrySet();
        //System.out.println( locationOfPieces );

        //loop through all pieces
        for ( Map.Entry<Integer, Integer> entry : locationOfPieces ) {

            switch ( Math.abs( entry.getValue() ) ) {//temporarily absolute value just to check type of piece  

                case W_PAWN:
                    //System.out.println( "generating pawn moves" );
                    generatePseudoPawnMoves( entry.getKey(), normal );
                    break;
                case W_KNIGHT:
                    //System.out.println( "generating knight moves" );
                    generatePseudoKnightMoves( entry.getKey(), normal );
                    break;
                case W_BISHOP:
                    //System.out.println( "generating bishop moves" );
                    generatePseudoBishopMoves( entry.getKey(), normal );
                    break;
                case W_KING:
                    //System.out.println( "generating king moves" );
                    generatePseudoKingMoves( entry.getKey(), normal );
                    break;
                case W_QUEEN:
                    //System.out.println( "generating queen moves" );
                    generatePseudoQueenMoves( entry.getKey(), normal );
                    break;
                case W_ROOK:
                    //System.out.println( "generating rook moves" );
                    generatePseudoRookMoves( entry.getKey(), normal );
                    break;

            }

        }
        
        Collections.sort( moves, MVV_LVA_SCORE );

        return moves;

    }

    private void generatePseudoPawnMoves( int square, boolean normal ) {

        int capture = EMPTY_SQUARE;
        final int PUSH, DOUBLE_PUSH, CAPTURE_WEST, CAPTURE_EAST, PAWN, QUEEN, ROOK, BISHOP, KNIGHT, BACK_RANK, START_RANK;
        
        if( sideToMove == WHITE ){
            
            PUSH = NORTH;
            DOUBLE_PUSH = NN;
            CAPTURE_WEST = NW;
            CAPTURE_EAST = NE;
            PAWN = W_PAWN;
            QUEEN = W_QUEEN;
            ROOK = W_ROOK;
            BISHOP = W_BISHOP;
            KNIGHT = W_KNIGHT;
            BACK_RANK = 7;
            START_RANK = 1;
            
        } else {
            
            PUSH = SOUTH;
            DOUBLE_PUSH = SS;
            CAPTURE_WEST = SW;
            CAPTURE_EAST = SE;
            PAWN = B_PAWN;
            QUEEN = B_QUEEN;
            ROOK = B_ROOK;
            BISHOP = B_BISHOP;
            KNIGHT = B_KNIGHT;
            BACK_RANK = 0;
            START_RANK = 6;
            
        }
        
        if ( squareIsEmpty( square + PUSH ) ) {

            int target = square + PUSH;
            
            if ( getRank( target ) == BACK_RANK ) {

                moves.add( createMove( PAWN, square, target, QUEEN, MOVE_TYPE_PROMOTION, capture ) );

                moves.add( createMove( PAWN, square, target, ROOK, MOVE_TYPE_PROMOTION, capture ) );

                moves.add( createMove( PAWN, square, target, BISHOP, MOVE_TYPE_PROMOTION, capture ) );

                moves.add( createMove( PAWN, square, target, KNIGHT, MOVE_TYPE_PROMOTION, capture ) );

            } else if ( normal ){

                moves.add( createMove( PAWN, square, target, PAWN, MOVE_TYPE_NORMAL, capture ) );
                
            }

            if ( getRank( square ) == START_RANK && normal ) {

                if ( squareIsEmpty( square + DOUBLE_PUSH ) ) {

                    moves.add( createMove( PAWN, square, square + DOUBLE_PUSH, PAWN, MOVE_TYPE_NORMAL, capture ) );

                }

            }

        }

        capture = offTheBoard( square + CAPTURE_WEST ) ? EMPTY_SQUARE : position[ square + CAPTURE_WEST ];

        if ( ( capture * position[ square ] ) < 0 ) {

            int target = square + CAPTURE_WEST;
            
            if ( getRank( target ) == BACK_RANK ) {

                moves.add( createMove( PAWN, square, target, QUEEN, ( MOVE_TYPE_PROMOTION | MOVE_TYPE_CAPTURE ), capture ) );

                moves.add( createMove( PAWN, square, target, ROOK, ( MOVE_TYPE_PROMOTION | MOVE_TYPE_CAPTURE ), capture ) );

                moves.add( createMove( PAWN, square, target, BISHOP, ( MOVE_TYPE_PROMOTION | MOVE_TYPE_CAPTURE ), capture ) );

                moves.add( createMove( PAWN, square, target, KNIGHT, ( MOVE_TYPE_PROMOTION | MOVE_TYPE_CAPTURE ), capture ) );

            } else {

                moves.add( createMove( PAWN, square, target, PAWN, MOVE_TYPE_CAPTURE, capture ) );

            }

        }

        capture = offTheBoard( square + CAPTURE_EAST ) ? EMPTY_SQUARE : position[ square + CAPTURE_EAST ];

        if ( ( capture * position[ square ] ) < 0 ) {
            
            int target = square + CAPTURE_EAST;

            if ( getRank( target ) == BACK_RANK ) {

                moves.add( createMove( PAWN, square, target, QUEEN, ( MOVE_TYPE_PROMOTION | MOVE_TYPE_CAPTURE ), capture ) );

                moves.add( createMove( PAWN, square, target, ROOK, ( MOVE_TYPE_PROMOTION | MOVE_TYPE_CAPTURE ), capture ) );

                moves.add( createMove( PAWN, square, target, BISHOP, ( MOVE_TYPE_PROMOTION | MOVE_TYPE_CAPTURE ), capture ) );

                moves.add( createMove( PAWN, square, target, KNIGHT, ( MOVE_TYPE_PROMOTION | MOVE_TYPE_CAPTURE ), capture ) );

            } else {

                moves.add( createMove( PAWN, square, target, PAWN, MOVE_TYPE_CAPTURE, capture ) );

            }

        }

        //if enpassant is available, then pawn can also take pawn by moving to enpassant square
        if ( ( ( square + CAPTURE_WEST ) == enPassant ) && !offTheBoard( square + CAPTURE_WEST ) ) {

            moves.add( createMove( PAWN, square, square + CAPTURE_WEST, PAWN, MOVE_TYPE_EP, -PAWN ) );

        }

        if ( ( ( square + CAPTURE_EAST ) == enPassant ) && !offTheBoard( square + CAPTURE_EAST ) ) {

            moves.add( createMove( PAWN, square, square + CAPTURE_EAST, PAWN, MOVE_TYPE_EP, -PAWN ) );

        }

    }

    private void generatePseudoKnightMoves( int square, boolean normal ) {

        int knightMove;
        int piece = isWhitePiece( position[ square] ) ? W_KNIGHT : B_KNIGHT;

        for ( int direction = 0; direction < 8; direction++ ) {

            knightMove = square + PIECE_VECTORS[ INDEX_KNIGHT_DIRECTION ][ direction];

            if ( !offTheBoard( knightMove ) ) {

                if ( position[ knightMove] == EMPTY_SQUARE && normal ) {

                    moves.add( createMove( piece, square, knightMove, piece, MOVE_TYPE_NORMAL, EMPTY_SQUARE ) );

                } else if ( position[ square ] * position[ knightMove ] < 0 ) {

                    moves.add( createMove( piece, square, knightMove, piece, MOVE_TYPE_CAPTURE, position[ knightMove ] ) );

                }

            }

        }

    }

    private void generatePseudoKingMoves( int square, boolean normal ) {
        /*
         * castling is impossible if
         * - king in check
         * - king will be in check
         * - king moves across attacked square 
         */
        int kingMove;
        int piece = isWhitePiece( position[ square] ) ? W_KING : B_KING;

        for ( int direction = 0; direction < 8; direction++ ) {

            kingMove = square + PIECE_VECTORS[ INDEX_KING_DIRECTION ][ direction ];

            if ( !offTheBoard( kingMove ) ) {

                if ( position[ kingMove ] == EMPTY_SQUARE && normal ) {

                    moves.add( createMove( piece, square, kingMove, piece, MOVE_TYPE_NORMAL, EMPTY_SQUARE ) );

                } else if ( position[ square ] * position[ kingMove] < 0 ) {

                    moves.add( createMove( piece, square, kingMove, piece, MOVE_TYPE_CAPTURE, position[ kingMove] ) );

                }
            }

        }

        if ( ( sideToMove == WHITE ) && normal ) {

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

        } else if ( ( sideToMove == BLACK ) && normal ) {

            if ( ( castlingAvailability & CAB_KING_SIDE ) == CAB_KING_SIDE ) {

                if ( ( position[ square + 1] == EMPTY_SQUARE ) && ( position[ square + 2] == EMPTY_SQUARE )
                        && ( !isAttacked( WHITE, square ) && !isAttacked( WHITE, square + 1 ) && !isAttacked( WHITE, square + 2 ) ) ) {

                    moves.add( createMove( piece, square, square + 2, piece, MOVE_TYPE_CASTLE, EMPTY_SQUARE ) );

                }

            }

            if ( ( castlingAvailability & CAB_QUEEN_SIDE ) == CAB_QUEEN_SIDE ) {

                    if ( ( position[ square - 1] == EMPTY_SQUARE ) && ( position[ square - 2] == EMPTY_SQUARE ) && ( position[ square - 3] == EMPTY_SQUARE )
                            && ( !isAttacked( WHITE, square ) && !isAttacked( WHITE, square - 1 ) && !isAttacked( WHITE, square - 2 ) ) ) {

                        moves.add( createMove( piece, square, square - 2, piece, MOVE_TYPE_CASTLE, EMPTY_SQUARE ) );

                    }

            }

        }

    }

    private void generatePseudoBishopMoves( int square, boolean normal ) {
        int bishopMove;
        int piece = isWhitePiece( position[ square] ) ? W_BISHOP : B_BISHOP;

        for ( int direction = 0; direction < PIECE_VECTORS[ INDEX_BISHOP_DIRECTION].length; direction++ ) {

            int step = 1;
            bishopMove = square + PIECE_VECTORS[ INDEX_BISHOP_DIRECTION][ direction] * step;

            while ( !offTheBoard( bishopMove ) ) {

                if ( position[ bishopMove] == EMPTY_SQUARE && normal ) {

                    moves.add( createMove( piece, square, bishopMove, piece, MOVE_TYPE_NORMAL, EMPTY_SQUARE ) );
                    step++;

                } else if ( position[ square] * position[ bishopMove] < 0 ) {

                    moves.add( createMove( piece, square, bishopMove, piece, MOVE_TYPE_CAPTURE, position[ bishopMove] ) );
                    break;

                } else {

                    break;

                }

                bishopMove = square + PIECE_VECTORS[ INDEX_BISHOP_DIRECTION][ direction] * step;

            }

        }

    }

    private void generatePseudoQueenMoves( int square, boolean normal ) {
        int queenMove;
        int piece = isWhitePiece( position[ square] ) ? W_QUEEN : B_QUEEN;

        for ( int direction = 0; direction < PIECE_VECTORS[ INDEX_QUEEN_DIRECTION].length; direction++ ) {

            int step = 1;
            queenMove = square + PIECE_VECTORS[ INDEX_QUEEN_DIRECTION][ direction] * step;

            while ( !offTheBoard( queenMove ) ) {

                if ( position[ queenMove] == EMPTY_SQUARE && normal ) {

                    moves.add( createMove( piece, square, queenMove, piece, MOVE_TYPE_NORMAL, EMPTY_SQUARE ) );
                    step++;

                } else if ( position[ square] * position[ queenMove] < 0 ) {

                    moves.add( createMove( piece, square, queenMove, piece, MOVE_TYPE_CAPTURE, position[ queenMove] ) );
                    break;

                } else {

                    break;

                }

                queenMove = square + PIECE_VECTORS[ INDEX_QUEEN_DIRECTION][ direction] * step;

            }

        }
    }

    private void generatePseudoRookMoves( int square, boolean normal ) {
        int rookMove;
        int piece = isWhitePiece( position[ square] ) ? W_ROOK : B_ROOK;

        for ( int direction = 0; direction < PIECE_VECTORS[ INDEX_ROOK_DIRECTION].length; direction++ ) {

            int step = 1;
            rookMove = square + PIECE_VECTORS[ INDEX_ROOK_DIRECTION][ direction] * step;

            while ( !offTheBoard( rookMove ) ) {

                if ( position[ rookMove] == EMPTY_SQUARE && normal ) {

                    moves.add( createMove( piece, square, rookMove, piece, MOVE_TYPE_NORMAL, EMPTY_SQUARE ) );
                    step++;

                } else if ( position[ square] * position[ rookMove] < 0 ) {

                    moves.add( createMove( piece, square, rookMove, piece, MOVE_TYPE_CAPTURE, position[ rookMove] ) );
                    break;

                } else {

                    break;

                }

                rookMove = square + PIECE_VECTORS[ INDEX_ROOK_DIRECTION][ direction] * step;

            }

        }
    }

    public void makeMove( Move move ) {

        //switch side to move
        sideToMove *= -1;

        //check reset of halfMoveClock
        if ( ( Math.abs( move.pieceFrom ) == W_PAWN ) || ( ( move.type & MOVE_TYPE_CAPTURE ) == MOVE_TYPE_CAPTURE ) ) {
            halfMoveClock = 0;
        } else {
            halfMoveClock++;
        }

        if ( move.pieceFrom < 0 ) {
            fullMoveNumber++;
        }

        //piece leaves square
        clearSquare( move.from );

        if ( ( move.type & MOVE_TYPE_CAPTURE ) == MOVE_TYPE_CAPTURE ) {

            clearSquare( move.to );

            if ( isWhitePiece( move.capture ) ) {

                valueMaterialWhite -= PIECE_VALUE_MAPPINGS.get( Math.abs( move.capture ) );

            } else {

                valueMaterialBlack -= PIECE_VALUE_MAPPINGS.get( Math.abs( move.capture ) );

            }

        }

        //also covers promotion       
        fillSquare( move.pieceTo, move.to );

        if ( ( move.type & MOVE_TYPE_PROMOTION ) == MOVE_TYPE_PROMOTION ) {

            if ( isWhitePiece( move.pieceFrom ) ) {

                valueMaterialWhite -= VALUE_PAWN;
                valueMaterialWhite += PIECE_VALUE_MAPPINGS.get( Math.abs( move.pieceTo ) );

            } else {

                valueMaterialBlack -= VALUE_PAWN;
                valueMaterialBlack += PIECE_VALUE_MAPPINGS.get( Math.abs( move.pieceTo ) );

            }

        }

        if ( move.pieceFrom == W_KING ) {

            whiteKingPosition = move.to;

        } else if ( move.pieceFrom == B_KING ) {

            blackKingPosition = move.to;

        }

        //update castle flags if king or rook has moved
        
        switch ( move.from ) {
            case 7 : //h1
                castlingAvailability &= ~CAW_KING_SIDE;
                break;
            case 4 : //e1
                castlingAvailability &= ~( CAW_KING_SIDE | CAW_QUEEN_SIDE );
                break;
            case 0 : //a1
                castlingAvailability &= ~CAW_QUEEN_SIDE;
                break;
            case 119 ://h8
                castlingAvailability &= ~CAB_KING_SIDE;
                break;
            case 116 ://e8
                castlingAvailability &= ~( CAB_KING_SIDE | CAB_QUEEN_SIDE );
                break;
            case 112 ://a8
                castlingAvailability &= ~CAB_QUEEN_SIDE;
                break;
        }

        switch ( move.to ) {
            case 7 : //h1
                castlingAvailability &= ~CAW_KING_SIDE;
                break;
            case 4 : //e1
                castlingAvailability &= ~( CAW_KING_SIDE | CAW_QUEEN_SIDE );
                break;
            case 0 : //a1
                castlingAvailability &= ~CAW_QUEEN_SIDE;
                break;
            case 119 : //h8
                castlingAvailability &= ~CAB_KING_SIDE;
                break;
            case 116 : //e8
                castlingAvailability &= ~( CAB_KING_SIDE | CAB_QUEEN_SIDE );
                break;
            case 112 : //a8
                castlingAvailability &= ~CAB_QUEEN_SIDE;
                break;
        }

        //when move is castle, then also move rook 
        if ( ( move.type & MOVE_TYPE_CASTLE ) == MOVE_TYPE_CASTLE ) {

            switch ( move.to ) {

                case 6 : //g1
                    clearSquare( SQUARE_INDEX_MAPPINGS.get( "h1" ) );
                    fillSquare( W_ROOK, SQUARE_INDEX_MAPPINGS.get( "f1" ) );
                    break;

                case 2 : //c1
                    clearSquare( SQUARE_INDEX_MAPPINGS.get( "a1" ) );
                    fillSquare( W_ROOK, SQUARE_INDEX_MAPPINGS.get( "d1" ) );
                    break;

                case 118 : //g8
                    clearSquare( SQUARE_INDEX_MAPPINGS.get( "h8" ) );
                    fillSquare( B_ROOK, SQUARE_INDEX_MAPPINGS.get( "f8" ) );
                    break;

                case 114 : //c8
                    clearSquare( SQUARE_INDEX_MAPPINGS.get( "a8" ) );
                    fillSquare( B_ROOK, SQUARE_INDEX_MAPPINGS.get( "d8" ) );
                    break;

            }

        }

        //set enpassant to -1
        enPassant = -1;

        if ( Math.abs( move.pieceFrom ) == W_PAWN && Math.abs( move.from - move.to ) == 32 ) {

            enPassant = ( move.from + move.to ) / 2;

        }

        if ( move.type == MOVE_TYPE_EP ) {

            if ( isWhitePiece( move.pieceFrom ) ) {

                clearSquare( move.to - 16 );
                valueMaterialBlack -= VALUE_PAWN;

            } else {

                clearSquare( move.to + 16 );
                valueMaterialWhite -= VALUE_PAWN;

            }

        }

    }

    public void unmakeMove( Move move ) {

        sideToMove *= -1;

        halfMoveClock = move.halfMoveClock;

        enPassant = move.enPassant;

        if ( move.pieceFrom < 0 ) {

            fullMoveNumber--;

        }

        clearSquare( move.to );

        fillSquare( move.pieceFrom, move.from );

        if ( move.pieceFrom == W_KING ) {

            whiteKingPosition = move.from;

        } else if ( move.pieceFrom == B_KING ) {

            blackKingPosition = move.from;

        }

        if ( ( move.type & MOVE_TYPE_CAPTURE ) == MOVE_TYPE_CAPTURE ) {

            fillSquare( move.capture, move.to );

            if ( isWhitePiece( move.capture ) ) {

                valueMaterialWhite += PIECE_VALUE_MAPPINGS.get( Math.abs( move.capture ) );

            } else {

                valueMaterialBlack += PIECE_VALUE_MAPPINGS.get( Math.abs( move.capture ) );

            }

        }

        if ( ( move.type & MOVE_TYPE_PROMOTION ) == MOVE_TYPE_PROMOTION ) {

            if ( isWhitePiece( move.pieceFrom ) ) {

                valueMaterialWhite += VALUE_PAWN;
                valueMaterialWhite -= PIECE_VALUE_MAPPINGS.get( Math.abs( move.pieceTo ) );

            } else {

                valueMaterialBlack += VALUE_PAWN;
                valueMaterialBlack -= PIECE_VALUE_MAPPINGS.get( Math.abs( move.pieceTo ) );

            }

        }

        if ( move.type == MOVE_TYPE_CASTLE ) {

            switch ( move.to ) {

                case 6 : //g1
                    clearSquare( SQUARE_INDEX_MAPPINGS.get( "f1" ) );
                    fillSquare( W_ROOK, SQUARE_INDEX_MAPPINGS.get( "h1" ) );
                    break;

                case 2 : //c1
                    clearSquare( SQUARE_INDEX_MAPPINGS.get( "d1" ) );
                    fillSquare( W_ROOK, SQUARE_INDEX_MAPPINGS.get( "a1" ) );
                    break;

                case 118 : //g8
                    clearSquare( SQUARE_INDEX_MAPPINGS.get( "f8" ) );
                    fillSquare( B_ROOK, SQUARE_INDEX_MAPPINGS.get( "h8" ) );
                    break;

                case 114 : //c8
                    clearSquare( SQUARE_INDEX_MAPPINGS.get( "d8" ) );
                    fillSquare( B_ROOK, SQUARE_INDEX_MAPPINGS.get( "a8" ) );
                    break;

            }

        }

        castlingAvailability = move.castleAvailability;

        if ( move.type == MOVE_TYPE_EP ) {

            if ( sideToMove == WHITE ) {

                fillSquare( B_PAWN, move.to - 16 );
                valueMaterialBlack += VALUE_PAWN;

            } else {

                fillSquare( W_PAWN, move.to + 16 );
                valueMaterialWhite += VALUE_PAWN;

            }

        }

    }

    public boolean isAttacked( int byColor, int square ) {

        //pawns
        if ( byColor == WHITE ) {

            if ( !offTheBoard( square + SW ) ) {

                if ( position[ square + SW] == W_PAWN ) {
                    //System.out.println( "white pawn attack" );
                    return true;

                }

            }

            if ( !offTheBoard( square + SE ) ) {

                if ( position[ square + SE] == W_PAWN ) {
                    //System.out.println( "white pawn attack" );
                    return true;

                }

            }

        } else {

            if ( !offTheBoard( square + NW ) ) {

                if ( position[ square + NW] == B_PAWN ) {
                    //System.out.println( "black pawn attack" );
                    //System.out.println( this );
                    return true;

                }

            }

            if ( !offTheBoard( square + NE ) ) {

                if ( position[ square + NE] == B_PAWN ) {
                    //System.out.println( "black pawn attack" );
                    //System.out.println( this );
                    return true;

                }

            }

        }

        //knights
        if ( knightAttack( byColor, square ) ) {
            //System.out.println( "knight attack" );
            return true;

        }

        //kings
        if ( kingAttack( byColor, square ) ) {
            //System.out.println( "king attack" );
            return true;

        }

        //straight
        if ( straightAttack( byColor, square ) ) {
            //System.out.println( "straightattack" );
            return true;

        }

        //diagonal
        if ( diagonalAttack( byColor, square ) ) {
            //System.out.println( "diagonalattack" );
            return true;

        }


        return false;
    }

    public static int getRank( int square ) {
        return ( square >> 4 );
    }

    public static int getFile( int square ) {
        return ( square & 7 );
    }

    private boolean squareIsEmpty( int square ) {
        return position[ square] == EMPTY_SQUARE;
    }

    private void clearSquare( int square ) {

        if ( isWhitePiece( position[square] ) ) {

            locationOfWhitePieces.remove( square );

            switch ( position[ square] ) {
                case W_PAWN:
                    valuePieceSquareWhite -= PIECE_SQUARE_WPAWN[ square ];
                    break;
                case W_KNIGHT:
                    valuePieceSquareWhite -= PIECE_SQUARE_WKNIGHT[ square ];
                    break;
                case W_BISHOP:
                    valuePieceSquareWhite -= PIECE_SQUARE_WBISHOP[ square ];
                    break;
                case W_ROOK:
                    valuePieceSquareWhite -= PIECE_SQUARE_WROOK[ square ];
                    break;
                 case W_KING:
                    valuePieceSquareWhite -= PIECE_SQUARE_WKING[ square ];
                    break;
            }

        } else {

            locationOfBlackPieces.remove( square );

            switch ( position[ square] ) {
                case B_PAWN:
                    valuePieceSquareBlack -= PIECE_SQUARE_BPAWN[ square ];
                    break;
                case B_KNIGHT:
                    valuePieceSquareBlack -= PIECE_SQUARE_BKNIGHT[ square ];
                    break;
                case B_BISHOP:
                    valuePieceSquareBlack -= PIECE_SQUARE_BBISHOP[ square ];
                    break;
                case B_ROOK:
                    valuePieceSquareBlack -= PIECE_SQUARE_BROOK[ square ];
                    break;
                 case B_KING:
                    valuePieceSquareBlack -= PIECE_SQUARE_BKING[ square ];
                    break;
            }

        }

        position[ square] = EMPTY_SQUARE;

    }

    private void fillSquare( int piece, int square ) {

        if ( isWhitePiece( piece ) ) {

            locationOfWhitePieces.put( square, piece );

            switch ( piece ) {
                case W_PAWN:
                    valuePieceSquareWhite += PIECE_SQUARE_WPAWN[ square ];
                    break;
                case W_KNIGHT:
                    valuePieceSquareWhite += PIECE_SQUARE_WKNIGHT[ square ];
                    break;
                case W_BISHOP:
                    valuePieceSquareWhite += PIECE_SQUARE_WBISHOP[ square ];
                    break;
                case W_ROOK:
                    valuePieceSquareWhite += PIECE_SQUARE_WROOK[ square ];
                    break;
                case W_KING:
                    valuePieceSquareWhite += PIECE_SQUARE_WKING[ square ];
                    break;
            }

        } else if ( isBlackPiece( piece ) ) {

            locationOfBlackPieces.put( square, piece );

            switch ( piece ) {
                case B_PAWN:
                    valuePieceSquareBlack += PIECE_SQUARE_BPAWN[ square ];
                    break;
                case B_KNIGHT:
                    valuePieceSquareBlack += PIECE_SQUARE_BKNIGHT[ square ];
                    break;
                case B_BISHOP:
                    valuePieceSquareBlack += PIECE_SQUARE_BBISHOP[ square ];
                    break;
                case B_ROOK:
                    valuePieceSquareBlack += PIECE_SQUARE_BROOK[ square ];
                    break;
                case B_KING:
                    valuePieceSquareBlack += PIECE_SQUARE_BKING[ square ];
                    break;
            }

        }

        position[ square ] = piece;

    }

    private boolean isWhitePiece( int piece ) {
        return piece > 0;
    }

    private boolean isBlackPiece( int piece ) {
        return piece < 0;
    }

    private boolean offTheBoard( int square ) {

        return ( ( square & 0x88 ) > 0 );

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
        int king = ( byColor == WHITE ) ? W_KING : B_KING;

        for ( int direction = 0; direction < 8; direction++ ) {

            kingMove = square + PIECE_VECTORS[ INDEX_KING_DIRECTION][ direction];

            if ( !offTheBoard( kingMove ) ) {

                if ( position[ kingMove] == king ) {

                    return true;

                }

            }

        }

        return false;

    }

    private boolean straightAttack( int byColor, int square ) {
        
        int sliderMove;
        int queen = ( byColor == WHITE ) ? W_QUEEN : B_QUEEN;
        int rook = ( byColor == WHITE ) ? W_ROOK : B_ROOK;
        
        for ( int direction = 0; direction < PIECE_VECTORS[ INDEX_ROOK_DIRECTION].length; direction++ ) {

            int step = 1;
            sliderMove = square + PIECE_VECTORS[ INDEX_ROOK_DIRECTION][ direction] * step;

            while ( !offTheBoard( sliderMove ) ) {

                if ( position[ sliderMove] != EMPTY_SQUARE ) {

                    if ( ( position[ sliderMove] == queen ) || ( position[ sliderMove] == rook ) ) {

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

                    if ( ( position[ sliderMove] == queen ) || ( position[ sliderMove] == bishop ) ) {
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

    Move createMove( int pieceFrom, int from, int to, int pieceTo, int type, int capture ) {

        return new Move( pieceFrom, from, to, pieceTo, type, capture, castlingAvailability, halfMoveClock, enPassant );

    }

    public int evaluate( int depthLeft ) {

        //king can be in check and there are no possible moves, then evaluate to checkmate
        List<Move> pseudoMoves = generatePseudoMoves();

        boolean noLegalMoves = true;
        Move move;
        int numberOfPseudoMoves = pseudoMoves.size();

        for ( int i = 0; i < numberOfPseudoMoves; i++ ) {

            move = pseudoMoves.get( i );

            makeMove( move );

            int kingPosition = ( sideToMove == WHITE ) ? blackKingPosition : whiteKingPosition;

            if ( !isAttacked( sideToMove, kingPosition ) ) {

                noLegalMoves = false;

                unmakeMove( move );

                break;

            }

            unmakeMove( move );

        }

        if ( noLegalMoves ) {

            int kingPosition = ( sideToMove == WHITE ) ? whiteKingPosition : blackKingPosition;

            if ( isAttacked( -sideToMove, kingPosition ) ) {

                return -( VALUE_MATE - ( MAX_DEPTH_SEARCH - depthLeft ) );

            } else {

                //stalemate
                return VALUE_DRAW;

            }
        }

        Set<Map.Entry<Integer, Integer>> entrySetWhite = locationOfWhitePieces.entrySet();
        Set<Map.Entry<Integer, Integer>> entrySetBlack = locationOfBlackPieces.entrySet();

        if ( entrySetWhite.size() <= 2 && entrySetBlack.size() <= 2 ) {

            //two pieces are only left, must be kings, thus a draw
            if ( entrySetWhite.size() == 1 && entrySetBlack.size() == 1 ) {

                return VALUE_DRAW;

            }


            for ( Map.Entry<Integer, Integer> entry : entrySetWhite ) {

                //with only a bishop or knight there is no mate possibility
                if ( entry.getValue() == W_BISHOP || entry.getValue() == W_KNIGHT ) {

                    return VALUE_DRAW;

                }

            }

            for ( Map.Entry<Integer, Integer> entry : entrySetBlack ) {

                //with only a bishop or knight there is no mate possibility
                if ( entry.getValue() == B_BISHOP || entry.getValue() == B_KNIGHT ) {

                    return VALUE_DRAW;

                }

            }

        }

        return ( numberOfPseudoMoves / 10 ) + ( ( valueMaterialWhite + valuePieceSquareWhite ) - ( valueMaterialBlack + valuePieceSquareBlack ) ) * sideToMove;

    }
    
    public List<Move> generateQuiescence(){
        
        return generateMoves( false );
        
    }
    
    public List<Move> generatePseudoMoves(){
        
        return generateMoves( true );
        
    }

    public void makeMoves( String[] moves ) {

        for ( int i = 0; i < moves.length; i++ ) {

            List<Move> listOfMoves = generatePseudoMoves();

            for ( Move move : listOfMoves ) {

                if ( move.toString().equals( moves[i] ) ) {

                    makeMove( move );

                    break;

                }

            }

        }

    }

}
