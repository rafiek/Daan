/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package daan;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;

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
        Engine engine = new Engine("5rk1/p3qppp/8/3R4/8/1P4P1/PQ5P/7K b - - 0 1");
        System.out.println( engine.board );
        engine.search(5);
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
                System.out.println("\t id name Daan");
                System.out.println("\t id author Rafiek Mohamedjoesoef");
                //no options so send "uciok"
                System.out.println("\t uciok");
            }
            
            if(command.equals("quit")){
                System.out.println("\t goodbye");
                System.exit(0);
            }          
            
            if(command.equals("isready")){
                
                System.out.println("\t readyok");
            }
            
            if(command.startsWith("position")){
                
            }
            
            if(command.equals("go")){
                
            }
        }
    }
}
