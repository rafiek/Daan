package benchmark;

import ai.Engine;
import representation.Move;
import utils.Constants;
import utils.Utils;

import java.io.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import static utils.Constants.ATTACK_TABLE;

/**
 *
 * @author Rafiek Mohamedjoesoef <Rafiek.Mohamedjoesoef@hva.nl>
 * created 24-mrt-2013, 10:08:30
 */
public class WAC implements Benchmark{
    
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
    
    private final List<String> positions = new ArrayList<>();
    
    public static void main( String[] args ){
        
        WAC wac = new WAC();
        
        wac.loadPositions();
        wac.benchmark();
        
    }

    private void loadPositions() {
        
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

    private void benchmark() {
        
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

            if (epd.contains("am")) {

                endIndex = epd.indexOf( "am" );

            } else {

                endIndex = epd.indexOf( "bm" );

            }
            
            engine = new Engine( epd.substring( 0, endIndex - 1 ) );
            //System.out.println( engine.board );
            long time = System.nanoTime();
            
            if( engine.getBoard().sideToMove == Constants.WHITE ){
                engine.setWhiteTime( 300000 );
            } else {
                engine.setBlackTime( 300000 );
            } 
                
            engine.search();
            Move bestMove = engine.getBestMove();
            
            try {
                
                out.write( Utils.getKeyByValue( Constants.PIECE_CHARACTER_MAPPINGS, bestMove.pieceFrom ) + "" + bestMove.getLine() );
                out.newLine();
                out.write( epd );
                out.newLine();
                out.write( "time: " + twoDForm.format( ( System.nanoTime() - time ) / Math.pow( 10, 9 ) ) + " nodes: " + engine.getVisitedNodes() );
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
