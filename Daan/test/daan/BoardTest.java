/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package daan;

import daan.representation.Board;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author Rafiek Mohamedjoesoef <Rafiek.Mohamedjoesoef@hva.nl>
 */
public class BoardTest {
    
    public BoardTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    @Test
    public void testPromotion() {
        Board b = new Board("8/1P6/8/8/8/8/8/8 w - - 0 1");
        // TODO review the generated test code and remove the default call to fail.
        System.out.println(b.generateMoves());
    }
}
