package main;

import org.junit.jupiter.api.Test;

import static main.lab3.*;
import static org.junit.jupiter.api.Assertions.*;

class lab3Test_1 {

    @Test
    void queryBridgeWordstest_1() {
        readTextFile("D:\\2024spring_lab\\software_engineer\\lab3\\src\\main\\input.txt");

        // 路径1：“word1"不在图中
        addNode("hidden");
        assertEquals("No word1 in the graph!", queryBridgeWords("word1", "hidden"));
    }
    @Test
    void queryBridgeWordstest_2() {
        readTextFile("D:\\2024spring_lab\\software_engineer\\lab3\\src\\main\\input.txt");

        // 路径2：“word2"不在图中
        addNode("hidden");
        assertEquals("No word2 in the graph!", queryBridgeWords("hidden", "word2"));
    }
    @Test
    void queryBridgeWordstest_3() {
        readTextFile("D:\\2024spring_lab\\software_engineer\\lab3\\src\\main\\input.txt");

        // 路径3："word1"和"word2"都不在图中
        assertEquals("No word1 and word2 in the graph!", queryBridgeWords("word1", "word2"));
    }
    @Test
    void queryBridgeWordstest_4() {
        readTextFile("D:\\2024spring_lab\\software_engineer\\lab3\\src\\main\\input.txt");

        // 路径4:""word1"和"word2"都在图中,但word1没有邻居
        addNode("word1");
        addNode("word2");
        assertEquals("No bridge words from word1 to word2!", queryBridgeWords("word1", "word2"));
    }
    @Test
    void queryBridgeWordstest_5() {
        readTextFile("D:\\2024spring_lab\\software_engineer\\lab3\\src\\main\\input.txt");

        // 路径5:""word1"和"word2"都在图中,word1有邻居，但无桥接词
        addNode("word1");
        addNode("word2");
        addNode("bridge");
        addEdge("word1","bridge",1);
        assertEquals("No bridge words from word1 to word2!", queryBridgeWords("word1", "word2"));
    }
    @Test
    void queryBridgeWordstest_6() {
        readTextFile("D:\\2024spring_lab\\software_engineer\\lab3\\src\\main\\input.txt");

        // 路径5:""word1"和"word2"都在图中,word1有邻居，且有桥接词
        addNode("word1");
        addNode("word2");
        addNode("bridge");
        addEdge("word1","bridge",1);
        addEdge("bridge","word2",1);
        assertEquals("The bridge words from word1 to word2 are: bridge", queryBridgeWords("word1", "word2"));
    }
}