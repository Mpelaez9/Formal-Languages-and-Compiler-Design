package hashTableTests;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import hashTable.MyHashTable;

/*
 * AUTHOR: Marina Peláez Martínez
 * 2023
 * JUNIT test class for testing MyHashTable class. 
 */
class MyHashTableTest {

	@Test
    public void testInsertElements() {
        MyHashTable table = new MyHashTable(10);

        assertEquals(0, table.insertElements("abc"));
        assertEquals(0, table.insertElements("def"));
        assertEquals(-1, table.insertElements("abc")); // Duplicate element
        assertEquals(-2, table.insertElements(null)); // Null element
    }

    @Test
    public void testRemoveElements() {
        MyHashTable table = new MyHashTable(10);

        table.insertElements("abc");
        table.insertElements("def");

        assertEquals(0, table.removeElements("abc"));
        assertEquals(-1, table.removeElements("xyz")); // Element not found
        assertEquals(-2, table.removeElements(null)); // Null element
    }

    @Test
    public void testFindElements() {
        MyHashTable table = new MyHashTable(10);

        table.insertElements("abc");
        table.insertElements("def");

        assertEquals(5, table.findElements("abc")); // Returns the position where it is.
        assertEquals(3, table.findElements("def"));
        assertEquals(-1, table.findElements("xyz")); // Element not found
    }
    
    @Test
    public void testGetSize() {
        // Since 10 is a odd number, the Hash Table will get a 11 size because is the next prime number

        MyHashTable table = new MyHashTable(10);

        assertEquals(11, table.getSize());
    }
    
    
    @Test
    public void testGetNumberOfElements() {
        MyHashTable table = new MyHashTable(10);

        table.insertElements("abc");
        table.insertElements("def");

        assertEquals(2, table.getNumberOfElements());
    }
    
    @Test
    public void testToStringEmpty() {
        MyHashTable table = new MyHashTable(10);

        String expected = "-;-;-;-;-;-;-;-;-;-;-;[Size: 11 Num.Elems.: 0]";
        assertEquals(expected, table.toString());
    }


    @Test
    public void testToString() {
        MyHashTable table = new MyHashTable(11);

        table.insertElements("abc");
        table.insertElements("def");

        String expected = "-;-;-;def;-;abc;-;-;-;-;-;[Size: 11 Num.Elems.: 2]";
        assertEquals(expected, table.toString());
    }
    
   
    
    


}
