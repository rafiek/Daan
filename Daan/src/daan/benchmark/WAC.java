/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package daan.benchmark;

import daan.ai.Engine;
import daan.representation.Move;
import daan.utils.Constants;
import daan.utils.Utils;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Rafiek Mohamedjoesoef <Rafiek.Mohamedjoesoef@hva.nl>
 * created 24-mrt-2013, 10:08:30
 */
public class WAC implements Benchmark{
    
    List<String> positions = new ArrayList<>();
    
    public static void main( String[] args ){
        
        WAC wac = new WAC();
        
        wac.loadPositions();
        wac.benchmark();
        
    }

    @Override
    public void loadPositions() {
        
        FileInputStream wacFile = null;
        
        try {
            
            wacFile = new FileInputStream( "WAC_test_positions.txt" );
            
        }
        catch ( FileNotFoundException ex ) {
            
            Logger.getLogger( WAC.class.getName() ).log( Level.SEVERE, null, ex );
            
        }
        
        InputStreamReader inputStreamReader = new InputStreamReader( wacFile );
        
        try (BufferedReader bufferedReader = new BufferedReader( inputStreamReader )){
            
            String line;
            
            while( (line = bufferedReader.readLine()) != null ){
                
                positions.add( line );
            
            }
            
        }
        
        catch ( IOException ex ) {
            
            Logger.getLogger( WAC.class.getName() ).log( Level.SEVERE, null, ex );
            
        }
        
    }

    @Override
    public void benchmark() {
        
        Iterator iterator = positions.iterator();
        Engine engine;
        int i=1;       
        FileWriter fstream = null;
        List<Integer> passed = new ArrayList<>();
        List<Integer> failed = new ArrayList<>();
        long totalTime = System.nanoTime();
        DecimalFormat twoDForm = new DecimalFormat("#.##");

        try {
            fstream = new FileWriter( "out.txt" );
        }
        catch ( IOException ex ) {
            System.out.println( ex );
        }
        
        BufferedWriter out = new BufferedWriter( fstream );
        
        while( iterator.hasNext() ){
            
            System.out.println( "--------------------NUMBER "+( i )+"--------------------" );            
            String epd = (String) iterator.next();
            int endIndex;

            if ( epd.indexOf( "am" ) != -1 ) {

                endIndex = epd.indexOf( "am" );

            } else {

                endIndex = epd.indexOf( "bm" );

            }
            
            engine = new Engine( epd.substring( 0, endIndex - 1 ) );
            long time = System.nanoTime();
            Move bestMove = engine.search( Constants.MAX_DEPTH_SEARCH, 300000, 300000 );
            
            try {
                
                out.write( Utils.getKeyByValue( Constants.PIECE_CHARACTER_MAPPINGS, bestMove.pieceFrom ) + "" + bestMove.getLine() );
                out.newLine();
                out.write( epd );
                out.newLine();
                out.write( "time: " + twoDForm.format( ( System.nanoTime() - time ) / Math.pow( 10, 9 ) ) + " nodes: " + engine.visitedNodes );
                out.newLine();
                
                int stopIndex;
                if( epd.indexOf( '+' ) > 0 ){
                    stopIndex = epd.indexOf( '+' );
                } else {
                    stopIndex = epd.indexOf( ';' );                    
                }
                
                String epdBestMove = epd.substring( endIndex + 3, stopIndex );
                
                if(epdBestMove.equals( bestMove.toShortAlgebraicNotation())){
                    out.write( "PASS");
                    passed.add( i );
                }else{
                    out.write( "FAIL");
                    failed.add( i );
                }
                
                out.newLine();
                out.newLine();
                out.flush();
                
            }
            catch ( IOException ex ) {
                System.out.println( ex );
            }
            i++;
        }
        
        try {
            
            out.write( "passed: " + passed.size() );
            out.newLine();
            out.write( "failed: " + failed.size() );
            out.newLine();
            out.write( "total time: " + twoDForm.format( ( System.nanoTime() - totalTime ) / Math.pow( 10, 9 ) ) );
            
        }
        catch ( IOException ex ) {
            Logger.getLogger( WAC.class.getName() ).log( Level.SEVERE, null, ex );
        }
        
        
        
        try {
            out.close();
        }
        catch ( IOException ex ) {
            System.out.println( ex );
        }
        
        
                
    }

}
