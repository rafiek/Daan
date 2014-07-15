package main;

import ai.Engine;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;

import static utils.Constants.*;

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
        
    }


    /**
     * @param args the command line arguments
     */
    public static void main(String[] args)
    {
        
        Engine engine = null;
        
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
                System.out.println("id name main.Daan");
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
                if(command.contains("fen")){
                    
                    String fen = command.substring( command.indexOf( "fen" ) + 4 );
                    engine = new Engine( fen );
                    
                }else if(command.contains("startpos")){
                    
                    engine = new Engine();
                    
                }
                
                if(command.contains("moves")){
                    //engine = new Engine();
                    String moves = command.substring( command.indexOf( "moves" ) + 6 );
                    engine.makeMoves( moves.split(" ") );
                }
                
                
            }
            
            if(command.contains("go")){
                
                long wtime = 0;
                long btime = 0;
                
                 if(command.contains("movetime")){
                    String time = command.substring( command.indexOf( "movetime" ) + "movetime".length()+1 );
                    long moveTime = Long.parseLong( time.substring( 0, time.indexOf( " " ) ) );
                    
                    engine.setMaxThinkingTime( moveTime );
                    
                }                
                
                if (command.contains("wtime")) {
                    String time = command.substring( command.indexOf( "wtime" ) + "wtime".length() + 1 );
                    wtime = Long.parseLong( time.substring( 0, time.indexOf( " " ) ) );
                    
                    if( engine.getBoard().sideToMove == WHITE ){
                        
                        engine.setWhiteTime( wtime );                        
                        
                    }
                    
                }
                
                if (command.contains("btime")) {
                    String time = command.substring( command.indexOf( "btime" ) + "btime".length() + 1 );
                    if (time.contains(" ")) {
                        btime = Long.parseLong( time.substring( 0, time.indexOf( " " ) ) );
                    } else {
                        btime = Long.parseLong( time );
                    }

                    if( engine.getBoard().sideToMove == BLACK ){
                        
                        engine.setBlackTime( btime );
                        
                    }
                                       
                }
                
                engine.search();
                
            }
        }
    }
}
