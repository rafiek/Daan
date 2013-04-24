/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package daan;

import daan.ai.Engine;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;
import static daan.utils.Constants.*;

/**
 *
 * @author Rafiek Mohamedjoesoef <Rafiek.Mohamedjoesoef@hva.nl>
 */
public class Daan{
    
    //7 bits -> K=64, Q=32, R=16, B=8, N=4, WP=2, BP=1
    static {
        
        //0x77 offset is used to divide array in first part negative direction and second part positive direction
                
        for( int i = 1; i < 120; i++){ //for all distances
            
            if( i == 1 ){ //KQR=1, EAST and -EAST=WEST
                
                ATTACK_TABLE[ i + 0x77 ] = 0b1110000;
                ATTACK_TABLE[ -i + 0x77 ] = 0b1110000;
                                
            } else if( ( i > 1 ) && ( i < 8 ) ){ // QR = multiple of 1 up to 8
                
                ATTACK_TABLE[ i + 0x77 ] = 0b0110000;
                ATTACK_TABLE[ -i + 0x77 ] = 0b0110000;
                
            } else if( ( i == 14 ) || ( i == 18 ) || ( i == 31 ) || ( i == 33 ) ){ // 14, 18, 31, 33 == N move
                
                ATTACK_TABLE[ i + 0x77 ] = 0b0000100;
                ATTACK_TABLE[ -i + 0x77 ] = 0b0000100;
                
            } else if( i % 15 == 0 ){ //NW and -NW=SE 
                
                if( i == 15 ){ //KQBP = 15
                    
                    ATTACK_TABLE[ i + 0x77 ] = 0b1101010; 
                    ATTACK_TABLE[ -i + 0x77 ] = 0b1101001;  
                    
                } else { //QB = multiple of 15
                    
                    ATTACK_TABLE[ i + 0x77 ] = 0b0101000;
                    ATTACK_TABLE[ -i + 0x77 ] = 0b0101000;
                    
                }
        
            } else if ( i % 16 == 0 ) { //NORTH and -NORTH=SOUTH

                if ( i == 16 ) { // KQR = 16

                    ATTACK_TABLE[ i + 0x77 ] = 0b1110000;
                    ATTACK_TABLE[ -i + 0x77 ] = 0b1110000;

                } else { // QR = multiple of 16

                    ATTACK_TABLE[ i + 0x77 ] = 0b0110000;
                    ATTACK_TABLE[ -i + 0x77 ] = 0b0110000;

                }

            } else if ( i % 17 == 0 ) {

                if ( i == 17 ) { // KQBP = 17

                    ATTACK_TABLE[ i + 0x77 ] = 0b1101010; 
                    ATTACK_TABLE[ -i + 0x77 ] = 0b1101001; 

                } else { // QB = multiple of 17 

                    ATTACK_TABLE[ i + 0x77 ] = 0b0101000;
                    ATTACK_TABLE[ -i + 0x77 ] = 0b0101000;

                }

            }

        }
        
        //System.out.println( Arrays.toString( ATTACK_TABLE ) );
        
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args)
    {
        
        Engine engine = new Engine("R7/P4k2/8/8/8/8/r7/6K1 w - -");
        //System.out.println( engine.board.generateMoves() );
        //Engine engine = null;
        //engine = new Engine();
        //engine.start_perft();
        //System.out.println( engine.board );
        engine.setWhiteTime( 300000 );
        //engine.setBlackTime( 300000 );
        engine.search( MAX_DEPTH_SEARCH );
        
        //engine = new Engine();
        //engine.start_perft();
        
        InputStreamReader inputStreamReader = new InputStreamReader(System.in);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        String command = "";
        
        while(true){
        
            try{
                command = bufferedReader.readLine();
            }
            catch (IOException ex){
                Logger.getLogger(Daan.class.getName()).log(Level.SEVERE, "unable to read from standard input", ex);
            }
            
            if (command.equals("uci")) {
                System.out.println("id name Daan");
                System.out.println("id author Rafiek Mohamedjoesoef");
                //no options so send "uciok"
                System.out.println("uciok");
            }
            
            if(command.equals("quit")){
                System.out.println("goodbye");
                System.exit(0);
            }          
            
            if(command.equals("isready")){
                if( engine == null ){
                    
                    engine = new Engine();
                    
                }                
                
                System.out.println("readyok");
            }
            
            if( command.equals( "ucinewgame") ){
                engine = new Engine();
            }
            
            if(command.startsWith("position")){
                if(command.indexOf( "fen" ) > -1){
                    
                    String fen = command.substring( command.indexOf( "fen" ) + 4 );
                    engine = new Engine( fen );
                    
                }else if(command.indexOf( "startpos" ) > -1){
                    
                    engine = new Engine();
                    
                }
                
                if( command.indexOf( "moves" ) > -1 ){
                    //engine = new Engine();
                    String moves = command.substring( command.indexOf( "moves" ) + 6 );
                    engine.makeMoves( moves.split(" ") );
                }
                
                
            }
            
            if(command.indexOf("go") > -1){
                
                long wtime = 0;
                long btime = 0;
                
                 if( command.indexOf( "movetime" ) > -1){
                    String time = command.substring( command.indexOf( "movetime" ) + "movetime".length()+1 );
                    long moveTime = Long.parseLong( time.substring( 0, time.indexOf( " " ) ) );
                    
                    engine.setMaxThinkingTime( moveTime );
                    
                }                
                
                if ( command.indexOf( "wtime" ) > -1 ) {
                    String time = command.substring( command.indexOf( "wtime" ) + "wtime".length() + 1 );
                    wtime = Long.parseLong( time.substring( 0, time.indexOf( " " ) ) );
                    
                    if( engine.board.sideToMove == WHITE ){
                        
                        engine.setWhiteTime( wtime );                        
                        
                    }
                    
                }
                
                if ( command.indexOf( "btime" ) > -1 ) {
                    String time = command.substring( command.indexOf( "btime" ) + "btime".length() + 1 );
                    if ( time.indexOf( " " ) > -1 ) {
                        btime = Long.parseLong( time.substring( 0, time.indexOf( " " ) ) );
                    } else {
                        btime = Long.parseLong( time );
                    }

                    if( engine.board.sideToMove == BLACK ){
                        
                        engine.setBlackTime( btime );
                        
                    }
                                       
                }
                
                engine.search( MAX_DEPTH_SEARCH );
                
            }
        }
    }
}
