package main;

import org.junit.jupiter.api.Test;

import static main.lab3.queryBridgeWords;
import static main.lab3.readTextFile;
import static org.junit.jupiter.api.Assertions.*;

class lab3Test {

    @Test
    void queryBridgeWordstest1() {
        readTextFile("D:\\2024spring_lab\\software_engineer\\lab3\\src\\main\\input.txt");

        // 测试用例1：word1和word2之间存在桥接词
        assertEquals("The bridge words from in to hidden are: a", queryBridgeWords("in", "hidden"));
    }
    @Test
    void queryBridgeWordstest2() {
        readTextFile("D:\\2024spring_lab\\software_engineer\\lab3\\src\\main\\input.txt");

        // 测试用例2：word1和word2之间不存在桥接词
        assertEquals("No bridge words from a to every!", queryBridgeWords("a", "every"));
    }
    @Test
    void queryBridgeWordstest3() {
        readTextFile("D:\\2024spring_lab\\software_engineer\\lab3\\src\\main\\input.txt");

        // 测试用例3：word1不存在于图中
        assertEquals("No hello in the graph!", queryBridgeWords("hello", "hidden"));
    }
    @Test
    void queryBridgeWordstest4() {
        readTextFile("D:\\2024spring_lab\\software_engineer\\lab3\\src\\main\\input.txt");

        // 测试用例4：word2不存在于图中
        assertEquals("No hello in the graph!", queryBridgeWords("a", "hello"));
    }
    @Test
    void queryBridgeWordstest5() {
        readTextFile("D:\\2024spring_lab\\software_engineer\\lab3\\src\\main\\input.txt");

        // 测试用例5：word1和word2都不存在于图中
        assertEquals("No hello and adm in the graph!", queryBridgeWords("hello", "adm"));
    }
}