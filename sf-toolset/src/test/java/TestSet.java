import org.junit.Test;

import java.time.Instant;
import java.util.LinkedList;

import static org.junit.Assert.assertEquals;

public class TestSet {
    @Test
    public void time() {
        //ISO format check
        String s = "2019-08-13T06:57:12.345Z";
        Instant i1 = Instant.parse(s);
        assertEquals(s, i1.toString());
    }

    @Test
    public void stack() {
        LinkedList<String> stack = new LinkedList<>();
        stack.push("1");
        stack.push("2");
        stack.push("3");
        stack.push("4");
        assertEquals(stack.size(), 4);
        assertEquals("4", stack.get(0));
        assertEquals("1", stack.get(stack.size() - 1));
        assertEquals("4", stack.pop());
        assertEquals("3", stack.get(0));
        stack.push("5");
        assertEquals("5", stack.get(0));
    }

}
