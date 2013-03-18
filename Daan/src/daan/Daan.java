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
public class Daan {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args)
    {
        //Engine engine = new Engine("8/8/1KbB4/Q1p5/2R2n2/r1q1k3/1P2N3/8 w - - 0 1");
        //System.out.println( engine.board.generateMoves() );
        Engine engine = null;
        //engine = new Engine();
        //engine.start_perft();
        //System.out.println( engine.board );
        //engine.search(5);
        
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
                
                engine.search( MAX_DEPTH_SEARCH );
                
            }
        }
    }
}
