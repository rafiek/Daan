package utils;

import representation.Move;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Rafiek Mohamedjoesoef <Rafiek.Mohamedjoesoef@hva.nl>
 */
public interface Constants {
    
    public static final int WHITE   = 1;
    public static final int BLACK   = -1;
    
    public static final int EMPTY_SQUARE = 0;
    
    public static final int VALUE_QUEEN     = 900;
    public static final int VALUE_ROOK      = 500;
    public static final int VALUE_BISHOP    = 330;
    public static final int VALUE_KNIGHT    = 320;
    public static final int VALUE_PAWN      = 100;
    
    public static final int VALUE_MATE      = 1000000;
    public static final int VALUE_DRAW      = 0;
    
    public static final int W_KING      = 64;
    public static final int W_QUEEN     = 32;
    public static final int W_ROOK      = 16;
    public static final int W_BISHOP    = 8;
    public static final int W_KNIGHT    = 4;
    public static final int W_PAWN      = 2;
    
    public static final int B_KING      = -64;
    public static final int B_QUEEN     = -32; 
    public static final int B_ROOK      = -16;
    public static final int B_BISHOP    = -8;
    public static final int B_KNIGHT    = -4;
    public static final int B_PAWN      = -1;    
    
    public static final int CAW_KING_SIDE     = 1; //castling availability white king side
    public static final int CAW_QUEEN_SIDE    = 2;
    public static final int CAB_KING_SIDE     = 4;
    public static final int CAB_QUEEN_SIDE    = 8;
    
    public static final int NORTH   = 16;
    public static final int NN      = NORTH+NORTH;
    public static final int SOUTH   = -16;
    public static final int SS      = SOUTH+SOUTH;
    public static final int EAST    = 1;
    public static final int WEST    = -1;
    public static final int NE      = 17;
    public static final int SW      = -17;
    public static final int NW      = 15;
    public static final int SE      = -15;   
    
    public final int ATTACK_TABLE_SIZE = 239;
    public static int[] ATTACK_TABLE = new int[ ATTACK_TABLE_SIZE ];
    
    public static final int INDEX_KING_DIRECTION = 0;
    public static final int INDEX_QUEEN_DIRECTION = 1;
    public static final int INDEX_ROOK_DIRECTION = 2;
    public static final int INDEX_BISHOP_DIRECTION = 3;
    public static final int INDEX_KNIGHT_DIRECTION = 4;

    public final static boolean[] SLIDE = { false, true, true, true, false }; //K, Q, R, B, N  

    public final static int[][] PIECE_VECTORS = {//K, Q, R, B, N
        { SW, SOUTH, SE, WEST, EAST, NW, NORTH, NE },
        { SW, SOUTH, SE, WEST, EAST, NW, NORTH, NE },
        { SOUTH, WEST, EAST, NORTH },
        { SW, SE, NW, NE },
        { -33, -31, -18, -14, 14, 18, 31, 33 }
    };
   
    public static final String FEN_START_POSITION = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";
    
    public static final Map<Character, Integer> PIECE_CHARACTER_MAPPINGS = 
            Collections.unmodifiableMap(
                    new HashMap<Character, Integer>() {
                        {
                            put('r', B_ROOK);
                            put('n', B_KNIGHT);
                            put('b', B_BISHOP);
                            put('q', B_QUEEN);
                            put('k', B_KING);
                            put('p', B_PAWN);
                            put('P', W_PAWN);
                            put('R', W_ROOK);
                            put('N', W_KNIGHT);
                            put('B', W_BISHOP);
                            put('Q', W_QUEEN);
                            put('K', W_KING);                            
                        }

                    });
    
    public static final Map<String, Integer> SQUARE_INDEX_MAPPINGS = 
            Collections.unmodifiableMap(
                    new HashMap<String, Integer>() {
                        {
                            put("a1", 0);   put("b1", 1);   put("c1", 2);   put("d1", 3);   put("e1", 4);   put("f1", 5);   put("g1", 6);   put("h1", 7);                
                            put("a2", 16);  put("b2", 17);  put("c2", 18);  put("d2", 19);  put("e2", 20);  put("f2", 21);  put("g2", 22);  put("h2", 23);                
                            put("a3", 32);  put("b3", 33);  put("c3", 34);  put("d3", 35);  put("e3", 36);  put("f3", 37);  put("g3", 38);  put("h3", 39);                
                            put("a4", 48);  put("b4", 49);  put("c4", 50);  put("d4", 51);  put("e4", 52);  put("f4", 53);  put("g4", 54);  put("h4", 55);                
                            put("a5", 64);  put("b5", 65);  put("c5", 66);  put("d5", 67);  put("e5", 68);  put("f5", 69);  put("g5", 70);  put("h5", 71);                
                            put("a6", 80);  put("b6", 81);  put("c6", 82);  put("d6", 83);  put("e6", 84);  put("f6", 85);  put("g6", 86);  put("h6", 87);                
                            put("a7", 96);  put("b7", 97);  put("c7", 98);  put("d7", 99);  put("e7", 100); put("f7", 101); put("g7", 102); put("h7", 103);                
                            put("a8", 112); put("b8", 113); put("c8", 114); put("d8", 115); put("e8", 116); put("f8", 117); put("g8", 118); put("h8", 119);                
                                            
                        }

                    });
    
    public static final Map<Integer, Integer> PIECE_VALUE_MAPPINGS = 
            Collections.unmodifiableMap(
                    new HashMap<Integer, Integer>() {
                        {
                            
                            put( W_QUEEN    , VALUE_QUEEN     );
                            put( W_ROOK     , VALUE_ROOK      );
                            put( W_BISHOP   , VALUE_BISHOP    );
                            put( W_KNIGHT   , VALUE_KNIGHT    );
                            put( W_PAWN     , VALUE_PAWN      );
                            put( -B_PAWN    , VALUE_PAWN      );
                            put( W_KING     , 0             );
                           
                        }

                    });
    
    public static final int MOVE_TYPE_NORMAL    = 0;
    public static final int MOVE_TYPE_CAPTURE   = 1;
    public static final int MOVE_TYPE_EP        = 2;
    public static final int MOVE_TYPE_CASTLE    = 4;
    public static final int MOVE_TYPE_PROMOTION = 8;

    public static final int START_VALUE_ALPHA   = -10000000;
    public static final int START_VALUE_BETA    = 10000000;
    
    public static final int MAX_DEPTH_SEARCH    = 64;
    public static int ASPIRATION = 50;
    
    public static final Comparator<Move> MVV_LVA_SCORE =
            new Comparator<Move>(){
                
                @Override
                public int compare( Move m1, Move m2 ){
                    
                    return m2.calculateMVVLVAScore() - m1.calculateMVVLVAScore();
                }
                
            };
    
    public final int[] PIECE_SQUARE_BPAWN =
    {
     0,  0,  0,  0,  0,  0,  0,  0, 0, 0, 0, 0, 0, 0, 0, 0,
    50, 50, 50, 50, 50, 50, 50, 50, 0, 0, 0, 0, 0, 0, 0, 0,
    10, 10, 20, 30, 30, 20, 10, 10, 0, 0, 0, 0, 0, 0, 0, 0,
     5,  5, 10, 25, 25, 10,  5,  5, 0, 0, 0, 0, 0, 0, 0, 0,
     0,  0,  0, 20, 20,  0,  0,  0, 0, 0, 0, 0, 0, 0, 0, 0,
     5, -5,-10,  0,  0,-10, -5,  5, 0, 0, 0, 0, 0, 0, 0, 0,
     5, 10, 10,-20,-20, 10, 10,  5, 0, 0, 0, 0, 0, 0, 0, 0,
     0,  0,  0,  0,  0,  0,  0,  0, 0, 0, 0, 0, 0, 0, 0, 0
    };
    
    public final int[] PIECE_SQUARE_WPAWN =
    {
     0,  0,  0,  0,  0,  0,  0,  0, 0, 0, 0, 0, 0, 0, 0, 0,
     5, 10, 10,-20,-20, 10, 10,  5, 0, 0, 0, 0, 0, 0, 0, 0,
     5, -5,-10,  0,  0,-10, -5,  5, 0, 0, 0, 0, 0, 0, 0, 0,
     0,  0,  0, 20, 20,  0,  0,  0, 0, 0, 0, 0, 0, 0, 0, 0,
     5,  5, 10, 25, 25, 10,  5,  5, 0, 0, 0, 0, 0, 0, 0, 0,
    10, 10, 20, 30, 30, 20, 10, 10, 0, 0, 0, 0, 0, 0, 0, 0,
    50, 50, 50, 50, 50, 50, 50, 50, 0, 0, 0, 0, 0, 0, 0, 0,
     0,  0,  0,  0,  0,  0,  0,  0, 0, 0, 0, 0, 0, 0, 0, 0
    };
    
    public final int[] PIECE_SQUARE_BKNIGHT =
    {
        -50,-40,-30,-30,-30,-30,-40,-50, 0, 0, 0, 0, 0, 0, 0, 0,
        -40,-20,  0,  0,  0,  0,-20,-40, 0, 0, 0, 0, 0, 0, 0, 0,
        -30,  0, 10, 15, 15, 10,  0,-30, 0, 0, 0, 0, 0, 0, 0, 0,
        -30,  5, 15, 20, 20, 15,  5,-30, 0, 0, 0, 0, 0, 0, 0, 0,
        -30,  0, 15, 20, 20, 15,  0,-30, 0, 0, 0, 0, 0, 0, 0, 0,
        -30,  5, 10, 15, 15, 10,  5,-30, 0, 0, 0, 0, 0, 0, 0, 0,
        -40,-20,  0,  5,  5,  0,-20,-40, 0, 0, 0, 0, 0, 0, 0, 0,
        -50,-40,-30,-30,-30,-30,-40,-50, 0, 0, 0, 0, 0, 0, 0, 0    
    };
    
    public final int[] PIECE_SQUARE_WKNIGHT =
    {
        -50,-40,-30,-30,-30,-30,-40,-50, 0, 0, 0, 0, 0, 0, 0, 0,
        -40,-20,  0,  5,  5,  0,-20,-40, 0, 0, 0, 0, 0, 0, 0, 0,
        -30,  5, 10, 15, 15, 10,  5,-30, 0, 0, 0, 0, 0, 0, 0, 0,
        -30,  0, 15, 20, 20, 15,  0,-30, 0, 0, 0, 0, 0, 0, 0, 0,
        -30,  5, 15, 20, 20, 15,  5,-30, 0, 0, 0, 0, 0, 0, 0, 0,
        -30,  0, 10, 15, 15, 10,  0,-30, 0, 0, 0, 0, 0, 0, 0, 0,
        -40,-20,  0,  0,  0,  0,-20,-40, 0, 0, 0, 0, 0, 0, 0, 0,
        -50,-40,-30,-30,-30,-30,-40,-50, 0, 0, 0, 0, 0, 0, 0, 0    
    };
    
    public final int[] PIECE_SQUARE_BBISHOP =
    {
        -20,-10,-10,-10,-10,-10,-10,-20, 0, 0, 0, 0, 0, 0, 0, 0,
        -10,  0,  0,  0,  0,  0,  0,-10, 0, 0, 0, 0, 0, 0, 0, 0,
        -10,  0,  5, 10, 10,  5,  0,-10, 0, 0, 0, 0, 0, 0, 0, 0,
        -10,  5,  5, 10, 10,  5,  5,-10, 0, 0, 0, 0, 0, 0, 0, 0,
        -10,  0, 10, 10, 10, 10,  0,-10, 0, 0, 0, 0, 0, 0, 0, 0,
        -10, 10, 10, 10, 10, 10, 10,-10, 0, 0, 0, 0, 0, 0, 0, 0,
        -10,  5,  0,  0,  0,  0,  5,-10, 0, 0, 0, 0, 0, 0, 0, 0,
        -20,-10,-10,-10,-10,-10,-10,-20, 0, 0, 0, 0, 0, 0, 0, 0
    };
    
    public final int[] PIECE_SQUARE_WBISHOP =
    {
        -20,-10,-10,-10,-10,-10,-10,-20, 0, 0, 0, 0, 0, 0, 0, 0,
        -10,  5,  0,  0,  0,  0,  5,-10, 0, 0, 0, 0, 0, 0, 0, 0,
        -10, 10, 10, 10, 10, 10, 10,-10, 0, 0, 0, 0, 0, 0, 0, 0,
        -10,  0, 10, 10, 10, 10,  0,-10, 0, 0, 0, 0, 0, 0, 0, 0,
        -10,  5,  5, 10, 10,  5,  5,-10, 0, 0, 0, 0, 0, 0, 0, 0,
        -10,  0,  5, 10, 10,  5,  0,-10, 0, 0, 0, 0, 0, 0, 0, 0,
        -10,  0,  0,  0,  0,  0,  0,-10, 0, 0, 0, 0, 0, 0, 0, 0,
        -20,-10,-10,-10,-10,-10,-10,-20, 0, 0, 0, 0, 0, 0, 0, 0,     
    };
    
    public final int[] PIECE_SQUARE_BROOK =
    {
        0,  0,  0,  0,  0,  0,  0,  0, 0, 0, 0, 0, 0, 0, 0, 0,     
        5, 10, 10, 10, 10, 10, 10,  5, 0, 0, 0, 0, 0, 0, 0, 0,     
       -5,  0,  0,  0,  0,  0,  0, -5, 0, 0, 0, 0, 0, 0, 0, 0,     
       -5,  0,  0,  0,  0,  0,  0, -5, 0, 0, 0, 0, 0, 0, 0, 0,     
       -5,  0,  0,  0,  0,  0,  0, -5, 0, 0, 0, 0, 0, 0, 0, 0,     
       -5,  0,  0,  0,  0,  0,  0, -5, 0, 0, 0, 0, 0, 0, 0, 0,     
       -5,  0,  0,  0,  0,  0,  0, -5, 0, 0, 0, 0, 0, 0, 0, 0,     
        0,  0,  0,  5,  5,  0,  0,  0, 0, 0, 0, 0, 0, 0, 0, 0
    };
    
    public final int[] PIECE_SQUARE_WROOK =
    {
        0, 0, 0, 5, 5, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
       -5, 0, 0, 0, 0, 0, 0,-5, 0, 0, 0, 0, 0, 0, 0, 0,
       -5, 0, 0, 0, 0, 0, 0,-5, 0, 0, 0, 0, 0, 0, 0, 0,
       -5, 0, 0, 0, 0, 0, 0,-5, 0, 0, 0, 0, 0, 0, 0, 0,
       -5, 0, 0, 0, 0, 0, 0,-5, 0, 0, 0, 0, 0, 0, 0, 0,
       -5, 0, 0, 0, 0, 0, 0,-5, 0, 0, 0, 0, 0, 0, 0, 0,
        5,10,10,10,10,10,10,10, 5, 0, 0, 0, 0, 0, 0, 0,
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0     
    };
    
    public final int[] PIECE_SQUARE_BKING = 
    {
        -30,-40,-40,-50,-50,-40,-40,-30, 0, 0, 0, 0, 0, 0, 0, 0,
        -30,-40,-40,-50,-50,-40,-40,-30, 0, 0, 0, 0, 0, 0, 0, 0,
        -30,-40,-40,-50,-50,-40,-40,-30, 0, 0, 0, 0, 0, 0, 0, 0,
        -30,-40,-40,-50,-50,-40,-40,-30, 0, 0, 0, 0, 0, 0, 0, 0,
        -20,-30,-30,-40,-40,-30,-30,-20, 0, 0, 0, 0, 0, 0, 0, 0,
        -10,-20,-20,-20,-20,-20,-20,-10, 0, 0, 0, 0, 0, 0, 0, 0,
         20, 20,  0,  0,  0,  0, 20, 20, 0, 0, 0, 0, 0, 0, 0, 0,
         20, 30, 10,  0,  0, 10, 30, 20, 0, 0, 0, 0, 0, 0, 0, 0
    };
    
    public final int[] PIECE_SQUARE_WKING = 
    {
         20, 30, 10,  0,  0, 10, 30, 20, 0, 0, 0, 0, 0, 0, 0, 0,
         20, 20,  0,  0,  0,  0, 20, 20, 0, 0, 0, 0, 0, 0, 0, 0,
        -10,-20,-20,-20,-20,-20,-20,-10, 0, 0, 0, 0, 0, 0, 0, 0,
        -20,-30,-30,-40,-40,-30,-30,-20, 0, 0, 0, 0, 0, 0, 0, 0,
        -30,-40,-40,-50,-50,-40,-40,-30, 0, 0, 0, 0, 0, 0, 0, 0,
        -30,-40,-40,-50,-50,-40,-40,-30, 0, 0, 0, 0, 0, 0, 0, 0,
        -30,-40,-40,-50,-50,-40,-40,-30, 0, 0, 0, 0, 0, 0, 0, 0,
        -30,-40,-40,-50,-50,-40,-40,-30, 0, 0, 0, 0, 0, 0, 0, 0
    };
    
    public final int[] PIECE_SQUARE_BKING_ENDGAME = 
    {
        -50,-40,-30,-20,-20,-30,-40,-50,0, 0, 0, 0, 0, 0, 0, 0,
        -30,-20,-10,  0,  0,-10,-20,-30,0, 0, 0, 0, 0, 0, 0, 0,
        -30,-10, 20, 30, 30, 20,-10,-30,0, 0, 0, 0, 0, 0, 0, 0,
        -30,-10, 30, 40, 40, 30,-10,-30,0, 0, 0, 0, 0, 0, 0, 0,
        -30,-10, 30, 40, 40, 30,-10,-30,0, 0, 0, 0, 0, 0, 0, 0,
        -30,-10, 20, 30, 30, 20,-10,-30,0, 0, 0, 0, 0, 0, 0, 0,
        -30,-30,  0,  0,  0,  0,-30,-30,0, 0, 0, 0, 0, 0, 0, 0,
        -50,-30,-30,-30,-30,-30,-30,-50,0, 0, 0, 0, 0, 0, 0, 0
    };
    
    public final int[] PIECE_SQUARE_WKING_ENDGAME = 
    {
        -50,-30,-30,-30,-30,-30,-30,-50,0, 0, 0, 0, 0, 0, 0, 0,
        -30,-30,  0,  0,  0,  0,-30,-30,0, 0, 0, 0, 0, 0, 0, 0,
        -30,-10, 20, 30, 30, 20,-10,-30,0, 0, 0, 0, 0, 0, 0, 0,
        -30,-10, 30, 40, 40, 30,-10,-30,0, 0, 0, 0, 0, 0, 0, 0,
        -30,-10, 30, 40, 40, 30,-10,-30,0, 0, 0, 0, 0, 0, 0, 0,
        -30,-10, 20, 30, 30, 20,-10,-30,0, 0, 0, 0, 0, 0, 0, 0,
        -30,-20,-10,  0,  0,-10,-20,-30,0, 0, 0, 0, 0, 0, 0, 0,
        -50,-40,-30,-20,-20,-30,-40,-50,0, 0, 0, 0, 0, 0, 0, 0
    };
    
    public final int[] PIECE_SQUARE_BQUEEN = 
    {
        -20,-10,-10, -5, -5,-10,-10,-20, 0, 0, 0, 0, 0, 0, 0, 0,
        -10,  0,  0,  0,  0,  0,  0,-10, 0, 0, 0, 0, 0, 0, 0, 0,
        -10,  0,  5,  5,  5,  5,  0,-10, 0, 0, 0, 0, 0, 0, 0, 0,
         -5,  0,  5,  5,  5,  5,  0, -5, 0, 0, 0, 0, 0, 0, 0, 0,
          0,  0,  5,  5,  5,  5,  0, -5, 0, 0, 0, 0, 0, 0, 0, 0,
        -10,  5,  5,  5,  5,  5,  0,-10, 0, 0, 0, 0, 0, 0, 0, 0,
        -10,  0,  5,  0,  0,  0,  0,-10, 0, 0, 0, 0, 0, 0, 0, 0,
        -20,-10,-10, -5, -5,-10,-10,-20, 0, 0, 0, 0, 0, 0, 0, 0
    };
    
    public final int[] PIECE_SQUARE_WQUEEN = 
    {
         -20, -10,-10, -5, -5,-10,-10,-20, 0, 0, 0, 0, 0, 0, 0, 0,
         -10,   0,  5,  0,  0,  0,  0,-10, 0, 0, 0, 0, 0, 0, 0, 0,
         -10,   5,  5,  5,  5,  5,  0,-10, 0, 0, 0, 0, 0, 0, 0, 0,
           0,   0,  5,  5,  5,  5,  0, -5, 0, 0, 0, 0, 0, 0, 0, 0,
          -5,   0,  5,  5,  5,  5,  0, -5, 0, 0, 0, 0, 0, 0, 0, 0,
         -10,   0,  5,  5,  5,  5,  0,-10, 0, 0, 0, 0, 0, 0, 0, 0,
         -10,   0,  0,  0,  0,  0,  0,-10, 0, 0, 0, 0, 0, 0, 0, 0,
         -20, -10,-10, -5, -5,-10,-10,-20, 0, 0, 0, 0, 0, 0, 0, 0,
    };
        
}
