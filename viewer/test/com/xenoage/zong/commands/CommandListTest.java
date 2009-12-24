package com.xenoage.zong.commands;

import static org.junit.Assert.*;

import org.junit.Test;

import com.xenoage.zong.commands.test.AddCommand;
import com.xenoage.zong.commands.test.MulCommand;


/**
 * Test cases for a CommandList.
 * 
 * @author Andreas Wenger
 */
public class CommandListTest
{
  
  
  /**
   * Tests executing and undoing a CommandList by
   * executing the commands individually, and then
   * undoing and redoing the list as a whole.
   */
  @Test public void testSingleExecute()
  {
    //((3+4)*2)+5 = 19
    int[] number = {3};
    AddCommand cmd3plus4 = new AddCommand(number, 4);
    cmd3plus4.execute();
    MulCommand cmd7mul2 = new MulCommand(number, 2);
    cmd7mul2.execute();
    AddCommand cmd14plus5 = new AddCommand(number, 5);
    cmd14plus5.execute();
    assertEquals(19, number[0]);
    //create list
    CommandList list = new CommandList("test", true);
    list.addCommand(cmd3plus4);
    list.addCommand(cmd7mul2);
    list.addCommand(cmd14plus5);
    //undo
    list.undo();
    assertEquals(3, number[0]);
    //redo
    list.execute();
    assertEquals(19, number[0]);
  }
  
  
  /**
   * Tests executing and undoing a CommandList by
   * adding the unexecuted commands, and then
   * execuuting, undoing and redoing the list as a whole.
   */
  @Test public void testExecuteAsWhole()
  {
    //((3+4)*2)+5 = 19
    int[] number = {3};
    AddCommand cmd3plus4 = new AddCommand(number, 4);
    MulCommand cmd7mul2 = new MulCommand(number, 2);
    AddCommand cmd14plus5 = new AddCommand(number, 5);
    //create list
    CommandList list = new CommandList("test", true);
    list.addCommand(cmd3plus4);
    list.addCommand(cmd7mul2);
    list.addCommand(cmd14plus5);
    //execute
    list.execute();
    assertEquals(19, number[0]);
    //undo
    list.undo();
    assertEquals(3, number[0]);
    //redo
    list.execute();
    assertEquals(19, number[0]);
  }
  

}
